package com.esferalia.aon.in.payroll.img;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;


public class DNIParser {

	public static String text;
	public static String dni;
	public static String nombre;
	public static String apellido1;
	public static String apellido2;
	public static String nacionalidad;

	// CONVIERTE IMAGEN DEL DNI A DOCUMENT USANDO BYTES
	public static String extractImage(byte[] bytes) {
		DniParserValidation.validateBytes(bytes);
		return extract(new Document().withBytes(ByteBuffer.wrap(bytes)));

	}

	// RECOGE UN INPUTSTREAM Y LO TRANSFORMA EN PDDocument

	public static void parse(InputStream is) throws IOException {
		DniParserValidation.validateInputstream(is);
		try (PDDocument doc = Loader.loadPDF(is)) {
			parser(doc);
		}
	}

	public void parse(List<InputStream> inputStreams) throws IOException {
		DniParserValidation.validateInputStreamList(inputStreams);
		for (InputStream is : inputStreams) {
			try (PDDocument doc = Loader.loadPDF(is)) {
				parser(doc);
			}
		}
	}

	// RECOGE LAS IMAGENES DEL PDF Y LAS ALMACENA EN BYTE[]
	public static Collection<byte[]> getImages(PDDocument document) throws IOException {
		DniParserValidation.validatePDDoc(document);
		LinkedList<byte[]> images = new LinkedList<byte[]>();
		for (PDPage page : document.getPages()) {
			PDResources pdResources = page.getResources();
			for (COSName name : pdResources.getXObjectNames()) {
				PDXObject o = pdResources.getXObject(name);
				if (o instanceof PDImageXObject) {
					PDImageXObject image = (PDImageXObject) o;
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					ImageIO.write(image.getImage(), "PNG", byteArrayOutputStream);
					images.add(byteArrayOutputStream.toByteArray());
				}
			}
		}
		return images;
	}

	public static String parser(PDDocument doc) throws IOException {
		DniParserValidation.validatePDDoc(doc);
		AccessPermission ap = doc.getCurrentAccessPermission();

		if (!ap.canExtractContent()) {
			//CREAR NUEVA EXCEPCION
			throw new ImgDniException("You do not have permission to extract text");
		}
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setSortByPosition(true);
		String extractedText = stripper.getText(doc);
		if (isBlank(extractedText)) {
			extractedText = getImages(doc).stream().map(img -> DNIParser.extractImage(img))
					.collect(Collectors.joining(System.lineSeparator()));
		}
		DNIParser.setText(extractedText);
		return DNIParser.getText();
	}

	// UNA VEZ CONVERTIDO EL FORMATO DEL DNI LO PASA A TEXTO PLANO
	public static String extract(Document doc) {

		AmazonTextract client = AmazonTextractClientBuilder.defaultClient();

		DniParserValidation.validateDoc(doc);

		DetectDocumentTextRequest detectDocumentTextRequest = new DetectDocumentTextRequest().withDocument(doc);

		DetectDocumentTextResult detectDocumentTextResult = client.detectDocumentText(detectDocumentTextRequest);
		
		detectDocumentTextResult.getBlocks().stream().filter(b -> b.getText() != null)
				.filter(b -> b.getBlockType().equals("LINE")).forEach(b -> {

				});

		Block blocks[] = detectDocumentTextResult.getBlocks().stream().filter(b -> b.getText() != null)
				.filter(b -> b.getBlockType().equals("LINE")).toArray(Block[]::new);

		String text = null;

		if (blocks != null && blocks.length > 0) {
			LinkedList<Block> lines = new LinkedList<>();
			for (int i = 0; i < blocks.length; i++) {
				lines.add(blocks[i]);
			}
			for (int i = 1; i < blocks.length; i++) {
				Block block = blocks[i];
				Block line = lines.peekLast();
				if (intersects(line, block))
					line.setText(line.getText() + " " + block.getText());
				else if (lines.equals(line))
					lines.add(block);
				text = lines.stream().map(Block::getText).collect(Collectors.joining("\r\n"));
			}

		}
		return text;
	}

	public static void getNewDniBothPdf(String text, DniDataListener listener) {
		DniParserValidation.validateText(text);
		String[] lineas = text.split("\n");
		
		for (int i = 0; i < lineas.length; i++) {
			String linea = lineas[i];
			if (linea.startsWith("DNI") || linea.startsWith("DOCUMENTO NACIONAL DE IDENTIDAD")) {
				dni = lineas[i + 1];
				if (!DNIParser.validateDni(dni)) {
					dni = "";
				}
				listener.onDniData(dni);
				
			} else if (linea.startsWith("APELLIDOS") || linea.startsWith("APALLIDOS")) {
				apellido1 = lineas[i + 1];
				if (!DNIParser.validateNames(apellido1)) {
					apellido1 = "";
				}
				apellido2 = lineas[i + 2];
				if (!DNIParser.validateNames(apellido2)) {
					apellido2 = "";
				}
				listener.onApellidosData(apellido1, apellido2);
			} else if (linea.startsWith("NOMBRE") || linea.startsWith("NONBRE")) {
				nombre = lineas[i + 1];
				if (!DNIParser.validateNames(nombre)) {
					nombre = "";
				}
				listener.onNombreData(nombre);
			} else if (linea.startsWith("NACIONALIDAD")) {
				nacionalidad = lineas[i + 3];
				if (!DNIParser.validateNationality(nacionalidad)) {
					nacionalidad = "";
				}
				listener.onNacionalidadData(nacionalidad);
				if (nacionalidad.equals("ESP")) {
					nacionalidad = "ESPAÑA";
				}

			}
		}

	}

	public static boolean validateNames(String name) {
		name = name.trim();
		String patternName = "[a-zA-Z\\s]+";
		Pattern pattern = Pattern.compile(patternName);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}

	public static boolean validateDni(String dni) {
		dni = dni.trim();
		String patternDni = "\\d{8}[A-HJ-NP-TV-Z]";
		Pattern pattern = Pattern.compile(patternDni);
		Matcher matcher = pattern.matcher(dni);
		return matcher.matches();
	}

	public static boolean validateNationality(String nacionalidad) {
		nacionalidad = nacionalidad.trim();
		String patternNat = "[A-Z]{3}";
		Pattern pattern = Pattern.compile(patternNat);
		Matcher matcher = pattern.matcher(nacionalidad);
		return matcher.matches();
	}

	private static boolean intersects(Block b1, Block b2) {
		float top1 = b1.getGeometry().getBoundingBox().getTop();
		float height1 = b1.getGeometry().getBoundingBox().getHeight();

		float top2 = b2.getGeometry().getBoundingBox().getTop();
		if (Math.abs(top2 - top1) <= height1 / 2.00) {
			return true;
		}
		return false;
	}

	private static boolean isBlank(String cs) {
		if (cs == null || (cs.length()) == 0) {
			return true;
		}
		return cs.trim().length() == 0;
	}

	public static String getText() {
		return text;
	}

	public static void setText(String text) {
		DNIParser.text = text;
	}

}
