package com.esferalia.aon.ui.payroll.utils;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esferalia.aon.ui.payroll.controller.ContractGenerationWizard;
import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

public class PdfToImage {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ContractGenerationWizard.class.getName());
	
	public static void create(String pFile, String path){
		File file = new File(pFile);
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(file, "r");
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
					channel.size());
			PDFFile pdffile = new PDFFile(buf);
			// draw the first page to an image
			int num = pdffile.getNumPages();
			for (int i = 1; i <= num; i++)
			{
				PDFPage page = pdffile.getPage(i);
				// get the width and height for the doc at the default zoom
				int width = (int) page.getBBox().getWidth();
				int height = (int) page.getBBox().getHeight();
				Rectangle rect = new Rectangle(0, 0, width, height);
				int rotation = page.getRotation();
				Rectangle rect1 = rect;
				if (rotation == 90 || rotation == 270)
					rect1 = new Rectangle(0, 0, rect.height, rect.width);
				// generate the image
				BufferedImage img = (BufferedImage) page.getImage(
				rect.width, rect.height, // width & height
						rect1, // clip rect
						null, // null for the ImageObserver
						true, // fill background with white
						true // block until drawing is done
						);
				ImageIO.write(img, "png", new File(path + i + ".png"));
			}
		} catch (FileNotFoundException e1) {
			System.err.println(e1.getLocalizedMessage());
		} catch (IOException e) {
			System.err.println(e.getLocalizedMessage());
		}
		System.out.println("PDF images created!");
	}
	
	public static List<BufferedImage> create(String pFile){
		List<BufferedImage> picList = new ArrayList<BufferedImage>();
		File file = new File(pFile);
		RandomAccessFile raf;
		try {
			raf = new RandomAccessFile(file, "r");
			FileChannel channel = raf.getChannel();
			ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0,
					channel.size());
			PDFFile pdffile = new PDFFile(buf);
			int num = pdffile.getNumPages();
			for (int i = 1; i <= num; i++)
			{
				PDFPage page = pdffile.getPage(i);
				int width = (int) page.getBBox().getWidth();
				int height = (int) page.getBBox().getHeight();
				Rectangle rect = new Rectangle(0, 0, width, height);
				int rotation = page.getRotation();
				Rectangle rect1 = rect;
				if (rotation == 90 || rotation == 270)
					rect1 = new Rectangle(0, 0, rect.height, rect.width);
				BufferedImage img = (BufferedImage) page.getImage(
				rect.width, rect.height, 
						rect1, 
						null, 
						true, 
						true 
						);
				picList.add(img);
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return picList;
	}
	private static final double FACTOR_1X = 1.2;
	private static final double FACTOR_2X = 1.4;
//	private static int width;
//	private static int height;
//	public int getWidth() {
//		return width;
//	}
//	public void setWidth(int width) {
//		this.width = width;
//	}
//	public int getHeight() {
//		return height;
//	}
//	public void setHeight(int height) {
//		this.height = height;
//	}
	
//	public static BufferedImage create(URL url, int pageNumber, int zoom, int width, int height){
////		List<BufferedImage> picList = new ArrayList<BufferedImage>();
//		BufferedImage img = new BufferedImage(width, height,BufferedImage.TYPE_BYTE_BINARY); 
//		Image img2 = null;
//		try {
//			ByteBuffer buf = getAsByteArray(url);
//			PDFFile pdffile = new PDFFile(buf);
////			int num = pdffile.getNumPages();
////			for (int i = 1; i <= num; i++)
////			{
//				PDFPage page = pdffile.getPage(pageNumber);
//				int width2 = (int) page.getBBox().getWidth();
//				int height2 = (int) page.getBBox().getHeight();
////				width = getFactorizedValue(page.getBBox().getWidth(), zoom);
////				height = getFactorizedValue(page.getBBox().getHeight(), zoom);
////				width = getFactorizedValue(width, zoom);
////				height = getFactorizedValue(height, zoom);
////				escalePage(page, width, height);
//				
////				page.getBBox().setFrame(0, 0, width, height);
//				
//				Rectangle rect = new Rectangle(0, 0, width2, height2);
//				int rotation = page.getRotation();
//				Rectangle rect1 = rect;
//				if (rotation == 90 || rotation == 270)
//					rect1 = new Rectangle(0, 0, rect.height, rect.width);
//				img = (BufferedImage) page.getImage(
//						rect.width, rect.height, 
//						rect1, 
//						null, 
//						true, 
//						true 
//				);
////				img2 = page.getImage(
////						rect.width, rect.height, 
////						rect1, 
////						null, 
////						true, 
////						true 
////				);
////				img2 = img2.getScaledInstance(width, height, Image.SCALE_SMOOTH);
//				
//				// Convert Image to BufferedImage
////				Graphics2D bufImageGraphics = img.createGraphics();
////				bufImageGraphics.drawImage(img2, 0, 0, null);
//				
//				
////				picList.add(img);
////			}
//		} catch (FileNotFoundException e) {
//			LOGGER.error(e.getMessage(), e);
//		} catch (IOException e) {
//			LOGGER.error(e.getMessage(), e);
//		}
////		return picList;
//		return img;
//	}
	
	public static BufferedImage create(URL url, int pageNumber, int zoom, int width, int height){
//		List<BufferedImage> picList = new ArrayList<BufferedImage>();
		BufferedImage img = null;;
		Image img2 = null;;
		try {
			ByteBuffer buf = getAsByteArray(url);
			PDFFile pdffile = new PDFFile(buf);
//			int num = pdffile.getNumPages();
//			for (int i = 1; i <= num; i++)
//			{
				PDFPage page = pdffile.getPage(pageNumber);
				int width2 = (int) page.getBBox().getWidth()+zoom;
				int height2 = (int) page.getBBox().getHeight()+zoom;
//				width = getFactorizedValue(page.getBBox().getWidth(), zoom);
//				height = getFactorizedValue(page.getBBox().getHeight(), zoom);
//				width = getFactorizedValue(width, zoom);
//				height = getFactorizedValue(height, zoom);
//				escalePage(page, width, height);
				
//				page.getBBox().setFrame(0, 0, width, height);
				
				Rectangle rect = new Rectangle(0, 0, width2, height2);
				int rotation = page.getRotation();
				Rectangle rect1 = rect;
				if (rotation == 90 || rotation == 270)
					rect1 = new Rectangle(0, 0, rect.height, rect.width);
//				img = (BufferedImage) page.getImage(
//						rect.width, rect.height, 
//						rect1, 
//						null, 
//						true, 
//						true 
//				);
				
				
				Rectangle2D r2d = page.getBBox ();
//				double width = r2d.getWidth ();
//				double height = r2d.getHeight ();
				width /= 72.0;
				height /= 72.0;
				int res = Toolkit.getDefaultToolkit ().getScreenResolution ();
				width *= res;
				height *= res;
				// height:842px; width:595px
				width = (int) (595*2);
				height = (int) (842*2);
				img = (BufferedImage) page.getImage ((int) width, (int) height, r2d, null, true, true);


				
				
				
				
				
				
				
				
//				img2 = page.getImage(
//						rect.width, rect.height, 
//						rect1, 
//						null, 
//						true, 
//						true 
//				);
//				img = (BufferedImage)img2.getScaledInstance(width, height, Image.SCALE_DEFAULT);
//				picList.add(img);
//			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
//		return picList;
		return img;
	}
	
//	private static void escalePage(PDFPage page, int width, int height) {
//	        if(pg == null)
//	            pg = curFile.getPage(curpage + 1);
//
//	        if(iiZoom > 300)
//	            iZoom = 300;
//	        else if(iiZoom < 50)
//	            iZoom = 50;
//	        else
//	            iZoom = iiZoom;

	        // Calculate the width of the page and the width of the display to display either the texts
//	        float fWidthPag = pg.getWidth();
//	        float fHeightPag = pg.getHeight();
//	        float fWidthVisible = (this.getWidth() * iZoom) / 100;
//	        float fHeightVisible = (fHeightPag * fWidthVisible)/fWidthPag;

//	        page.setPreferredSize(new Dimension((int)width,(int)height));
//	        page.setBounds(0,0,(int)width,(int)height);
//	    }

	
//	private static int getFactorizedValue(double value, int zoom){
//		if(zoom==0){
//			return (int)value;
//		} else if(zoom==1){
//			return (int)(value*FACTOR_1X);
//		} else if(zoom==2){
//			return (int)(value*FACTOR_2X);
//		}
//		return (int)value;
//	}
	
	public static ByteBuffer getAsByteArray(URL url) throws IOException {
        ByteArrayOutputStream tmpOut = new ByteArrayOutputStream();
//        URLConnection connection = url.openConnection();
//        int contentLength = connection.getContentLength();
        InputStream in = url.openStream();
        byte[] buf = new byte[512];
        int len;
        while (true) {
            len = in.read(buf);
            if (len == -1) {
                break;
            }
            tmpOut.write(buf, 0, len);
        }
        tmpOut.close();
        ByteBuffer bb = ByteBuffer.wrap(tmpOut.toByteArray(), 0, tmpOut.size());
        return bb;
	}

	/**
	 *@param args
	 */
	public static void main(String[] args) {
		if (args.length != 2)
		{
			System.err.println("Usage:Pdf2Image pdf imageFolder");
			return;
		}
		create(args[0], args[1]);
	}

}
