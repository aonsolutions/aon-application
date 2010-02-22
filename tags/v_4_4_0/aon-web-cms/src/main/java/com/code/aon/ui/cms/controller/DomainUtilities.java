package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;

import com.code.aon.cms.enumeration.ModularPageOptionType;
import com.code.aon.cms.enumeration.PageType;
import com.code.aon.cms.enumeration.SidebarType;
import com.code.aon.ui.cms.util.ControllerUtil;

public class DomainUtilities {
	
	private static final Logger LOGGER = Logger.getLogger(DomainUtilities.class.getName());

	private boolean adminProfile = false;
	
	public DomainUtilities(){
		initPageType();
		initSidebarType();
		initModularPageOptionType();
		initMenu();
	}
	
	public void assignAdminProfile(){
		adminProfile = true;
	}
	
	//*************************************************************
	// PAGETYPE
	//*************************************************************
	
	private Properties propDefPageType = new Properties();
	private Properties propDomainPageType = new Properties();
	
	private void initPageType(){
		try {
			propDefPageType.load(PageType.class.getResourceAsStream(PAGETYPE));
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		
		File file = new File( ControllerUtil.getConfigPath(), PAGETYPE );
		if ( file.exists() ) {
			InputStream stream = null;
			try{
				stream = new FileInputStream(file);
				propDomainPageType.load(stream);
			}catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
			}finally{
				IOUtils.closeQuietly(stream);
				stream= null;
			}
		}
	}

