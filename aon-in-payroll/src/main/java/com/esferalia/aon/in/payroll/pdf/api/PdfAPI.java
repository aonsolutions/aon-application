package com.esferalia.aon.in.payroll.pdf.api;

import static com.esferalia.aon.in.payroll.pdf.api.toolkit.ConsoleToolkit.endSection;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.ConsoleToolkit.jump;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.ConsoleToolkit.log;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.ConsoleToolkit.slog;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.ConsoleToolkit.startSection;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.ConsoleToolkit.tab;
import static com.esferalia.aon.in.payroll.pdf.api.toolkit.ConsoleToolkit.title;

import com.esferalia.aon.in.payroll.pdf.api.components.advanced.PdfTable;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfBox;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfComponent;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfFile;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfImage;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfPage;
import com.esferalia.aon.in.payroll.pdf.api.components.basic.PdfText;

/**
 * 
<p>A powerful API to create PDFs based on PDFBOX</p>
@author akrck02
@version 1.4-AK

**/
public class PdfAPI {
	public static String version = "1.4-AK";
		
	/**
	 * <h1>Get help for the API bases</h1> 
	 * <p>Shows basic information of the API.</p>
	 */
	public static void help() {
		title("Pdf API v" + version + " by Akrck02");
		tab(2);
		jump(1);
		slog(" A pdfbox based API for pdf designing and printing.");
		
		jump(1);
		startSection(" PDF beans: ");
			slog(PdfFile.describe());	
			slog(PdfComponent.describe());
			slog(PdfPage.describe());
			slog(PdfBox.describe());					
			slog(PdfText.describe());	
			slog(PdfImage.describe());
			slog(PdfTable.describe());
		endSection();	
		
		log("Pdfbox version","2.0.19");
		jump(1);
		slog("All the classes have Javadoc descriptions to make this API developer friendly :)");
	}
	public static void main(String[] args) {
		help();
	}
}
