package com.code.aon.ui.finance.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.finance.A3Writer;
import com.code.aon.ui.finance.AplifisaWriter;
import com.code.aon.ui.finance.BasicExporter;
import com.code.aon.ui.finance.ExcelWriter;
import com.code.aon.ui.finance.GeyceWriter;
import com.code.aon.ui.finance.InvoiceExportConfiguration;
import com.code.aon.ui.finance.LogicWinWriter;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class ExporterController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExporterController.class.getName());
	
	private InvoiceExportConfiguration configuration;
	
	private String backAction;
	
	private boolean finished;
	
	private Map<String,byte[]> dataMap;
	
	private String fileName;
	
	public InvoiceExportConfiguration getConfiguration() {
		return configuration;
	}
	
	public boolean isFinished() {
		return this.finished;
	}

	public String getBackAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	public String initialAction() {
		if (! this.configuration.isConfigured() ) {
			AonUtil.addErrorMessageFromBundle(ICommonMessages.FINANCE_EXPORTER_NOT_CONFIG);
			return null;
		}
		return this.backAction;
	}
	
	public String backAction() {
		return backAction;
	}		

	public void onConfig( ActionEvent event ) {
		this.configuration = new InvoiceExportConfiguration();
	}
	
	public void onInit( ActionEvent event ) {
		this.finished = false;
		this.configuration = new InvoiceExportConfiguration();
	}
	
	public void onSaveConfiguration( ActionEvent event ) {
		this.configuration.save();
	}
	
	public BasicExporter start() {
		BasicExporter exporter = null;
		setDataMap(null);
		this.finished = false;
		switch ( this.configuration.getType() ) {
			case GEYCE:
				exporter = new GeyceWriter(this.configuration);
				break;
			case A3:
				exporter = new A3Writer(this.configuration);
				break;
			case APLIFISA:
				exporter = new AplifisaWriter(this.configuration);
				break;
			case LOGIC_WIN:
				exporter = new LogicWinWriter(this.configuration);
				break;
			case EXCEL:
				exporter = new ExcelWriter(this.configuration);
				break;
		}
		this.fileName = exporter.getFileName();
		return exporter;
	}
	
	public void finish() {
		this.finished = true;
	}
	
	public void onDownload( ActionEvent event ) {
    	if ((dataMap != null) && !dataMap.isEmpty() ) {
    		try {
	    		if ( dataMap.size() == 1 ) {
	    			Map.Entry<String,byte[]> entry = dataMap.entrySet().iterator().next();
	    			downloadFile(entry.getKey(), entry.getValue());
	    		} else {
	    			downloadZip(dataMap);
	    		}
    		} catch (Throwable th) {
    			LOGGER.error(th.getMessage(), th);
    			AonUtil.addErrorMessage(th.getMessage());
    			throw new AbortProcessingException(th.getMessage(), th);
    		}
    	}
		onFinish(event);
	}
	
	public void onFinish( ActionEvent event ) {
    	this.finished = false;
    	LogPanelController.getInstance().onCloseWindow(event);
	}
	
	private void downloadFile( String name, byte[] data ) {
    	InputStream in = new ByteArrayInputStream(data);
        DownloadUtil.downloadAttachment(name, null, in, data.length);	    				    					
	}

    private File getZipFile( Map<String,byte[]> dataMap ) throws IOException {
    	File file = File.createTempFile( "geyce", "." + MimeType.MIME_ZIP.getExtension());
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		for( Map.Entry<String,byte[]> entry : dataMap.entrySet() ) {
			byte[] data = entry.getValue();
			if (! ArrayUtils.isEmpty(data) ) {
	            zipOut.putNextEntry(new ZipEntry(entry.getKey()));
	            zipOut.write(data);
	        	zipOut.closeEntry();				
			}
		}
		zipOut.close();
		return file;
    }
	
	private void downloadZip( Map<String,byte[]> dataMap ) throws IOException {
		File zipFile = getZipFile(dataMap);
		InputStream in = new BufferedInputStream(new FileInputStream(zipFile));
        DownloadUtil.downloadAttachment(this.fileName, MimeType.MIME_ZIP, in, zipFile.length() );
	}

	public void onTypeChanged( ActionEvent event ) {
    	getConfiguration().initAccountSize();
	}

	public void setDataMap(Map<String, byte[]> dataMap) {
		this.dataMap = dataMap;
	}
	
}