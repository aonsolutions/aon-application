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
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

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
		
		JSONObject json = new JSONObject();
		JSONArray array = ContractServlet.getContractMediaList(domain, login, year);
		json.put("contract", array);
		
		Company company = AON.getCompanyForDomain(domain.getName(), domain.getId(), login);
		JSONObject jsonCompany = ToJSON.objectToJSON(company.getId(), company.getName());
		jsonCompany.put("document", company.getDocument());
		json.put("company", jsonCompany);
		json.put("year", year);
		File file = createPdf(json);
		
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
	
	public static File createPdf(JSONObject json) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("Listado Media contratos " + json.getInt("year"), "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		Document document = new Document(PageSize.A4);
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			document.open();
			
			PdfPTable table0 = new PdfPTable(2);
			table0.setWidthPercentage(100);

			float[] medidaCeldas0 = {1f, 9f};
			try {
				table0.setWidths(medidaCeldas0);
			} catch (DocumentException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
			JSONObject company = json.getJSONObject("company");
			
			PdfPCell c010 = new PdfPCell(new Phrase("Empresa",getFont1()));
			c010.setBorder(PdfPCell.NO_BORDER);
			table0.addCell(c010);
			
			PdfPCell c019 = new PdfPCell(new Phrase(company.getString("name"),getFont2()));
			c019.setBorder(PdfPCell.NO_BORDER);
			table0.addCell(c019);
			
			PdfPCell c018 = new PdfPCell(new Phrase("C.I.F.",getFont1()));
			c018.setBorder(PdfPCell.NO_BORDER);
			table0.addCell(c018);
			
			PdfPCell c017 = new PdfPCell(new Phrase(company.getString("document"),getFont2()));
			c017.setBorder(PdfPCell.NO_BORDER);
			table0.addCell(c017);
			
			document.add(table0);
			
			Paragraph order = new Paragraph(" ");
			order.add(getSeparator());
			document.add(order);
			
			document.add(new Paragraph(" "));
			
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
			
			JSONArray array = json.getJSONArray("contract");
			
			Double totalManFixed = 0.0, totalWomanFixed = 0.0, totalManUnfixed = 0.0, totalWomanUnfixed = 0.0, totalDiscap = 0.0;
			
			for(Integer i = 0 ; i < array.length(); i++){
				
				JSONObject contract = array.getJSONObject(i);
				
				PdfPCell c00 = new PdfPCell(new Phrase(contract.getString("document"),getFont2()));
				c00.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c00);
				
				PdfPCell c11 = new PdfPCell(new Phrase(contract.getString("name"),getFont2()));
				c11.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c11);
				
				Date sd = dateTimeFormat.parse(contract.getString("start_date"));
				PdfPCell c22 = new PdfPCell(new Phrase(dateFormat.format(sd),getFont2()));
				c22.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c22);
				
				Date ed = dateTimeFormat.parse(contract.getString("end_date"));
				PdfPCell c33 = new PdfPCell(new Phrase(dateFormat.format(ed),getFont2()));
				c33.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c33);
				
				PdfPCell c44 = new PdfPCell(new Phrase(contract.getString("quotation_group"),getFont2()));
				c44.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c44);
				
				Double manFixed = contract.getJSONObject("gender").getInt("id") != 1 ? contract.getDouble("fixed") : 0.0;
				totalManFixed = totalManFixed + manFixed;
				PdfPCell c55 = new PdfPCell(new Phrase(Double.toString(manFixed),getFont2()));
				c55.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c55);
				
				Double womanFixed = contract.getJSONObject("gender").getInt("id") == 1 ? contract.getDouble("fixed") : 0.0;
				totalWomanFixed = totalWomanFixed + womanFixed;
				PdfPCell c552 = new PdfPCell(new Phrase(Double.toString(womanFixed),getFont2()));
				c552.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c552);

				Double manUnfixed = contract.getJSONObject("gender").getInt("id") != 1 ? contract.getDouble("unfixed") : 0.0;
				totalManUnfixed = totalManUnfixed + manUnfixed;
				PdfPCell c66 = new PdfPCell(new Phrase(Double.toString(manUnfixed),getFont2()));
				c66.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c66);
				
				Double womanUnfixed = contract.getJSONObject("gender").getInt("id") == 1 ? contract.getDouble("unfixed") : 0.0;
				totalWomanUnfixed = totalWomanUnfixed + womanUnfixed;
				PdfPCell c662 = new PdfPCell(new Phrase(Double.toString(womanUnfixed),getFont2()));
				c662.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c662);
				
				Double discap = contract.getJSONObject("disability").getInt("id") != -1 ? contract.getDouble("fixed") + contract.getDouble("unfixed") : 0.0;
				PdfPCell c77 = new PdfPCell(new Phrase(Double.toString(discap),getFont2()));
				c77.setBorder(PdfPCell.NO_BORDER);
				table.addCell(c77);
			}
			
			document.add(table);
	
			Paragraph order2 = new Paragraph(" ");
			order2.add(getSeparator());
			document.add(order2);
			
			document.add(new Paragraph(" "));
			
			PdfPTable tableN1 = new PdfPTable(7);
			tableN1.setWidthPercentage(100);
			float[] medidaCeldasN1 = {6f, 1f, 0.5f, 0.5f, 0.5f, 0.5f ,1f};
			try {
				tableN1.setWidths(medidaCeldasN1);
			} catch (DocumentException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
			
			tableN1.addCell(emptyCell());
			
			PdfPCell total = new PdfPCell(new Phrase("TOTAL",getFont1()));
			total.setBorder(PdfPCell.NO_BORDER);
			tableN1.addCell(total);
			
			
			PdfPCell tmf = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalManFixed)),getFont1()));
			tmf.setBorder(PdfPCell.NO_BORDER);
			tableN1.addCell(tmf);
			
			PdfPCell twf = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalWomanFixed)),getFont1()));
			twf.setBorder(PdfPCell.NO_BORDER);
			tableN1.addCell(twf);

			PdfPCell tmu = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalManUnfixed)),getFont1()));
			tmu.setBorder(PdfPCell.NO_BORDER);
			tableN1.addCell(tmu);
			
			PdfPCell twu = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalWomanUnfixed)),getFont1()));
			twu.setBorder(PdfPCell.NO_BORDER);
			tableN1.addCell(twu);
			
			PdfPCell td = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalDiscap)),getFont1()));
			td.setBorder(PdfPCell.NO_BORDER);
			tableN1.addCell(td);

			document.add(tableN1);
			PdfPTable tableN = new PdfPTable(3);
			tableN.setWidthPercentage(100);

			float[] medidaCeldasN = {7f, 2f, 1f};
			try {
				tableN.setWidths(medidaCeldasN);
			} catch (DocumentException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
			}
		
			tableN.addCell(emptyCell());
			
			PdfPCell tf1 = new PdfPCell(new Phrase("Total Fijo",getFont1()));
			tf1.setBorder(PdfPCell.NO_BORDER);
			tableN.addCell(tf1);
			
			Double totalFixed = totalManFixed + totalWomanFixed;
			PdfPCell tf = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalFixed)),getFont1()));
			tf.setBorder(PdfPCell.NO_BORDER);
			tableN.addCell(tf);
			
			tableN.addCell(emptyCell());
			
			PdfPCell tnf1 = new PdfPCell(new Phrase("Total No Fijo",getFont1()));
			tnf1.setBorder(PdfPCell.NO_BORDER);
			tableN.addCell(tnf1);
			
			Double totalUnfixed = totalManUnfixed + totalWomanUnfixed;
			PdfPCell tnf = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalUnfixed)),getFont1()));
			tnf.setBorder(PdfPCell.NO_BORDER);
			tableN.addCell(tnf);
			
			tableN.addCell(emptyCell());
			
			PdfPCell te1 = new PdfPCell(new Phrase("Total Empresa",getFont1()));
			te1.setBorder(PdfPCell.NO_BORDER);
			tableN.addCell(te1);
			
			Double totalCompany = totalFixed + totalUnfixed;
			PdfPCell te = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalCompany)),getFont1()));
			te.setBorder(PdfPCell.NO_BORDER);
			tableN.addCell(te);

			document.add(tableN);
			
			
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
	
	private static PdfPCell emptyCell() {
		PdfPCell cell = new PdfPCell(new Phrase("",getFont2()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
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
	
	private static Paragraph getSeparator(){
		Paragraph separator = new Paragraph();
		LineSeparator line = new LineSeparator();
        line.setOffset(-2);
        separator.add(line);
        return separator;
	}
}
