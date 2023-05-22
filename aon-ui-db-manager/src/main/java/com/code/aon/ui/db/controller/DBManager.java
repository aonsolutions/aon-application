package com.code.aon.ui.db.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.metadata.ClassMetadata;
import org.richfaces.event.UploadEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.hibernate.IConfigurationFactory;
import com.code.aon.common.dao.hibernate.ISessionFactoryNameProvider;
import com.code.aon.common.dao.hibernate.ReplicationMode;
import com.code.aon.common.util.AonFile;
import com.code.aon.db.HibernateDataManager;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.ui.db.hibernate.ReplicateConfigurationFactory;
import com.code.aon.ui.db.hibernate.ReplicateSessionFactoryNameProvider;
import com.code.aon.ui.db.hibernate.TransferObjectImportVisitor;
import com.code.aon.ui.util.AonUtil;

public class DBManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(DBManager.class.getName());
	
	private List<AonFile> files;
	
	private ISessionFactoryNameProvider previousNameProvider;
	
	private IConfigurationFactory previousConfigurationFactory;

	private String pojos;
	
	private Boolean ignoreDependencies;
	
	private String replicationMode;
		
	public String getPojos() {
		return pojos;
	}

	public void setPojos(String pojos) {
		this.pojos = pojos;
	}

	public Boolean getIgnoreDependencies() {
		return ignoreDependencies;
	}

	public void setIgnoreDependencies(Boolean ignoreDependencies) {
		this.ignoreDependencies = ignoreDependencies;
	}
	
	public String getReplicationMode() {
		return replicationMode;
	}

	public void setReplicationMode(String replicationMode) {
		this.replicationMode = replicationMode;
	}

	public ReplicationMode resolverReplicationMode() {
		ReplicationMode rm = ReplicationMode.valueOf(this.replicationMode);
		if ( rm == null ) {
			return ReplicationMode.EXCEPTION;
		}
		return rm;
	}
	
	protected void configure(HibernateDataManager hdm, boolean export) {
		if (! StringUtils.isEmpty(getPojos()) ) {
			hdm.setIncludeEntities( getIncludeEntities() );
		}
		if ( getIgnoreDependencies() != null ) {
			hdm.setIgnoreDependencies( getIgnoreDependencies().booleanValue() );
		}
	}

	private void responseZip(File file) throws IOException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        writeZip(response, file);
        context.responseComplete();    	
    }
    
    private void writeZip(HttpServletResponse response, File xmlFile) throws IOException {
        String outFilename = "db.zip";
        response.setContentType("application/x-zip-compressed");
		response.setHeader("Content-disposition", "attachment; filename=\""
				+ outFilename + "\"");

		File tempFile = File.createTempFile(outFilename, ".zip");
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(tempFile) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);

		InputStream in = new BufferedInputStream( new FileInputStream(xmlFile) );
		zipOut.putNextEntry(new ZipEntry("aon_master.xml"));
		IOUtils.copy( in, zipOut );
       	zipOut.closeEntry();
       	in.close();                

		zipOut.close();
        long size = tempFile.length();
        if ( size > 0 ) {
            response.setHeader("Content-Length", String.valueOf(size));	
        }
        ServletOutputStream sos = response.getOutputStream();
        InputStream fileIn = new BufferedInputStream( new FileInputStream(tempFile) );
        IOUtils.copy( fileIn, sos );
        fileIn.close();
        sos.close();
		response.flushBuffer();
		tempFile.delete();
    }
    
	private List<Class<? extends Serializable>> getIncludeEntities() {
    	List<Class<? extends Serializable>> list = new LinkedList<Class<? extends Serializable>>();
    	for( String pojo : StringUtils.split(getPojos(), " ,") ) {
    		try {
				list.add( (Class) DBManager.class.forName(pojo) );
			} catch (ClassNotFoundException e) {
				LOGGER.error( "Class not found: " + pojo );
			}
    	}
    	return list;
    }
	
	public void onExport( ActionEvent event ) {
		HibernateDataManager hdm = new HibernateDataManager();
		hdm.setExportData(true);
		try {
			File xmlFile = File.createTempFile("aon-master", ".xml");
			hdm.setFile( xmlFile );
			String factoryName = HibernateUtil.getSessionFactoryName();
			SessionFactory factory = HibernateUtil.getSessionFactory(factoryName);
			hdm.setExportFactory( factory );
			configure(hdm, true);
			hdm.execute();
			responseZip( xmlFile );
			xmlFile.delete();
		} catch (Throwable e) {
			LOGGER.error( ">>>> onExport " + e.getMessage() );
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	public void onInitImport( ActionEvent event ) {
		this.files = new ArrayList<AonFile>();
	}
	
	public List<AonFile> getFiles() {
		return files;
	}

	public void setFiles(List<AonFile> files) { 
		this.files = files;
	}	
	
	public synchronized void fileUploaded(UploadEvent event) throws IOException {
	    files.add(AttachmentUtil.fileUploaded(event));	    
	}	
	
	public void fileDeleted( ActionEvent event ) {
        FacesContext context = FacesContext.getCurrentInstance();
		String indexValue = context.getExternalContext().getRequestParameterMap().get("index");
		if (! StringUtils.isEmpty(indexValue) ) {
			int index = Integer.valueOf(indexValue);
			this.files.remove(index);
		}
	}
	
	@SuppressWarnings("rawtypes")
	private List<ClassMetadata> getEntities() {
		List<ClassMetadata> entities = new LinkedList<ClassMetadata>();
		String factoryName = HibernateUtil.getSessionFactoryName();
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(factoryName);
		Iterator i = sessionFactory.getAllClassMetadata().values().iterator();
		while ( i.hasNext() ) {
			entities.add( (ClassMetadata) i.next() );
		}
		return entities;
	}
	
	private void initImport() {
		this.previousConfigurationFactory = HibernateUtil.getConfigurationFactory();
		this.previousNameProvider = HibernateUtil.getSessionFactoryNameProvider();
		List<ClassMetadata> entities = getEntities();
		ReplicateConfigurationFactory configurationFactory = new ReplicateConfigurationFactory(this.previousConfigurationFactory);
		configurationFactory.setEntities( entities );
		HibernateUtil.setConfigurationFactory(configurationFactory);
		HibernateUtil.setSessionFactoryNameProvider(ReplicateSessionFactoryNameProvider.getInstance());
	}

	private void finishImport() {
		HibernateUtil.setConfigurationFactory(this.previousConfigurationFactory);
		HibernateUtil.setSessionFactoryNameProvider(this.previousNameProvider);
	}
	
	public void onImport( ActionEvent event ) {
		HibernateDataManager hdm = new HibernateDataManager();
		hdm.setImportData(true);
		try {
			initImport();
			String factoryName = HibernateUtil.getSessionFactoryName();
			SessionFactory sessionFactory = HibernateUtil.getSessionFactory(factoryName);
			hdm.setImportFactory( sessionFactory );
			TransferObjectImportVisitor visitor = new TransferObjectImportVisitor(sessionFactory, 0);
			if (! StringUtils.isEmpty(getReplicationMode()) ) {
				visitor.setReplicationMode( resolverReplicationMode() );
			}
			hdm.setVisitor(visitor);
			configure(hdm, false);
			for( AonFile file : this.files ) {
				InputStream in = file.openStream();
				hdm.setInputStream( in );
				hdm.execute();	
				in.close();
			}
		} catch (Throwable e) {
			LOGGER.error( ">>>> onExport " + e.getMessage() );
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			finishImport();
		}
	}
	
}
