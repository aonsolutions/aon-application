package com.esferalia.aon.in.payroll.pdf.Pdf_API.settings;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.jump;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.slog;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.start_section;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.tb;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.untab;

public class PdfSettings {

	public static enum ALIGNMENT {
	    CENTER,
	    LEFT,
	    RIGHT;
	}
	
	public static enum PAGE_TYPE{
		HORIZONTAL,
		VERTICAL
	}
	
	public static enum BORDER_POSITION{
		BOTTOM,
		TOP,
		LEFT,
		RIGHT
	}
	
	
	//------------HELP INFO------------
	public static void help() {
		jump(1);

		start_section("Pdf API available settings:");
		slog("ALIGNMENT" 		+ tb(5) + "Alignment of an element in PDF");
		slog("PAGE_TYPE" 		+ tb(5) + "Type of a PdfPage");
		slog("BORDER POSITION" 	+ tb(3) + "Border position (not implemented yet)");

		jump(1);
		untab();
	}	
}
