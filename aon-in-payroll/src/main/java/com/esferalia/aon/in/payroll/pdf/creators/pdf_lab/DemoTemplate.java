package com.esferalia.aon.in.payroll.pdf.creators.pdf_lab;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLACK;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors.BLUE;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfFonts.HELVETICA;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT.JUSTIFY;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.PAGE_TYPE.HORIZONTAL;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.drawTextJustified;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit.get_lines;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfFile;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfImage;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.components.basic.PdfText;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfColors;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.settings.PdfSettings.ALIGNMENT;
import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.PDFToolkit;
import com.esferalia.aon.in.payroll.pdf.creators.exceptions.CanNotCreatePdfException;

public class DemoTemplate extends PdfFile {

	public DemoTemplate(float x, float y, PDDocument doc, ResourceBundle words, OutputStream out, int limit) {
		super(x, y, doc, words, out, limit);
	}

	public static void print(OutputStream out) throws CanNotCreatePdfException {
		
		DemoTemplate temp = null;
		try{
			ResourceBundle words = ResourceBundle.getBundle("com.esferalia.aon.in.payroll.pdf.creators.pdf_lab.bundles.Demo_bundle");
			temp = new DemoTemplate(20, 550,new PDDocument(), words, out,100);
			temp.set_defaults(HELVETICA, 10f, BLACK, PdfColors.GRAY);
			
			for (int i = 0; i < 1; i++) {
				temp.new_page(HORIZONTAL);
				draw_text_bundle(temp);	
			}
			
			temp.print();			
		} catch (Exception e) {
			if(temp != null) try {temp.close();} catch (IOException e1) {}
			throw new CanNotCreatePdfException(e);
		}

	}

	private static void draw_text_bundle(DemoTemplate temp) throws IOException {
		String text = 
				"Que causa baja en la misma con fecha 01/03/2021,"
				+ " por el motivo de BAJA VOLUNTARIA y percibe en este"
				+ " momento la cantidad de CUATROCIENTOS CINCUENTA EUROS "
				+ "(450) por los servicios prestados y por la totalidad "
				+ "de los que le puedan corresponder derivados de esta relación "
				+ "laboral hasta el día que causó baja en la misma, y "
				+ "por los conceptos que a continuación se detallan:Con el percibo de dicha cantidad"
				+ " el empleado declara hallarse completamente saldado y finiquitado por todos y "
				+ "cuantos devengos salariales y derechos le pudieran corresponder por razón del"
				+ " trabajo realizado para el empleador, quedando totalmente rescindida la relación"
				+ " laboral que unía a las partes, sin que tenga derecho a posterior reclamación o"
				+ " indemnización por concepto alguno, y renuncia expresamente a cualquier acción"
				+ " procesal (civil, penal o de otra índole) contra el empleador. ";
		
		PdfText comp = new PdfText(20, 400, 500, 0, temp.contents, text, BLACK, HELVETICA, 10f, JUSTIFY);
		comp.draw();
		comp.border(BLUE);

	}

	public static void main(String[] args) {
		try {
			FileOutputStream fos = new FileOutputStream("Demo.pdf");
			print(fos);
		} catch (FileNotFoundException | CanNotCreatePdfException e) {e.printStackTrace();}
	}

}
