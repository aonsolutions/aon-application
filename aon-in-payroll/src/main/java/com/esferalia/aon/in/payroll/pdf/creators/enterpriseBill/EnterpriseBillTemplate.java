package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill;

import com.esferalia.aon.in.payroll.pdf.creators.PDFToolkit;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class EnterpriseBillTemplate {

	private static String filename = "./EnterpriseBill.pdf";
	private static float height;
	private static float width;
	private static float top;
	private static float bottom;
	private static float x;
	private static float y;
	private final static float top_info_height = 140;
	private final static float bottom_info_height = 140;
	private static float limit;

	private static byte[] background;
	private static byte[] qr_code;


	//CREATE THE PDF DOCUMENT
	public static void create(String name,EnterpriseBill bill) throws IOException {
		try (PDDocument doc = new PDDocument()) {

			if(name != null) filename = name;
			top = (float) bill.getTop_px();
			bottom = (float) bill.getBottom_px();
			background = bill.getBackground().readAllBytes();
			qr_code = bill.getQr_code().readAllBytes();

			PDPageContentStream contents = draw_page(doc, bill);
			y = height - top - top_info_height-5;
			for (EnterpriseBillEntry entry : bill.getEntries()){

				x = 50;

				if(y <= limit){
					contents.close();
					contents = draw_page(doc,bill);
					y = height - top - top_info_height - 5;
					x = 50;
				}

				PDFToolkit.drawText(contents, entry.getDescription(), x+5, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 8);
				x += 250;

				PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,69,15),entry.getQuantity()+"",PDFToolkit.BLACK,PDFToolkit.HELVETICA,8,4.5f,0);
				x+=70;

				PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,69,15),PDFToolkit.format(entry.getPrice()),PDFToolkit.BLACK,PDFToolkit.HELVETICA,8,4.5f,0);
				x+=70;

				PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,39,15),entry.getPercent()+"",PDFToolkit.BLACK,PDFToolkit.HELVETICA,8,4.5f,0);
				x+=40;

				PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,69,15),PDFToolkit.format(entry.getAmount()),PDFToolkit.BLACK,PDFToolkit.HELVETICA,8,4.5f,0);
				y -= 10;
			}
			contents.close();
			doc.save(filename);
		}
	}

	//DRAW PAGE
	public static PDPageContentStream draw_page(PDDocument doc, EnterpriseBill bill) throws IOException {
		PDPage page = PDFToolkit.createVerticalPage();
		doc.addPage(page);

		PDPageContentStream contents = new PDPageContentStream(doc, page);
		PDFToolkit.drawImage(doc,contents,background,0,0,page.getMediaBox().getWidth(),page.getMediaBox().getHeight());

		width = page.getMediaBox().getWidth();
		height = page.getMediaBox().getHeight();
		limit = bottom_info_height + bottom;

		x = 50f;
		y = height - top - 20;

		draw_top_info(contents,bill);

		if(bill.isDetailed())			draw_detailed_header(contents);
		else 							draw_simple_header(contents);

		draw_bottom_info(contents,doc,bill);
		return contents;
	}

	//DRAW UPPER INFO
	public static void draw_top_info(PDPageContentStream contents, EnterpriseBill bill) throws IOException {
		PDFToolkit.drawText(contents, "FACTURA", x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 16);
		y-=30;

		PDFToolkit.drawText(contents, "Numero: " + bill.getReference(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 11);
		y-=4;

		PDFToolkit.drawBox(contents,x,y, 200,.5f,PDFToolkit.BLACK);
		y-= 16;

		PDFToolkit.drawText(contents, "Fecha: " + formatDate(bill.getDate(),"dd,MM,yyyy").get(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 11);
		y-=4;

		PDFToolkit.drawBox(contents,x,y, 200,.5f,PDFToolkit.BLACK);
		y-=16;

		PDFToolkit.drawText(contents, "N.I.F: " + bill.getDocument(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 11);
		y-=4;

		PDFToolkit.drawBox(contents,x,y, 200,.5f,PDFToolkit.BLACK);
		y-=6;
		x+=250;

		PDFToolkit.drawBox(contents,x,y, 250,80,PDFToolkit.LIGHT_GRAY);
		x+=10;
		y = height - top - 35;

		PDFToolkit.drawText(contents, bill.getName(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA_BOLD, 12);
		y-=15;

		PDFToolkit.drawText(contents, bill.getAddress(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 9);
		y-=10;

		PDFToolkit.drawText(contents, bill.getZip_city_province(), x, y, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 9);
	}

	//DRAW DETAILED HEADER
	public static void draw_detailed_header(PDPageContentStream contents) throws IOException {
		y -= 70;
		x = 50;

		PDFToolkit.drawBox(contents,x,y, 249,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents,new PDRectangle(x,y,249,15),"Descripción",PDFToolkit.WHITE,PDFToolkit.HELVETICA_BOLD,9,4.5f);
		x+=250;

		PDFToolkit.drawBox(contents,x,y,69,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents,new PDRectangle(x,y,69,15),"Cantidad",PDFToolkit.WHITE,PDFToolkit.HELVETICA_BOLD,9,4.5f);
		x+=70;

		PDFToolkit.drawBox(contents,x,y,69,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents,new PDRectangle(x,y,69,15),"Precio",PDFToolkit.WHITE,PDFToolkit.HELVETICA_BOLD,9,4.5f);
		x+=70;

		PDFToolkit.drawBox(contents,x,y,39,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents,new PDRectangle(x,y,39,15),"%Dto.",PDFToolkit.WHITE,PDFToolkit.HELVETICA_BOLD,9,4.5f);
		x+=40;

		PDFToolkit.drawBox(contents,x,y,69,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,69,15),"Importe",PDFToolkit.WHITE,PDFToolkit.HELVETICA_BOLD,9, 5,4.5f);
	}

	//DRAW SIMPLE HEADER
	public static void draw_simple_header(PDPageContentStream contents) throws IOException {
		y -= 70;
		x = 50;
		PDFToolkit.drawBox(contents,x,y, 429,15,PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Descripción", x+5f, y+4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9);
		x+=430;

		PDFToolkit.drawBox(contents,x,y,69,15,PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Importe", x+5f, y+4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA_BOLD, 9);
	}

	//DRAW BOTTOM INFO
	public static void draw_bottom_info(PDPageContentStream contents, PDDocument doc, EnterpriseBill bill) throws IOException {
		x = 50;
		y = bottom + 10;
		PDFToolkit.drawImage(doc,contents,qr_code,x,y,120,120);

		draw_taxes(contents,bill);
		draw_finances(contents,bill);
	}

	//DRAW TAXES
	public static void draw_taxes(PDPageContentStream contents, EnterpriseBill bill) throws IOException {

		x = 240;
		y = bottom + 107;

		PDFToolkit.drawBox(contents,x,y,79,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,79,15),"Base",PDFToolkit.WHITE,PDFToolkit.HELVETICA,9, 5, 4.5f);
		x+=80;

		PDFToolkit.drawBox(contents,x,y,49,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,49,15),"%",PDFToolkit.WHITE,PDFToolkit.HELVETICA,9, 5, 4.5f);
		x+=50;

		PDFToolkit.drawBox(contents,x,y,59,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents,new PDRectangle(x,y,59,15),"Tipo",PDFToolkit.WHITE,PDFToolkit.HELVETICA,9, 4.5f);
		x+=60;

		PDFToolkit.drawBox(contents,x,y,49,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,49,15),"Cuota",PDFToolkit.WHITE,PDFToolkit.HELVETICA,9, 5, 4.5f);
		x+=50;

		PDFToolkit.drawBox(contents,x,y,69,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextCenter(contents,new PDRectangle(x,y,69,15),"Total factura",PDFToolkit.WHITE,PDFToolkit.HELVETICA_BOLD,9, 4.5f);

		double sum = 0;
		for (EnterpriseBillTax tax: bill.getTaxes()){
			x = 240;
			PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,79,15),PDFToolkit.format(tax.getBase()),PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, -12);
			x+=80;
			PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,49,15),PDFToolkit.format(tax.getPercentage()),PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, -12);
			x+=50;
			PDFToolkit.drawTextCenter(contents,new PDRectangle(x,y,59,15),tax.getType(),PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, -12);
			x+=60;
			PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,49,15),PDFToolkit.format(tax.getQuota()),PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, -12);
			x+=50;

			sum += tax.getQuota();
			y-= 10;
		}

