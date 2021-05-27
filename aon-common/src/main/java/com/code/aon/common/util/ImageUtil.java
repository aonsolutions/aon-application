package com.code.aon.common.util;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;

/**
 * The Class ImageUtil.
 */
public class ImageUtil {

	private static final int DEFAULT_TYPE = BufferedImage.TYPE_INT_RGB;
	
	private static final String DEFAULT_FORMAT = MimeType.MIME_JPEG.getExtension();
	
	/** The Constant LOGGER. */
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ImageUtil.class);

	/**
	 * This method returns true if the specified image has transparent pixels.
	 * 
	 * @param image
	 *            the image
	 * @return true, if successful
	 */
	public static boolean hasAlpha(Image image) {
		// If buffered image, the color model is readily available
		if (image instanceof BufferedImage) {
			BufferedImage bimage = (BufferedImage) image;
			return bimage.getColorModel().hasAlpha();
		}

		// Use a pixel grabber to retrieve the image's color model;
		// grabbing a single pixel is usually sufficient
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
		}

		// Get the image's color model
		ColorModel cm = pg.getColorModel();
		return cm.hasAlpha();
	}

	/**
	 * This method returns a buffered image with the contents of an image.
	 * 
	 * @param image
	 *            the image
	 * @return the buffered image
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}

		// This code ensures that all the pixels in the image are loaded
		image = new ImageIcon(image).getImage();

		// Determine if the image has transparent pixels; for this method's
		// implementation, see Determining If an Image Has Transparent Pixels
		boolean hasAlpha = hasAlpha(image);

		// Create a buffered image with a format that's compatible with the
		// screen
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		try {
			// Determine the type of transparency of the new buffered image
			int transparency = Transparency.OPAQUE;
			if (hasAlpha) {
				transparency = Transparency.BITMASK;
			}

			// Create the buffered image
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image
					.getHeight(null), transparency);
		} catch (HeadlessException e) {
			// The system does not have a screen
		}

		if (bimage == null) {
			// Create a buffered image using the default color model
			int type = BufferedImage.TYPE_INT_RGB;
			if (hasAlpha) {
				type = BufferedImage.TYPE_INT_ARGB;
			}
			bimage = new BufferedImage(image.getWidth(null), image
					.getHeight(null), type);
		}

		// Copy image to buffered image
		Graphics g = bimage.createGraphics();

		// Paint the image onto the buffered image
		g.drawImage(image, 0, 0, null);
		g.dispose();

		return bimage;
	}

	/**
	 * Gets the image.
	 * 
	 * @param file
	 *            the file
	 * @return the image
	 */
	public static BufferedImage getBufferedImage(File file) {
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = ImageIO.read(file);
		} catch (Throwable th) {
			LOGGER.error("ImageIO error reading image " + file + ". "
					+ th.getMessage(), th);
		}
		if (bufferedImage == null) {
			Image image = getImage(file);
			bufferedImage = toBufferedImage(image);
		}
		return bufferedImage;
	}

	/**
	 * Gets the image.
	 * 
	 * @param data
	 *            the data
	 * @return the image
	 */
	public static BufferedImage getBufferedImage(byte[] data) {
		BufferedImage bufferedImage = null;
		try {
			ByteArrayInputStream in = new ByteArrayInputStream(data);
			bufferedImage = ImageIO.read(in);
			in.close();
		} catch (Throwable th) {
			LOGGER.error("ImageIO error reading image. " + th.getMessage(), th);
		}
		if (bufferedImage == null) {
			Image image = getImage(data);
			bufferedImage = toBufferedImage(image);
		}
		return bufferedImage;
	}
	
	/**
	 * Gets the image.
	 * 
	 * @param file
	 *            the file
	 * @return the image
	 */
	public static Image getImage(File file) {
		return new ImageIcon(file.getAbsolutePath()).getImage();
	}

	/**
	 * Gets the image.
	 * 
	 * @param data
	 *            the data
	 * @return the image
	 */
	public static Image getImage(byte[] data) {
		return new ImageIcon(data).getImage();
	}
	
	/**
	 * Gets the resize dimension.
	 * 
	 * @param image the image
	 * @param maxWidth the max width
	 * @param maxHeight the max height
	 * @return the resize dimension
	 */
	public static Dimension getResizeDimension( Image image, int maxWidth, int maxHeight ) {
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		double scale = (double) maxWidth / (double) height;

		if (width > height) {
			scale = (double) maxWidth / (double) width;
		}

		int scaledWidth = (int) (scale * width);
		int scaledHeight = (int) (scale * height);

		if (scaledHeight > maxHeight) {
			scale = (double) maxHeight / (double) height;
			scaledWidth = (int) (scale * width);
			scaledHeight = (int) (scale * height);
		}		
		return new Dimension(scaledWidth, scaledHeight);
	}	

	/**
	 * Gets the proportional width.
	 * 
	 * @param image the image
	 * @param height the height
	 * @return the proportional width
	 */
	public static int getProportionalWidth( BufferedImage image, int height ) {
		return getProportionalWidth(image.getWidth(), image.getHeight(), height);
	}

	/**
	 * Gets the proportional width.
	 * 
	 * @param originalWidth the image original width
	 * @param originalHeight the image original height
	 * @param height the height
	 * @return the proportional width
	 */
	public static int getProportionalWidth( int originalWidth, int originalHeight, int height ) {
		double scale = (double)originalHeight / (double)height;
		return (int)((double)originalWidth / scale);
	}
	
	/**
	 * Gets the proportional height.
	 * 
	 * @param image the image
	 * @param width the width
	 * @return the proportional height
	 */
	public static int getProportionalHeight( BufferedImage image, int width ) {
		return getProportionalHeight(image.getWidth(), image.getHeight(), width);
	}	

	/**
	 * Gets the proportional height.
	 * 
	 * @param originalWidth the image original width
	 * @param originalHeight the image original height
	 * @param width the width
	 * @return the proportional height
	 */
	public static int getProportionalHeight( int originalWidth, int originalHeight, int width ) {
		double scale = (double)originalWidth / (double)width;
		return (int)((double)originalHeight / scale);
	}	
	
	/**
	 * Scale.
	 * 
	 * @param image the image
	 * @param width the width
	 * @param height the height
	 * @return the buffered image
	 */
	public static BufferedImage scale(BufferedImage image, int width, int height) {
		double sx = (double) width / (double) image.getWidth();
		double sy = (double) height / (double) image.getHeight();
		int type = ( image.getType() == 0) ? DEFAULT_TYPE : image.getType();
		BufferedImage outImage = new BufferedImage(width, height, type);
		AffineTransform trans = new AffineTransform();
		trans.scale(sx, sy);
		Graphics2D g = outImage.createGraphics();
		g.drawImage(image, trans, null);
		g.dispose();
		return outImage;
	}
	
	/**
	 * Gets the image.
	 * 
	 * @param image the image
	 * @param formatName the format name
	 * @return the image
	 */
	public static byte[] getImage( BufferedImage image, String formatName ) {
        try {
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageOutputStream out = ImageIO.createImageOutputStream(baos);
			String _formatName = formatName;
        	if ( StringUtils.isBlank(_formatName) ) {
        		_formatName = DEFAULT_FORMAT;
        	}			
			ImageIO.write( image, _formatName, out );
			out.close();
			return baos.toByteArray();
		} catch (IOException e) {
			LOGGER.error( "Error getting image", e );
		}
		return null;
	}

	/**
	 * Gets the JPEG image.
	 * 
	 * @param image the image
	 * @param compressionQuality the compression quality
	 * @return the JPEG image
	 */
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
	
	/**
	 * Write buffered image.
	 * 
	 * @param image the image
	 * @param file the file
	 * @return true, if successful
	 */
	public static boolean writeBufferedImage( BufferedImage image, File file ) {
        try {
        	String formatName = FilenameUtils.getExtension(file.getName());
        	if ( StringUtils.isBlank(formatName) ) {
        		formatName = DEFAULT_FORMAT;
        	}
        	return ImageIO.write( image, formatName, file );
        } catch (IOException e) {
			LOGGER.error( "Error saving image to " + file, e );        	
        }
        return false;
    }	

	/**
	 * Write buffered image.
	 * 
	 * @param image the image
	 * @param out the out
	 * @param type the type
	 * @return true, if successful
	 */
	public static boolean writeBufferedImage( BufferedImage image, OutputStream out, MimeType type ) {
        try {
			String _formatName = DEFAULT_FORMAT;
        	if ( type != null ) {
        		_formatName = type.getExtension();
        	}			
        	return ImageIO.write( image, _formatName, out );
        } catch (IOException e) {
			LOGGER.error( "Error saving image to " + type, e );        	
        }
        return false;
    }	

}
