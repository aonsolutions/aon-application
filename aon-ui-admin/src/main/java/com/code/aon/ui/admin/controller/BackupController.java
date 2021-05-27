package com.code.aon.ui.admin.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.Domain;
import net.aonsolutions.core.dbutils.AonDomainDump;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.dbutils.IDumpListener;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.ui.admin.DumpThread;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.util.AonUtil;

public class BackupController implements IDumpListener, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(BackupController.class);
	
	private Domain domain;
	private boolean includeParentDomain;
	private File backupFile;
	private int progressValue;
	private int maxProgressValue;
	private String progressMessage;
	private boolean enabledProgressBar;
	private Locale locale;
	private String tableStartMessage;
	private String tableProgressMessage;
	private String tableFinishMessage;
	private String infoMessage;
	private String errorMessage;
	
	public BackupController() {
		this.locale = AonUtil.getCurrentLocale();
		this.tableStartMessage = AonUtil.getMessage(ICommonMessages.ADMIN_BACKUP_TABLE_START);
		this.tableProgressMessage = AonUtil.getMessage(ICommonMessages.ADMIN_BACKUP_TABLE_PROGRESS);
		this.tableFinishMessage = AonUtil.getMessage(ICommonMessages.ADMIN_BACKUP_TABLE_FINISH);
		this.infoMessage = AonUtil.getMessage(ICommonMessages.ADMIN_BACKUP_INFO);		
		this.errorMessage = AonUtil.getMessage(ICommonMessages.ADMIN_BACKUP_ERROR);
	}

	public boolean isIncludeParentDomain() {
		return includeParentDomain;
	}

	public void setIncludeParentDomain(boolean includeParentDomain) {
		this.includeParentDomain = includeParentDomain;
	}

	public boolean isBackupAvailable() {
		return this.backupFile != null && this.backupFile.exists();
	}

	private void cleanBackupFile() {
		if ( isBackupAvailable() ) {
			this.backupFile.delete();
			this.backupFile = null;
		}
	}
	
	public void onInit( ActionEvent event ) {
		setIncludeParentDomain(false);
		cleanBackupFile();
		resetProgress();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			this.domain = (Domain) bean.get(DomainManager.getCurrentDomain());
		} catch (ManagerBeanException e) {
			LOGGER.error( e.getMessage(), e );
		}			
	}
	
	private String getBackupName() {
		return StringUtils.replace(domain.getName(), ".", "-");
	}
	
	private void resetProgress() {
		this.progressValue = 0;
		this.maxProgressValue = 0;
		setProgressMessage(null);
		this.enabledProgressBar = false;
	}
	
	public void onPrepareBackup( ActionEvent event ) {
		resetProgress();
		setProgressMessage(AonUtil.getMessage(ICommonMessages.BACKUP_START));
		DumpThread dumpThread = new DumpThread(this, AonUtil.getDomainName());
		dumpThread .start();
		this.enabledProgressBar = true;
	}
	
	public void makeBackup( String domainName ) {
		ZipOutputStream zipOut = null;
		Connection connection = null;
        try {
			String name = getBackupName();
        	this.backupFile = File.createTempFile(name, "." + MimeType.MIME_ZIP.getExtension());
			OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(this.backupFile) );
			zipOut = new ZipOutputStream(fileOut);
			zipOut.putNextEntry(new ZipEntry(name + ".sql"));

			connection  = DatabaseUtil.getConnection(domainName);
			Writer writer = new OutputStreamWriter(zipOut, CharEncoding.ISO_8859_1);
			AonDomainDump dump = new AonDomainDump(connection);
			dump.setListener(this);
			boolean includeParent = this.domain.isEnableHeredity() || isIncludeParentDomain();
			dump.execute(this.domain.getId(), includeParent, writer);
			
           	zipOut.closeEntry();
        } catch (Throwable e) {
			LOGGER.error(">>>> onDump: ", e);
			this.progressValue = this.maxProgressValue + 1;
			setProgressMessage( format(errorMessage, e.getMessage()) );
		} finally {
			IOUtils.closeQuietly(zipOut);
			DbUtils.closeQuietly(connection);
		}		
	}	
	
	public void onDownloadBackup( ActionEvent event ) {
		HttpServletResponse response = null;
		OutputStream out = null;
        try {
        	String name = getBackupName();
			long size = this.backupFile.length();
        	response = DownloadUtil.getResponse();
        	out = DownloadUtil.initDownload(response, name, MimeType.MIME_ZIP, size);
            InputStream fileIn = new BufferedInputStream( new FileInputStream(this.backupFile) );
            IOUtils.copy( fileIn, out );
            IOUtils.closeQuietly(fileIn);
		} catch (Throwable e) {
			LOGGER.error(">>>> onDownloadBackup: ", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DownloadUtil.finishDownload(response, out);
			cleanBackupFile();
		}
	}
	
	public boolean isEnabledProgressBar() {
		return this.enabledProgressBar;
	}

	public int getProgressValue() {
		return progressValue;
	}

	public long getProgressValuePercent() {
		if ( this.maxProgressValue > 0 && this.progressValue > 0 ) {
			double value = (this.progressValue * 100.0)/this.maxProgressValue;
			return Math.round( value );
		}
		return 0;
	}

	public int getMaxProgressValue() {
		return maxProgressValue;
	}

	public String getProgressMessage() {
		return progressMessage;
	}

	private void setProgressMessage(String progressMessage) {
		this.progressMessage = progressMessage;
	}

	private String format( String value, Object ... arguments ) {
		MessageFormat mf = new MessageFormat( value, locale );
		return mf.format( arguments );		
	}
	
	@Override
	public synchronized void initDump(String databaseName, String version, int numberOfTables) {
		this.progressValue = 0;
		this.maxProgressValue = numberOfTables;
		setProgressMessage( format(infoMessage, databaseName, version) );
	}

	@Override
	public synchronized void startDumpTable(String table) {
		this.progressValue++;
		setProgressMessage( format(tableStartMessage, table) );
	}
	
	@Override
	public synchronized void dumpTable(String table, int rowCount) {
		if ( rowCount%100 == 0 ) {
			setProgressMessage( format(tableProgressMessage, table, rowCount) );	
		}
	}

	@Override
	public synchronized void endDumpTable(String table, int rowCount) {
		setProgressMessage( format(tableFinishMessage, table, rowCount) );
	}

	@Override
	public synchronized void finishDump() {
		this.progressValue = this.maxProgressValue + 1;
		setProgressMessage(null);
	}
	
	public boolean isShowIncludeParentDomain() {
		Domain parent = domain.getParent();
		if ( (parent != null) && (parent.getId() != null) ) {
			return ! this.domain.isEnableHeredity();			
		}
		return false;
	}
	
}