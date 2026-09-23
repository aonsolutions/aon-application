package net.aonsolutions.aon.in.pdf.maker.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.Test;

/**
 * Las fotos hechas con el movil llegan con los pixeles sin girar y el giro anotado en la EXIF.
 * Estas pruebas comprueban que el PDF sale derecho para las ocho orientaciones posibles.
 */
public class ExifOrientationTest {

	private static final int WIDTH = 240;
	private static final int HEIGHT = 120;
	private static final int BLOCK = 60;
	private static final int TOLERANCE = 24; // margen para el ruido del JPEG

	private static final Color[] COLORS = { Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.MAGENTA,
			Color.CYAN, Color.BLACK, Color.WHITE };

	@Test
	public void readsEveryOrientation() throws Exception {
		for (int orientation = 1; orientation <= 8; orientation++) {
			assertEquals("big endian, orientacion " + orientation, orientation,
					ExifOrientation.read(exif(jpeg(source()), orientation, true)));
			assertEquals("little endian, orientacion " + orientation, orientation,
					ExifOrientation.read(exif(jpeg(source()), orientation, false)));
		}
	}

	@Test
	public void readsDefaultWithoutExif() throws Exception {
		assertEquals(ExifOrientation.DEFAULT, ExifOrientation.read(jpeg(source())));
		assertEquals(ExifOrientation.DEFAULT, ExifOrientation.read(png(source())));
		assertEquals(ExifOrientation.DEFAULT, ExifOrientation.read(new byte[] { 1, 2, 3 }));
		assertEquals(ExifOrientation.DEFAULT, ExifOrientation.read(null));
	}

	@Test
	public void buildsUprightPdfForEveryOrientation() throws Exception {
		BufferedImage source = source();
		for (int orientation = 1; orientation <= 8; orientation++) {
			for (boolean bigEndian : new boolean[] { true, false }) {
				BufferedImage page = toPdfAndRender(exif(jpeg(source), orientation, bigEndian));
				assertNull("orientacion " + orientation + ", big endian " + bigEndian,
						compare(page, upright(source, orientation)));
			}
		}
	}

	@Test
	public void keepsImagesWithoutExifUntouched() throws Exception {
		BufferedImage source = source();
		BufferedImage page = toPdfAndRender(jpeg(source));
		assertEquals(WIDTH, page.getWidth());
		assertEquals(HEIGHT, page.getHeight());
		assertNull(compare(page, source));
	}

	/** Rejilla de 4x2 bloques de colores planos, para reconocer giros y espejos. */
	private static BufferedImage source() {
		BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		int i = 0;
		for (int y = 0; y < HEIGHT; y += BLOCK)
			for (int x = 0; x < WIDTH; x += BLOCK) {
				g.setColor(COLORS[i++]);
				g.fillRect(x, y, BLOCK, BLOCK);
			}
		g.dispose();
		return image;
	}

