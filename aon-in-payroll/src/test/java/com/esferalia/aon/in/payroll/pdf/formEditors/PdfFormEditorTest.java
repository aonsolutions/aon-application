package com.esferalia.aon.in.payroll.pdf.formEditors;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.pdf.formEditors.PdfFieldRenamer;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class PdfFormEditorTest {

	
	public final static String out = "pdf_out/";
	
	
	//CHECKS IF IS CREATED DIRECTORY
	private void checkDirectory() {
		File dir = new File(out);
		if(!dir.exists()) dir.mkdir();
	}
	
	//TEMPORAL PRACTICAS TESTS
	@Test
	public void renamePdfPracticasTest() throws FileNotFoundException {
		checkDirectory();
		try(InputStream is = PdfFormEditorTest.class.getResourceAsStream("Practicas.pdf")){
			PdfFieldRenamer.rename_pdf_fields(is,out + "PracticasTest.pdf","Input_",null);
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

	@Test
	public void displayPdfPracticasTest() throws FileNotFoundException {
		checkDirectory();
		try(InputStream is = PdfFormEditorTest.class.getResourceAsStream("Practicas.pdf")){
			PdfFormEditor.display_pdf_form_values(is,out + "DisplayedPracticasTest.pdf");
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

	@Test
	public void displayPdfPracticasTestValues() throws FileNotFoundException {
		checkDirectory();
		try(InputStream is = PdfFormEditorTest.class.getResourceAsStream("Practicas.pdf")){
			PdfFormEditor.display_pdf_form_values(is,out + "DisplayedValuesPracticasTest.pdf");
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

	
	//TEMPORAL FORMACION TESTS
	@Test
	public void renamePdfFormacionTest() throws FileNotFoundException {
		checkDirectory();
		try(InputStream is = PdfFormEditorTest.class.getResourceAsStream("Formacion.pdf")){
			PdfFormEditor.rename_pdf_fields(is,out + "FormacionTest.pdf","Input_",null);
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}
	
	//TEMPORAL INDEFINIDO TESTS
	@Test
	public void renamePdfIndefinidoTest() throws FileNotFoundException {
		checkDirectory();
		try(InputStream is = PdfFormEditorTest.class.getResourceAsStream("Indefinido.pdf")){
			PdfFormEditor.rename_pdf_fields(is,out + "IndefinidoTest.pdf","Input_",null);
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

	@Test
	public void displayPdfIndefinidoTest() throws FileNotFoundException {
		checkDirectory();
		try(InputStream is = PdfFormEditorTest.class.getResourceAsStream("Indefinido.pdf")){
			PdfFormEditor.display_pdf_form_values(is,out + "DisplayedIndefinidoTest.pdf");
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

	
	//TEMPORAL CONTRACT TESTS
	@Test
	public void renamePdfTemporalTest() throws FileNotFoundException {
		checkDirectory();
		try(InputStream is = PdfFormEditorTest.class.getResourceAsStream("Temporal.pdf")){
			PdfFormEditor.rename_pdf_fields(is,out + "TemporalTest.pdf","Input_",null);
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

	@Test
	public void displayPdfTemporalTest() throws FileNotFoundException {
		checkDirectory();
		try(InputStream is = PdfFormEditorTest.class.getResourceAsStream("Temporal.pdf")){
			PdfFormEditor.display_pdf_form_values(is,out + "DisplayedTemporalTest.pdf");
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}
	
	@Test
	public void fillPdfTemporalTest() throws FileNotFoundException {
		checkDirectory();
		try(InputStream is = PdfFormEditorTest.class.getResourceAsStream("Temporal.pdf")){
			HashMap<String,String> values = new HashMap<>();
			values.put("Texto4","cosas");
			values.put("BOTON_CLA1","72");
			
			PdfFormEditor.fill_form_fields(is,values, out + "TemporalFillTest.pdf");
		} catch (IOException | UnknownPDFException e) {e.printStackTrace();}
	}

}

