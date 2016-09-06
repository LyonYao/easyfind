package com.analysis;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.analysis.extractors.BasicExtractor;
import com.analysis.extractors.PagingExtractor;
import com.analysis.extractors.ScrollExtractor;
import com.analysis.model.FetchItem;

public class DataFetch {
	private final ExecutorService executor = Executors.newFixedThreadPool(10);
	private final static String FETCH_LIST = "fetch_list.xml";
	private final static Logger LOG=Logger.getLogger(DataFetch.class);
	private final static Set<FetchItem> fetchList=new HashSet<>();
	static {
		loadList();
	}

	private static void loadList() {
		// load fetch list
		InputStream resourceAsStream = DataFetch.class
				.getResourceAsStream(FETCH_LIST);
		SAXReader saxReader = new SAXReader();
		Document doc;
		try {
			doc = saxReader.read(resourceAsStream);
			Element rootElement = doc.getRootElement();
			@SuppressWarnings("unchecked")
			Iterator<Element> typesIt = rootElement.elementIterator("item");
			while (typesIt.hasNext()) {
				Element itemEl = typesIt.next();
				FetchItem item=new FetchItem(itemEl.attributeValue("name"),itemEl.attributeValue("url"),itemEl.attributeValue("loader_class"),itemEl.attributeValue("match_config"));
				fetchList.add(item);
			}
			LOG.info("Fetch list loaded...");
		} catch (DocumentException e) {
			LOG.error("Load fetch list failed...");
			e.printStackTrace();
		}
		

	}

	public void fectch() {
		for(final FetchItem item:fetchList){
			try{
				System.err.println("Fetch"+item.getName());
				final String loadClassName = item.getLoadClassName();
				Class<?> loadClass = Class.forName(loadClassName);
				if(loadClass.getGenericSuperclass()!=PageLoader.class){
					throw new Exception("Error load class name "+loadClassName);
				}
				Constructor<?> constructor = loadClass.getConstructor(String.class);
				Object pageLoader = constructor.newInstance(item.getUrl());
				BasicExtractor<?> extractor=null;
				if(pageLoader instanceof PagingLoader){
					extractor= new PagingExtractor((PagingLoader) pageLoader,item);
				}else if(pageLoader instanceof ScrollLoader){
					extractor= new ScrollExtractor((ScrollLoader) pageLoader,item);
				}
				if(extractor!=null){
					executor.execute(extractor);
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
