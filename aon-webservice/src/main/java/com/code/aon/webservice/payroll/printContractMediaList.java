package com.code.aon.webservice.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

@SuppressWarnings("serial")
@WebServlet(name = "PrintContractMediaList", urlPatterns = {"/aon_gwt_aio/print_contract_media_list/*"})
public class printContractMediaList extends HttpServlet{
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private static final Logger LOGGER  = Logger.getLogger(printContractMediaList.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Print Salaried Staff - GET METHOD");
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Integer year =  2016;//Integer.parseInt(parameters.get("year"));
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		
		JSONArray array = ContractServlet.getContractMediaList(domain, login, year);
		
		
		File file = createPdf(array, year);
		
        Utils.addCorsHeader(resp);
        resp.setContentType(MimeType.PDF.getName());
		resp.setHeader("Content-disposition", "inline; filename=\"" + file.getName() + ".pdf\";");
		FileInputStream fileInpurOs =  new FileInputStream(file);
		AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
		resp.flushBuffer();

		fileInpurOs.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Print Salaried Staff - POST METHOD");
	}
	
	public static File createPdf(JSONArray array, Integer year) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("Listado Media contratos " + year, "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			document.open();
			
			PdfPTable table1 = new PdfPTable(8);
			
			float[] medidaCeldas1 = {1f,3f, 1f, 1f,1f,1f,1f,1f};
			try {
				table1.setWidths(medidaCeldas1);
			} catch (DocumentException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
			
			table1.setWidthPercentage(100);

			PdfPCell c01 = new PdfPCell(new Phrase("",getFont1()));
			c01.setBorder(PdfPCell.NO_BORDER);
			table1.addCell(c01);
			
			PdfPCell c12 = new PdfPCell(new Phrase("",getFont1()));
			c12.setBorder(PdfPCell.NO_BORDER);
			table1.addCell(c12);
			
			PdfPCell c21 = new PdfPCell(new Phrase("",getFont1()));
			c21.setBorder(PdfPCell.NO_BORDER);
			table1.addCell(c21);
			
			PdfPCell c31 = new PdfPCell(new Phrase("",getFont1()));
			c31.setBorder(PdfPCell.NO_BORDER);
			table1.addCell(c31);
			
			PdfPCell c41 = new PdfPCell(new Phrase("",getFont1()));
			c41.setBorder(PdfPCell.NO_BORDER);
			table1.addCell(c41);
			
			PdfPCell c51 = new PdfPCell(new Phrase("FIJO",getFont1()));
			c51.setBorder(PdfPCell.NO_BORDER);
			table1.addCell(c51);
			
			PdfPCell c61 = new PdfPCell(new Phrase("NO FIJO",getFont1()));
			c61.setBorder(PdfPCell.NO_BORDER);
			table1.addCell(c61);

			PdfPCell c71 = new PdfPCell(new Phrase("",getFont1()));
			c71.setBorder(PdfPCell.NO_BORDER);
			table1.addCell(c71);
			
			document.add(table1);
			
			PdfPTable table = new PdfPTable(10);
			
			float[] medidaCeldas = {1f,3f, 1f, 1f,1f,0.5f,0.5f,0.5f,0.5f,1f};
			try {
				table.setWidths(medidaCeldas);
			} catch (DocumentException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
			
			table.setWidthPercentage(100);

			PdfPCell c0 = new PdfPCell(new Phrase("Documento",getFont1()));
			c0.setBorder(PdfPCell.NO_BORDER);
			table.addCell(c0);
			
			PdfPCell c1 = new PdfPCell(new Phrase("Nombre",getFont1()));
			c1.setBorder(PdfPCell.NO_BORDER);
			table.addCell(c1);
			
			PdfPCell c2 = new PdfPCell(new Phrase("Fecha Inicio",getFont1()));
			c2.setBorder(PdfPCell.NO_BORDER);
			table.addCell(c2);
			
			PdfPCell c3 = new PdfPCell(new Phrase("Fecha Fin",getFont1()));
			c3.setBorder(PdfPCell.NO_BORDER);
			table.addCell(c3);
			
			PdfPCell c4 = new PdfPCell(new Phrase("GC",getFont1()));
			c4.setBorder(PdfPCell.NO_BORDER);
			table.addCell(c4);
			
			PdfPCell c5 = new PdfPCell(new Phrase("H",getFont1()));
			c5.setBorder(PdfPCell.NO_BORDER);
			table.addCell(c5);
			
			PdfPCell c52 = new PdfPCell(new Phrase("M",getFont1()));
			c52.setBorder(PdfPCell.NO_BORDER);
			table.addCell(c52);
			
			PdfPCell c6 = new PdfPCell(new Phrase("H",getFont1()));
			c6.setBorder(PdfPCell.NO_BORDER);
			table.addCell(c6);
			
			PdfPCell c62 = new PdfPCell(new Phrase("M",getFont1()));
			c62.setBorder(PdfPCell.NO_BORDER);
			table.addCell(c62);
			
			
			PdfPCell c7 = new PdfPCell(new Phrase("Discap",getFont1()));
			c7.setBorder(PdfPCell.NO_BORDER);
			table.addCell(c7);
			
			for(Integer i = 0 ; i < array.length(); i++){
				JSONObject json = array.getJSONObject(i);
				
				PdfPCell c00 = new PdfPCell(new Phrase(json.getString("document"),getFont2()));
				c00.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c00);
				
				PdfPCell c11 = new PdfPCell(new Phrase(json.getString("name"),getFont2()));
				c11.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c11);
				
				Date sd = dateTimeFormat.parse(json.getString("start_date"));
				PdfPCell c22 = new PdfPCell(new Phrase(dateFormat.format(sd),getFont2()));
				c22.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c22);
				
				Date ed = dateTimeFormat.parse(json.getString("end_date"));
				PdfPCell c33 = new PdfPCell(new Phrase(dateFormat.format(ed),getFont2()));
				c33.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c33);
				
