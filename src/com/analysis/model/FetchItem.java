package com.analysis.model;

public class FetchItem {
	private String name;
	private String url;
	private String loadClassName;
	private String matchXmlName;
	private String type;
	public FetchItem(String name, String url, String loadClassName,
			String matchXmlName) {
		super();
		this.name = name;
		this.url = url;
		this.loadClassName = loadClassName;
		this.matchXmlName = matchXmlName;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getLoadClassName() {
		return loadClassName;
	}
	public void setLoadClassName(String loadClassName) {
		this.loadClassName = loadClassName;
	}
	public String getMatchXmlName() {
		return matchXmlName;
	}
	public void setMatchXmlName(String matchXmlName) {
		this.matchXmlName = matchXmlName;
	}
	

}
