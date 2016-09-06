package com.analysis;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class ScrollLoader extends PageLoader {

	protected ScrollLoader(String url) throws FailingHttpStatusCodeException,
			MalformedURLException, IOException {
		super(url);
	}

}
