package com.esferalia.aon.in.payroll.img;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

import com.amazonaws.services.textract.model.Document;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class DNIParserTest {
	DNIParser dniParser = new DNIParser();

	@Test 
	public void getNullImagesTest() {
		PDDocument document = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> DNIParser.getImages(document));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}

	@Test
	public void parserNullTest() {
		PDDocument document = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.parser(document));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}

	@Test
	public void extractNullTest() {
		Document document = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> dniParser.extract(document));
		assertEquals(AonError.NULL_FILE_UPLOADED.getMessage(), e.getMessage());
	}




}
