package com.code.aon.ui.mailing;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.Registry;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;

public class MailingManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(MailingManager.class.getName());
	
	private static final String FILENAME = "mailing";
	
	public static List<MailData> generateMailingList(Collection<Integer> collection) throws ManagerBeanException {
		List<MailData> list = new LinkedList<MailData>();
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(sessionName);
		StatelessSession session = sessionFactory.openStatelessSession();
		try {
			for( Integer id : collection ) {
				Registry registry = (Registry) session.get(Registry.class, id);
				list.add( new MailData(registry) );
			}
		} catch (HibernateException he) {
			LOGGER.error(">>>> onClearStatus ", he);
			AonUtil.addErrorMessage(he.getMessage());
			throw new AbortProcessingException(he.getMessage(), he);			
		} finally {
			session.close();
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}			
		}
		
		return list;
	}
	
	private static void generateMailing(List<MailData> list, OutputStream out) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		
		writer.write(parseStructure());
		writer.newLine();
		for( MailData data : list ) {
			writer.write(data.toString());
			writer.newLine();			
		}
		writer.flush();
	}

    private static File getZipFile( List<MailData> list ) throws IOException {
    	File file = File.createTempFile( FILENAME, "." + MimeType.MIME_ZIP.getExtension());
    	BufferedOutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		String name = FILENAME + "." + MimeType.MIME_TXT.getExtension();
		zipOut.putNextEntry(new ZipEntry(name));
		generateMailing(list, zipOut);
		zipOut.closeEntry();
		IOUtils.closeQuietly(zipOut);
		return file;
    }
	
	public static void generateMailing(List<MailData> list) throws IOException {
		File zipFile = getZipFile(list);
		InputStream in = new BufferedInputStream(new FileInputStream(zipFile));
		String name = FILENAME + "." + MimeType.MIME_ZIP.getExtension();
        DownloadUtil.downloadAttachment(name, MimeType.MIME_ZIP, in, zipFile.length() );
        IOUtils.closeQuietly(in);
        FileUtils.deleteQuietly(zipFile);
	}	
	
	private static String parseStructure() {
		return "ID,DOCUMENT,NAME,SURNAME,ADDRESS,CITY,ZIP,GEOZONE,PHONE,CELLULAR,FAX,EMAIL";
	}

}
