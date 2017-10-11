package com.code.aon.webservice.common;

import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;

public class PdfUtils {

	public static Font getTitleFont(){
		Font font = new Font();
		font.setSize(16);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	public static Font getBoeInfoFont(){
		Font font = new Font();
		font.setSize(13);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	public static Font getFont1(){
		Font font1 = new Font();
		font1.setSize(8);
		font1.setStyle(Font.BOLD);
		return font1;
	}
	
	public static Font getFont2(){
		Font font2 = new Font();
		font2.setSize(8);
		return font2;
	}

	public static Font getFont3(){
		Font font1 = new Font();
		font1.setSize((float) 10.5);
		font1.setStyle(Font.BOLD);
		return font1;
	}
	
	public static Font getFont4(){
		Font font1 = new Font();
		font1.setSize(12);
		return font1;
	}
	
	public static PdfPCell emptyCell() {
		PdfPCell cell = new PdfPCell(new Phrase("",getFont2()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	public static PdfPCell stringCell(String str) {
		PdfPCell cell = new PdfPCell(new Phrase(str,getFont2()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	public static PdfPCell boldCell(String str) {
		PdfPCell cell = new PdfPCell(new Phrase(str,getFont1()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	public static PdfPCell stringCell(String str, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(str,font));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	public static PdfPCell boldCell(String str, Font font) {
		PdfPCell cell = new PdfPCell(new Phrase(str,font));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
}
