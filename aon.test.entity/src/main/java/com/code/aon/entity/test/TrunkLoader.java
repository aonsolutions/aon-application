package com.code.aon.entity.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import org.apache.commons.io.IOUtils;

public class TrunkLoader extends ClassLoader {
	private ClassPool pool;
	private String dir = "/tmp/compare";
	private String aonVersion = "6.19-SNAPSHOT"; 
	
	public TrunkLoader(String projectName) throws NotFoundException, IOException {
		pool = new ClassPool(true);
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
		String jar = projectName + "-" + aonVersion + ".jar";
		String group = "com/code/aon/";
		if ("aon.calendar".equals(projectName) || "aon.payroll".equals(projectName)) {
			group = "com/esferalia/aon/";	
		}
		String url = "http://dev.esferalia.com/maven2_repositories/inhouse_snapshot/"+ group + projectName + "/"+aonVersion+"/" + jar;
		String output = dir + "/" + jar;
		File outputFile = new File(output);
		if (!outputFile.exists()) {
			getJAR(url, output);
		}
		pool.insertClassPath(output);
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			CtClass cc = pool.get(name);
			byte[] b = cc.toBytecode();
			return defineClass(name, b, 0, b.length);
		} catch (NotFoundException e) {
			throw new ClassNotFoundException();
		} catch (IOException e) {
			throw new ClassNotFoundException();
		} catch (CannotCompileException e) {
			throw new ClassNotFoundException();
		}
	}

	public CtClass getClass(String name) throws ClassNotFoundException {
		try {
			CtClass cc = pool.get(name);
			return cc;
		} catch (NotFoundException e) {
			throw new ClassNotFoundException();
		}
	}

	public void getJAR(String theUrl, String filename) throws IOException {
		try {
			URL gotoUrl = new URL(theUrl);
			InputStream input = gotoUrl.openStream();
			FileOutputStream output = new FileOutputStream(filename);
			IOUtils.copy(input, output);
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			throw ioe;
		}
	}
}
