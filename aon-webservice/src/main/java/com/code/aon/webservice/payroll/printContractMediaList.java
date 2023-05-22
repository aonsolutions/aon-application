package com.code.aon.webservice.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
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

	private static final Logger LOGGER  = Logger.getLogger(printContractMediaList.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Print Salaried Staff - GET METHOD");
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Integer year = Integer.parseInt(parameters.get("year"));
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		
		JSONObject json = new JSONObject();
		JSONArray array = ContractServlet.getContractMediaList(domain, login, year);
		json.put("contract_act", array);
		
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
		
		Document document = new Document(PageSize.A4.rotate());
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			document.open();
			writeDocument(document, json);	
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
	
	public static void writeDocument(Document document, JSONObject json) throws DocumentException, JSONException, ParseException{
		PdfPTable t = new PdfPTable(2);
		t.setWidthPercentage(100);
		t.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		PdfPTable table0 = new PdfPTable(2);
		table0.setWidthPercentage(100);

		float[] medidaCeldas0 = {2f, 8f};
		try {
			table0.setWidths(medidaCeldas0);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		JSONObject company = json.getJSONObject("company");


		table0.addCell(boldCell("Empresa"));
		table0.addCell(stringCell(company.getString("name")));
		
		table0.addCell(boldCell("C.I.F."));
		table0.addCell(boldCell(company.getString("document")));

		t.addCell(table0);
		
		t.addCell(getTitle(Integer.toString(json.getInt("year"))));
		
		document.add(t);
		
		Paragraph order = new Paragraph(" ");
		order.add(getSeparator());
		document.add(order);
		
		document.add(new Paragraph(" "));
		
		PdfPTable table1 = new PdfPTable(6);
		
		float[] medidaCeldas1 = {7f, 1f, 2f,1f,1f,1f};
		try {
			table1.setWidths(medidaCeldas1);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		table1.setWidthPercentage(100);

		table1.addCell(emptyCell());		
		table1.addCell(boldCell("CONTRATO"));
		table1.addCell(emptyCell());
		table1.addCell(boldCell("FIJO"));
		table1.addCell(boldCell("NO FIJO"));
		table1.addCell(emptyCell());
		
		document.add(table1);
		
		PdfPTable table = new PdfPTable(11);
		
		float[] medidaCeldas = {1f,3f,3f, 1f, 1f,1f,0.5f,0.5f,0.5f,0.5f,1f};
		try {
			table.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		table.setWidthPercentage(100);
		
		table.addCell(boldCell("Documento"));
		table.addCell(boldCell("Nombre"));
		table.addCell(boldCell("Categoria"));
		table.addCell(boldCell("Fecha Inicio"));
		table.addCell(boldCell("Fecha Fin"));
		table.addCell(boldCell("GC"));
		table.addCell(boldCell("H"));
		table.addCell(boldCell("M"));
		table.addCell(boldCell("H"));
		table.addCell(boldCell("M"));
		table.addCell(boldCell("Discap"));
		
		JSONArray array = json.getJSONArray("contract_act");
		
		Double totalManFixed = 0.0, totalWomanFixed = 0.0, totalManUnfixed = 0.0, totalWomanUnfixed = 0.0, totalDiscap = 0.0;
		Double totalManFixed2 = 0.0, totalWomanFixed2 = 0.0, totalManUnfixed2 = 0.0, totalWomanUnfixed2 = 0.0, totalDiscap2 = 0.0;

		for(Integer i = 0 ; i < array.length(); i++){
			JSONObject contract = array.getJSONObject(i);
			table.addCell(stringCell(contract.optString("document")));
			table.addCell(stringCell(contract.optString("name")));
			table.addCell(stringCell(
					contract.opt("category") != null &&
					contract.getJSONObject("category").opt("name") != null ?
					contract.getJSONObject("category").getString("name") : ""));
			
			Date sd = AonDateUtils.dateTimeParse(contract.getString("start_date"));
			table.addCell(stringCell(AonDateUtils.simpleFormat(sd)));
			
			String date = contract.getString("end_date");
			if(!date.equals("-")){
				Date ed = AonDateUtils.dateTimeParse(date);
				date = AonDateUtils.simpleFormat(ed);
			}		
			table.addCell(stringCell(date));			
			table.addCell(stringCell(contract.getString("quotation_group")));
			
			Double manFixed = contract.getJSONObject("gender").getInt("id") != 1 ? contract.getDouble("fixed") : 0.0;
			totalManFixed = totalManFixed + manFixed;
			Double manFixed2 = contract.getJSONObject("gender").getInt("id") != 1 ? contract.getDouble("end_fixed") : 0.0;
			totalManFixed2 = totalManFixed2 + manFixed2;
			table.addCell(stringCell(Double.toString(manFixed)));
			
			Double womanFixed = contract.getJSONObject("gender").getInt("id") == 1 ? contract.getDouble("fixed") : 0.0;
			totalWomanFixed = totalWomanFixed + womanFixed;
			Double womanFixed2 = contract.getJSONObject("gender").getInt("id") == 1 ? contract.getDouble("end_fixed") : 0.0;
			totalWomanFixed2 = totalWomanFixed2 + womanFixed2;
			table.addCell(stringCell(Double.toString(womanFixed)));

			Double manUnfixed = contract.getJSONObject("gender").getInt("id") != 1 ? contract.getDouble("unfixed") : 0.0;
			totalManUnfixed = totalManUnfixed + manUnfixed;
			Double manUnfixed2 = contract.getJSONObject("gender").getInt("id") != 1 ? contract.getDouble("end_unfixed") : 0.0;
			totalManUnfixed2 = totalManUnfixed2 + manUnfixed2;
			table.addCell(stringCell(Double.toString(manUnfixed)));
			
			Double womanUnfixed = contract.getJSONObject("gender").getInt("id") == 1 ? contract.getDouble("unfixed") : 0.0;
			totalWomanUnfixed = totalWomanUnfixed + womanUnfixed;
			Double womanUnfixed2 = contract.getJSONObject("gender").getInt("id") == 1 ? contract.getDouble("end_unfixed") : 0.0;
			totalWomanUnfixed2 = totalWomanUnfixed2 + womanUnfixed2;
			table.addCell(stringCell(Double.toString(womanUnfixed)));
			
			Double discap = contract.getJSONObject("disability").getInt("id") != -1 ? contract.getDouble("fixed") + contract.getDouble("unfixed") : 0.0;
			totalDiscap = totalDiscap + discap;
			Double discap2 = contract.getJSONObject("disability").getInt("id") != -1 ? contract.getDouble("end_fixed") + contract.getDouble("end_unfixed") : 0.0;
			totalDiscap2 = totalDiscap2 + discap2;
			table.addCell(stringCell(Double.toString(discap)));
		}
		
		document.add(table);

		Paragraph order2 = new Paragraph(" ");
		order2.add(getSeparator());
		document.add(order2);
		
		document.add(new Paragraph(" "));
		
		PdfPTable tableN1 = new PdfPTable(7);
		tableN1.setWidthPercentage(100);
		float[] medidaCeldasN1 = {8f, 2f, 0.5f, 0.5f, 0.5f, 0.5f ,1f};
		try {
			tableN1.setWidths(medidaCeldasN1);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		tableN1.addCell(emptyCell());
		tableN1.addCell(boldCell("Total Media Periodo"));
		tableN1.addCell(boldCell(Double.toString(AonMathUtils.round(totalManFixed))));
		tableN1.addCell(boldCell(Double.toString(AonMathUtils.round(totalWomanFixed))));
		tableN1.addCell(boldCell(Double.toString(AonMathUtils.round(totalManUnfixed))));
		tableN1.addCell(boldCell(Double.toString(AonMathUtils.round(totalWomanUnfixed))));
		tableN1.addCell(boldCell(Double.toString(AonMathUtils.round(totalDiscap))));
		
		tableN1.addCell(emptyCell());
		tableN1.addCell(boldCell("Total a 31/12"));
		tableN1.addCell(boldCell(Integer.toString(totalManFixed2.intValue())));
		tableN1.addCell(boldCell(Integer.toString(totalWomanFixed2.intValue())));
		tableN1.addCell(boldCell(Integer.toString(totalManUnfixed2.intValue())));
		tableN1.addCell(boldCell(Integer.toString(totalWomanUnfixed2.intValue())));
		tableN1.addCell(boldCell(Integer.toString(totalDiscap2.intValue())));

		document.add(tableN1);
		PdfPTable tableN = new PdfPTable(4);
		tableN.setWidthPercentage(100);

		float[] medidaCeldasN = {8f, 3f, 1f, 1f};
		try {
			tableN.setWidths(medidaCeldasN);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	
		tableN.addCell(emptyCell());
		tableN.addCell(emptyCell());
		tableN.addCell(boldCell("Media"));
		tableN.addCell(boldCell("31/12"));
		
		tableN.addCell(emptyCell());
		tableN.addCell(boldCell("Total Fijo"));
		Double totalFixed = totalManFixed + totalWomanFixed;
		tableN.addCell(boldCell(Double.toString(AonMathUtils.round(totalFixed))));
		Double totalFixed2 = totalManFixed2 + totalWomanFixed2;
		tableN.addCell(boldCell(Integer.toString(totalFixed2.intValue())));		
		
		tableN.addCell(emptyCell());
		tableN.addCell(boldCell("Total No Fijo"));
		Double totalUnfixed = totalManUnfixed + totalWomanUnfixed;
		tableN.addCell(boldCell(Double.toString(AonMathUtils.round(totalUnfixed))));
		Double totalUnfixed2 = totalManUnfixed2 + totalWomanUnfixed2;
		tableN.addCell(boldCell(Integer.toString(totalUnfixed2.intValue())));
		
		tableN.addCell(emptyCell());
		tableN.addCell(boldCell("Total Empresa"));
		Double totalCompany = totalFixed + totalUnfixed;
		tableN.addCell(boldCell(Double.toString(AonMathUtils.round(totalCompany))));
		Double totalCompany2 = totalFixed2 + totalUnfixed2;
		tableN.addCell(boldCell(Integer.toString(totalCompany2.intValue())));

		document.add(tableN);
	}
	
	private static PdfPTable getTitle(String year){
		String boeInfo = "INFORME DE PERSONAL ASALARIADO";

		PdfPCell cell = new PdfPCell(new Paragraph(boeInfo, getTitleFont()));
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setBorder(PdfPCell.NO_BORDER);

		PdfPTable titleTable = new PdfPTable(1);
		titleTable.addCell(cell);
		
		PdfPCell c = new PdfPCell(new Phrase("Ejercicio " + year,getFont1()));
		c.setHorizontalAlignment(Element.ALIGN_RIGHT);
		c.setBorder(PdfPCell.NO_BORDER);	
		titleTable.addCell(c);
	
		return titleTable;
	}
	
	private static Font getTitleFont(){
		Font font = new Font();
		font.setSize(12);
		font.setStyle(Font.BOLD);
		return font;
	}
	
	private static PdfPCell emptyCell() {
		PdfPCell cell = new PdfPCell(new Phrase("",getFont2()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	private static PdfPCell stringCell(String str) {
		PdfPCell cell = new PdfPCell(new Phrase(str,getFont2()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	private static PdfPCell boldCell(String str) {
		PdfPCell cell = new PdfPCell(new Phrase(str,getFont1()));
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
