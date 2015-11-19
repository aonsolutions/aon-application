package com.code.aon.ui.loader.custom;

import java.io.InputStream;

public interface ICustomLoaderFactory {
	
	public boolean accept(byte[] data);
	
	public void load(InputStream file);
	
}
