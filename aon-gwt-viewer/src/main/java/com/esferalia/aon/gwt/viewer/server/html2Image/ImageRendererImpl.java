package com.esferalia.aon.gwt.viewer.server.html2Image;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import javax.imageio.ImageWriteParam;

import org.w3c.dom.Document;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.simple.Graphics2DRenderer;

public class ImageRendererImpl implements ImageRenderer {
	public static final int DEFAULT_WIDTH = 768;
	public static final int DEFAULT_HEIGHT = 768;

	private DocumentHolder documentHolder;

	private int width = DEFAULT_WIDTH;
	private int height = DEFAULT_HEIGHT;
	private boolean autoHeight = true;

	private String imageFormat = null;
	private float writeCompressionQuality = 1.0f;
	private int writeCompressionMode = ImageWriteParam.MODE_COPY_FROM_METADATA;
	private String writeCompressionType = null;
	private Box rootBox;

	private BufferedImage bufferedImage;
	private int cacheImageType = -1;
	private Document cacheDocument;

	public ImageRendererImpl(DocumentHolder documentHolder) {
		this.documentHolder = documentHolder;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public ImageRenderer setWidth(int width) {
		this.width = width;
		return this;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public ImageRenderer setHeight(int height) {
		this.height = height;
		return this;
	}

	@Override
	public boolean isAutoHeight() {
		return autoHeight;
	}

	@Override
	public ImageRenderer setAutoHeight(boolean autoHeight) {
		this.autoHeight = autoHeight;
		return this;
	}

	public String getImageFormat() {
		return imageFormat;
	}

	public ImageRenderer setImageType(String imageType) {
		this.imageFormat = imageType;
		return this;
	}

	@Override
	public float getWriteCompressionQuality() {
		return writeCompressionQuality;
	}

	@Override
	public ImageRenderer setWriteCompressionQuality(float writeCompressionQuality) {
		this.writeCompressionQuality = writeCompressionQuality;
		return this;
	}

	@Override
	public int getWriteCompressionMode() {
		return writeCompressionMode;
	}

	@Override
	public ImageRenderer setWriteCompressionMode(int writeCompressionMode) {
		this.writeCompressionMode = writeCompressionMode;
		return this;
	}

	@Override
	public String getWriteCompressionType() {
		return writeCompressionType;
	}

	@Override
	public ImageRenderer setWriteCompressionType(String writeCompressionType) {
		this.writeCompressionType = writeCompressionType;
		return this;
	}

	@Override
	public BufferedImage getBufferedImage(int imageType) {
		final Document document = documentHolder.getDocument();
		if (bufferedImage != null || cacheImageType != imageType || cacheDocument != document) {
			cacheImageType = imageType;
			cacheDocument = document;
			Graphics2DRenderer renderer = new Graphics2DRenderer();
			renderer.setDocument(document, document.getDocumentURI());
			Dimension dimension = new Dimension(width, height);
			bufferedImage = new BufferedImage(width, height, imageType);

			if (autoHeight) {
				// do layout with temp buffer
				Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
				renderer.layout(graphics2D, new Dimension(width, height));
				graphics2D.dispose();

				Rectangle size = renderer.getMinimumSize();
				final int autoWidth = (int) size.getWidth();
				final int autoHeight = (int) size.getHeight();
				bufferedImage = new BufferedImage(autoWidth, autoHeight, imageType);
				dimension = new Dimension(autoWidth, autoHeight);
			}

			Graphics2D graphics2D = (Graphics2D) bufferedImage.getGraphics();
			renderer.layout(graphics2D, dimension);
			renderer.render(graphics2D);
			rootBox = renderer.getPanel().getRootBox();
			graphics2D.dispose();
		}
		return bufferedImage;
	}

	@Override
	public Box getRootBox() {
		if (rootBox == null) {
			getBufferedImage();
		}
		return rootBox;
	}

	@Override
	public ImageRendererImpl clearCache() {
		bufferedImage = null;
		rootBox = null;
		cacheDocument = null;
		cacheImageType = -1;
		return this;
	}

	public BufferedImage getBufferedImage() {
		return getBufferedImage(BufferedImage.TYPE_INT_ARGB);
	}

}