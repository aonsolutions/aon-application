/*
 * FCKeditor - The text editor for internet
 * Copyright (C) 2003-2005 Frederico Caldeira Knabben
 * 
 * Licensed under the terms of the GNU Lesser General Public License:
 * 		http://www.opensource.org/licenses/lgpl-license.php
 * 
 * For further information visit:
 * 		http://www.fckeditor.net/
 * 
 * File Name: ConnectorServlet.java
 * 	Java Connector for Resource Manager class.
 * 
 * Version:  2.3
 * Modified: 2005-08-11 16:29:00
 * 
 * File Authors:
 * 		Simone Chiaretta (simo@users.sourceforge.net)
 */

package com.code.aon.ui.cms.servlet;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.code.aon.cms.Config;
import com.code.aon.cms.GenericPageDetail;
import com.code.aon.cms.Language;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.Constants;

/**
 * Servlet to upload and browse files.<br>
 * 
 * This servlet accepts 4 commands used to retrieve and create files and folders
 * from a server directory. The allowed commands are:
 * <ul>
 * <li>GetFolders: Retrive the list of directory under the current folder
 * <li>GetFoldersAndFiles: Retrive the list of files and directory under the
 * current folder
 * <li>CreateFolder: Create a new directory under the current folder
 * <li>FileUpload: Send a new file to the server (must be sent with a POST)
 * </ul>
 * 
 * @author Simone Chiaretta (simo@users.sourceforge.net)
 */

public class GalleryConnectorServlet extends HttpServlet implements Constants {

	/** Se obtiene el Logger adecuado */
	private static final Logger LOGGER = Logger.getLogger(GalleryConnectorServlet.class.getName());

	private static final String IMAGE = "Image";

	private static final String FILE = "File";
	
	private static final String GENERIC = "CMSGP";

	private static final String ROOT_FOLDER = "/";

	private static HttpSession session;

	private HttpServletRequest request;
	
	// private Locale locale;

	/**
	 * Initialize the servlet.<br>
	 * Retrieve from the servlet configuration the "baseDir" which is the root
	 * of the file repository:<br>
	 * If not specified the value of "/UserFiles/" will be used.
	 * 
	 */
	public void init() throws ServletException {
	}

