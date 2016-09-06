package com.analysis;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class PagingLoader extends PageLoader {

	public PagingLoader(String url) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		super(url);
	}

}
