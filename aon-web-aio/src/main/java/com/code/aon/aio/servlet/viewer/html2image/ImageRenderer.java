package com.code.aon.aio.servlet.viewer.html2image;

import java.awt.image.BufferedImage;

import org.xhtmlrenderer.render.Box;

public interface ImageRenderer {
	
	Box getRootBox();
	
	int getWidth();

	ImageRenderer setWidth(int width);

	int getHeight();

	ImageRenderer setHeight(int height);

	boolean isAutoHeight();

	ImageRenderer setAutoHeight(boolean autoHeight);

	ImageRenderer setImageType(String imageType);

	BufferedImage getBufferedImage(int imageType);

	BufferedImage getBufferedImage();

	ImageRendererImpl clearCache();

	float getWriteCompressionQuality();

	ImageRenderer setWriteCompressionQuality(float writeCompressionQuality);

	int getWriteCompressionMode();

	ImageRenderer setWriteCompressionMode(int writeCompressionMode);

	String getWriteCompressionType();

	ImageRenderer setWriteCompressionType(String writeCompressionType);
}