	public boolean hasPageType(PageType type){
		if (adminProfile) return true;
		try{
			if (propDomainPageType.get(type.toString())!=null){
				if ("true".equalsIgnoreCase(""+propDomainPageType.get(type.toString())))
					return true;
			}else{
				if ("true".equalsIgnoreCase(""+propDefPageType.get(type.toString())))
					return true;
			}
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		return false;
	}

	private static String PAGETYPE = "pagetype.properties";

	//*************************************************************
	// SIDEBARTYPE
	//*************************************************************
	
	private Properties propDefSidebarType = new Properties();
	private Properties propDomainSidebarType = new Properties();
	
	private void initSidebarType(){
		try {
			propDefSidebarType.load(PageType.class.getResourceAsStream(SIDEBARTYPE));
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		
		File file = new File( ControllerUtil.getConfigPath(), SIDEBARTYPE );
		if ( file.exists() ) {
			InputStream stream = null;
			try{
				stream = new FileInputStream(file);
				propDomainSidebarType.load(stream);
			} catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
			}finally{
				IOUtils.closeQuietly(stream);
				stream= null;
			}
		}
	}
	
	public boolean hasSidebarType(SidebarType type){
		if (adminProfile) return true;
		try{
			if (propDomainSidebarType.get(type.toString())!=null){
				if ("true".equalsIgnoreCase(""+propDomainSidebarType.get(type.toString())))
					return true;
			}else{
				if ("true".equalsIgnoreCase(""+propDefSidebarType.get(type.toString())))
					return true;
			}
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		return false;
	}

	private static String SIDEBARTYPE = "sidebartype.properties";
	
	//*************************************************************
	// MODULARPAGEOPTIONTYPE
	//*************************************************************

	private Properties propDefModularPageOptionType = new Properties();
	private Properties propDomainModularPageOptionType = new Properties();
	
	private void initModularPageOptionType(){
		try {
			propDefModularPageOptionType.load(PageType.class.getResourceAsStream(MODULARPAGEOPTIONTYPE));
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		
		File file = new File( ControllerUtil.getConfigPath(), MODULARPAGEOPTIONTYPE );
		if ( file.exists() ) {
			InputStream stream = null;
			try {
				stream = new FileInputStream(file);
				propDomainModularPageOptionType.load(stream);
			} catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
			} finally {
				IOUtils.closeQuietly(stream);
				stream= null;
			}			
		}
	}
	
	public boolean hasModularPageOptionType(ModularPageOptionType type){
		if (adminProfile) return true;
		try{
			if (propDomainModularPageOptionType.get(type.toString())!=null){
				if ("true".equalsIgnoreCase(""+propDomainModularPageOptionType.get(type.toString())))
					return true;
			}else{
				if ("true".equalsIgnoreCase(""+propDefModularPageOptionType.get(type.toString())))
					return true;
			}
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		return false;
	}

	private static String MODULARPAGEOPTIONTYPE = "modularpageoptiontype.properties";
	
	//*************************************************************
	// MENU
	//*************************************************************

	private Properties propDefMenu = new Properties();
	private Properties propDomainMenu = new Properties();
	
	private void initMenu(){
		try {
			propDefMenu.load(PageType.class.getResourceAsStream(MENU));
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		
		File file = new File( ControllerUtil.getConfigPath(), MENU );
		if ( file.exists() ) {
			InputStream stream = null;
			try {
				stream = new FileInputStream(file);
				propDomainMenu.load(stream);
			} catch (Throwable th) {
				LOGGER.log(Level.SEVERE, th.getMessage(), th);
			} finally {
				IOUtils.closeQuietly(stream);
				stream= null;
			}
		}
	}
	
	public boolean hasMenuOption(String option){
		if (adminProfile) return true;
		try{
			if (propDomainMenu.get(option)!=null){
				if ("true".equalsIgnoreCase(""+propDomainMenu.get(option)))
					return true;
			}else{
				if ("true".equalsIgnoreCase(""+propDefMenu.get(option)))
					return true;
			}
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		return false;
	}

	private static String MENU = "menu.properties";
	
	private static String MENU_MODULAR_OPTION = "MODULAR_OPTION";
	private static String MENU_GENERIC_PAGE = "GENERIC_PAGE";
	private static String MENU_LINK = "MENU_LINK";
	private static String MENU_FAQ = "MENU_FAQ";
	private static String MENU_ARTICLE = "MENU_ARTICLE";
	private static String MENU_BANNER = "MENU_BANNER";
	private static String MENU_DOWNLOAD = "MENU_DOWNLOAD";
	private static String MENU_ALBUM = "MENU_ALBUM";
	private static String MENU_DIRECT_ACCESS = "MENU_DIRECT_ACCESS";
	private static String MENU_ACTIVITY = "MENU_ACTIVITY";
	private static String MENU_PRODUCT = "MENU_PRODUCT";
	private static String MENU_HIRU = "MENU_HIRU";
	private static String MENU_BULLETIN = "MENU_BULLETIN";
	private static String MENU_SPORT = "MENU_SPORT";

	public boolean isModularMenu(){
		return hasMenuOption(MENU_MODULAR_OPTION);
	}

	public boolean isGenericPageMenu(){
		return hasMenuOption(MENU_GENERIC_PAGE);
	}

	public boolean isLinkMenu(){
		return hasMenuOption(MENU_LINK);
	}

	public boolean isFaqMenu(){
		return hasMenuOption(MENU_FAQ);
	}

	public boolean isArticleMenu(){
		return hasMenuOption(MENU_ARTICLE);
	}

	public boolean isBannerMenu(){
		return hasMenuOption(MENU_BANNER);
	}

	public boolean isDownloadMenu(){
		return hasMenuOption(MENU_DOWNLOAD);
	}

	public boolean isAlbumMenu(){
		return hasMenuOption(MENU_ALBUM);
	}

	public boolean isDirectAccessMenu(){
		return hasMenuOption(MENU_DIRECT_ACCESS);
	}

	public boolean isActivityMenu(){
		return hasMenuOption(MENU_ACTIVITY);
	}

	public boolean isProductMenu(){
		return hasMenuOption(MENU_PRODUCT);
	}
	
	public boolean isHiruMenu(){
		return hasMenuOption(MENU_HIRU);
	}

	public boolean isBulletinMenu(){
		return hasMenuOption(MENU_BULLETIN);
	}

	public boolean isSportMenu(){
		return hasMenuOption(MENU_SPORT);
	}
}
