package com.esferalia.aon.in.payroll.img;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
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
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.type.Country;

public class DNIParser {

	private String text;

	// CONVIERTE IMAGEN DEL DNI A DOCUMENT USANDO BYTES
	private String extractImage(byte[] bytes) {
		DNIParserValidation.validateBytes(bytes);
		return extract(new Document().withBytes(ByteBuffer.wrap(bytes)));
	}

	// RECOGE UN INPUTSTREAM Y LO TRANSFORMA EN PDDocument

	private void parse(InputStream is) throws IOException {
		DNIParserValidation.validateInputstream(is);
		try (PDDocument doc = Loader.loadPDF(is)) {
			parser(doc);
		}
	}
	
	public String parsePublic(InputStream is) throws IOException{
		parse(is);
		return getText();
		
	}

	// RECOGE LAS IMAGENES DEL PDF Y LAS ALMACENA EN BYTE[]
	public static Collection<byte[]> getImages(PDDocument document) throws IOException {
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
			throw new PersonDocumentExtractException("You do not have permission to extract text");
		}
		PDFTextStripper stripper = new PDFTextStripper();
		stripper.setSortByPosition(true);
		String extractedText = stripper.getText(doc);
		if (isBlank(extractedText)) {
			extractedText = getImages(doc).stream().map(this::extractImage)
					.collect(Collectors.joining(System.lineSeparator()));
		}
		setText(extractedText);
		return getText();
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

	private Person getDniDataPerson(String text) {
		Person person = new Person();
		
		String dni = getDocumentDNI(text);
		String name = getDocumentName(text);
		String[] surnames = getDocumentSurnames(text);
		String surName1 = surnames[0];
		String surName2 = surnames[1];
		Country country = getNationality(text);

		person.setDocument(dni);
		person.setFirstSurname(surName1);
		person.setSecondSurname(surName2);
		person.setName(name);
		person.setNationality(country);
		
		return person;
	}
	
	
	public Person getDniPerson(String text) {
		return getDniDataPerson(text);
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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDocumentDNI(String text) {
		String document = "";
		String documentDNIRegex = "^[0-9]{8}[A-HJ-NP-TV-Z]$";
		Pattern pattern = Pattern.compile(documentDNIRegex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			document = matcher.group();
		}
		
		return document;
	}
	
	//sin usar de momento
//	private String getDocumentCIF(String text) {
//		String document = "";
//		String documentCIFRegex = "^[A-HJ-NP-TV-Z][0-9]{8}$";
//		Pattern pattern = Pattern.compile(documentCIFRegex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		if (matcher.find()) {
//			document = matcher.group();
//		}
//		
//		return document;
//	}
//	
//	private String getForeignDocument(String text) {
//		String document = "";
//		String documentForeignRegex = "^[A-HJ-NP-TV-Z][0-9]{7}[A-HJ-NP-TV-Z]$";
//		Pattern pattern = Pattern.compile(documentForeignRegex, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
//		Matcher matcher = pattern.matcher(text);
//		if (matcher.find()) {
//			document = matcher.group();
//		}
//		
//		return document;
//	}
	
	private Country getNationality(String text) {
	    String nationalityRegex = "\\b[A-Z][A-Z]+\\b";
	    Pattern pattern = Pattern.compile(nationalityRegex);
	    Matcher matcher = pattern.matcher(text);

	    while (matcher.find()) {
	        String potentialNationality = matcher.group();
	        for (Country country : Country.values()) {
	            if (country.getIso3().equals(potentialNationality)) {
	                return country;
	            }
	        }
	    }
	    return null; 
	}
	

	
	private String getDocumentName(String text) {
	    String name = null;
	    Pattern pattern = Pattern.compile("DNI[\\s\\S]*?(NOMBRE|NONBRE)\\s+([^\\n]+)", Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(text);
	    if (matcher.find()) {
	        name = matcher.group(2).trim();
	    }
	    return name;
	}
	
	private String[] getDocumentSurnames(String text) {
		  String[] apellidos = new String[2];
		    Pattern pattern = Pattern.compile("DNI[\\s\\S]*?(APELLIDOS|APALLIDOS)\\s+([^\\n]+)\\s+([^\\n]+)", Pattern.CASE_INSENSITIVE);
		    Matcher matcher = pattern.matcher(text);
		    
		    if (matcher.find()) {
		        apellidos[0] = matcher.group(2).trim();
		        apellidos[1] = matcher.group(3).trim();
		    }
		    return apellidos;
	}
}
