package com.esferalia.aon.in.payroll.img;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;

import solutions.aon.in.invoice.img.InvoiceIMGException;
import solutions.aon.in.invoice.pdf.InvoicePDFException;

public class DNIParser {

	// CONVIERTE IMAGEN DEL DNI A DOCUMENT USANDO LOS BYTES
	public static String extractImage(byte[] bytes) throws InvoiceIMGException {
		return extract(new Document().withBytes(ByteBuffer.wrap(bytes)));

	}

	// CONVIERTE PDF DEL DNI A DOCUMENT USANDO LOS BYTES
	public static String extractPdf(byte[] bytes) throws InvoicePDFException {
		return extract(new Document().withBytes(ByteBuffer.wrap(bytes)));

	}

	// UNA VEZ CONVERTIDO EL FORMATO DEL DNI LO PASA A TEXTO PLANO
	public static String extract(Document doc) {
		if (doc != null && doc.getBytes() != null
				&& (doc.getBytes().position() + doc.getBytes().remaining()) > (10 * 1024 * 1024)) {
			throw new InvoiceIMGException("Las imagenes a analizar no pueden superar los 5MB de tamaño");
		}

		AmazonTextract client = AmazonTextractClientBuilder.defaultClient();

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

	// FORMATO PARA DNI POSTERIOR A 2021 CUANDO SOLO ES PARTE DE ALANTE
	public static void getDataNewFormatDni(String text, DniDataListener listener) {
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

			}
		}

	}

	// FORMATO PARA DNI POSTERIOR A 2021 CUANDO SOLO ES PARTE DE ATRAS
	public static void getDataNewFormatDniBack(String text, DniDataListener listener) {
		String[] lineas = text.split("\n");
		String direccion = null;
		String localidad = null;
		String lugarNacimiento = null;
		String nombrePadre = null;
		String nombreMadre = null;

		System.out.println(text);

		for (int i = 0; i < lineas.length; i++) {
			String linea = lineas[i];

			if (linea.startsWith("DOMICILIO")) {
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
				nombreMadre = lineas[i + 3];
				listener.onNombreMadreData(nombreMadre);
			}

		}

	}

	// FORMATO PARA DNI POSTERIOR A 2021 CUANDO SOLO ES PARTE DE ATRAS Y EL
	// DOCUMENTO ES UN PDF
	public static void getDataNewFormatDniBackPdf(String text, DniDataListener listener) {
		String[] lineas = text.split("\n");
		String direccion = null;
		String localidad = null;
		String lugarNacimiento = null;
		String nombrePadre = null;
		String nombreMadre = null;

		for (int i = 0; i < lineas.length; i++) {
			String linea = lineas[i];

			if (linea.startsWith("DOMICILIO")) {
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

	// FORMATO PARA DNI POSTERIOR A 2021 Y VIENEN LAS DOS PARTES JUNTAS
	// Y EL DOCUMENTO ES UNA IMAGEN
	public static void getDataDniNewFormatBothSheet(String text, DniDataListener listener) {
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

	// FORMATO PARA DNI POSTERIOR A 2021 Y VIENEN LAS DOS PARTES JUNTAS EN PDF
	public static void getDniDataBothSheetFromPdf(String text, DniDataListener listener) {
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
		String nombrePadres = null;

//			System.out.println(text);

		for (int i = 0; i < lineas.length; i++) {
			String linea = lineas[i];
			if (linea.startsWith("DNI")) {
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

	// FORMATO PARA DNI ANTERIOR A 2021 CUANDO SOLO ES LA PARTE DE ALANTE
	public static void getDataOldFormatDni(String text, DniDataListener listener) {
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

	// FORMATO PARA DNI ANTERIOR A 2021 CUANDO SOLO ES LA PARTE DE ATRAS
	public static void getDataOldFormatDniBack(String text, DniDataListener listener) {
		String[] lineas = text.split("\n");
		String direccion = null;
		String localidad = null;
		String lugarNacimiento = null;
		String nombrePadres = null;

		for (int i = 0; i < lineas.length; i++) {
			String linea = lineas[i];
			if (linea.startsWith("DOMICILIO")) {
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

	private static boolean intersects(Block b1, Block b2) {
		float top1 = b1.getGeometry().getBoundingBox().getTop();
		float height1 = b1.getGeometry().getBoundingBox().getHeight();

		float top2 = b2.getGeometry().getBoundingBox().getTop();
		if (Math.abs(top2 - top1) <= height1 / 2.00) {
			return true;
		}
		return false;
	}

}
