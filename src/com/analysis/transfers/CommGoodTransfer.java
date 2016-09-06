package com.analysis.transfers;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.analysis.model.Good;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlElement;

public class CommGoodTransfer implements GoodTransfer {
	private static Logger LOG=Logger.getLogger(CommGoodTransfer.class);
	private Element matchEl;

	public CommGoodTransfer(Element element) {
		this.matchEl = element;
		
	}

	@Override
	public Object translate(HtmlElement element) {
		Iterator<Element> elementIterator = matchEl.elementIterator();
		Good target=new Good();
		while (elementIterator.hasNext()) {
			Element next = (Element) elementIterator.next();
			String name = next.getName();
			String elReg=next.attributeValue("el");
			String type=next.attributeValue("type");
			String attr=next.attributeValue("attr_name");
			try {
				buildGood(target,element.asXml(),name,elReg,type,attr);
			} catch (DocumentException e) {
				e.printStackTrace();
			}
		}
		return target;
	}

	/**
	 * @param element
	 * @param name
	 * @param elReg
	 * @param type
	 * @param attr
	 * @return
	 * @throws DocumentException 
	 */
	private void buildGood(Good target,String xmlHtml,String name, String elReg, String type, String attr) throws DocumentException {
		//TODO build Object information
		String[] split = elReg.split("/");
		Document doc = DocumentHelper.parseText(xmlHtml);
        Element root = doc.getRootElement();
        Element find=null;
        Element curEl=root;
		for(int i=0;i<split.length;i++){
			String cur=split[i];
			String el=cur;
			int index=0;
			if(cur.indexOf(".")!=-1){
				String[] pos = cur.split("\\.");
				el=pos[0];
				index=Integer.parseInt(pos[1]);
			}
			@SuppressWarnings("unchecked")
			List<Element> finded = curEl.elements(el);
			curEl = finded.get(index);
			find=curEl;
		}
		if(find!=null){
			try {
				String val=null;
				if(type.equals("text")){
					val=find.getTextTrim();
				}else if(type.equals("attr")){
					val=find.attributeValue(attr);
				}else{
					LOG.error("Error config for "+target.getClass().getName() +" ::"+name);
					return;
				}
				Field field = target.getClass().getDeclaredField(name);
				field.setAccessible(true);
				if(field.getType()==Integer.class){
					field.set(target, Integer.parseInt(val));
				}else if(field.getType()==Float.class){
					field.set(target, Float.parseFloat(val));
				}else if(field.getType()==String.class){
					field.set(target, val);
				}else{
					LOG.error("Do not support "+field.getType().getName()+" to set value");
				}
				
			} catch (NoSuchFieldException e) {
				LOG.error("Can not find the field "+name+" for "+ target.getClass().getName());
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}


}
