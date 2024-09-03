package com.esferalia.aon.in.payroll.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class PrintTextLocations extends PDFTextStripper {
	private float y = 0;

	public PrintTextLocations() throws IOException {
		super();
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		try (PDDocument document = Loader.loadPDF(PrintTextLocations.class.getResourceAsStream("nomina.pdf"))) {
			PDFTextStripper stripper = new PrintTextLocations();
			stripper.setSortByPosition(true);
			stripper.setStartPage(0);
			stripper.setEndPage(document.getNumberOfPages());

			Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
			stripper.writeText(document, dummy);
		}
	}

	/**
	 * Override the default functionality of PDFTextStripper.
	 */
	@Override
	protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
		int prevind=0;
		float prevY=0;
		for(int i=0;i<textPositions.size();i++) {
			if(textPositions.get(i).getYDirAdj()>prevY) {
				prevind=i;
				prevY=textPositions.get(i).getYDirAdj();
			}
			else if(textPositions.get(i).getYDirAdj()<prevY) {
				TextPosition tp = textPositions.get(i);
				for(int j=i;j>=prevind;j--) {
					
				}
			}
		}
		
		for (TextPosition text : textPositions) {
			String a = "String[" + text.getXDirAdj() + "," + text.getYDirAdj() + " fs=" + text.getFontSize()
					+ " xscale=" + text.getXScale() + " height=" + text.getHeightDir() + " space="
					+ text.getWidthOfSpace() + " width=" + text.getWidthDirAdj() + "]" + text.getUnicode();
		}
	}

}
