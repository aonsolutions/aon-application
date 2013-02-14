package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.cms.Config;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.hiru.XmlBuilder;
import com.code.aon.ui.cms.hiru.XmlBuilderException;
import com.code.aon.ui.cms.hiru.XmlBuilderListener;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.publisher.util.FTPUtil;
import com.code.aon.ui.util.AonUtil;

public class HiruGeneratorController implements XmlBuilderListener, ICMSConstants {
	
	private String url;
	
	private File sourceFolder;
	
	private boolean generated;

	private LogPanelController logger;
	
	public HiruGeneratorController(){
		url = ControllerUtil.getWebURL()+"/"+Constants.DOCUMENTS_PATH+"/"+"hiru";		
		sourceFolder = new File( ControllerUtil.getDocumentsPath(), "hiru" );
		logger = LogPanelController.getInstance();
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
		FTPUtil ftp = new FTPUtil(logger);
		try {
			initGenerator();
			Config config = ControllerUtil.getCurrentConfig();	
			String destination = config.getFtp_path()+"/"+Constants.DOCUMENTS_PATH+"/"+"hiru";		
			Properties properties = ControllerUtil.getFtpProperties(config);
			ftp.connect(properties);
			if ( ftp.isConnected() ) {
				ftp.synchronize(sourceFolder, destination);
			}
			finalizeGenerator();
		} catch ( Throwable th ) {
			generatorError(th);
		} finally {
			ftp.close();
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
		logger.info(message);
	}

	private void initGenerator(){
		System.gc();
	}

	private void finalizeGenerator() {
		finalizeGenerator(false);
	}
	
	private void finalizeGenerator( boolean withErrors ) {
		logger.info("----------------------------------------------------------------------------------------------------------------------------------------");
		if ( withErrors ) {
			logger.info( AonUtil.getMessage(ICMSConstants.BUNDLE_NAME, CMS_GENERATOR_ERROR) );
		} else {
			logger.info( AonUtil.getMessage(ICMSConstants.BUNDLE_NAME, CMS_GENERATOR_FINISHED) );
		}		
		logger.info("----------------------------------------------------------------------------------------------------------------------------------------");
		logger.finish();
		System.gc();
	}

	private void generatorError( Throwable th ) {
		logger.error( th.getMessage() );
		finalizeGenerator( true );
		throw new AbortProcessingException(th.getMessage(), th);		
	}	
	
}
