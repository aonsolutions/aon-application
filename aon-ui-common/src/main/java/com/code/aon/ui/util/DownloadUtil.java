package com.code.aon.ui.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
import com.code.aon.ui.common.ICommonConstants;

/**
 * The Class DownloadUtil.
 */
public class DownloadUtil implements ICommonConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(DownloadUtil.class);

	/**
	 * Download attachment.
	 *
	 * @param attach the attach
	 */
	public static void downloadAttachment(IAttachment attach) {
		InputStream in = new ByteArrayInputStream(attach.getData());
		long size = ArrayUtils.getLength(attach.getData());
		downloadAttachment(attach.getDescription(), attach.getMimeType(), in, size);
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

	private static String getFileName( String fileName, MimeType type ) {
		String extension = FilenameUtils.getExtension(fileName); 
		if ( StringUtils.isEmpty(extension) && (type != null) ) {
			return fileName + "." + type.getExtension();
		}
		return fileName;
	}
	
	private static MimeType resolveMimeType( String fileName, MimeType type) {
		if ( type == null ) {
			if (! StringUtils.isEmpty(fileName) ) {
				return MimeResolver.getMimeTypeByExtension(fileName);
			}
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
			if ( (mimeType != MimeType.MIME_PDF) && (mimeType != MimeType.MIME_SIGNED_PDF) ) {
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
		IOUtils.closeQuietly(out);
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
