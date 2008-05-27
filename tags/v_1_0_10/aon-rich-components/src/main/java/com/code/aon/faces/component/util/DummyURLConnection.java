package com.code.aon.faces.component.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import sun.net.www.protocol.jar.Handler;
import sun.net.www.protocol.jar.JarURLConnection;

public class DummyURLConnection extends JarURLConnection {

	public DummyURLConnection(URL url, Handler handler)
			throws MalformedURLException, IOException {
		super(url, handler);
	}

	@Override
	public long getLastModified() {
		long result = 0;
		try {
			result = getJarFileURL().openConnection().getLastModified();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
