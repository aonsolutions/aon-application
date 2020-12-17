package com.esferalia.aon.in.payroll.pdf.formParsers;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import junit.framework.TestCase;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

public class InternshipsWorkParserTest{

	@Test
	public void parsePdfPracticasTest() throws FileNotFoundException {
		try(InputStream is = InternshipsWorkParserTest.class.getResourceAsStream("Practicas.pdf")){
			InternshipsWorkParser.parse(is,"PracticasTest.pdf");
		} catch (IOException e) {e.printStackTrace();}
	}

	@Test
	public void parsePdfFormacionTest() throws FileNotFoundException {
		try(InputStream is = InternshipsWorkParserTest.class.getResourceAsStream("Formacion.pdf")){
			InternshipsWorkParser.parse(is,"FormacionTest.pdf");
		} catch (IOException e) {e.printStackTrace();}
	}

	@Test
	public void parsePdfIndefinidoTest() throws FileNotFoundException {
		try(InputStream is = InternshipsWorkParserTest.class.getResourceAsStream("Indefinido.pdf")){
			InternshipsWorkParser.parse(is,"IndefinidoTest.pdf");
		} catch (IOException e) {e.printStackTrace();}
	}

	@Test
	public void parsePdfTemporalTest() throws FileNotFoundException {
		try(InputStream is = InternshipsWorkParserTest.class.getResourceAsStream("Temporal.pdf")){
			InternshipsWorkParser.parse(is,"TemporalTest.pdf");
		} catch (IOException e) {e.printStackTrace();}
	}


}

