package com.code.aon.ui.finance.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.Tax;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.finance.A3Writer;
import com.code.aon.ui.finance.AplifisaWriter;
import com.code.aon.ui.finance.BasicExporter;
import com.code.aon.ui.finance.ContaPlusWriter;
import com.code.aon.ui.finance.DiamaconWriter;
import com.code.aon.ui.finance.DsiWriter;
import com.code.aon.ui.finance.ExcelWriter;
import com.code.aon.ui.finance.GeyceWriter;
import com.code.aon.ui.finance.GlasofWriter;
import com.code.aon.ui.finance.InvoiceExportConfiguration;
import com.code.aon.ui.finance.LogicWinWriter;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ExporterController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(ExporterController.class.getName());
	
	private InvoiceExportConfiguration configuration;
	
	private String backAction;
	
	private boolean finished;
	
	private Map<String,File> dataMap;
	
	private String fileName;
	
	public InvoiceExportConfiguration getConfiguration() {
		if (configuration == null) {
			configuration = new InvoiceExportConfiguration();
		}
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
			case DSI_GESTION:				
				exporter = new DsiWriter(this.configuration);
				break;
			case GLASOF:				
				exporter = new GlasofWriter(this.configuration);
				break;
			case CONTA_PLUS:				
				exporter = new ContaPlusWriter(this.configuration);
				break;
			case DIAMACON:				
				exporter = new DiamaconWriter(this.configuration);
				break;
		}
		this.fileName = exporter.getFileName();
		return exporter;
	}
	
	public void finish() {
		LogPanelController log = LogPanelController.getInstance();
		this.finished = true;
		if ( dataMap.isEmpty() ) {
			log.error( AonUtil.getMessage(ICommonMessages.FINANCE_EXPORTER_NO_DATA) );										
		}
		log.finish();
	}
	
	public void onDownload( ActionEvent event ) {
    	if (! isDataEmpty() ) {
    		try {
	    		if ( dataMap.size() == 1 ) {
	    			Map.Entry<String,File> entry = dataMap.entrySet().iterator().next();
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
		for( File file : dataMap.values() ) {
			FileUtils.deleteQuietly(file);
		}    	
    	LogPanelController.getInstance().onCloseWindow(event);
	}
	
	private void downloadFile( String name, File file ) throws FileNotFoundException {
    	InputStream in = new BufferedInputStream(new FileInputStream(file));
        DownloadUtil.downloadAttachment(name, null, in, file.length());	    
        IOUtils.closeQuietly(in);
	}

    private File getZipFile( Map<String,File> dataMap ) throws IOException {
    	File file = File.createTempFile( "geyce", "." + MimeType.MIME_ZIP.getExtension());
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		for( Map.Entry<String,File> entry : dataMap.entrySet() ) {
			byte[] data = FileUtils.readFileToByteArray(entry.getValue());
			if (! ArrayUtils.isEmpty(data) ) {
	            zipOut.putNextEntry(new ZipEntry(entry.getKey()));
	            zipOut.write(data);
	        	zipOut.closeEntry();				
			}
		}
		zipOut.close();
		return file;
    }
	
	private void downloadZip( Map<String,File> dataMap ) throws IOException {
		File zipFile = getZipFile(dataMap);
		InputStream in = new BufferedInputStream(new FileInputStream(zipFile));
        DownloadUtil.downloadAttachment(this.fileName, MimeType.MIME_ZIP, in, zipFile.length() );
        FileUtils.deleteQuietly(zipFile);
	}

	public void onTypeChanged( ActionEvent event ) {
    	getConfiguration().initAccountSize();
    	getConfiguration().initVats();
	}

	public void setDataMap(Map<String,File> dataMap) {
		this.dataMap = dataMap;
	}
	
	public boolean isDataEmpty() {
		return (dataMap == null) || dataMap.isEmpty();		
	}

	public List<SelectItem> getSelectableTaxs() throws ManagerBeanException {
		List<SelectItem> taxs = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Tax.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.TAX_TYPE), TaxType.VAT);
		criteria.addOrder(bean.getFieldName(IEntityAlias.TAX_NAME));
		Iterator<ITransferObject> iter = bean.getList(criteria).iterator();
		while(iter.hasNext()){
			Tax tax = (Tax)iter.next();
			SelectItem item = new SelectItem(tax, tax.getName() + " (" + tax.getPercentage() + "%)");
			taxs.add(item);
		}
		return taxs;
	}		
	
	public void onAddVat(ActionEvent event) {
		getConfiguration().addEmptyTax();
	}
	
	public void onRemoveVat(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));
		getConfiguration().removeTax(index);
	}			
	
}