package com.esferalia.aon.in.payroll.img;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
	public  String dni;
	public  String nombre;
	public  String apellido1;
	public  String apellido2;
	public  String nacionalidad;
	public  String sexo;

	// CONVIERTE IMAGEN DEL DNI A DOCUMENT USANDO BYTES
	public  String extractImage(byte[] bytes) throws InvoiceIMGException {
		return extract(new Document().withBytes(ByteBuffer.wrap(bytes)));

	}

	// RECOGE UN INPUTSTREAM Y LO TRANSFORMA EN PDDocument
	public void parse(InputStream is) throws IOException {
		try (PDDocument doc = Loader.loadPDF(is)) {
			parser(doc);
		} 
	}
	
	public void parse(List<InputStream> inputStreams) throws IOException {
	    for (InputStream is : inputStreams) {
	        try (PDDocument doc = Loader.loadPDF(is)) {
	            parser(doc);
	        }
	    }
	}

	// RECOGE LAS IMAGENES DEL PDF Y LAS ALMACENA EN BYTE[]
	public static Collection<byte[]> getImages(PDDocument document) throws IOException {
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

	
	public static String extract(Document doc) {
	    if (doc != null && doc.getBytes() != null && (doc.getBytes().position() + doc.getBytes().remaining()) > (20 * 1024 * 1024)) {
	        throw new InvoiceIMGException("Las imagenes a analizar no pueden superar los 5MB de tamaño");
	    }

	    AmazonTextract client = AmazonTextractClientBuilder.defaultClient();

	    DetectDocumentTextRequest detectDocumentTextRequest = new DetectDocumentTextRequest().withDocument(doc);

	    DetectDocumentTextResult detectDocumentTextResult = client.detectDocumentText(detectDocumentTextRequest);

	    Block blocks[] = detectDocumentTextResult.getBlocks().stream()
	            .filter(b -> b.getText() != null && b.getBlockType().equals("LINE"))
	            .toArray(Block[]::new);

	    StringBuilder sb = new StringBuilder();

	    for (Block block : blocks) {
	        sb.append(block.getText()).append(System.lineSeparator());
	    }

	    return sb.toString();
	}
	
//	public static void getNewDniBothPdf(String text, DniDataListener listener) {
//	    System.out.println(text);
//
//	    String dniPattern = "\\b\\d{8}[A-HJ-NP-TV-Z]\\b";
//	    Pattern dniPatternRegex = Pattern.compile(dniPattern);
//	    Matcher dniMatcher = dniPatternRegex.matcher(text);
//
//	    String dniValido = null;
//	    String nombre = null;
//	    String apellido1 = null;
//	    String apellido2 = null;
//	    String sexo = null;
//	    String nacionalidad = null;
//	    Date fechaNacimiento = null;
//
//	    while (dniMatcher.find()) {
//	        String dni = dniMatcher.group();
//	        if (validateDni(dni)) {
//	            if (dniValido == null || dniValido.compareTo(dni) < 0) {
//	                dniValido = dni;
//	            }
//	        }
//	    }
//
//	    // Extracción del nombre
//	    String nombrePattern = "NOMBRE: ([A-Za-z ]+)";
//	    Pattern nombrePatternRegex = Pattern.compile(nombrePattern);
//	    Matcher nombreMatcher = nombrePatternRegex.matcher(text);
//
//	    if (nombreMatcher.find()) {
//	        nombre = nombreMatcher.group(1);
//	    }
//
//	    // Extracción de los apellidos
//	    String apellidosPattern = "APELLIDOS: ([A-Za-z ]+)";
//	    Pattern apellidosPatternRegex = Pattern.compile(apellidosPattern);
//	    Matcher apellidosMatcher = apellidosPatternRegex.matcher(text);
//
//	    if (apellidosMatcher.find()) {
//	        String apellidos = apellidosMatcher.group(1);
//	        String[] partesApellidos = apellidos.split(" ");
//	        apellido1 = partesApellidos[0];
//	        apellido2 = partesApellidos[1];
//	    }
//
//	    // Extracción del sexo
//	    String sexoPattern = "SEXO ([MF])";
//	    Pattern sexoPatternRegex = Pattern.compile(sexoPattern);
//	    Matcher sexoMatcher = sexoPatternRegex.matcher(text);
//
//	    if (sexoMatcher.find()) {
//	        sexo = sexoMatcher.group(1);
//	    }
//
//	    // Extracción de la nacionalidad
//	    String nacionalidadPattern = "NACIONALIDAD ([A-Za-z ]+)";
//	    Pattern nacionalidadPatternRegex = Pattern.compile(nacionalidadPattern);
//	    Matcher nacionalidadMatcher = nacionalidadPatternRegex.matcher(text);
//
//	    if (nacionalidadMatcher.find()) {
//	        nacionalidad = nacionalidadMatcher.group(1);
//	    }
//
//	    // Extracción de la fecha de nacimiento
//	    String fechaNacimientoPattern = "\\b\\d{2} \\d{2} \\d{4}\\b";
//	    Pattern fechaNacimientoPatternRegex = Pattern.compile(fechaNacimientoPattern);
//	    Matcher fechaNacimientoMatcher = fechaNacimientoPatternRegex.matcher(text);
//
//	    if (fechaNacimientoMatcher.find()) {
//	        String fechaNacimientoStr = fechaNacimientoMatcher.group();
//	        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
//	        try {
//	            fechaNacimiento = format.parse(fechaNacimientoStr);
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//	    }
//
//	    // Envío de los datos al listener
//	    listener.onDniData(dniValido);
//	    listener.onNombreData(nombre);
//	    listener.onApellidosData(apellido1, apellido2);
//	    listener.onSexoData(sexo);
//	    listener.onNacionalidadData(nacionalidad);
//	    listener.onFechaNacimientoData(fechaNacimiento);
//
//	    // Resto del código para extraer los demás datos...
//	}


	// UNA VEZ CONVERTIDO EL FORMATO DEL DNI LO PASA A TEXTO PLANO
//	public static String extract(Document doc) {
//		if (doc != null && doc.getBytes() != null
//				&& (doc.getBytes().position() + doc.getBytes().remaining()) > (20 * 1024 * 1024)) {
//			throw new InvoiceIMGException("Las imagenes a analizar no pueden superar los 5MB de tamaño");
//		}
//
//		AmazonTextract client = AmazonTextractClientBuilder.defaultClient();
//
//		DetectDocumentTextRequest detectDocumentTextRequest = new DetectDocumentTextRequest().withDocument(doc);
//
//		DetectDocumentTextResult detectDocumentTextResult = client.detectDocumentText(detectDocumentTextRequest);
//
//		detectDocumentTextResult.getBlocks().stream().filter(b -> b.getText() != null)
//				.filter(b -> b.getBlockType().equals("LINE")).forEach(b -> {
//
//				});
//
//		Block blocks[] = detectDocumentTextResult.getBlocks().stream().filter(b -> b.getText() != null)
//				.filter(b -> b.getBlockType().equals("LINE")).toArray(Block[]::new);
//
//		String text = null;
//
//		if (blocks != null && blocks.length > 0) {
//			LinkedList<Block> lines = new LinkedList<Block>();
//			for (int i = 0; i < blocks.length; i++) {
//				lines.add(blocks[i]);
//			}
//			for (int i = 1; i < blocks.length; i++) {
//				Block block = blocks[i];
//				Block line = lines.peekLast();
//				if (intersects(line, block))
//					line.setText(line.getText() + " " + block.getText());
//				else if (lines.equals(line))
//					lines.add(block);
//
//				text = lines.stream().map(Block::getText).collect(Collectors.joining("\r\n"));
//			}
//
//		}
//		return text;
//	}



	// FORMATO PARA DNI POSTERIOR A 2021 VIENEN LAS DOS PARTES JUNTAS
	// Y EL DOCUMENTO ES UNA IMAGEN
	public static void getNewDniBothJpg(String text, DniDataListener listener) {
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
	public void getNewDniBothPdf(String text, DniDataListener listener) {
		System.out.println(text);
		DNIParser dnip = new DNIParser();
		
		String[] lineas = text.split("\n");
		String fechaNacimiento = null;
		Date fechaNac = null;
		String sexo = null;
		String direccion = null;
		String localidad = null;
		String lugarNacimiento = null;
		String nombrePadres = null;
		String dni = "";
		String nombre = null;
		String apellido1 = null;
		String apellido2 = null;
		String nacionalidad = "";
		
		for (int i = 0; i < lineas.length; i++) {
			String linea = lineas[i];
			if (linea.startsWith("DNI")) {
				dni = lineas[i + 1];
				  if (!validateDni(dni)) {
		                dni = "";	  						// El DNI no es válido, continuar buscando
		                continue;
		            }
				  
				listener.onDniData(dni);
				dnip.setDni(dni);
			} else if (linea.startsWith("APELLIDOS")) {
				apellido1 = lineas[i + 1];
				apellido2 = lineas[i + 2];
				listener.onApellidosData(apellido1, apellido2);
			} else if (linea.startsWith("NOMBRE")) {
				nombre = lineas[i + 1];
				listener.onNombreData(nombre);
			} else if (linea.startsWith("SEXO")) {
				sexo = lineas[i + 3];
				listener.onSexoData(sexo);
			} else if (linea.startsWith("NACIONALIDAD")) {
				nacionalidad = lineas[i + 3];
				listener.onNacionalidadData(nacionalidad);
			} else if (linea.startsWith("NACIMIENTO")) {
				fechaNacimiento = lineas[i + 3];
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
				nombrePadres = lineas[i + 1];
				String[] aux = nombrePadres.split("/");
				String part1 = aux[0];
				String part2 = aux[1];
				listener.onNombrePadreData(part1);
				listener.onNombreMadreData(part2);
			}

		}

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
		DNIParser.text = text;
	}
	
	public void setDni(String dni) {
		this.dni = dni;
	}

    public String getDni() {
        return dni;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }

    public String getApellido2() {
        return apellido2;
    }
    
    public void setSexo(String sexo) {
    	this.sexo = sexo;
    }
    
    public String getSexo() {
    	return sexo;
    }
}
