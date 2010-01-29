package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FileNotFoundException;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.cms.Config;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.hiru.XmlBuilder;
import com.code.aon.ui.cms.hiru.XmlBuilderException;
import com.code.aon.ui.cms.hiru.XmlBuilderListener;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.FTPUtil;
import com.code.aon.ui.util.AonUtil;

public class HiruGeneratorController implements XmlBuilderListener, ICMSConstants {
	
	private String url;
	
	private File sourceFolder;
	
	private boolean generated;

	private GeneratorStatusController status;
	
	public HiruGeneratorController(){
		url = ControllerUtil.getWebURL()+"/"+Constants.DOCUMENTS_PATH+"/"+"hiru";		
		sourceFolder = new File( ControllerUtil.getDocumentsPath(), "hiru" );
		status = (GeneratorStatusController)AonUtil.getRegisteredBean(GENERATOR_STATUS);
	}
	
	public void onGenerateXmlFiles(ActionEvent event) throws ManagerBeanException{
		try {
			initGenerator();
			generate();
			finalizeGenerator();
			this.generated = true;
		} catch ( Throwable th ) {
			generatorError(th);
		}		
	}
	
	private void generate() throws FileNotFoundException, XmlBuilderException {
		XmlBuilder b = new XmlBuilder(sourceFolder, url);
		b.addXmlBuilderListener(this);
		b.generate();		
	}
	
	public void onPublish(ActionEvent event) throws ManagerBeanException {
		try {
			initGenerator();
			Config config = ControllerUtil.getCurrentConfig();	
			String destinationFolder = config.getFtp_path()+"/"+Constants.DOCUMENTS_PATH+"/"+"hiru";		
			String server = config.getFtp_server();
			String user = config.getFtp_user();
			String password = config.getFtp_password();			
			FTPUtil.uploadFTP(destinationFolder, sourceFolder, server, user, password);
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		}		
	}

	public void onInit(ActionEvent event){
		this.generated = false;
	}

	public boolean isGenerated() {
		return this.generated;
	}
	
	@Override
	public void addMessage(String message) {
		status.info(message);
	}

	private void initGenerator(){
		System.gc();
		status.onInit(null);
	}

	private void finalizeGenerator() {
		finalizeGenerator(false);
	}
	
	private void finalizeGenerator( boolean withErrors ) {
		status.info("----------------------------------------------------------------------------------------------------------------------------------------");
		if ( withErrors ) {
			status.info( AonUtil.getMessage(ICMSConstants.BUNDLE_NAME, CMS_GENERATOR_ERROR) );
		} else {
			status.info( AonUtil.getMessage(ICMSConstants.BUNDLE_NAME, CMS_GENERATOR_FINISHED) );
		}		
		status.info("----------------------------------------------------------------------------------------------------------------------------------------");
		status.finalized();
		System.gc();
	}

	private void generatorError( Throwable th ) {
		status.error( th.getMessage() );
		finalizeGenerator( true );
		throw new AbortProcessingException(th.getMessage(), th);		
	}	
	
}
