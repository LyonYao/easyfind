package com.analysis;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public abstract class PageLoader {
	protected WebClient webClient = new WebClient(BrowserVersion.CHROME);
	protected HtmlPage  page;
	protected PageLoader(final String url) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setJavaScriptEnabled(false);
		webClient.getOptions().setActiveXNative(false);
		page = webClient.getPage(url);
		
	}
	public HtmlElement getBody(){
		return page.getBody();
	}
}
