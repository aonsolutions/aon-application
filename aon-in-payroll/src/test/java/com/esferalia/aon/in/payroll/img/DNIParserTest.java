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
		String file = "/tmp/Seguridad-informática.pdf";
		InputStream is = new FileInputStream(file);
		byte[] bytes = IOUtils.toByteArray(is);
		AonCoreException e = assertThrows(AonCoreException.class, 
				() -> DNIParser.extractImage(bytes));
		assertEquals(AonError.FILE_SIZE_EXCEEDED.getMessage(), e.getMessage());
	}

	public void getDniNullJpg() {
		String text = null;
		MyDniDataListener listener = new MyDniDataListener();
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> DNIParser.getNewDniBothJpg(text, listener));
		assertEquals(AonError.NULL_TEXT_RECEIVED.getMessage(), e.getMessage());
	} 
	
	@Test
	public void getDniNullPdf() {
		String text = null;
		MyDniDataListener listener = new MyDniDataListener();
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> DNIParser.getNewDniBothPdf(text, listener));
		assertEquals(AonError.NULL_TEXT_RECEIVED.getMessage(), e.getMessage());
	} 
}
