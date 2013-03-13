package com.code.aon.ui.admin.controller;


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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
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
import com.code.aon.common.util.ConnectionProvider;
import com.code.aon.config.Domain;
import com.code.aon.dbutils.AonDomainDump;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DataSourceUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class BackupController {

	private final static Logger LOGGER = LoggerFactory.getLogger(BackupController.class);
	
	private Domain domain;
	private boolean includeChildDomains;
	
	public boolean isIncludeChildDomains() {
		return includeChildDomains;
	}

	public void setIncludeChildDomains(boolean includeChildDomains) {
		this.includeChildDomains = includeChildDomains;
	}

	public void onInit( ActionEvent event ) {
		setIncludeChildDomains(false);
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
			if (this.domain.isEnableHeredity()) {
				domains.add(domain.getParent().getId());
			}
		} else if ( domain.isDisableDomainManagement() && isIncludeChildDomains() ) {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), this.domain.getId());
			ProjectionList pl = new ProjectionList(Projection.property(IEntityAlias.DOMAIN_ID));
			domains.addAll( bean.getList(pl, criteria) );
		}
		return domains.toArray(new Integer[domains.size()]);
	}
	
	public void onDump( ActionEvent event ) {
		Connection connection = null;
		HttpServletResponse response = null;
		OutputStream out = null;
		File tempFile = null;
        try {
			String name = StringUtils.replace(domain.getName(), ".", "-");
        	tempFile = File.createTempFile(name, "." + MimeType.MIME_ZIP.getExtension());
			OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(tempFile) );
			ZipOutputStream zipOut = new ZipOutputStream(fileOut);
			zipOut.putNextEntry(new ZipEntry(name + ".sql"));

			Properties properties = DataSourceUtil.getDBProperties();
			connection = ConnectionProvider.getConnection(properties);
			Writer writer = new OutputStreamWriter(zipOut, CharEncoding.ISO_8859_1);
			AonDomainDump dump = new AonDomainDump(connection);
			dump.execute(getDomains(), writer);
			
           	zipOut.closeEntry();
			zipOut.close();

			long size = tempFile.length();
        	response = DownloadUtil.getResponse();
        	out = DownloadUtil.initDownload(response, name, MimeType.MIME_ZIP, size);
            InputStream fileIn = new BufferedInputStream( new FileInputStream(tempFile) );
            IOUtils.copy( fileIn, out );
            IOUtils.closeQuietly(fileIn);
		} catch (Throwable e) {
			LOGGER.error(">>>> onDump: ", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DbUtils.closeQuietly(connection);
			DownloadUtil.finishDownload(response, out);
			if ( (tempFile != null) && (tempFile.exists()) ) {
	    		tempFile.delete();
			}
		}
	}
	
}