package com.analysis.extractors;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import com.analysis.PagingLoader;
import com.analysis.model.FetchItem;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.mysql.jdbc.Driver;

public class PagingExtractor extends BasicExtractor<PagingLoader> {
	private static final Logger LOG =Logger.getLogger(PagingExtractor.class);
	private volatile Set<String> willExe=new HashSet<>();
	private Connection connection;
	private CallableStatement prepareCall;
	public PagingExtractor(PagingLoader loader,FetchItem fetchItem) {
		super(loader, fetchItem);
		try {
			Class.forName(Driver.class.getName());
			this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/wchart_manager?characterEncoding=UTF-8", "root","root");
			this.prepareCall = connection.prepareCall("use wchart_manager");
			int executeQuery = prepareCall.executeUpdate("delete  from phone_section");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void extract() {
		loadData();
	}
	private void loadData(){
		try {
			checkAnalysisConfig();
			fetchGoods();
			fetchPagingInfo();
			gotoNextPage();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	/**
	 * fetch goods ....
	 * @throws Exception
	 */
	private void fetchGoods() throws Exception {
		HtmlElement body = this.loader.getBody();
		Element el=this.configCache.get(fetchItem.getMatchXmlName());
		if(el==null){
			throw new Exception("Can not find the match config");
		}
		List<HtmlElement> containers= body.getElementsByAttribute(el.attributeValue("tag"), el.attributeValue("attr_name"), el.attributeValue("attr_val"));
		for(int index=0;index<containers.size();index++){
			HtmlElement container = containers.get(index);
			Element element = el.element("pro");
			List<HtmlElement> pro = container.getElementsByAttribute(element.attributeValue("tag"), element.attributeValue("attr_name"), element.attributeValue("attr_val"));
			if(pro.size()==0){
				continue;
			}
			HtmlElement proElement = pro.get(0);
			final String proName=proElement.getElementsByTagName(element.attributeValue("el")).get(0).getTextContent();
			element = el.element("city");
			List<HtmlElement> cities = container.getElementsByAttribute(element.attributeValue("tag"), element.attributeValue("attr_name"), element.attributeValue("attr_val"));
			if(cities.size()==0){
				continue;
			}
			HtmlElement cElement = cities.get(0);
			List<HtmlElement> citiesEl = cElement.getHtmlElementsByTagName(element.attributeValue("el"));
			for(HtmlElement a:citiesEl){
				final String cityName = a.getTextContent();
				final String href = a.getAttribute("href");
				if(href!=null&&!href.isEmpty()){
					LOG.info("P:"+proName+",C:"+cityName);
					willExe.add("P:"+proName+",C:"+cityName);
					ScheduledExecutor.getExecutor().execute(new Runnable() {
						@Override
						public void run() {
							fetchCity(href,proName,cityName);
							
						}
					});
				}
				
			}
		}
		
		
	}
	public void fetchCity(final String href,final String pro,final String city) {
		
		
		try {
			PagingLoader loader=new PagingLoader(href);
			HtmlElement bodyEl = loader.getBody();
			List<HtmlElement> containers= bodyEl.getElementsByAttribute("div", "class", "all");
			HtmlElement htmlElement = containers.get(0);
			List<HtmlElement> heads = htmlElement.getElementsByAttribute("div", "class", "num_bg");
			List<HtmlElement> sections = htmlElement.getHtmlElementsByTagName("ul");
			for(int index=0;index<heads.size();index++){
				HtmlElement head = heads.get(index);
				HtmlElement ul = sections.get(index);
				head.removeChild("span", 0);
				DomNodeList<HtmlElement> list = ul.getElementsByTagName("a");
				for(HtmlElement a:list){
					String sql="INSERT INTO phone_section(section,city,operator,province)VALUES"+
					"('"+a.getTextContent().trim()+"','"+city+"','"+head.getTextContent().split(" ")[0]+"','"+pro+"')";
					//LOG.info(sql);
					try {
						java.sql.CallableStatement prepareCall = connection.prepareCall(sql);
						int executeQuery = prepareCall.executeUpdate();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			willExe.remove("P:"+pro+",C:"+city);
			LOG.info("P:"+pro+",C:"+city+" executed, no finish count:"+willExe.size());
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error(href);
			ScheduledExecutor.getExecutor().execute(new Runnable() {
				@Override
				public void run() {
					fetchCity(href,pro,city);
				}
			});
		} 
	}

	/**
	 * find page information
	 */
	private void fetchPagingInfo() {
		
	}

	/**
	 * jump to next page
	 */
	private void gotoNextPage() {
		
	}

}
