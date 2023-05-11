package com.esferalia.aon.in.payroll.img;


import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.stream.Collectors;


import com.amazonaws.services.textract.AmazonTextract;
import com.amazonaws.services.textract.AmazonTextractClientBuilder;
import com.amazonaws.services.textract.model.Block;
import com.amazonaws.services.textract.model.DetectDocumentTextRequest;
import com.amazonaws.services.textract.model.DetectDocumentTextResult;
import com.amazonaws.services.textract.model.Document;

import solutions.aon.in.invoice.img.InvoiceIMGException;



public class DNIParser {

	public static String extract(byte [] bytes) throws InvoiceIMGException {
		return extract(new Document().withBytes(ByteBuffer.wrap(bytes))); 
		
	}
	
	public static String extract(Document doc) {
		if (doc != null && doc.getBytes() != null && (doc.getBytes().position() +doc.getBytes().remaining()) > (5*1024*1024)) {
			throw new InvoiceIMGException("Las imagenes a analizar no pueden superar los 5MB de tamaño");
		}
						
		AmazonTextract client = AmazonTextractClientBuilder.defaultClient();

        DetectDocumentTextRequest detectDocumentTextRequest =
				new DetectDocumentTextRequest().withDocument(doc);
		
		DetectDocumentTextResult detectDocumentTextResult = client.detectDocumentText(detectDocumentTextRequest);
			
		
		detectDocumentTextResult.getBlocks().stream()
		.filter(b -> b.getText() !=null)
		.filter(b -> b.getBlockType().equals("LINE")).forEach(b -> {
			
		});
		
		Block blocks[] = detectDocumentTextResult.getBlocks().stream()
				.filter(b -> b.getText() != null)
				.filter(b ->b.getBlockType().equals("LINE"))
				.toArray(Block[] :: new);
		
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
					line.setText(line.getText()+ " " + block.getText() );
				else if(lines.equals(line))
					lines.add(block);
				
				text = lines.stream().map(Block::getText).collect(Collectors.joining("\r\n"));
			}
			
		}
		return text;
	}
	
	//FORMAT FOR DNI OF 2021 ONWARDS
	public static Object[] getDataNewFormatDni(String text) {

		String [] lineas = text.split("\n");
		String dni = null;
		String nombre = null;
		String apellido1= null;
		String apellido2 = null;
		String fechaNacimiento = null;
		Date fechaNac= null;
		String sexo = null;
		String nacionalidad = null;
		
		for(int i = 0; i < lineas.length; i++) {
			String linea = lineas[i];
			if (linea.startsWith("DOCUMENTO NACIONAL DE IDENTIDAD")) {
				dni = lineas[i+1];
			}else if (linea.startsWith("APELLIDOS")) {
				apellido1 = lineas[i+1];
				apellido2 = lineas[i+2];
			}else if(linea.startsWith("NOMBRE")){
				nombre = lineas[i+1];
			}else if(linea.startsWith("SEXO")) {
				sexo = lineas[i+3];
			}else if(linea.startsWith("NACIONALIDAD")) {
				nacionalidad = lineas[i+3];
			}else if(linea.startsWith("NACIMIENTO")) {
				fechaNacimiento = lineas[i+3];
				
			}
		}
		
		 Object[] data = new Object[7];
		    data[0] = apellido1;
		    data[1] = apellido2;
		    data[2] = nombre;
		    data[3] = sexo;
		    data[4] = nacionalidad;
		    data[5] = dni;
		    
		    SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
			try {
				fechaNac = format.parse(fechaNacimiento);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			data[6] = fechaNac;
			
		
		    for (int i = 0; i < data.length; i++) {
		    System.out.println(data[i]);

		}
			return data;
		
		
	}
	
	//FORMAT FOR PRE2021 DNI 
	public static Object[] getDataOldFormatDni(String text) {
	    String [] lineas = text.split("\n");
	    String dni = null;
	    String nombre = null;
	    String apellido1= null;
	    String apellido2 = null;
	    String fechaNacimiento = null;
	    Date fechaNac= null;
	    String sexo = null;
	    String nacionalidad = null;

	    for(int i = 0; i < lineas.length; i++) {
	        String linea = lineas[i];

	        if (linea.startsWith("APELLIDOS")) {
	            apellido1 = lineas[i+1];
	            apellido2 = lineas[i+2];
	        } else if(linea.startsWith("NOMBRE")) {
	            nombre = lineas[i+1];
	        } else if(linea.startsWith("SEXO")) {
	            sexo = lineas[i+2];
	        } else if(linea.startsWith("NACIONALIDAD")) {
	            nacionalidad = lineas[i+2];
	        } else if (linea.startsWith("FECHA DE NACIMIENTO")) {
	            fechaNacimiento = lineas[i+1];
	        } else if (linea.startsWith("DNI")) {
	            dni = lineas[i];
	        }
	    }

	    Object[] data = new Object[7]; 
	    data[0] = apellido1;
	    data[1] = apellido2;
	    data[2] = nombre;
	    data[3] = sexo;
	    data[4] = nacionalidad;
	    data[5] = dni;

	    SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy");
	    try {
	        fechaNac = formatter.parse(fechaNacimiento);
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }

	    data[6] = fechaNac;
	    for (int i = 0; i < data.length; i++) {
	    System.out.println(data[i]);

	}
	    return data;
	}
	
	
	
	private static boolean intersects(Block b1, Block b2) {
		float top1 = b1.getGeometry().getBoundingBox().getTop();
		float height1 = b1.getGeometry().getBoundingBox().getHeight();
		
		float top2 = b2.getGeometry().getBoundingBox().getTop();
		if (Math.abs(top2 - top1) <= height1 /2.00) {
			return true;
		}
		return false;
	}
	



}