	private static byte[] jpeg(BufferedImage image) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", out);
		return out.toByteArray();
	}

	private static byte[] png(BufferedImage image) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "png", out);
		return out.toByteArray();
	}

	/** Inserta detras del SOI un APP1/Exif con un unico tag: Orientation. */
	private static byte[] exif(byte[] jpeg, int orientation, boolean bigEndian) throws Exception {
		ByteArrayOutputStream tiff = new ByteArrayOutputStream();
		tiff.write(bigEndian ? new byte[] { 'M', 'M' } : new byte[] { 'I', 'I' });
		writeShort(tiff, 42, bigEndian);
		writeInt(tiff, 8, bigEndian);
		writeShort(tiff, 1, bigEndian); // una entrada
		writeShort(tiff, 0x0112, bigEndian); // Orientation
		writeShort(tiff, 3, bigEndian); // SHORT
		writeInt(tiff, 1, bigEndian);
		writeShort(tiff, orientation, bigEndian);
		writeShort(tiff, 0, bigEndian); // resto del campo de valor
		writeInt(tiff, 0, bigEndian); // no hay IFD1
		byte[] payload = tiff.toByteArray();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.write(jpeg, 0, 2); // SOI
		out.write(0xFF);
		out.write(0xE1); // APP1
		writeShort(out, 2 + 6 + payload.length, true); // la longitud siempre es big endian
		out.write(new byte[] { 'E', 'x', 'i', 'f', 0, 0 });
		out.write(payload);
		out.write(jpeg, 2, jpeg.length - 2);
		return out.toByteArray();
	}

	private static void writeShort(ByteArrayOutputStream out, int value, boolean bigEndian) {
		if (bigEndian) {
			out.write((value >> 8) & 0xFF);
			out.write(value & 0xFF);
		} else {
			out.write(value & 0xFF);
			out.write((value >> 8) & 0xFF);
		}
	}

	private static void writeInt(ByteArrayOutputStream out, int value, boolean bigEndian) {
		if (bigEndian) {
			writeShort(out, (value >> 16) & 0xFFFF, true);
			writeShort(out, value & 0xFFFF, true);
		} else {
			writeShort(out, value & 0xFFFF, false);
			writeShort(out, (value >> 16) & 0xFFFF, false);
		}
	}

	private static BufferedImage toPdfAndRender(byte[] image) throws Exception {
		byte[] pdf;
		try (ImageToPdf imageToPdf = new ImageToPdf(image)) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			imageToPdf.save(out);
			pdf = out.toByteArray();
		}
		try (PDDocument document = Loader.loadPDF(pdf)) {
			return new PDFRenderer(document).renderImage(0, 1f, ImageType.RGB);
		}
	}

	/**
	 * Implementacion de referencia: remapea los pixeles uno a uno, sin usar la matriz del PDF.
	 */
	private static BufferedImage upright(BufferedImage source, int orientation) {
		boolean swapsSides = ExifOrientation.swapsSides(orientation);
		BufferedImage upright = new BufferedImage(swapsSides ? HEIGHT : WIDTH, swapsSides ? WIDTH : HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		for (int y = 0; y < HEIGHT; y++)
			for (int x = 0; x < WIDTH; x++) {
				int nx, ny;
				switch (orientation) {
				case 2: nx = WIDTH - 1 - x;  ny = y;              break;
				case 3: nx = WIDTH - 1 - x;  ny = HEIGHT - 1 - y; break;
				case 4: nx = x;              ny = HEIGHT - 1 - y; break;
				case 5: nx = y;              ny = x;              break;
				case 6: nx = HEIGHT - 1 - y; ny = x;              break;
				case 7: nx = HEIGHT - 1 - y; ny = WIDTH - 1 - x;  break;
				case 8: nx = y;              ny = WIDTH - 1 - x;  break;
				default: nx = x;             ny = y;
				}
				upright.setRGB(nx, ny, source.getRGB(x, y));
			}
		return upright;
	}

	/** @return el primer punto que no cuadra, o null si la pagina coincide con lo esperado. */
	private static String compare(BufferedImage actual, BufferedImage expected) {
		if (actual.getWidth() != expected.getWidth() || actual.getHeight() != expected.getHeight())
			return "tamano " + actual.getWidth() + "x" + actual.getHeight() + ", esperado " + expected.getWidth() + "x"
					+ expected.getHeight();

		for (int y = BLOCK / 2; y < expected.getHeight(); y += BLOCK)
			for (int x = BLOCK / 2; x < expected.getWidth(); x += BLOCK) {
				Color a = new Color(actual.getRGB(x, y));
				Color e = new Color(expected.getRGB(x, y));
				if (Math.abs(a.getRed() - e.getRed()) > TOLERANCE || Math.abs(a.getGreen() - e.getGreen()) > TOLERANCE
						|| Math.abs(a.getBlue() - e.getBlue()) > TOLERANCE)
					return "pixel (" + x + "," + y + ") es " + a + " y se esperaba " + e;
			}
		return null;
	}
}
