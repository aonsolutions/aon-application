package com.code.aon.ui.cms.controller;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Config;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.hiru.XmlBuilder;
import com.code.aon.ui.cms.hiru.XmlBuilderException;
import com.code.aon.ui.cms.hiru.XmlBuilderListener;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.FTPUtil;

public class HiruGeneratorController implements XmlBuilderListener{

	private String url;
	
	private String destinationFolder;

	private String sourceFolder;

	String server;
	
	String user;
	
	String password;

	public HiruGeneratorController(){
		super();
		Config config = ControllerUtil.getCurrentConfig();
		url = ControllerUtil.getWebURL()+"/"+Constants.DOCUMENTS_PATH+"/"+"hiru";		
		destinationFolder = config.getFtp_path()+"/"+Constants.DOCUMENTS_PATH+"/"+"hiru";		
		sourceFolder = ControllerUtil.getDocumentsPath()+"/"+"hiru";
		server = config.getFtp_server();
		user = config.getFtp_user();
		password = config.getFtp_password();
	}
	
	public void onGenerateXmlFiles(ActionEvent event) throws ManagerBeanException{
		XmlBuilder b = new XmlBuilder(sourceFolder, url);
		b.addXmlBuilderListener(this);
		try {
			b.generate();
			this.generated = true;
		} catch (FileNotFoundException e) {
			throw new ManagerBeanException(e);
		} catch (XmlBuilderException e) {
			throw new ManagerBeanException(e);
		}finally{
			this.activePoll = false;
		}
	}
	
	public void onPublish(ActionEvent event) throws ManagerBeanException {
		this.activePoll = true;
		this.messages = new ArrayList<String>();
		try {
			if (FTPUtil.uploadFTP(destinationFolder,
					sourceFolder,
					server,
					user,
					password))
				this.published = true;
		}catch (Exception e) {
		}finally{
			this.activePoll = false;
		}
	}
	
	private boolean generated = false;

	private boolean published = false;

	private boolean activePoll = false;

	private List<String> messages;

	public void onInit(ActionEvent event){
		this.generated = false;
		this.published = false;
		this.activePoll = true;
		this.messages = new ArrayList<String>();
	}
	
	public List<String> getMessages() {
		return this.messages;
	}
	
	public boolean isActivePoll() {
		return this.activePoll;
	}

	public boolean isGenerated() {
		return this.generated;
	}

	public boolean isPublished() {
		return this.published;
	}

	public void onActivePoll(ActionEvent event){
		this.activePoll = true;
	}

	public void addMessage(String message) {
		this.messages.add(message);
	}

}
