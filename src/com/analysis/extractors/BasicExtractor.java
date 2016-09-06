package com.analysis.extractors;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.analysis.PageLoader;
import com.analysis.model.FetchItem;

public abstract class BasicExtractor<T extends PageLoader> implements Runnable{
	protected final T loader;
	protected final FetchItem fetchItem;
	protected final Map<String, Element> configCache=new HashMap<String, Element>();
	public BasicExtractor(final T loader,final FetchItem fetchItem) {
		super();
		this.loader = loader;
		this.fetchItem = fetchItem;
	}
	protected abstract void extract();
	@Override
	public void run() {
		System.err.println("excute");
		extract();
	}
	protected void checkAnalysisConfig() {
		String matchXmlName = this.fetchItem.getMatchXmlName();
		if(configCache.containsKey(matchXmlName))return;
		InputStream resourceAsStream = BasicExtractor.class
				.getResourceAsStream("/com/analysis/config/"+matchXmlName);
		SAXReader saxReader = new SAXReader();
		try {
			Document doc = saxReader.read(resourceAsStream);
			Element rootElement = doc.getRootElement();
			configCache.put(matchXmlName, rootElement);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
	}
}
