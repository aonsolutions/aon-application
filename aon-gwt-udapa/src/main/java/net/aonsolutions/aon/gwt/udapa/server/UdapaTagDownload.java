package net.aonsolutions.aon.gwt.udapa.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import net.aonsolutions.aon.gwt.udapa.server.printQuality.Title;

@WebServlet(name = "udapaTagDownload", urlPatterns = {"/aon_gwt_aio/download_udapa_tag/*"})
public class UdapaTagDownload extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {	       
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		String position_str = parameters.get("position");
		Integer position = Integer.parseInt(position_str);
		
		String caliber1_str = parameters.get("caliber1");
		Integer caliber1 = Integer.parseInt(caliber1_str);
		String destiny1 = parameters.get("destiny1");
		
		String caliber2_str = parameters.get("caliber2");
		Integer caliber2 = Integer.parseInt(caliber2_str);
		String destiny2 = parameters.get("destiny2");

		String caliber3_str = parameters.get("caliber3");
		Integer caliber3 = Integer.parseInt(caliber3_str);
		String destiny3 = parameters.get("destiny3");

		String caliber4_str = parameters.get("caliber4");
		Integer caliber4 = Integer.parseInt(caliber4_str);
		String destiny4 = parameters.get("destiny4");
		
		String caliber5_str = parameters.get("caliber5");
		Integer caliber5 = Integer.parseInt(caliber5_str);
		String destiny5 = parameters.get("destiny5");

		Integer[] calibers = new Integer[] {caliber1, caliber2, caliber3, caliber4, caliber5};
		String[] destinies = new String[] {destiny1, destiny2, destiny3, destiny4, destiny5};
		
		String variety = parameters.get("variety");
		String observation = parameters.get("observation");
		
		String supplier = parameters.get("supplier");
		String productor = parameters.get("productor");

		File file = null;

		String dataResponseIdStr = parameters.get("id");
		Integer dataResponseId = Integer.parseInt(dataResponseIdStr);
		
		UdapaImpl udp = new UdapaImpl();
		HashMap<String, String> map = udp.getValues(domain.getName(), domain.getId(), dataResponseId);
		
		DataResponse dr = AON.getDataResponse(domain.getName(), domain.getId(), login, DataResponseSource.QUALITY, f -> f.getIdProperty().eq(dataResponseId));
		map.put("number", dr.getCode());
				
		file = createPdf(map, position, calibers, destinies, observation, variety, supplier, productor);
		
