package com.esferalia.aon.in.payroll.pdf.Pdf_API.settings;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.jump;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.slog;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.start_section;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.tb;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.untab;

import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit;

public class PdfFonts {
	public final static PDFont HELVETICA = PDType1Font.HELVETICA;
	public final static PDFont HELVETICA_BOLD = PDType1Font.HELVETICA_BOLD;
	public final static PDFont COURIER = PDType1Font.COURIER;
	public final static PDFont COURIER_BOLD = PDType1Font.COURIER_BOLD;

	// ------------HELP INFO------------
	public static void help() {
		jump(1);

		start_section("Pdf API available fonts:");
		slog("Name 			                       Bold");
		slog("-------------------------------------------------------");
		slog("HELVETICA" 		+ tb(5) + "no");
		slog("HELVETICA_BOLD" 	+ tb(3) + "yes");
		slog("COURIER" 			+ tb(6) + "no");
		slog("COURIER_BOLD" 	+ tb(4) + "yes");

		jump(1);
		untab();

	}
}
