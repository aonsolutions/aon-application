package net.aonsolutions.aon.in.pdf.maker.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageFormatTest {

	@Test
	public void recognizesRealImagesByTheirBytes() throws Exception {
		assertEquals(ImageFormat.JPEG, ImageFormat.of(encode("jpg")));
		assertEquals(ImageFormat.PNG, ImageFormat.of(encode("png")));
		assertEquals(ImageFormat.GIF, ImageFormat.of(encode("gif")));
		assertEquals(ImageFormat.BMP, ImageFormat.of(encode("bmp")));
	}

	@Test
	public void recognizesHeicByItsBrand() {
		// caja ftyp de un HEIC de iPhone: tamano, "ftyp" y la marca del formato.
		assertEquals(ImageFormat.HEIF, ImageFormat.of(isoBmff("heic")));
		assertEquals(ImageFormat.HEIF, ImageFormat.of(isoBmff("mif1")));
		assertEquals(ImageFormat.AVIF, ImageFormat.of(isoBmff("avif")));
		// un mp4 tambien es ISO-BMFF, pero no es una imagen.
		assertEquals(ImageFormat.UNKNOWN, ImageFormat.of(isoBmff("mp42")));
	}

	@Test
	public void ignoresWhatIsNotAnImage() {
		assertEquals(ImageFormat.UNKNOWN, ImageFormat.of("%PDF-1.4 algo".getBytes()));
		assertEquals(ImageFormat.UNKNOWN, ImageFormat.of(new byte[] { 1, 2, 3 }));
		assertEquals(ImageFormat.UNKNOWN, ImageFormat.of((byte[]) null));
		assertFalse(ImageFormat.UNKNOWN.isImage());
	}

	@Test
	public void fallsBackToTheFileName() {
		assertEquals(ImageFormat.HEIF, ImageFormat.ofFileName("invoices/dominio/b1/user/job/00__IMG_0042.HEIC"));
		assertEquals(ImageFormat.JPEG, ImageFormat.ofFileName("foto.jpeg"));
		assertEquals(ImageFormat.UNKNOWN, ImageFormat.ofFileName("factura.pdf"));
		assertEquals(ImageFormat.UNKNOWN, ImageFormat.ofFileName("sinextension"));
		assertEquals(ImageFormat.UNKNOWN, ImageFormat.ofFileName(null));
	}

	@Test
	public void readsTheContentTypeWhenItSaysSomething() {
		assertEquals(ImageFormat.JPEG, ImageFormat.ofContentType("image/jpeg"));
		assertEquals(ImageFormat.JPEG, ImageFormat.ofContentType("IMAGE/JPG"));
		assertEquals(ImageFormat.HEIF, ImageFormat.ofContentType("image/heic-sequence"));
		assertEquals(ImageFormat.PNG, ImageFormat.ofContentType("image/png; charset=binary"));
		assertEquals(ImageFormat.UNKNOWN, ImageFormat.ofContentType("binary/octet-stream"));
		assertEquals(ImageFormat.UNKNOWN, ImageFormat.ofContentType("application/pdf"));
		assertEquals(ImageFormat.UNKNOWN, ImageFormat.ofContentType(null));
	}

	@Test
	public void prefersTheBytesOverTheFileName() throws Exception {
		// un JPEG guardado con el nombre cambiado sigue siendo un JPEG.
		assertEquals(ImageFormat.JPEG, ImageFormat.of(encode("jpg"), "factura.png"));
		// y si los bytes no dicen nada, decide el nombre.
		assertEquals(ImageFormat.HEIF, ImageFormat.of(new byte[] { 0 }, "IMG_0042.heic"));
	}

	@Test
	public void knowsWhichFormatsImageIoCannotRead() {
		assertTrue(ImageFormat.HEIF.needsCodec());
		assertTrue(ImageFormat.AVIF.needsCodec());
		assertFalse(ImageFormat.JPEG.needsCodec());
		assertFalse(ImageFormat.PNG.needsCodec());
	}

	private static byte[] encode(String format) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB), format, out);
		return out.toByteArray();
	}

	private static byte[] isoBmff(String brand) {
		byte[] data = new byte[32];
		data[3] = 24; // tamano de la caja
		System.arraycopy("ftyp".getBytes(), 0, data, 4, 4);
		System.arraycopy(brand.getBytes(), 0, data, 8, 4);
		return data;
	}
}
