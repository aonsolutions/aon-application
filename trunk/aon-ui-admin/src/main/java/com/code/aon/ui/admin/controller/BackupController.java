package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.BACKUP_START;
import static com.code.aon.ui.admin.controller.IAdminConstants.BUNDLE_NAME;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
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

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.config.Domain;
import com.code.aon.dbutils.AonDomainDump;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.dbutils.IDumpListener;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.admin.DumpThread;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class BackupController implements IDumpListener {

	private final static Logger LOGGER = LoggerFactory.getLogger(BackupController.class);
	
	private final static String BACKUP_INFO = AonUtil.getMessage(BUNDLE_NAME, "admin_backup_info");
	private final static String BACKUP_TABLE_START = AonUtil.getMessage(BUNDLE_NAME, "admin_backup_table_start");
	private final static String BACKUP_TABLE_PROGRESS = AonUtil.getMessage(BUNDLE_NAME, "admin_backup_table_progress");
	private final static String BACKUP_TABLE_FINISH = AonUtil.getMessage(BUNDLE_NAME, "admin_backup_table_finish");
	private final static String BACKUP_ERROR = AonUtil.getMessage(BUNDLE_NAME, "admin_backup_error");
	
	private Domain domain;
	private boolean includeChildDomains;
	private boolean includeParentDomain;
	private File backupFile;
	private DumpThread dumpThread;
	private int progressValue;
	private int maxProgressValue;
	private String progressMessage;
	private boolean enabledProgressBar;
	private Locale locale = AonUtil.getCurrentLocale();
	
	public boolean isIncludeChildDomains() {
		return includeChildDomains;
	}

	public void setIncludeChildDomains(boolean includeChildDomains) {
		this.includeChildDomains = includeChildDomains;
	}
	
	public boolean isIncludeParentDomain() {
		return includeParentDomain;
	}

	public void setIncludeParentDomain(boolean includeParentDomain) {
		this.includeParentDomain = includeParentDomain;
	}

	public boolean isBackupAvailable() {
		return (this.backupFile != null) && (this.backupFile.exists());
	}

	private void cleanBackupFile() {
		if ( isBackupAvailable() ) {
			this.backupFile.delete();
			this.backupFile = null;
		}
	}
	
	public void onInit( ActionEvent event ) {
		setIncludeChildDomains(false);
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
	
	@SuppressWarnings("unchecked")
	private Integer[] getDomains() throws ManagerBeanException {
		List<Integer> domains = new LinkedList<Integer>();
		domains.add(this.domain.getId());
		if ( domain.getParent() != null ) {
			if (this.domain.isEnableHeredity() || isIncludeParentDomain()) {
				domains.add(domain.getParent().getId());
			}
		} else if ( domain.isDomainManagement() && isIncludeChildDomains() ) {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), this.domain.getId());
			String idAlias = bean.getFieldName(IEntityAlias.DOMAIN_ID);
			ProjectionList pl = new ProjectionList(Projection.property(idAlias));
			domains.addAll( bean.getList(pl, criteria) );
		}
		return domains.toArray(new Integer[domains.size()]);
	}
	
	private String getBackupName() {
		return StringUtils.replace(domain.getName(), ".", "-");
	}
	
	private void resetProgress() {
		this.progressValue = 0;
		this.maxProgressValue = 0;
		setProgressMessage(null);
		this.enabledProgressBar = false;
		this.dumpThread = null;
	}
	
	public void onPrepareBackup( ActionEvent event ) {
		resetProgress();
		setProgressMessage(AonUtil.getMessage(BUNDLE_NAME, BACKUP_START));
		this.dumpThread = new DumpThread(this, AonUtil.getDomainName());
		this.dumpThread .start();
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
			dump.execute(getDomains(), writer);
			
           	zipOut.closeEntry();
        } catch (Throwable e) {
			LOGGER.error(">>>> onDump: ", e);
			this.progressValue = this.maxProgressValue + 1;
			setProgressMessage( format(BACKUP_ERROR, e.getMessage()) );
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
		if ( (this.maxProgressValue > 0) && (this.progressValue > 0) ) {
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
		setProgressMessage( format(BACKUP_INFO, databaseName, version) );
	}

	@Override
	public synchronized void startDumpTable(String table) {
		this.progressValue++;
		setProgressMessage( format(BACKUP_TABLE_START, table) );
	}
	
	@Override
	public synchronized void dumpTable(String table, int rowCount) {
		setProgressMessage( format(BACKUP_TABLE_PROGRESS, table, rowCount) );
	}

	@Override
	public synchronized void endDumpTable(String table, int rowCount) {
		setProgressMessage( format(BACKUP_TABLE_FINISH, table, rowCount) );
	}

	@Override
	public synchronized void finishDump() {
		this.progressValue = this.maxProgressValue + 1;
		setProgressMessage(null);
	}
	
}