//		PDFToolkit.drawTextRight(contents,new PDRectangle(x,bottom + 107,69,15), PDFToolkit.format(sum) + " €",PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, -12);
		PDFToolkit.drawTextRight(contents,new PDRectangle(x,bottom + 107,69,15), PDFToolkit.format(sum) + " \u20AC",PDFToolkit.BLACK,PDFToolkit.HELVETICA, 7, 5, -12);
	}

	//DRAW FINANCES
	public static void draw_finances(PDPageContentStream contents, EnterpriseBill bill) throws IOException {
		x = 180;
		y = bottom + 50;

		PDFToolkit.drawBox(contents,x,y,59,15,PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Fecha", x+5f, y+4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9);
		x += 60;

		PDFToolkit.drawBox(contents,x,y,79,15,PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Forma de pago", x+5f, y+4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9);
		x += 80;

		PDFToolkit.drawBox(contents,x,y,159,15,PDFToolkit.BLACK);
		PDFToolkit.drawText(contents, "Cuenta Bancaria", x+5f, y+4.5f, PDFToolkit.WHITE, PDFToolkit.HELVETICA, 9);
		x += 160;

		PDFToolkit.drawBox(contents,x,y,69,15,PDFToolkit.BLACK);
		PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,69,15),"Importe",PDFToolkit.WHITE,PDFToolkit.HELVETICA,9, 5,4.5f);


		for (EnterpriseBillFinance finance: bill.getFinances()) {
			x = 180;
			PDFToolkit.drawText(contents, PDFToolkit.formatDate(finance.getDue_date(),"dd/MM/yyyy").get(), x+5f, y-12, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			x += 60;
			PDFToolkit.drawText(contents, finance.getPaymethod(), x+5f, y-12, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			x += 80;
			PDFToolkit.drawText(contents, finance.getIban(), x+5f, y-12, PDFToolkit.BLACK, PDFToolkit.HELVETICA, 7);
			x += 160;
			PDFToolkit.drawTextRight(contents,new PDRectangle(x,y,69,15),PDFToolkit.format(finance.getAmount()),PDFToolkit.BLACK,PDFToolkit.HELVETICA,7, 5,-12);

			y-= 10;
		}


	}

	//FORMAT DATE TO STRING IN A SPECIFIC FORMAT
	public static Optional<String> formatDate(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date));
		return formattedDate;
	}




}
