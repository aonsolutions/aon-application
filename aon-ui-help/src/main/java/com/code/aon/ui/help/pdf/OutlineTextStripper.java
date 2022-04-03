package com.code.aon.ui.help.pdf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import com.code.aon.ui.help.pdf.items.TextType;

public class OutlineTextStripper extends PDFTextStripper {

	private List<PdfText> lines;

	private double lastY;

	public OutlineTextStripper() throws IOException {
		super();
		this.setSortByPosition(true);
		this.lines = new ArrayList<PdfText>();
		this.lastY = -1;
	}

	protected void writeString(String text, List<TextPosition> textPositions) throws IOException {

		TextPosition pos  = textPositions.get(0);
		
		if(text.trim().equals("") || text.contains("MANUAL DE USUARIO"))
			return;
		
		// If it is a new line
		if (textPositions.get(0).getEndY() != lastY) {
			
			int page = getCurrentPageNo() - 2;
			if (page < 0) {
				page = 0;
			}
			
			PdfText line = new PdfText(text, page);
			line.setType(TextType.getValue((float)pos.getFontSize()));
			line.setX(pos.getX());
			line.setY(pos.getPageHeight() - pos.getY());
			lines.add(line);
			

		} else {
			lines.get(lines.size() - 1).appendText(" " + text);
		}


		lastY = pos.getEndY();

	}

	public List<PdfText> getLines() {
		return this.lines;
	}

}
