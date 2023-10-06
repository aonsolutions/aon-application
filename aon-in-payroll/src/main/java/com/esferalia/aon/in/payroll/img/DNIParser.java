package com.esferalia.aon.in.payroll.img;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
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

import solutions.aon.in.invoice.img.InvoiceIMGException;
import solutions.aon.in.invoice.pdf.InvoicePDFException;

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
	    DNIParser dnip = new DNIParser();

	    if (!ap.canExtractContent()) {
	        throw new InvoicePDFException("You do not have permission to extract text");
	    }
	    PDFTextStripper stripper = new PDFTextStripper();
	    stripper.setSortByPosition(true);
	    String extractedText = stripper.getText(doc);
	    if (isBlank(extractedText)) {
	        extractedText = getImages(doc).stream().map(img -> dnip.extractImage(img)).collect(Collectors.joining(System.lineSeparator()));
	    }
	    dnip.setText( extractedText);
	    return dnip.getText();
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
			LinkedList<Block> lines = new LinkedList<Block>();
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

	// FORMATO PARA DNI POSTERIOR A 2021 VIENEN LAS DOS PARTES JUNTAS
	// Y EL DOCUMENTO ES UNA IMAGEN
	public static void getNewDniBothJpg(String text, DniDataListener listener) {
		DniParserValidation.validateText(text);
		String[] lineas = text.split("\n");
		String dni = null;
		String nombre = null;
		String apellido1 = null;
		String apellido2 = null;
		String fechaNacimiento = null;
		Date fechaNac = null;
		String sexo = null;
		String nacionalidad = null;
		String direccion = null;
		String localidad = null;
		String lugarNacimiento = null;
		String nombrePadre = null;
		String nombreMadre = null;

		for (int i = 0; i < lineas.length; i++) {
			String linea = lineas[i];
			if (linea.startsWith("DOCUMENTO NACIONAL DE IDENTIDAD")) {
				dni = lineas[i + 1];
				listener.onDniData(dni);
			} else if (linea.startsWith("APELLIDOS")) {
				apellido1 = lineas[i + 1];
				apellido2 = lineas[i + 2];
				listener.onApellidosData(apellido1, apellido2);
			} else if (linea.startsWith("NOMBRE")) {
				nombre = lineas[i + 1];
				listener.onNombreData(nombre);
			} else if (linea.startsWith("SEXO")) {
				sexo = lineas[i + 1];
				listener.onSexoData(sexo);
			} else if (linea.startsWith("NACIONALIDAD")) {
				nacionalidad = lineas[i + 4];
				listener.onNacionalidadData(nacionalidad);
			} else if (linea.startsWith("NACIMIENTO")) {
				fechaNacimiento = lineas[i + 4];
				SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
				try {
					fechaNac = format.parse(fechaNacimiento);
					listener.onFechaNacimientoData(fechaNac);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (linea.startsWith("DOMICILIO")) {
				direccion = lineas[i + 1];
				listener.onDireccionData(direccion);
				localidad = lineas[i + 2];
				listener.onLocalidadData(localidad);
			} else if (linea.startsWith("LUGAR DE NACIMIENTO")) {
				lugarNacimiento = lineas[i + 1];
				listener.onLugarNacimientoData(lugarNacimiento);
			} else if (linea.startsWith("HIJO/A DE")) {
				nombrePadre = lineas[i + 1];
				listener.onNombrePadreData(nombrePadre);
				nombreMadre = lineas[i + 2];
				listener.onNombreMadreData(nombreMadre);
			}

		}
	}

	
	public boolean validateDni(String dni) {
		dni = dni.trim();
        String patternDni = "\\d{8}[A-HJ-NP-TV-Z]";		
        Pattern pattern = Pattern.compile(patternDni);
        Matcher matcher = pattern.matcher(dni);
		return matcher.matches();
	}
	
	
	public boolean validateNationality(String nacionalidad) { 
		nacionalidad = nacionalidad.trim();
		String patternNat = "[A-Z]{3}";
		Pattern pattern = Pattern.compile(patternNat);
		Matcher matcher = pattern.matcher(nacionalidad);
		return matcher.matches();
	}
	
//	public boolean validateNames(String nombre) {
//		nombre = nombre.trim();
//		String patternNames ="[A-Z]";
//		Pattern pattern = Pattern.compile(patternNames);
//		Matcher matcher = pattern.matcher(nombre);
//		return matcher.matches();
//	}

	// FORMATO PARA DNI POSTERIOR A 2021 Y VIENEN LAS DOS PARTES JUNTAS EN PDF
	public static void getNewDniBothPdf(String text, DniDataListener listener) {
		DniParserValidation.validateText(text);
		DNIParser dnip = new DNIParser();
		String[] lineas = text.split("\n");
		String dni = null;
		String nombre = null;
		String apellido1 = null;
		String apellido2 = null;
		String fechaNacimiento = null;
		Date fechaNac = null;
		String sexo = null;
		String nacionalidad = null;

		for (int i = 0; i < lineas.length; i++) {
			String linea = lineas[i];

			if (linea.startsWith("APELLIDOS")) {
				apellido1 = lineas[i + 1];
				apellido2 = lineas[i + 2];
				listener.onApellidosData(apellido1, apellido2);
			} else if (linea.startsWith("NOMBRE")) {
				nombre = lineas[i + 1];
				listener.onNombreData(nombre);
			} else if (linea.startsWith("SEXO")) {
				sexo = lineas[i + 2];
				listener.onSexoData(sexo);
			} else if (linea.startsWith("NACIONALIDAD")) {
				nacionalidad = lineas[i + 2];
				listener.onNacionalidadData(nacionalidad);
			} else if (linea.startsWith("FECHA DE NACIMIENTO")) {
				fechaNacimiento = lineas[i + 1];
				SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
				try {
					fechaNac = format.parse(fechaNacimiento);
					listener.onFechaNacimientoData(fechaNac);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (linea.startsWith("DNI")) {
				dni = lineas[i];
				listener.onDniData(dni);

			}
		}
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

	public void setText(String text) {
		DNIParser.text = text;
	}

}
