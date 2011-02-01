package com.code.aon.ui.publisher.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;

public class SanselanUtil {

	private static final Logger LOGGER = LoggerFactory.getLogger(SanselanUtil.class.getName());
	
	/**
	 * Gets the image.
	 * 
	 * @param file
	 *            the file
	 * @return the image
	 */
	public static BufferedImage getBufferedImage(File file) {
		try {
			return Sanselan.getBufferedImage(file);
		} catch (ImageReadException e) {
			LOGGER.error( "Error reading " + file, e );
		} catch (IOException e) {
			LOGGER.error( "Error reading " + file, e );			
		}
		return null;
	}

	/**
	 * Gets the image.
	 * 
	 * @param data
	 *            the data
	 * @return the image
	 */
	public static BufferedImage getBufferedImage(byte[] data) {
		try {
			return Sanselan.getBufferedImage(data);
		} catch (ImageReadException e) {
			LOGGER.error( "Error image", e );			
		} catch (IOException e) {
			LOGGER.error( "Error image", e );			
		}
		return null;
	}	
	
	public static ImageFormat getImageFormat( MimeType type ) {
		switch ( type ) {
			case MIME_BMP:
				return ImageFormat.IMAGE_FORMAT_BMP;
			case MIME_GIF:
				return ImageFormat.IMAGE_FORMAT_GIF;
			case MIME_JPEG:
				return ImageFormat.IMAGE_FORMAT_JPEG;
			case MIME_PNG:
				return ImageFormat.IMAGE_FORMAT_PNG;
			case MIME_TIFF:
				return ImageFormat.IMAGE_FORMAT_TIFF;
			case MIME_ICO:
				return ImageFormat.IMAGE_FORMAT_ICO;
		}
		return null;
	}
	
	
	public static boolean writeBufferedImage( BufferedImage image, File file, MimeType type ) {
		try {
			Sanselan.writeImage(image, file, getImageFormat(type), null);
			return true;
		} catch (ImageWriteException e) {
			LOGGER.error( "Error writing image " + file, e );
		} catch (IOException e) {
			LOGGER.error( "Error writing image " + file, e );
		}
		return false;
    }		

}
