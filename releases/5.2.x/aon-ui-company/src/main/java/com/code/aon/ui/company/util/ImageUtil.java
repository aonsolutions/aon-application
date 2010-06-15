package com.code.aon.ui.company.util;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);
	
	public static BufferedImage getImage( byte[] data ) {
		BufferedImage result = null;
		try {
			result = ImageIO.read( new ByteArrayInputStream(data) );
		} catch (Throwable e) {
			LOGGER.debug( "Error loading image with ImageIO", e );
		}
		return result;
	}	

	public static int getProportionalWidth( BufferedImage image, int height ) {
		int image_width = image.getWidth();
		int image_height = image.getHeight();
		double scale = (double)image_height / (double)height;
		return (int)((double)image_width / scale);
	}

	public static int getProportionalHeight( BufferedImage image, int width ) {
		int image_width = image.getWidth();
		int image_height = image.getHeight();
		double scale = (double)image_width / (double)width;
		return (int)((double)image_height / scale);
	}

	public static BufferedImage scale(BufferedImage image, int width, int height, int type) {
		double sx = (double) width / (double) image.getWidth();
		double sy = (double) height / (double) image.getHeight();
		BufferedImage outImage = new BufferedImage(width, height, type);
		AffineTransform trans = new AffineTransform();
		trans.scale(sx, sy);
		Graphics2D g = outImage.createGraphics();
		g.drawImage(image, trans, null);
		g.dispose();
		return outImage;
	}

	public static BufferedImage scale(BufferedImage image, int width, int height) {
		return scale( image, width, height, image.getType() );
	}
	
	public static byte[] getJPEGImage( BufferedImage image, int compressionQuality ) {
        try {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageOutputStream out = ImageIO.createImageOutputStream(baos);
			saveJPEGImage( image, out, compressionQuality );
			out.close();
			return baos.toByteArray();
		} catch (IOException e) {
			LOGGER.error( "Error getting JPEG image", e );
		}
		return null;
	}
	
	private static void saveJPEGImage( BufferedImage image, ImageOutputStream out, int compressionQuality) throws IOException {
		if ( compressionQuality == -1 ) {
			ImageIO.write( image, "jpg", out );
		} else {
            // Find a jpeg writer
            ImageWriter writer = null;
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
            if (iter.hasNext()) {
                writer = (ImageWriter)iter.next();
            }
            writer.setOutput(out);
    
            // Set the compression quality
            JPEGImageWriteParam iwparam = new JPEGImageWriteParam(Locale.getDefault());
            iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            float quality = 0;
            if ( compressionQuality != 0 ) { 
            	quality = (float)compressionQuality/100;
            }
            iwparam.setCompressionQuality( quality );
            iwparam.setOptimizeHuffmanTables(true);
    
            // Write the image
            writer.write(null, new IIOImage(image, null, null), iwparam);
    
            // Cleanup
            out.flush();
            writer.dispose();
		}
    }		
}
