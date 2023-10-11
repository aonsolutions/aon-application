package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

import com.amazonaws.services.textract.model.Document;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class DNIParserTest {
	DNIParser dniParser = new DNIParser(); 
	
	@Test
	public void extractImageNullTest() {
		byte [] bytes = null;
		AonCoreException e = assertThrows(AonCoreException.class, 
				() ->DNIParser.extractImage(bytes));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}
	
	@Test
	public void parseNullTest() {
		InputStream is = null;
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> DNIParser.parse(is));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	} 
	
	@Test
	public void parseNullListTest() {
		List<InputStream> inputStreams = null;
			AonCoreException e = assertThrows(AonCoreException.class,
					()-> dniParser.parse(inputStreams));
		assertEquals(AonError.NULL_FILES_UPLOADED.getMessage(), e.getMessage());
	}
	
	@Test
	public void getNullImagesTest() {
		PDDocument document = null;
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> DNIParser.getImages(document));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}
	
	@Test
	public void parserNullTest() {
		PDDocument document = null;
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> DNIParser.parser(document));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}
	
	@Test
	public void extractNullTest() {
		Document document = null;
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> DNIParser.extract(document));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}

	@Test
	public void extractSizeExceededTest() throws Exception {
		String file = "src/test/resources/com/esferalia/aon/in/payroll/pdf/SegInf.pdf";
		InputStream is = new FileInputStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		AonCoreException e = assertThrows(AonCoreException.class, 
				() -> DNIParser.extractImage(bytes));
		assertEquals(AonError.FILE_SIZE_EXCEEDED.getMessage(), e.getMessage());
	}
	
//	@Test
//	public void getDniData() throws Exception {
//		System.out.println("PDF: ");
//		String file = "/tmp/dniJuanmaPDF.pdf";
//		MyDniDataListener listener = new MyDniDataListener();
//		InputStream is = new FileInputStream(file);
//		DNIParser.parse(is);
//		String text = DNIParser.getText();
//		String expectedDNi = "45339825V\r";
//		DNIParser.getNewDniBothPdf(text, listener);
//		String actualDni = DNIParser.dni;
//		assertEquals(expectedDNi, actualDni);
//	}
//	
//	@Test
//	public void getDniDataJpg() throws Exception{
//		System.out.println("JPG: ");
//		String file = "/tmp/dniJuanma.jpg";
//		MyDniDataListener listener = new MyDniDataListener();
//		InputStream is = new FileInputStream(file);
//		byte [] bytes = IOUtils.toByteArray(is);
//		String text = DNIParser.extractImage(bytes);
//		String expectedDNi = "ESP\r";
//		DNIParser.getNewDniBothPdf(text, listener);
//		String actualDni = DNIParser.nacionalidad;
//		System.out.println(expectedDNi +"-"+ actualDni);
//		assertEquals(expectedDNi, actualDni);
//		
//	}
//	
//	@Test
//	public void getDniOtherFormat() throws Exception{
//		String file = "/tmp/pruebaDNI.pdf";
//		MyDniDataListener listener = new MyDniDataListener();
//		InputStream is = new FileInputStream(file);
//		DNIParser.parse(is);
//		String text = DNIParser.getText();
//		System.out.println(text);
//		DNIParser.getNewDniBothPdf(text, listener);
//	}
//	
//	@Test
//	public void getDniOtherFormatJpg() throws Exception{
//		String file = "/tmp/DNIJordi.jpg";
//		MyDniDataListener listener = new MyDniDataListener();
//		InputStream is = new FileInputStream(file);
//		byte [] bytes = IOUtils.toByteArray(is);
//		String text = DNIParser.extractImage(bytes);
//		System.out.println(text);
//		DNIParser.getNewDniBothPdf(text, listener);
//	}
}
