package net.aonsolutions.aon.in.pdf.api.toolkit;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PDFImage {

	byte[] data;
	float x;
	float y;
	float width;
	float height;
	
	
	public PDFImage(byte[] data, float x, float y, float maxWidth, float maxHeight) throws IOException {
		this.data = data;
		this.x = x;
		this.y = y;
		float logoWidth = 0;
		float logoHeigth = 0;
		if (data != null) {
			BufferedImage bufferedImage = null;
			bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
			logoHeigth = bufferedImage.getHeight();
			logoWidth = bufferedImage.getWidth();
			System.out.println("Original logo size: " + logoWidth + " x " + logoHeigth);
			float proportion = logoHeigth/logoWidth;
			
			if (logoHeigth > maxHeight) {
				logoHeigth = maxHeight;
				logoWidth = logoHeigth / proportion;
			}
			if (logoWidth > maxWidth) {
				logoWidth = maxWidth;
				logoHeigth = logoWidth * proportion;
			}
		}
		
		System.out.println("Logo size: " + logoWidth + " x " + logoHeigth);
		this.width = logoWidth;
		this.height = logoHeigth;
	}
	
	public byte[] getData() {
		return data;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getWidth() {
		return width;
	}
	
	public float getHeight() {
		return height;
	}
}
