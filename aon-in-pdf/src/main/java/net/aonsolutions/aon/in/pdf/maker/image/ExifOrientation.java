package net.aonsolutions.aon.in.pdf.maker.image;

import org.apache.pdfbox.util.Matrix;

/**
 * Lectura del tag EXIF Orientation (0x0112) de una imagen JPEG.
 *
 * Las fotos hechas con el movil casi nunca se guardan "derechas": el sensor escribe los pixeles
 * tal cual los captura y anota en la EXIF como hay que girarlos al mostrarlos. Los navegadores y
 * las galerias aplican ese giro, pero ni ImageIO ni el formato PDF lo hacen, asi que al pasar la
 * imagen a PDF sin mas la pagina sale girada (normalmente apaisada) respecto a lo que vio el
 * usuario.
 *
 * Esta clase devuelve la orientacion y la matriz de transformacion que hay que aplicar al content
 * stream para dibujar la imagen ya derecha, sin recomprimir los pixeles.
 *
 * @see <a href="https://exiftool.org/TagNames/EXIF.html">EXIF Orientation</a>
 */
public final class ExifOrientation {

	/** Orientacion por defecto: la imagen ya esta derecha. */
	public static final int DEFAULT = 1;

	private static final int TAG_ORIENTATION = 0x0112;
	private static final int MARKER_APP1 = 0xE1;
	private static final int MARKER_SOS = 0xDA;

	private ExifOrientation() {
	}

	/**
	 * @param image bytes de la imagen.
	 * @return la orientacion EXIF (1..8), o {@link #DEFAULT} si la imagen no es JPEG, no tiene EXIF
	 *         o esta corrupta.
	 */
	public static int read(byte[] image) {
		try {
			int orientation = parse(image);
			return orientation >= 1 && orientation <= 8 ? orientation : DEFAULT;
		} catch (Exception e) {
			return DEFAULT;
		}
	}

	/**
	 * @return true si la orientacion gira la imagen 90 grados, es decir, si el ancho y el alto de
	 *         la pagina son los del pixel intercambiados.
	 */
	public static boolean swapsSides(int orientation) {
		return orientation >= 5 && orientation <= 8;
	}

	/**
	 * Matriz que coloca la imagen derecha dentro de una pagina cuyo tamano ya tiene en cuenta
	 * {@link #swapsSides(int)}.
	 *
	 * @param orientation orientacion EXIF (1..8).
	 * @param width  ancho en pixeles de la imagen (sin girar).
	 * @param height alto en pixeles de la imagen (sin girar).
	 * @return la matriz a aplicar antes de dibujar, o null si no hay que transformar nada.
	 */
	public static Matrix getTransform(int orientation, float width, float height) {
		switch (orientation) {
		case 2: // espejo horizontal
			return new Matrix(-1, 0, 0, 1, width, 0);
		case 3: // 180 grados
			return new Matrix(-1, 0, 0, -1, width, height);
		case 4: // espejo vertical
			return new Matrix(1, 0, 0, -1, 0, height);
		case 5: // espejo horizontal + 90 grados antihorario
			return new Matrix(0, -1, -1, 0, height, width);
		case 6: // 90 grados horario
			return new Matrix(0, -1, 1, 0, 0, width);
		case 7: // espejo horizontal + 90 grados horario
			return new Matrix(0, 1, 1, 0, 0, 0);
		case 8: // 90 grados antihorario
			return new Matrix(0, 1, -1, 0, height, 0);
		default:
			return null;
		}
	}

	private static int parse(byte[] image) {
		if (image == null || image.length < 4 || uint8(image, 0) != 0xFF || uint8(image, 1) != 0xD8)
			return DEFAULT;

		int i = 2;
		while (i + 4 <= image.length && uint8(image, i) == 0xFF) {
			int marker = uint8(image, i + 1);
			if (marker == MARKER_SOS || marker == 0xD9)
				return DEFAULT;

			int length = uint16(image, i + 2, true);
			if (length < 2)
				return DEFAULT;

			if (marker == MARKER_APP1 && isExif(image, i + 4))
				return parseTiff(image, i + 10, i + 2 + length);

			i += 2 + length;
		}
		return DEFAULT;
	}

	private static boolean isExif(byte[] image, int offset) {
		return offset + 6 <= image.length && image[offset] == 'E' && image[offset + 1] == 'x'
				&& image[offset + 2] == 'i' && image[offset + 3] == 'f' && image[offset + 4] == 0
				&& image[offset + 5] == 0;
	}

	/**
	 * @param tiff comienzo de la cabecera TIFF, que es el origen de todos los offsets internos.
	 * @param end  final del segmento APP1.
	 */
	private static int parseTiff(byte[] image, int tiff, int end) {
		if (tiff + 8 > end || end > image.length)
			return DEFAULT;

		boolean bigEndian;
		if (image[tiff] == 'M' && image[tiff + 1] == 'M')
			bigEndian = true;
		else if (image[tiff] == 'I' && image[tiff + 1] == 'I')
			bigEndian = false;
		else
			return DEFAULT;

		int ifd = tiff + (int) uint32(image, tiff + 4, bigEndian);
		if (ifd + 2 > end)
			return DEFAULT;

		int entries = uint16(image, ifd, bigEndian);
		for (int e = 0; e < entries; e++) {
			int entry = ifd + 2 + e * 12;
			if (entry + 12 > end)
				return DEFAULT;
			if (uint16(image, entry, bigEndian) == TAG_ORIENTATION)
				return uint16(image, entry + 8, bigEndian);
		}
		return DEFAULT;
	}

	private static int uint8(byte[] data, int offset) {
		return data[offset] & 0xFF;
	}

	private static int uint16(byte[] data, int offset, boolean bigEndian) {
		return bigEndian ? (uint8(data, offset) << 8) | uint8(data, offset + 1)
				: (uint8(data, offset + 1) << 8) | uint8(data, offset);
	}

	private static long uint32(byte[] data, int offset, boolean bigEndian) {
		return bigEndian
				? ((long) uint16(data, offset, true) << 16) | uint16(data, offset + 2, true)
				: ((long) uint16(data, offset + 2, false) << 16) | uint16(data, offset, false);
	}
}
