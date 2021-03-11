package com.esferalia.aon.in.payroll.pdf.Pdf_API.settings;

import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.jump;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.slog;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.start_section;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.tb;
import static com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit.untab;

import java.awt.Color;

import com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit.ConsoleToolkit;

public class PdfColors {
	public final static Color WHITE = new Color(0xffffff);
	public final static Color BLACK = new Color(0x404040);
	public final static Color DARKEST = new Color(0x000000);
	public final static Color BLUE = new Color(0x3a5b9e);
	public final static Color RED = new Color(0xf44336);
	public final static Color GREEN = new Color(0x27AE60);
	public final static Color LIGHT_GRAY = new Color(0xE7E7E7);
	public final static Color GRAY = new Color(0xADADAD);

	// ------------HELP INFO------------
	public static void help() {
		jump(1);
		
		start_section("Pdf API available colors:");
		slog("WHITE" 	  + tb(3) + rgb(WHITE));
		slog("BLACK" 	  + tb(3) + rgb(WHITE));
		slog("DARKEST"	  + tb(2) + rgb(DARKEST));
		slog("BLUE"		  + tb(3) + rgb(BLUE));
		slog("RED"		  + tb(4) + rgb(RED));
		slog("GREEN"	  + tb(3) + rgb(GREEN));
		slog("LIGHT_GRAY" + tb(2) + rgb(LIGHT_GRAY));
		slog("GRAY"		  + tb(3) + rgb(GRAY));
		
		jump(1);
		untab();
	}
	
	public static String rgb(Color c) {
		String s = "rgb(";
		s += c.getRed()   + ",";
		s += c.getGreen() + ",";
		s += c.getBlue()  + ")";
		return s;
	}

}
