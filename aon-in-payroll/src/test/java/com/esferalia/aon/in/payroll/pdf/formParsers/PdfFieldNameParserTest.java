package com.esferalia.aon.in.payroll.pdf.formParsers;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PdfFieldNameParserTest {

	@Test
	public void parsePdfPracticasTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Practicas.pdf")){
			PdfFieldNameParser.parse(is,"PracticasTest.pdf");
		} catch (IOException e) {e.printStackTrace();}
	}

	@Test
	public void parsePdfFormacionTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Formacion.pdf")){
			PdfFieldNameParser.parse(is,"FormacionTest.pdf");
		} catch (IOException e) {e.printStackTrace();}
	}

	@Test
	public void parsePdfIndefinidoTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Indefinido.pdf")){
			PdfFieldNameParser.parse(is,"IndefinidoTest.pdf");
		} catch (IOException e) {e.printStackTrace();}
	}

	@Test
	public void parsePdfTemporalTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Temporal.pdf")){
			PdfFieldNameParser.parse(is,"TemporalTest.pdf");
		} catch (IOException e) {e.printStackTrace();}
	}


}

