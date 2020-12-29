package com.esferalia.aon.in.payroll.pdf.formParsers;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import org.junit.Ignore;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PdfFieldNameParserTest {

	@Test
	public void renamePdfPracticasTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Practicas.pdf")){
			PdfFieldNameParser.rename_pdf(is,"PracticasTest.pdf","Input_",null);
		} catch (IOException e) {e.printStackTrace();}
	}

	@Test
	public void displayPdfPracticasTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Practicas.pdf")){
			PdfFieldNameParser.display_pdf_form_names(is,"DisplayedPracticasTest.pdf");
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

	@Test
	public void displayPdfPracticasTestValues() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Practicas.pdf")){
			PdfFieldNameParser.display_pdf_form_values(is,"DisplayedValuesPracticasTest.pdf");
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

	@Test
	public void renamePdfFormacionTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Formacion.pdf")){
			PdfFieldNameParser.rename_pdf(is,"FormacionTest.pdf","Input_",null);
		} catch (IOException e) {e.printStackTrace();}
	}

	@Test
	public void displayPdfFormacionTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Formacion.pdf")){
			PdfFieldNameParser.display_pdf_form_names(is,"DisplayedFormacionTest.pdf");
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

	@Test
	public void renamePdfIndefinidoTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Indefinido.pdf")){
			PdfFieldNameParser.rename_pdf(is,"IndefinidoTest.pdf","Input_",null);
		} catch (IOException e) {e.printStackTrace();}
	}

	@Test
	public void displayPdfIndefinidoTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Indefinido.pdf")){
			PdfFieldNameParser.display_pdf_form_names(is,"DisplayedIndefinidoTest.pdf");
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

	@Test
	public void renamePdfTemporalTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Temporal.pdf")){
			PdfFieldNameParser.rename_pdf(is,"TemporalTest.pdf","Input_",null);
		} catch (IOException e) {e.printStackTrace();}
	}

	@Test
	public void displayPdfTemporalTest() throws FileNotFoundException {
		try(InputStream is = PdfFieldNameParserTest.class.getResourceAsStream("Temporal.pdf")){
			PdfFieldNameParser.display_pdf_form_names(is,"DisplayedTemporalTest.pdf");
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

}

