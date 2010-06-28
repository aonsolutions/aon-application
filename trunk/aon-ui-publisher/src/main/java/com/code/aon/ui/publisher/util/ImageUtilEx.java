package com.code.aon.ui.publisher.util;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.ImageUtil;

import eu.medsea.mimeutil.MimeUtil;

public class ImageUtilEx {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageUtilEx.class.getName());
	
	static {
		MimeUtil.registerMimeDetector("eu.medsea.mimeutil.detector.MagicMimeMimeDetector");
	}

	@SuppressWarnings("unchecked")
	public static MimeType getMimeType( byte[] data, MimeType mimeType ) {
		if ( mimeType == null ) {
			Collection mimeTypes = MimeUtil.getMimeTypes(data);
			if (! mimeTypes.isEmpty() ) {
				eu.medsea.mimeutil.MimeType type = (eu.medsea.mimeutil.MimeType) mimeTypes.iterator().next();
				return MimeType.get(type.getMediaType());
			}			
			return null;			
		} else {
			return mimeType;
		}
	}	
	
	public static BufferedImage getBufferedImage(byte[] data, MimeType type) {
		BufferedImage image = null;
		if ( (type != null) && (type == MimeType.MIME_GIF) ) {
			image = SanselanUtil.getBufferedImage(data);
		}
		if ( image == null ) {
			image = ImageUtil.getBufferedImage(data);
		}
		return image;
	}		
	
	public static boolean writeBufferedImage( BufferedImage image, MimeType type, File file ) {
		boolean ok = false;
		if ( (type != null) && (type == MimeType.MIME_GIF) ) {
			ok = SanselanUtil.writeBufferedImage(image, file, type);
		}
		if (! ok) {
			try {
				OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
				ok = ImageUtil.writeBufferedImage(image, out, type);
				IOUtils.closeQuietly(out);
			} catch (FileNotFoundException e) {
				LOGGER.error( "Error writing image " + file, e );
			}
		}
		return ok;
    }	

}
