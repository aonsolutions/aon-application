package com.code.aon.ui.academy.controller;

import com.code.aon.AonVersion;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.academy.Course;
import com.code.aon.academy.CourseAlumn;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ui.mailing.MailData;
import com.code.aon.ui.mailing.MailingManager;
import com.code.aon.ui.util.DownloadUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class CourseController extends CourseListController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);

	private static final String COURSE_MAILING = "courseMailing";
	private String selectedTab;
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}	
	
	@SuppressWarnings("unchecked")
	private List<Integer> getCourseAlumns( Course course ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(CourseAlumn.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.COURSE_ALUMN_COURSE_ID), course.getId());
		ProjectionList pl = new ProjectionList(Projection.property(bean.getFieldName(IEntityAlias.COURSE_ALUMN_CUSTOMER_ID)));
		return bean.getList(pl, criteria);
	}

    private File getZipFile() throws IOException, ManagerBeanException {
    	File file = File.createTempFile( COURSE_MAILING, "." + MimeType.MIME_ZIP.getExtension());
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		Criteria criteria = getCriteria();		
		for( ITransferObject to : getManagerBean().getList(criteria) ) {
			Course course = (Course) to;
			List<Integer> alumns = getCourseAlumns(course);
			List<MailData> data = MailingManager.generateMailingList(alumns);
			String name = course.getCode() + "." + MimeType.MIME_TXT.getExtension();
            zipOut.putNextEntry(new ZipEntry(name));
            MailingManager.generateMailing(data, zipOut);
        	zipOut.closeEntry();				
		}
		zipOut.close();
		return file;
    }
	
	public void onGenerateTargetMailing( ActionEvent event ) {
		try {
			File zipFile = getZipFile();
			InputStream in = new BufferedInputStream(new FileInputStream(zipFile));
			String name = COURSE_MAILING + "." + MimeType.MIME_ZIP.getExtension();
	        DownloadUtil.downloadAttachment(name, MimeType.MIME_ZIP, in, zipFile.length() );
	        FileUtils.deleteQuietly(zipFile);					
		} catch ( Throwable e ) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
}