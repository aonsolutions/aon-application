package com.esferalia.aon.ui.payroll.utils;

import java.awt.Rectangle;
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
	
	public static List<BufferedImage> create(URL url){
		List<BufferedImage> picList = new ArrayList<BufferedImage>();
		try {
			ByteBuffer buf = getAsByteArray(url);
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
