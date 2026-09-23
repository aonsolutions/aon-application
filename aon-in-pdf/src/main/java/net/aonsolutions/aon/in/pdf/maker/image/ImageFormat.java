package net.aonsolutions.aon.in.pdf.maker.image;

/**
 * Formatos de imagen que nos pueden llegar, con lo necesario para reconocerlos sin fiarse del
 * content type.
 *
 * El navegador rellena el tipo de un fichero a partir de lo que sabe el sistema operativo, asi que
 * no siempre acierta: un HEIC en Windows, por ejemplo, llega con el tipo vacio, S3 lo guarda como
 * "binary/octet-stream" y el fichero se queda sin convertir a PDF. Por eso aqui se mira primero la
 * firma de los bytes, que es la unica fuente fiable, y si no la tenemos, la extension.
 */
public enum ImageFormat {

	JPEG("image/jpeg", "jpg", "jpeg", "jpe"),
	PNG("image/png", "png"),
	GIF("image/gif", "gif"),
	BMP("image/bmp", "bmp", "dib"),
	TIFF("image/tiff", "tif", "tiff"),
	WEBP("image/webp", "webp"),
	/** HEIC/HEIF: el formato de las fotos de iPhone. */
	HEIF("image/heic", "heic", "heif", "hif"),
	AVIF("image/avif", "avif"),
	UNKNOWN(null);

	/** Marcas ISO-BMFF que identifican una imagen HEIF. */
	private static final String[] HEIF_BRANDS = { "heic", "heix", "heim", "heis", "hevc", "hevx", "hevm", "hevs",
			"mif1", "msf1" };
	private static final String[] AVIF_BRANDS = { "avif", "avis" };

	private final String contentType;
	private final String[] extensions;

	private ImageFormat(String contentType, String... extensions) {
		this.contentType = contentType;
		this.extensions = extensions;
	}

	public String getContentType() {
		return contentType;
	}

	public boolean isImage() {
		return this != UNKNOWN;
	}

	/**
	 * @return true si es un formato que ImageIO no sabe leer y por tanto no podemos pasar a PDF.
	 */
	public boolean needsCodec() {
		return this == HEIF || this == AVIF || this == WEBP;
	}

	/**
	 * Reconoce el formato por la firma de los primeros bytes.
	 *
	 * @param data bytes de la imagen.
	 * @return el formato, o {@link #UNKNOWN} si no lo reconocemos.
	 */
	public static ImageFormat of(byte[] data) {
		if (data == null || data.length < 12)
			return UNKNOWN;

		if (starts(data, 0xFF, 0xD8, 0xFF))
			return JPEG;
		if (starts(data, 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A))
			return PNG;
		if (ascii(data, 0, "GIF8"))
			return GIF;
		if (ascii(data, 0, "BM"))
			return BMP;
		if (starts(data, 0x49, 0x49, 0x2A, 0x00) || starts(data, 0x4D, 0x4D, 0x00, 0x2A))
			return TIFF;
		if (ascii(data, 0, "RIFF") && ascii(data, 8, "WEBP"))
			return WEBP;

		// HEIF y AVIF son contenedores ISO-BMFF: "....ftyp" y detras la marca del formato.
		if (ascii(data, 4, "ftyp")) {
			for (String brand : HEIF_BRANDS)
				if (ascii(data, 8, brand))
					return HEIF;
			for (String brand : AVIF_BRANDS)
				if (ascii(data, 8, brand))
					return AVIF;
		}
		return UNKNOWN;
	}

	/**
	 * Reconoce el formato por la extension del nombre o de la clave de S3.
	 *
	 * @param fileName nombre del fichero, con o sin ruta.
	 * @return el formato, o {@link #UNKNOWN} si la extension no nos dice nada.
	 */
	public static ImageFormat ofFileName(String fileName) {
		if (fileName == null)
			return UNKNOWN;

		int dot = fileName.lastIndexOf('.');
		if (dot < 0 || dot == fileName.length() - 1)
			return UNKNOWN;

		String extension = fileName.substring(dot + 1).toLowerCase();
		for (ImageFormat format : values())
			for (String candidate : format.extensions)
				if (candidate.equals(extension))
					return format;
		return UNKNOWN;
	}

	/**
	 * Reconoce el formato por el content type, ignorando los tipos genericos que no dicen nada
	 * ("binary/octet-stream" y similares).
	 */
	public static ImageFormat ofContentType(String contentType) {
		if (contentType == null)
			return UNKNOWN;

		String type = contentType.toLowerCase();
		int semicolon = type.indexOf(';');
		if (semicolon > 0)
			type = type.substring(0, semicolon);
		type = type.trim();

		if (!type.startsWith("image/"))
			return UNKNOWN;

		String subtype = type.substring("image/".length());
		if (subtype.endsWith("-sequence"))
			subtype = subtype.substring(0, subtype.length() - "-sequence".length());
		if (subtype.startsWith("x-") || subtype.startsWith("vnd."))
			subtype = subtype.substring(subtype.indexOf('-') + 1);

		for (ImageFormat format : values())
			for (String extension : format.extensions)
				if (extension.equals(subtype))
					return format;
		return UNKNOWN;
	}

	/**
	 * Reconoce el formato mirando primero los bytes, que es lo fiable, y si no dicen nada, la
	 * extension.
	 */
	public static ImageFormat of(byte[] data, String fileName) {
		ImageFormat format = of(data);
		return format.isImage() ? format : ofFileName(fileName);
	}

	private static boolean starts(byte[] data, int... signature) {
		if (data.length < signature.length)
			return false;
		for (int i = 0; i < signature.length; i++)
			if ((data[i] & 0xFF) != signature[i])
				return false;
		return true;
	}

	private static boolean ascii(byte[] data, int offset, String text) {
		if (data.length < offset + text.length())
			return false;
		for (int i = 0; i < text.length(); i++)
			if (data[offset + i] != text.charAt(i))
				return false;
		return true;
	}
}
