package com.esferalia.aon.in.payroll.img;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
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

	private static String text;

	// CONVIERTE IMAGEN DEL DNI A DOCUMENT USANDO BYTES
	public String extractImage(byte[] bytes) {
		DNIParserValidation.validateBytes(bytes);
		return extract(new Document().withBytes(ByteBuffer.wrap(bytes)));
	}

	// RECOGE UN INPUTSTREAM Y LO TRANSFORMA EN PDDocument

	public void parse(InputStream is) throws IOException {
		DNIParserValidation.validateInputstream(is);
		try (PDDocument doc = Loader.loadPDF(is)) {
			parser(doc);
		}
	}

	public void parse(List<InputStream> inputStreams) throws IOException {
		DNIParserValidation.validateInputStreamList(inputStreams);
		for (InputStream is : inputStreams) {
			try (PDDocument doc = Loader.loadPDF(is)) {
				parser(doc);
			}
		}
	}

	// RECOGE LAS IMAGENES DEL PDF Y LAS ALMACENA EN BYTE[]
	protected static Collection<byte[]> getImages(PDDocument document) throws IOException {
		DNIParserValidation.validatePDDoc(document);
		LinkedList<byte[]> images = new LinkedList<>();
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

	protected String parser(PDDocument doc) throws IOException {
		DNIParserValidation.validatePDDoc(doc);
		AccessPermission ap = doc.getCurrentAccessPermission();

		if (!ap.canExtractContent()) {
			// CREAR NUEVA EXCEPCION
			throw new ImgDNIException("You do not have permission to extract text");
		}
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setSortByPosition(true);
		String extractedText = stripper.getText(doc);
		if (isBlank(extractedText)) {
			extractedText = getImages(doc).stream().map(this::extractImage)
					.collect(Collectors.joining(System.lineSeparator()));
		}
		DNIParser.setText(extractedText);
		return DNIParser.getText();
	}

	// UNA VEZ CONVERTIDO EL FORMATO DEL DNI LO PASA A TEXTO PLANO
	protected String extract(Document doc) {

		AmazonTextract client = AmazonTextractClientBuilder.defaultClient();

		DNIParserValidation.validateDoc(doc);

		DetectDocumentTextRequest detectDocumentTextRequest = new DetectDocumentTextRequest().withDocument(doc);

		DetectDocumentTextResult detectDocumentTextResult = client.detectDocumentText(detectDocumentTextRequest);

		detectDocumentTextResult.getBlocks().stream().filter(b -> b.getText() != null)
				.filter(b -> b.getBlockType().equals("LINE")).forEach(b -> {

				});

		Block[] blocks = detectDocumentTextResult.getBlocks().stream().filter(b -> b.getText() != null)
				.filter(b -> b.getBlockType().equals("LINE")).toArray(Block[]::new);

		String extract = null;

		if (blocks != null && blocks.length > 0) {
			LinkedList<Block> lines = new LinkedList<>();
			for (int i = 0; i < blocks.length; i++) {
				lines.addAll(Arrays.asList(blocks));
			}
			for (int i = 1; i < blocks.length; i++) {
				Block block = blocks[i];
				Block line = lines.peekLast();
				if (intersects(line, block))
					line.setText(line.getText() + " " + block.getText());
				else
					lines.add(block);
				extract = lines.stream().map(Block::getText).collect(Collectors.joining("\r\n"));
			}

		}
		return extract;
	}
	
	public List<String> getDNIData(String text) {
	    String[] lines = text.split("\n");
	    List<String> result = new ArrayList<>();

	    String dni = extractDNI(lines);
	    String apellido1 = extractApellido1(lines);
	    String apellido2 = extractApellido2(lines);
	    String nombre = extractNombre(lines);
	    String nacionalidad = extractNacionalidad(lines);

	    result.add(dni);
	    result.add(apellido1);
	    result.add(apellido2);
	    result.add(nombre);
	    result.add(nacionalidad);

	    return result;
	}

	private String extractDNI(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("DNI") || lines[i].startsWith("DOCUMENTO NACIONAL DE IDENTIDAD")) {
				String dni = lines[i + 1];
				if (validateDni(dni)) {
					return dni;
				}
			}
		}
		return "";
	}
	
	private String extractApellido1(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("APELLIDOS") || lines[i].startsWith("APALLIDOS")) {
				String apellido1 = lines[i + 1];
				if (validateNames(apellido1)) {
					return apellido1;
				}
			}
		}
		return "";
	}

	private String extractApellido2(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("APELLIDOS") || lines[i].startsWith("APALLIDOS")) {
				String apellido2 = lines[i + 2];
				if (validateNames(apellido2)) {
					return apellido2;
				}
			}
		}
		return "";
	}

	private String extractNombre(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("NOMBRE") || lines[i].startsWith("NONBRE")) {
				String nombre = lines[i + 1];
				if (validateNames(nombre)) {
					return nombre;
				}
			}
		}
		return "";	}

	private String extractNacionalidad(String[] lines) {
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].startsWith("NACIONALIDAD")) {
				String nacionalidad = lines[i + 1];
				if (validateNationality(nacionalidad)) {
					return nacionalidad;
				}
			}
		}
		return "";	}
	
	

	private boolean validateNames(String name) {
		name = name.trim();
		String patternName = "[a-zA-Z\\s]+";
		Pattern pattern = Pattern.compile(patternName);
		Matcher matcher = pattern.matcher(name);
		return matcher.matches();
	}

	private boolean validateDni(String dni) {
		dni = dni.trim();
		String patternDni = "\\d{8}[A-HJ-NP-TV-Z]";
		Pattern pattern = Pattern.compile(patternDni);
		Matcher matcher = pattern.matcher(dni);
		return matcher.matches();
	}

	private boolean validateNationality(String nacionalidad) {
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

		return (Math.abs(top2 - top1) <= height1 / 2.00);

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
