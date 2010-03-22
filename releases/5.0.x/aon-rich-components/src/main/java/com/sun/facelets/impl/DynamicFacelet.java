package com.sun.facelets.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;

import javax.el.ELException;
import javax.faces.FacesException;

import com.sun.facelets.Facelet;
import com.sun.facelets.FaceletContext;
import com.sun.facelets.FaceletException;
import com.sun.facelets.FaceletHandler;

public class DynamicFacelet {
	
	private static Object getPrivateProperty( Object o, String property ) {
		try {
			Field f = o.getClass().getDeclaredField(property);
			f.setAccessible(true);
			return f.get(o);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;				
	}
	
	private DefaultFacelet getDefaultFacelet( FaceletContext ctx ) {
		try {
			Field f = ctx.getClass().getDeclaredField("facelet");
			f.setAccessible(true);
			Object o = f.get(ctx);
			return (DefaultFacelet) o;
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;		
	}

	private static DefaultFaceletFactory getDefaultFaceletFactory( FaceletContext ctx ) {
		DefaultFacelet facelet = (DefaultFacelet) getPrivateProperty(ctx, "facelet");
		if ( facelet != null ) {
			return (DefaultFaceletFactory) getPrivateProperty(facelet, "factory");
		}
		return null;
	}
	
	public static Facelet createFacelet(FaceletContext ctx, URL url) throws IOException,
			FaceletException, FacesException, ELException {
		String alias = "/" + url.getFile();
		try {
			DefaultFaceletFactory dff =  getDefaultFaceletFactory(ctx);
			FaceletHandler h = dff.getCompiler().compile(url, alias);
			DefaultFacelet f = new DefaultFacelet(dff, dff.getCompiler()
					.createExpressionFactory(), url, alias, h);
			return f;
		} catch (FileNotFoundException fnfe) {
			throw new FileNotFoundException("Facelet " + alias
					+ " not found at: " + url.toExternalForm());
		}
	}
}
