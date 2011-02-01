package com.code.aon.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;

import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.detector.ExtensionMimeDetector;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;

/**
 * The Class MimeUtil.
 */
public class MimeResolver {

	private static final Logger LOGGER = LoggerFactory.getLogger(MimeResolver.class.getName());
	
	private static final String MAGIC_MIME_DETECTOR = MagicMimeMimeDetector.class.getName();
	
	private static final String EXTENSION_MIME_DETECTOR = ExtensionMimeDetector.class.getName();

	/**
	 * Gets the mime type by extension.
	 * 
	 * @param fileName the file name
	 * @return the mime type by extension
	 */
	public static MimeType getMimeTypeByExtension( String fileName ) {
		MimeType mt = null;
		String ext = FilenameUtils.getExtension( fileName );
		if (! StringUtils.isEmpty(ext) ) {
			mt = MimeType.getByExtension(ext);
			if ( mt == null ) {
				MimeUtil.registerMimeDetector(EXTENSION_MIME_DETECTOR);
				Collection<?> mimeTypes = MimeUtil.getMimeTypes(fileName);
				if (! mimeTypes.isEmpty() ) {
					eu.medsea.mimeutil.MimeType type = (eu.medsea.mimeutil.MimeType) mimeTypes.iterator().next();
					mt = MimeType.get(type.toString());
				}			
				MimeUtil.unregisterMimeDetector(EXTENSION_MIME_DETECTOR);				
			}
		}
		return mt;
	}	
	
	/**
	 * Gets the mime type.
	 * 
	 * @param data the data
	 * @return the mime type
	 */
	public static MimeType getMimeType( byte[] data ) {
		MimeType mt = null;
		MimeUtil.registerMimeDetector(MAGIC_MIME_DETECTOR);
		Collection<?> mimeTypes = MimeUtil.getMimeTypes(data);
		if (! mimeTypes.isEmpty() ) {
			eu.medsea.mimeutil.MimeType type = (eu.medsea.mimeutil.MimeType) mimeTypes.iterator().next();
			mt = MimeType.get(type.toString());
		}			
		MimeUtil.unregisterMimeDetector(MAGIC_MIME_DETECTOR);
		return mt;			
	}
	
	/**
	 * Gets the mime type.
	 * 
	 * @param in the in
	 * @return the mime type
	 */
	public static MimeType getMimeType( InputStream in ) {
		MimeType mt = null;
		MimeUtil.registerMimeDetector(MAGIC_MIME_DETECTOR);
		Collection<?> mimeTypes = MimeUtil.getMimeTypes(in);
		if (! mimeTypes.isEmpty() ) {
			eu.medsea.mimeutil.MimeType type = (eu.medsea.mimeutil.MimeType) mimeTypes.iterator().next();
			mt = MimeType.get(type.toString());
		}			
		MimeUtil.unregisterMimeDetector(MAGIC_MIME_DETECTOR);
		return mt;			
	}	
	
	/**
	 * Gets the mime type.
	 * 
	 * @param file the file
	 * @return the mime type
	 */
	public static MimeType getMimeType( File file ) {
		InputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			return getMimeType(in);
		} catch ( IOException e ) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			IOUtils.closeQuietly(in);
		}
		return null;			
	}

}