		resp.addHeader("Access-Control-Allow-Origin", "*");
	    resp.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
	    resp.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
	    resp.addHeader("Access-Control-Max-Age", "1728000");
        resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition", "inline; filename=\"" + file.getName() + ".pdf\";");
		FileInputStream fileInpurOs =  new FileInputStream(file);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		resp.flushBuffer();

		fileInpurOs.close();
	}
	
	private File createPdf(HashMap<String, String> map, Integer position, Integer[] calibers, String[] destinies, String observation,String variety, String supplier, String productor) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("quality", "pdf");
		} catch (IOException e) {}
		
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			
			document.open();	
						
			Integer p = position % 8 == 0 ? 8 : position % 8;
			Integer pos = p-1;
			PdfPTable t = new PdfPTable(2);
			t.setWidthPercentage(100);
			t.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
			while(p > 1) {
				PdfPCell cell = new PdfPCell(new Phrase(""));
				cell.setFixedHeight(180);
				cell.setBorder(PdfPCell.NO_BORDER);
				t.addCell(cell);
				p--;
			}
			
			for(Integer c1 = 0; c1 < calibers[0]; c1++) {
				PdfPCell cell = new PdfPCell(tagTable(map, destinies[0], "<45", observation, variety, supplier, productor, pdfWriter));
				
				cell.setPaddingBottom(getPaddingBotton(pos));
				cell.setPaddingTop(getPaddingTop(pos));
				cell.setPaddingRight(getPaddingRight(pos));
				cell.setPaddingLeft(getPaddingLeft(pos));
				cell.setBorder(PdfPCell.NO_BORDER);
				t.addCell(cell);
				pos++;
				if(pos.equals(8)) pos = 0;

			}
			
			for(Integer c2 = 0; c2 < calibers[1]; c2++) {
				PdfPCell cell = new PdfPCell(tagTable(map, destinies[1], "45/50", observation, variety, supplier, productor, pdfWriter));
				cell.setPaddingBottom(getPaddingBotton(pos));
				cell.setPaddingTop(getPaddingTop(pos));
				cell.setPaddingRight(getPaddingRight(pos));
				cell.setPaddingLeft(getPaddingLeft(pos));
				cell.setBorder(PdfPCell.NO_BORDER);
				t.addCell(cell);
				pos++;
				if(pos.equals(8)) pos = 0;
			}
			for(Integer c1 = 0; c1 < calibers[2]; c1++) {
				PdfPCell cell = new PdfPCell(tagTable(map, destinies[2], "50/60", observation, variety, supplier, productor, pdfWriter));
				cell.setPaddingBottom(getPaddingBotton(pos));
				cell.setPaddingTop(getPaddingTop(pos));
				cell.setPaddingRight(getPaddingRight(pos));
				cell.setPaddingLeft(getPaddingLeft(pos));
				cell.setBorder(PdfPCell.NO_BORDER);
				t.addCell(cell);
				pos++;
				if(pos.equals(8)) pos = 0;
			}
			
			for(Integer c2 = 0; c2 < calibers[3]; c2++) {
				PdfPCell cell = new PdfPCell(tagTable(map, destinies[3], "60/80", observation, variety, supplier, productor, pdfWriter));
				cell.setPaddingBottom(getPaddingBotton(pos));
				cell.setPaddingTop(getPaddingTop(pos));
				cell.setPaddingRight(getPaddingRight(pos));
				cell.setPaddingLeft(getPaddingLeft(pos));
				cell.setBorder(PdfPCell.NO_BORDER);
				t.addCell(cell);
				pos++;
				if(pos.equals(8)) pos = 0;
			}
			for(Integer c2 = 0; c2 < calibers[4]; c2++) {
				PdfPCell cell = new PdfPCell(tagTable(map, destinies[4], "S/C", observation, variety, supplier, productor, pdfWriter));
				cell.setPaddingBottom(getPaddingBotton(pos));
				cell.setPaddingTop(getPaddingTop(pos));
				cell.setPaddingRight(getPaddingRight(pos));
				cell.setPaddingLeft(getPaddingLeft(pos));
				cell.setBorder(PdfPCell.NO_BORDER);
				t.addCell(cell);
			}
			
			if((!isPar(calibers[0] + calibers[1] + calibers[2] + calibers[3] + calibers[4]) && !isPar(position))
				|| (isPar(calibers[0] + calibers[1] + calibers[2] + calibers[3] + calibers[4]) && isPar(position))) {
				PdfPCell cell = new PdfPCell(new Phrase(""));
				cell.setFixedHeight(180);
				cell.setBorder(PdfPCell.NO_BORDER);
				t.addCell(cell);
			}
		
			document.add(t);	
		} catch (DocumentException | IOException e) {}
		document.close();
		return archivoPDF;
	}
	
	private Boolean isPar(Integer i) {
		return i % 2 == 0; 
	}
	
	public static PdfPTable tagTable(HashMap<String, String> map, String destiny, String caliber, String observation, String variety, String supplier, String productor, PdfWriter pdfWriter){
	    	PdfPTable tag = new PdfPTable(2);
	    	
	    	Paragraph p = new Paragraph("UDAPA, S.COOP. 01015 VITORIA-GASTEIZ (ÁLAVA)", getFontX());
	    	
	    	PdfPCell cell = new PdfPCell(p);
	    	cell.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	cell.setBackgroundColor(BaseColor.GRAY);
			cell.setColspan(2);
			tag.addCell(cell);
			
	    	SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
			
			String transportDeliveryDate = map.get("transport_delivery_date");
			String date = "-";
			if(transportDeliveryDate != null && !"-".equals(transportDeliveryDate)){
				try {
					Date deliveryDate = dateTimeFormat.parse(transportDeliveryDate);
					date = dateFormat.format(deliveryDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
	    	PdfPCell c1 = new PdfPCell(new Phrase(date,getFont2()));
	    	c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	c1.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    	c1.setFixedHeight(30);
	    	tag.addCell(c1);
			
			PdfPCell c2 = new PdfPCell(new Phrase(variety,getFont2()));
			c2.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	c2.setVerticalAlignment(Element.ALIGN_MIDDLE);
			c2.setFixedHeight(30);
			tag.addCell(c2);

		   	PdfPCell c3 = new PdfPCell(new Phrase(caliber,getFont22()));
			c3.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	c3.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    	c3.setFixedHeight(30);
			tag.addCell(c3);
				
			if(destiny.length() > 10) destiny = destiny.substring(0, 10);
			PdfPCell c4 = new PdfPCell(new Phrase(destiny, getFont2()));
			c4.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	c4.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    	c4.setFixedHeight(30);
			tag.addCell(c4);
			
	    	Paragraph p1 = new Paragraph("Proveedor / Productor", getFontX());

			PdfPCell c5 = new PdfPCell(p1);
			c5.setHorizontalAlignment(Element.ALIGN_CENTER);
			c5.setBackgroundColor(BaseColor.GRAY);
			tag.addCell(c5);
			
	    	Paragraph p2 = new Paragraph("Lote", getFontX());
			
			PdfPCell c6 = new PdfPCell(p2);
			c6.setHorizontalAlignment(Element.ALIGN_CENTER);
			c6.setBackgroundColor(BaseColor.GRAY);
			tag.addCell(c6);
			
			PdfPCell c7 = new PdfPCell(new Phrase(supplier + " / " + productor, getFont2()));
			c7.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	c7.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    	c7.setFixedHeight(50);
			tag.addCell(c7);
			
			Barcode128 code = new Barcode128();
			String str = map.get("product_description");
			Integer pos = str.lastIndexOf("#");
			code.setCode(str.substring(pos + 1));
			Image img = code.createImageWithBarcode(pdfWriter.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK);
			
			PdfPCell c8 = new PdfPCell(img);
			c8.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	c8.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    	c8.setFixedHeight(50);
			tag.addCell(c8);
						
	    	PdfPCell cX = new PdfPCell(new Phrase(observation, getFont3()));
	    	cX.setHorizontalAlignment(Element.ALIGN_CENTER);
	    	cX.setVerticalAlignment(Element.ALIGN_MIDDLE);
	    	cX.setFixedHeight(30);
	    	cX.setColspan(2);
			tag.addCell(cX);
			
			return tag;
	    }
	
    public static PdfPCell getCell(PdfPTable content, String title) {
        PdfPCell cell = new PdfPCell(content);
        cell.setCellEvent(new Title(title));
        cell.setPadding(5);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }
	
	// ------------------- FONTS
    
    private Integer getPaddingTop(Integer pos){
    	if(pos.equals(0) || pos.equals(1)) {
    		return 0;
    	} else if(pos.equals(2) || pos.equals(3)) {
    		return 17;
    	} else if(pos.equals(4) || pos.equals(5)) {
    		return 25;
    	} else return 34;
    }
    
    private Integer getPaddingBotton(Integer pos){
    	if(pos.equals(0) || pos.equals(1)) {
    		return 34;
    	} else if(pos.equals(2) || pos.equals(3)) {
    		return 17;
    	} else if(pos.equals(4) || pos.equals(5)) {
    		return 9;
    	} else return 0;
    }
    
    private Integer getPaddingRight(Integer pos){
    	return isPar(pos) ? 15 : 0;
    }
    
    private Integer getPaddingLeft(Integer pos){
    	return isPar(pos) ? 0 : 15;
    }

	private static Font getFontX(){
		Font font2 = new Font();
		font2.setSize(5);
		return font2;
	}

	private static Font getFont2(){
		Font font2 = new Font();
		font2.setSize(14);
		font2.setStyle(Font.BOLD);
		return font2;
	}
		
	private static Font getFont3(){
		Font font2 = new Font();
		font2.setSize(10);
		font2.setStyle(Font.BOLD);
		return font2;
	}
		
	private static Font getFont22(){
		Font font2 = new Font();
		font2.setSize(20);
		font2.setStyle(Font.BOLD);
		return font2;
	}

}
