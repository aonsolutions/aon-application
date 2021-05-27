package com.code.aon.faces.component.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

/**
 * The Class DownloadUtil.
 */
public class DownloadUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(DownloadUtil.class);

	/**
	 * One week in milliseconds.
	 */
	private static final long ONE_HUNDRED_DAYS_MILLIS = 8640000000L;

	private static final int LAST_MODIFIED_YEAR = 2008;
	
	private static final int LAST_MODIFIED_MOTH = 7;
	
	private static final int LAST_MODIFIED_DAY = 14;
	
	private static final String MODIFY = calcModify();
	
	public static byte[] getData(IAttachment attach) {
		byte[] data = null;
		if (attach.getDriveId() != null) {
			InputStream in = null;
			try {
				String domainName = AonUtil.getDomainName();
				Domain domain = new Domain().setName(domainName).setId(attach.getDomain());
			
				User user = new User().setLogin(AonUtil.getRemoteUser() != null ? AonUtil.getRemoteUser() : "");
				
				DomainGserviceaccount googleAccount = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), user.getLogin());
				
				Drive drive = AonDrive.getInstace().serviceInitialize(googleAccount);
				File file = AonDrive.getInstace().getFile(drive,  attach.getDriveId());
				
				in = AonDrive.getInstace().downloadFile(drive, file.getId());
				data = IOUtils.toByteArray(in);
			} catch (IOException e) {
				LOGGER.error(e.getMessage()); 
			} finally {
				AonIOUtils.closeQuietly(in);
			}
		} else {
			data = attach.getData();
		}
		return data;
	}
	
	/**
	 * Download attachment.
	 *
	 * @param attach the attach
	 */
	public static void downloadAttachment(IAttachment attach) {
		downloadAttachment(attach, attach.getMimeType());
	}

	/**
	 * Download attachment.
	 *
	 * @param attach the attach
	 * @param mimeType Mime type 
	 */
	public static void downloadAttachment(IAttachment attach, MimeType mimeType) {
		byte[] data = getData(attach);
		InputStream in = new ByteArrayInputStream(data);
		long size = ArrayUtils.getLength(data);
		downloadAttachment(attach.getDescription(), mimeType, in, size);
	}
	
	/**
	 * Gets the response.
	 *
	 * @return the response
	 */
	public static HttpServletResponse getResponse() {
        FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
		if ( response instanceof HttpServletResponseWrapper) {
			response = (HttpServletResponse) ((HttpServletResponseWrapper) response).getResponse();
		}
		return response;
	}

	public static String getFileName( String fileName, MimeType type ) {
		String extension = FilenameUtils.getExtension(fileName); 
		if ( (type != null) && !StringUtils.isEmpty(type.getExtension()) &&
				!StringUtils.equals(extension, type.getExtension()) ) {
			return fileName + "." + type.getExtension();			
		}
		return fileName;
	}
	
	public static MimeType resolveMimeType( String fileName, MimeType type) {
		if ( (type == null) && !StringUtils.isEmpty(fileName) ) {
			return MimeResolver.getMimeTypeByExtension(fileName);
		}
		return type;
	}		

	public static MimeType resolveMimeType( IAttachment attachment ) {
		MimeType type = resolveMimeType(attachment.getDescription(), attachment.getMimeType());
		if ( type == null ) {
			type =  MimeResolver.getMimeType(attachment.getData());
		}
		return type;
	}		
	
	/**
	 * Inits the response.
	 *
	 * @param response the response
	 * @param fileName the file name
	 * @param type the type
	 * @param size the size
	 * @return the output stream
	 * @throws IOException 
	 */
	public static OutputStream initDownload( HttpServletResponse response, String fileName, MimeType type, long size ) throws IOException {
		MimeType mimeType = resolveMimeType(fileName, type);
		if ( mimeType != null ) {
			response.setContentType( mimeType.getName() );	
		}
		if (! StringUtils.isEmpty(fileName) ) {
			String name = getFileName(fileName, mimeType);
			if ( mimeType != MimeType.MIME_PDF && mimeType != MimeType.MIME_SIGNED_PDF
					&& !mimeType.getName().contains("image")) {
				response.setHeader("Content-Disposition", "attachment; filename=\"" + name + "\"");	
			} else {
				response.setHeader("Content-Disposition", "inline; filename=\"" + name + "\"");
			}
		}
		if ( size > 0 ) {
			response.setHeader("Content-Length", String.valueOf(size));	
		}
		return new BufferedOutputStream(response.getOutputStream());		
	}

	private static final String calcModify() {
		Date date = new GregorianCalendar( LAST_MODIFIED_YEAR, LAST_MODIFIED_MOTH, LAST_MODIFIED_DAY ).getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss z",Locale.ENGLISH);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(date);
	}	
	
	public static void setCacheable( HttpServletResponse response ) {
		// We set two headers: Cache-Control and Expires.
		// This combination lets browsers know that it is
		// okay to cache the resource indefinitely.

		// Set Cache-Control to "Public".
		response.setHeader("Cache-Control", "Public");

		response.setHeader("Last-Modified", MODIFY);

		// Set Expires to current time + one year.
		long currentTime = System.currentTimeMillis();

		response.setDateHeader("Expires", currentTime + ONE_HUNDRED_DAYS_MILLIS);
	}
	
	/**
	 * Inits the response.
	 *
	 * @param response the response
	 * @param fileName the file name
	 * @param type the type
	 * @return the output stream
	 * @throws IOException 
	 */
	public static OutputStream initDownload( HttpServletResponse response, String fileName, MimeType type ) throws IOException {
		return initDownload(response, fileName, type, -1);
	}	
	
	/**
	 * Finish download.
	 *
	 * @param response the response
	 * @param out the out
	 */
	public static void finishDownload( HttpServletResponse response, OutputStream out ) {
		AonIOUtils.closeQuietly(out);
		if ( response != null ) {
			try {
				response.flushBuffer();
			} catch (IOException e) {
				LOGGER.error( e.getMessage(), e );
			}	
		}
		FacesContext context = FacesContext.getCurrentInstance();
        context.responseComplete();    			
	}
	
	/**
	 * Download attachment.
	 *
	 * @param fileName the file name
	 * @param type the type
	 * @param in the in
	 * @param size the size
	 */
	public static void downloadAttachment(String fileName, MimeType type, InputStream in, long size) {
		HttpServletResponse response = null;
		OutputStream out = null;
		try {
	        response = getResponse();
			out = initDownload(response, fileName, type, size); 
			IOUtils.copyLarge(in, out);
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			finishDownload(response, out);
		}
	}	
	
}
