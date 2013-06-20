package com.code.aon.common.net;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import sun.net.www.protocol.jar.Handler;

public class DummyHandler extends Handler {

	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new DummyURLConnection(url, this);
	}
	
}