				PdfPCell c44 = new PdfPCell(new Phrase(json.getString("quotation_group"),getFont2()));
				c44.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c44);
				
				Double manFixed = json.getJSONObject("gender").getInt("id") != 1 ? json.getDouble("fixed") : 0.0;
				PdfPCell c55 = new PdfPCell(new Phrase(Double.toString(manFixed),getFont2()));
				c55.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c55);
				
				Double womanFixed = json.getJSONObject("gender").getInt("id") == 1 ? json.getDouble("fixed") : 0.0;
				PdfPCell c552 = new PdfPCell(new Phrase(Double.toString(womanFixed),getFont2()));
				c552.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c552);

				Double manUnfixed = json.getJSONObject("gender").getInt("id") != 1 ? json.getDouble("unfixed") : 0.0;
				PdfPCell c66 = new PdfPCell(new Phrase(Double.toString(manUnfixed),getFont2()));
				c66.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c66);
				
				Double womanUnfixed = json.getJSONObject("gender").getInt("id") == 1 ? json.getDouble("unfixed") : 0.0;
				PdfPCell c662 = new PdfPCell(new Phrase(Double.toString(womanUnfixed),getFont2()));
				c662.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c662);
				
				Double discap = json.getJSONObject("disability").getInt("id") != -1 ? json.getDouble("fixed") + json.getDouble("unfixed") : 0.0;
				PdfPCell c77 = new PdfPCell(new Phrase(Double.toString(discap),getFont2()));
				c77.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c77);
				
			}
			
			document.add(table);
		} catch (DocumentException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		document.close();
		return archivoPDF;
	}
	
	
	private static Font getFont1(){
		Font font1 = new Font();
		font1.setSize(8);
		font1.setStyle(Font.BOLD);
		return font1;
	}
	
	private static Font getFont2(){
		Font font2 = new Font();
		font2.setSize(8);
		return font2;
	}
}