	/**
	 * Manage the Get requests (GetFolders, GetFoldersAndFiles, CreateFolder).<br>
	 * 
	 * The servlet accepts commands sent in the following format:<br>
	 * connector?Command=CommandName&Type=ResourceType&CurrentFolder=FolderPath<br>
	 * <br>
	 * It execute the command and then return the results to the client in XML
	 * format. 
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		LOGGER.fine("--- BEGIN DOGET ---");
		LOGGER.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GET");
		this.request = request;
		this.session = request.getSession();
		
		response.setContentType("text/xml; charset=UTF-8");
		response.setHeader("Cache-Control", "no-cache");

		// this.locale = request.getLocale();
		String commandStr = request.getParameter("Command");
		String typeStr = request.getParameter("Type");
		String currentFolderStr = request.getParameter("CurrentFolder");

		String currentPath = "";
		if (FILE.equals(typeStr)) currentPath = "/" + DOCUMENTS_PATH + currentFolderStr;
		else if (IMAGE.equals(typeStr)) currentPath = "/" + IMAGES_PATH + currentFolderStr;

		Document document = null;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		} catch (ParserConfigurationException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}

		Node root = CreateCommonXml(document, commandStr, typeStr, currentFolderStr, currentPath);

		LOGGER.fine("Command = " + commandStr);

		PrintWriter out = null;
		try {
			if (commandStr.equals("GetFolders")) {
				getFolders(typeStr, currentFolderStr, root, document);
			} else if (commandStr.equals("GetFoldersAndFiles")) {
				getFolders(typeStr, currentFolderStr, root, document);
				getFiles(typeStr, currentFolderStr, root, document);
			} else if (commandStr.equals("CreateFolder")) {
				String retValue = "110";
				setCreateFolderResponse(retValue, root, document);
			}

			document.getDocumentElement().normalize();
			TransformerFactory tFactory = TransformerFactory.newInstance();
			Transformer transformer = tFactory.newTransformer();

			DOMSource source = new DOMSource(document);

			out = response.getWriter();
			StreamResult result = new StreamResult(out);
			transformer.transform(source, result);
			out.flush();
			
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		} finally {
			IOUtils.closeQuietly(out);
			out = null;
		}

	}

	private void setCreateFolderResponse(String retValue, Node root, Document doc) {
		Element myEl = doc.createElement("Error");
		myEl.setAttribute("number", retValue);
		root.appendChild(myEl);
	}

	private void getFolders(String typeStr, String currentFolderStr, Node root, Document doc) throws ManagerBeanException {
		String basePath = "";
		if (FILE.equals(typeStr)) basePath = getResourcesPath();
		else basePath = getImagesPath();
		String currentPath = basePath + "" + currentFolderStr;

		Element folders = doc.createElement("Folders");
		root.appendChild(folders);
		if ((FILE.equals(typeStr) || IMAGE.equals(typeStr))) {
			File dir = new File(currentPath);
			File files[] = dir.listFiles();
		    for (int i = 0; i < files.length; i++) {
		    	File temp = files[i];
		    	if (temp.isDirectory()) {
					Element myEl = doc.createElement("Folder");
					myEl.setAttribute("name", temp.getName());
					folders.appendChild(myEl);
		    	}
		    }
		}
	}

	private void getImageFiles(String currentFolderStr, Element files, Document doc) throws ManagerBeanException {
		LOGGER.info(">>>>>>>>>>>> Images currentFolderStr " + currentFolderStr );
		String basePath = getImagesPath();
		String currentPath = basePath + "" + currentFolderStr;

		File dir = new File(currentPath);
		ImageFileFilter filter = new ImageFileFilter();
		File list[] = dir.listFiles(filter);
		for (int i=0; i < list.length; i++) {
			File temp = list[i];
			if (!temp.isDirectory()) {
				Element myEl = doc.createElement("File");
				myEl.setAttribute("name", temp.getName());
				double length = temp.length() / 1024.0;
				int size = (int)(length * 100);
				
				myEl.setAttribute("size", "" + size / 100.0);
				files.appendChild(myEl);
			}
		}
	}

	private void getResourceFiles(String currentFolderStr, Element files, Document doc) throws ManagerBeanException {
		LOGGER.info(">>>>>>>>>>>> Documents currentFolderStr " + currentFolderStr );
		String basePath = getResourcesPath();
		String currentPath = basePath + "" + currentFolderStr;

		File dir = new File(currentPath);
		ResourceFileFilter filter = new ResourceFileFilter();
		File list[] = dir.listFiles(filter);
		for (int i=0; i < list.length; i++) {
			File temp = list[i];
			if (!temp.isDirectory()) {
				Element myEl = doc.createElement("File");
				myEl.setAttribute("name", temp.getName());
				long size = temp.length();
				myEl.setAttribute("size", "" + size / 1024);
				files.appendChild(myEl);
			}
		}
	}

	private void getGenericPageFiles(Element files, Document doc) throws ManagerBeanException {
		LOGGER.info(">>>>>>>>>>>> GenericPage currentFolderStr /");
		List<String> list = getCurrentGenericPagesAliases();
		for (int i=0; i < list.size(); i++) {
			String temp = list.get(i);
			Element myEl = doc.createElement("File");
			myEl.setAttribute("name", temp);
			myEl.setAttribute("size", "0");
			files.appendChild(myEl);
		}
	}

	private void getFiles(String typeStr, String currentFolderStr, Node root, Document doc) throws ManagerBeanException {
		Element files = doc.createElement("Files");
		root.appendChild(files);

		if (IMAGE.equals(typeStr)) getImageFiles(currentFolderStr, files, doc);
		else if (FILE.equals(typeStr)) getResourceFiles(currentFolderStr, files, doc);
		else if (GENERIC.equals(typeStr)) getGenericPageFiles(files, doc);
	}

	private Node CreateCommonXml(Document doc, String commandStr, String typeStr, String currentPath, String currentUrl) {

		Element root = doc.createElement("Connector");
		doc.appendChild(root);
		root.setAttribute("command", commandStr);
		root.setAttribute("resourceType", typeStr);

		Element myEl = doc.createElement("CurrentFolder");
		myEl.setAttribute("path", currentPath);
		myEl.setAttribute("url", currentUrl);
		root.appendChild(myEl);

		return root;

	}

	public static String getImagesPath() {
		if (session.getAttribute(SESSION_CONFIG) == null) {
			try {
				IManagerBean configBean = BeanManager.getManagerBean(Config.class);
				List<ITransferObject> list = configBean.getList(null);
				Config config = (Config)list.get(0);
				session.setAttribute(SESSION_CONFIG, config);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		Config config = (Config)session.getAttribute(SESSION_CONFIG);
		String path = DOMAINS_PATH + "/" + config.getDomain() + 
									"/" + WEBSITE_PATH + 
									"/" + config.getPreviewUrl() +
									"/" + IMAGES_PATH;
		return path;
	}
	
	public static String getResourcesPath() {
		if (session.getAttribute(SESSION_CONFIG) == null) {
			try {
				IManagerBean configBean = BeanManager.getManagerBean(Config.class);
				List<ITransferObject> list = configBean.getList(null);
				Config config = (Config)list.get(0);
				session.setAttribute(SESSION_CONFIG, config);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		Config config = (Config)session.getAttribute(SESSION_CONFIG);
		String path = DOMAINS_PATH + "/" + config.getDomain() + 
									"/" + WEBSITE_PATH + 
									"/" + config.getPreviewUrl() +
									"/" + DOCUMENTS_PATH;
		return path;
	}


	public static List<String> getCurrentGenericPagesAliases() {
		List<String> list = new ArrayList<String>();
		if (session.getAttribute(SESSION_CURRENT_LANGUAGE) == null) {
			LOGGER.info(">>>>>>>>>>>>> ERROR");
		}
		Language currentLanguage = (Language)session.getAttribute(SESSION_CURRENT_LANGUAGE);
		try {
			IManagerBean bean = BeanManager.getManagerBean(GenericPageDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.GENERIC_PAGE_DETAIL_LANGUAGE_ID), currentLanguage.getId());
			List<ITransferObject> l = (List<ITransferObject>)bean.getList(criteria);
			for (int i=0; i < l.size(); i++) {
				GenericPageDetail gpd = (GenericPageDetail)l.get(i);
				if (gpd.getGeneric_page().isActive()) {
					String page = Templates.GENERIC.getHtmlName();
					page = page.replaceAll("%NAME%", gpd.getGeneric_page().getAlias());
					list.add(page);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return list;
	}

	private class ImageFileFilter implements FilenameFilter {

		protected String extensions = ".jpg|.jpeg|.gif|.png";

		public boolean accept(File f, String s) {
			boolean found = false;
			if (s.lastIndexOf(".") >= 0) {
				String ext = s.substring(s.lastIndexOf("."));
				ext = ext.toLowerCase();
				found = (extensions.indexOf(ext) >= 0);
			}
	        return found;
	    }
	}

	private class ResourceFileFilter implements FilenameFilter {

		protected String extensions = ".pdf|.doc|.xls|.html|.htm";

		public boolean accept(File f, String s) {
			boolean found = false;
			if (s.lastIndexOf(".") >= 0) {
				String ext = s.substring(s.lastIndexOf("."));
				ext = ext.toLowerCase();
				found = (extensions.indexOf(ext) >= 0);
			}
	        return found;
	    }
	}

}
