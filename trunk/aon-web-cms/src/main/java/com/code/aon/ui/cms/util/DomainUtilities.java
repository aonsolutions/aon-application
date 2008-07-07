package com.code.aon.ui.cms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.enumeration.SidebarType;

public class DomainUtilities {

	public static boolean hasPageType(PageType type){
		try{
			Properties prop = new Properties();
			prop.load(PageType.class.getResourceAsStream(PAGETYPE));
			
			Properties prop_ = new Properties();
			String file = ControllerUtil.getConfigPath()+File.separator+PAGETYPE;
			InputStream stream = null;
			try{
				stream = new FileInputStream(file);
				prop_.load(stream);
			}catch (Exception e) {
			}
			
			if (prop_.get(type.toString())!=null){
				if ("true".equalsIgnoreCase(""+prop_.get(type.toString())))
					return true;
			}else{
				if ("true".equalsIgnoreCase(""+prop.get(type.toString())))
					return true;
			}
		}catch (Exception e) {
		}
		return false;
	}

	public static boolean hasSidebarType(SidebarType type){
		try{
			Properties prop = new Properties();
			prop.load(SidebarType.class.getResourceAsStream(SIDEBARTYPE));
			
			Properties prop_ = new Properties();
			String file = ControllerUtil.getConfigPath()+File.separator+SIDEBARTYPE;
			InputStream stream = null;
			try{
				stream = new FileInputStream(file);
				prop_.load(stream);
			}catch (Exception e) {
			}
			
			if (prop_.get(type.toString())!=null){
				if ("true".equalsIgnoreCase(""+prop_.get(type.toString())))
					return true;
			}else{
				if ("true".equalsIgnoreCase(""+prop.get(type.toString())))
					return true;
			}
		}catch (Exception e) {
		}
		return false;
	}

	public static boolean hasModularPageOptionType(ModularPageOptionType type){
		try{
			Properties prop = new Properties();
			prop.load(ModularPageOptionType.class.getResourceAsStream(MODULARPAGEOPTIONTYPE));
			
			Properties prop_ = new Properties();
			String file = ControllerUtil.getConfigPath()+File.separator+MODULARPAGEOPTIONTYPE;
			InputStream stream = null;
			try{
				stream = new FileInputStream(file);
				prop_.load(stream);
			}catch (Exception e) {
			}
			
			if (prop_.get(type.toString())!=null){
				if ("true".equalsIgnoreCase(""+prop_.get(type.toString())))
					return true;
			}else{
				if ("true".equalsIgnoreCase(""+prop.get(type.toString())))
					return true;
			}
		}catch (Exception e) {
		}
		return false;
	}

	private static String MODULARPAGEOPTIONTYPE = "modularpageoptiontype.properties";
	private static String SIDEBARTYPE = "sidebartype.properties";
	private static String PAGETYPE = "pagetype.properties";
}
