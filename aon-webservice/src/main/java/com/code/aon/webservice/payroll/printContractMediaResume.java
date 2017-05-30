package com.code.aon.webservice.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
import com.itextpdf.text.BaseColor;
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

@SuppressWarnings("serial")
@WebServlet(name = "PrintContractMediaResume", urlPatterns = {"/aon_gwt_aio/print_contract_media_resume/*"})
public class printContractMediaResume extends HttpServlet{
	
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	private static final Logger LOGGER  = Logger.getLogger(printContractMediaResume.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Print Salaried Staff - GET METHOD");
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Integer year = Integer.parseInt(parameters.get("year"));
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		
		JSONObject json = new JSONObject();
		JSONArray arrayAct = ContractServlet.getContractMediaList(domain, login, year);
		JSONArray arrayAnt = ContractServlet.getContractMediaList(domain, login, year-1);

		json.put("contract_act", arrayAct);
		json.put("contract_ant", arrayAnt);
		
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
			
			writeDocument(document, json);
			
			
			
		} catch (DocumentException | IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		document.close();
		return archivoPDF;
	}
	
	public static void writeDocument(Document document, JSONObject json) throws DocumentException{
		document.add(getTitle());
		
		document.add(new Paragraph(" "));
		
		JSONObject company = json.getJSONObject("company");
		document.add(getCompany(company));
		
		document.add(new Paragraph(" "));
		
		document.add(getSalariedStaff(json));
		
		document.add(new Paragraph(" "));
		
		document.add(getSalariedStaffCategory(json));
		
		document.add(new Paragraph(" "));
		
		document.add(getSalariedStaffGC(json));
	}
	
	private static PdfPTable getTitle(){
		String boeInfo = "INFORME DE PERSONAL ASALARIADO";
		Paragraph title = new Paragraph(boeInfo, getTitleFont());
		title.setAlignment(Element.ALIGN_CENTER);
		title.setIndentationRight(20);
		PdfPCell cell = new PdfPCell();
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.addElement(title);
		
		PdfPTable titleTable = new PdfPTable(1);
		titleTable.addCell(cell);
		return titleTable;
	}
	
	private static PdfPTable getCompany(JSONObject json){
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		PdfPCell empresa = new PdfPCell(new Phrase("EMPRESA",getFont1()));
		empresa.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(empresa);
		
		table.addCell(emptyCell());
		
		PdfPCell empresaName = new PdfPCell(new Phrase(json.getString("document") + " - " + json.getString("name"),getFont1()));
		empresaName.setColspan(2);
		table.addCell(empresaName);
		
		return table;
	}
	
	private static PdfPTable getSalariedStaff(JSONObject json){
		Integer yearAct = Integer.parseInt(json.getString("year"));
		Integer yearAnt = yearAct - 1;
		JSONArray arrayAct = json.getJSONArray("contract_act");
		Double fixedAct = 0.0, fixedAnt = 0.0, unfixedAct = 0.0, unfixedAnt = 0.0, 
			   fixedActH = 0.0, fixedAntH = 0.0, unfixedActH = 0.0, unfixedAntH = 0.0,
			   fixedActM = 0.0, fixedAntM = 0.0, unfixedActM = 0.0, unfixedAntM = 0.0,
			   discapAct = 0.0, discapAnt = 0.0 ;
		
		Double fixedAct2 = 0.0, fixedAnt2 = 0.0, unfixedAct2 = 0.0, unfixedAnt2 = 0.0, 
				   fixedActH2 = 0.0, fixedAntH2 = 0.0, unfixedActH2 = 0.0, unfixedAntH2 = 0.0,
				   fixedActM2 = 0.0, fixedAntM2 = 0.0, unfixedActM2 = 0.0, unfixedAntM2 = 0.0,
				   discapAct2 = 0.0, discapAnt2 = 0.0 ;
		
		for(Integer i = 0; i < arrayAct.length(); i++){
			fixedAct = fixedAct + arrayAct.getJSONObject(i).getDouble("fixed");
			fixedAct2 = fixedAct2 + arrayAct.getJSONObject(i).getDouble("end_fixed");
			unfixedAct = unfixedAct + arrayAct.getJSONObject(i).getDouble("unfixed");
			unfixedAct2 = unfixedAct2 + arrayAct.getJSONObject(i).getDouble("end_unfixed");
			if(arrayAct.getJSONObject(i).getJSONObject("disability").getInt("id") != -1){
				discapAct = discapAct + arrayAct.getJSONObject(i).getDouble("fixed") 
						+ arrayAct.getJSONObject(i).getDouble("unfixed");
				discapAct2 = discapAct2 + arrayAct.getJSONObject(i).getDouble("end_fixed") 
						+ arrayAct.getJSONObject(i).getDouble("end_unfixed");
			}
			if(arrayAct.getJSONObject(i).getJSONObject("gender").getInt("id") == 1){
				fixedActM = fixedActM + arrayAct.getJSONObject(i).getDouble("fixed");
				unfixedActM = unfixedActM + arrayAct.getJSONObject(i).getDouble("unfixed");
				fixedActM2 = fixedActM2 + arrayAct.getJSONObject(i).getDouble("end_fixed");
				unfixedActM2 = unfixedActM2 + arrayAct.getJSONObject(i).getDouble("end_unfixed");
			} else {
				fixedActH = fixedActH + arrayAct.getJSONObject(i).getDouble("fixed");
				unfixedActH = unfixedActH + arrayAct.getJSONObject(i).getDouble("unfixed");
				fixedActH2 = fixedActH2 + arrayAct.getJSONObject(i).getDouble("end_fixed");
				unfixedActH2 = unfixedActH2 + arrayAct.getJSONObject(i).getDouble("end_unfixed");
			}
		}
		JSONArray arrayAnt = json.getJSONArray("contract_ant");
		for(Integer i = 0; i < arrayAnt.length(); i++){
			fixedAnt = fixedAnt + arrayAnt.getJSONObject(i).getDouble("fixed");
			unfixedAnt = unfixedAnt + arrayAnt.getJSONObject(i).getDouble("unfixed");
			fixedAnt2 = fixedAnt2 + arrayAnt.getJSONObject(i).getDouble("end_fixed");
			unfixedAnt2 = unfixedAnt2 + arrayAnt.getJSONObject(i).getDouble("end_unfixed");
			if(arrayAnt.getJSONObject(i).getJSONObject("disability").getInt("id") != -1){
				discapAnt = discapAnt + arrayAnt.getJSONObject(i).getDouble("fixed") 
						+ arrayAnt.getJSONObject(i).getDouble("unfixed");
				discapAnt2 = discapAnt2 + arrayAnt.getJSONObject(i).getDouble("end_fixed") 
						+ arrayAnt.getJSONObject(i).getDouble("end_unfixed");
			}
			if(arrayAnt.getJSONObject(i).getJSONObject("gender").getInt("id") == 1){
				fixedAntM = fixedAntM + arrayAnt.getJSONObject(i).getDouble("fixed");
				unfixedAntM = unfixedAntM + arrayAnt.getJSONObject(i).getDouble("unfixed");
				fixedAntM2 = fixedAntM2 + arrayAnt.getJSONObject(i).getDouble("end_fixed");
				unfixedAntM2 = unfixedAntM2 + arrayAnt.getJSONObject(i).getDouble("end_unfixed");
			} else {
				fixedAntH = fixedAntH + arrayAnt.getJSONObject(i).getDouble("fixed");
				unfixedAntH = unfixedAntH + arrayAnt.getJSONObject(i).getDouble("unfixed");
				fixedAntH2 = fixedAntH2 + arrayAnt.getJSONObject(i).getDouble("end_fixed");
				unfixedAntH2 = unfixedAntH2 + arrayAnt.getJSONObject(i).getDouble("end_unfixed");
			}
		}
		
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		PdfPCell empresa = new PdfPCell(new Phrase("PERSONAL ASALARIADO",getFont1()));
		empresa.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(empresa);
		table.addCell(emptyCell());
		
		
		PdfPTable table2 = new PdfPTable(1);
		table2.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

		table2.addCell(stringCell("	a) Número medio de personas empleadas en el curso del ejercicio, por tipo de contrato y empleo con discapacidad:"));
		
		PdfPTable table3 = new PdfPTable(6);
		PdfPCell emptyCel = emptyCell();
		emptyCel.setColspan(2);
		table3.addCell(emptyCel);
		
		PdfPCell ejAct = new PdfPCell(new Phrase("EJERCICIO " + yearAct ,getFont1()));
		ejAct.setColspan(2);
		ejAct.setHorizontalAlignment(Element.ALIGN_CENTER);
		ejAct.setBorder(PdfPCell.NO_BORDER);
		table3.addCell(ejAct);
		
		PdfPCell ejAnt = new PdfPCell(new Phrase("EJERCICIO " + yearAnt,getFont1()));
		ejAnt.setColspan(2);
		ejAnt.setHorizontalAlignment(Element.ALIGN_CENTER);
		ejAnt.setBorder(PdfPCell.NO_BORDER);
		table3.addCell(ejAnt);
		
		PdfPCell ec = emptyCell();
		ec.setColspan(2);
		table3.addCell(ec);
		
		PdfPCell mediaF = new PdfPCell(new Phrase("Media Periodo" ,getFont2()));
		mediaF.setHorizontalAlignment(Element.ALIGN_CENTER);
		mediaF.setBorder(PdfPCell.NO_BORDER);
		
		PdfPCell endEjF = new PdfPCell(new Phrase("Total a 31/12",getFont2()));
		endEjF.setHorizontalAlignment(Element.ALIGN_CENTER);
		endEjF.setBorder(PdfPCell.NO_BORDER);
		
		PdfPCell media = new PdfPCell(new Phrase("Media" ,getFont2()));
		media.setHorizontalAlignment(Element.ALIGN_CENTER);
		media.setBorder(PdfPCell.NO_BORDER);
		
		PdfPCell endEj = new PdfPCell(new Phrase("31/12",getFont2()));
		endEj.setHorizontalAlignment(Element.ALIGN_CENTER);
		endEj.setBorder(PdfPCell.NO_BORDER);
		
		table3.addCell(mediaF);
		table3.addCell(endEjF);
		table3.addCell(mediaF);
		table3.addCell(endEjF);
		
		table3.addCell(emptyCell());
		
		table3.addCell(boldCell("FIJO"));
		
		table3.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedAct)),getFont2())));
		
		Integer fixedAct2Int = fixedAct2.intValue();
		table3.addCell(new PdfPCell(new Phrase(fixedAct2Int.toString(),getFont2())));
		
		table3.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedAnt)),getFont2())));

		Integer fixedAnt2Int = fixedAnt2.intValue();
		table3.addCell(new PdfPCell(new Phrase(fixedAnt2Int.toString(),getFont2())));
		
		table3.addCell(emptyCell());
		
		table3.addCell(boldCell("NO FIJO"));
		
		table3.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedAct)),getFont2())));
		
		Integer unfixedAct2Int = unfixedAct2.intValue();
		table3.addCell(new PdfPCell(new Phrase(unfixedAct2Int.toString(),getFont2())));
		
		table3.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedAnt)),getFont2())));
		
		Integer unfixedAnt2Int = unfixedAnt2.intValue();
		table3.addCell(new PdfPCell(new Phrase(unfixedAnt2Int.toString(),getFont2())));
		
		table2.addCell(table3);
	
		table2.addCell(stringCell("			Del cual: Personas empleadas con discapacidad mayor o igual al 33% (o calificación equivalente local):"));
		
		PdfPTable table4 = new PdfPTable(6);
		
		table4.addCell(emptyCell());
		table4.addCell(emptyCell());
	
		table4.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(discapAct)),getFont2())));
		
		Integer discapAct2Int = discapAct2.intValue();
		table4.addCell(new PdfPCell(new Phrase(discapAct2Int.toString(),getFont2())));
		
		table4.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(discapAnt)),getFont2())));
		
		Integer discapAnt2Int = discapAnt2.intValue();
		table4.addCell(new PdfPCell(new Phrase(discapAnt2Int.toString(),getFont2())));
		
		table2.addCell(table4);
		
		table2.addCell(stringCell("	b) Personal asalariado al término del ejercicio, por tipo de contrato y por sexo:"));
		
		PdfPTable table5 = new PdfPTable(9);
		
		table5.addCell(emptyCell());
		
		PdfPCell ejAct5 = new PdfPCell(new Phrase("EJERCICIO " + yearAct,getFont1()));
		ejAct5.setBorder(PdfPCell.NO_BORDER);
		ejAct5.setHorizontalAlignment(Element.ALIGN_CENTER);
		ejAct5.setColspan(4);
		table5.addCell(ejAct5);
		
		PdfPCell ejAnt5 = new PdfPCell(new Phrase("EJERCICIO " + yearAnt,getFont1()));
		ejAnt5.setBorder(PdfPCell.NO_BORDER);
		ejAnt5.setHorizontalAlignment(Element.ALIGN_CENTER);
		ejAnt5.setColspan(4);
		table5.addCell(ejAnt5);
		
		table5.addCell(emptyCell());
		
		PdfPCell ejAct6H = new PdfPCell(new Phrase("HOMBRES",getFont2()));
		ejAct6H.setColspan(2);
		ejAct6H.setHorizontalAlignment(Element.ALIGN_CENTER);
		PdfPTable tableX1 = new PdfPTable(2);
		tableX1.addCell(ejAct6H);
		tableX1.addCell(media);
		tableX1.addCell(endEj);
		PdfPCell x1 = new PdfPCell(tableX1);
		x1.setColspan(2);
		table5.addCell(x1);
		
		PdfPCell ejAct6M = new PdfPCell(new Phrase("MUJERES",getFont2()));
		ejAct6M.setColspan(2);
		ejAct6M.setHorizontalAlignment(Element.ALIGN_CENTER);
		PdfPTable tableX2 = new PdfPTable(2);
		tableX2.addCell(ejAct6M);
		tableX2.addCell(media);
		tableX2.addCell(endEj);
		PdfPCell x2 = new PdfPCell(tableX2);
		x2.setColspan(2);
		table5.addCell(x2);
		
		table5.addCell(x1);
		table5.addCell(x2);

		table5.addCell(boldCell("FIJO"));
		
		table5.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedActH)),getFont2())));
		
		Integer fixedActH2Int = fixedActH2.intValue();
		table5.addCell(new PdfPCell(new Phrase(fixedActH2Int.toString(),getFont2())));
		
		table5.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedActM)),getFont2())));
		
		Integer fixedActM2Int = fixedActM2.intValue();
		table5.addCell(new PdfPCell(new Phrase(fixedActM2Int.toString(),getFont2())));
		
		table5.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedAntH)),getFont2())));
		
		Integer fixedAntH2Int = fixedAntH2.intValue();
		table5.addCell(new PdfPCell(new Phrase(fixedAntH2Int.toString(),getFont2())));
		
		table5.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(fixedAntM)),getFont2())));
		
		Integer fixedAntM2Int = fixedAntM2.intValue();
		table5.addCell(new PdfPCell(new Phrase(fixedAntM2Int.toString(),getFont2())));
		
		table5.addCell(boldCell("NO FIJO"));
		
		table5.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedActH)),getFont2())));
		
		Integer unfixedActH2Int = unfixedActH2.intValue();
		table5.addCell(new PdfPCell(new Phrase(unfixedActH2Int.toString(),getFont2())));
		
		table5.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedActM)),getFont2())));
		
		Integer unfixedActM2Int = unfixedActM2.intValue();
		table5.addCell(new PdfPCell(new Phrase(unfixedActM2Int.toString(),getFont2())));
		
		table5.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedAntH)),getFont2())));
		
		Integer unfixedAntH2Int = unfixedAntH2.intValue();
		table5.addCell(new PdfPCell(new Phrase(unfixedAntH2Int.toString(),getFont2())));
		
		table5.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(unfixedAntM)),getFont2())));
		
		Integer unfixedAntM2Int = unfixedAntM2.intValue();
		table5.addCell(new PdfPCell(new Phrase(unfixedAntM2Int.toString(),getFont2())));
		
		table2.addCell(table5);
		

		table2.addCell(emptyCell());
		
		PdfPCell c = new PdfPCell(table2);
		c.setColspan(2);
		table.addCell(c);
		
		return table;
	}
	
	private static PdfPTable getSalariedStaffCategory(JSONObject json){
		Integer yearAct = Integer.parseInt(json.getString("year"));
		Integer yearAnt = yearAct - 1;
		JSONArray array = json.getJSONArray("categories");

		JSONArray arrayAct = json.getJSONArray("contract_act");
		JSONArray arrayAnt = json.getJSONArray("contract_ant");
		HashMap<Integer, Double> mapAct = new HashMap<>();
		HashMap<Integer, Double> mapAnt = new HashMap<>();
		Double otherAct1 = 0.0, otherAct2 = 0.0, otherAnt1 = 0.0, otherAnt2 = 0.0;
		for (int i = 0; i < arrayAct.length(); i++) {
			JSONObject js = arrayAct.getJSONObject(i);
			Integer key = js.getJSONObject("category").getInt("id");
			Double d = js.getDouble("fixed") + js.getDouble("unfixed");
			Double d2 = js.getDouble("end_fixed") + js.getDouble("end_unfixed");
			if(key >= 0){
				if(mapAct.containsKey(key)){
					mapAct.put(key, mapAct.get(key) + AonMathUtils.round(d));
					mapAct.put(key*-1, mapAct.get(key*-1) + AonMathUtils.round(d2));
				} else {
					mapAct.put(key, AonMathUtils.round(d));
					mapAct.put(key*-1, AonMathUtils.round(d2));
				}
			} else {
				otherAct1 = otherAct1 + AonMathUtils.round(d);
				otherAct2 = otherAct2 + AonMathUtils.round(d2);
			}
		}
		for (int i = 0; i < arrayAnt.length(); i++) {
			JSONObject js = arrayAnt.getJSONObject(i);
			Integer key = js.getJSONObject("category").getInt("id");
			Double d = js.getDouble("fixed") + js.getDouble("unfixed");
			Double d2 = js.getDouble("end_fixed") + js.getDouble("end_unfixed");
			if(key >= 0){
				if(mapAnt.containsKey(key)){
					mapAnt.put(key, AonMathUtils.round(mapAnt.get(key) + d));
					mapAnt.put(key*-1, AonMathUtils.round(mapAnt.get(key* -1) + d2));
				} else {
					mapAnt.put(key, AonMathUtils.round(d));
					mapAnt.put(key*-1, AonMathUtils.round(d2));
				}
			} else {
				otherAnt1 = otherAnt1 + AonMathUtils.round(d);
				otherAnt2 = otherAnt2 + AonMathUtils.round(d2);
			}
		}
		
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		PdfPCell empresa = new PdfPCell(new Phrase("PERSONAL ASALARIADO POR CATEGORIAS",getFont1()));
		empresa.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(empresa);
		
		table.addCell(emptyCell());

		PdfPTable table1 = new PdfPTable(5);
		table1.setWidthPercentage(100);
		float[] medidaCeldas1 = {3.8f, 0.55f, 0.55f, 0.55f, 0.55f};
		try {
			table1.setWidths(medidaCeldas1);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}

		table1.addCell(new PdfPCell(new Phrase("Número medio de personas empleadas en el curso del ejercicio, por categorías",getFont1())));
		
		PdfPCell media = new PdfPCell(new Phrase("Media" ,getFont2()));
		media.setHorizontalAlignment(Element.ALIGN_CENTER);
		media.setBorder(PdfPCell.NO_BORDER);
		
		PdfPCell endEj = new PdfPCell(new Phrase("31/12",getFont2()));
		endEj.setHorizontalAlignment(Element.ALIGN_CENTER);
		endEj.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable tableX1 = new PdfPTable(2);
		PdfPCell ejAct = new PdfPCell(new Phrase("EJERCICIO "+ yearAct,getFont1()));
		ejAct.setColspan(2);
		tableX1.addCell(ejAct);
		tableX1.addCell(media);
		tableX1.addCell(endEj);
		PdfPCell x11 = new PdfPCell(tableX1);
		x11.setColspan(2);
		table1.addCell(x11);
		
		PdfPCell ejAnt = new PdfPCell(new Phrase("EJERCICIO " + yearAnt,getFont1()));
		ejAnt.setColspan(2);
		PdfPTable tableX2 = new PdfPTable(2);
		tableX2.addCell(ejAnt);
		tableX2.addCell(media);
		tableX2.addCell(endEj);
		PdfPCell x2 = new PdfPCell(tableX2);
		x2.setColspan(2);
		table1.addCell(x2);
		
		for(Integer i = 0  ; i < array.length() ; i++){
			table1.addCell(stringCell(array.getJSONObject(i).getString("name")));
			
			Double dAct = mapAct.containsKey(array.getJSONObject(i).getInt("id")) ?
					mapAct.get(array.getJSONObject(i).getInt("id")): 0.0;
			table1.addCell(new PdfPCell(new Phrase(dAct.toString(),getFont2())));
			
			Integer dAct2 = mapAct.containsKey(array.getJSONObject(i).getInt("id") * -1) ?
					mapAct.get(array.getJSONObject(i).getInt("id") * -1).intValue() : 0;
			table1.addCell(new PdfPCell(new Phrase(dAct2.toString(),getFont2())));
			
			Double dAnt = mapAnt.containsKey(array.getJSONObject(i).getInt("id")) ?
					mapAnt.get(array.getJSONObject(i).getInt("id")): 0.0;
			table1.addCell(new PdfPCell(new Phrase(dAnt.toString(),getFont2())));
			
			Integer dAnt2 = mapAnt.containsKey(array.getJSONObject(i).getInt("id") * -1) ?
					mapAnt.get(array.getJSONObject(i).getInt("id") * -1).intValue(): 0;
			table1.addCell(new PdfPCell(new Phrase(dAnt2.toString(),getFont2())));
		}
		
		table1.addCell(stringCell("Otros"));
		
		table1.addCell( new PdfPCell(new Phrase(otherAct1.toString(),getFont2())));
		
		Integer otherAct2Int = otherAct2.intValue();
		table1.addCell(new PdfPCell(new Phrase(otherAct2Int.toString(),getFont2())));
		
		table1.addCell(new PdfPCell(new Phrase(otherAnt1.toString(),getFont2())));

		Integer otherAnt2Int = otherAnt2.intValue();
		table1.addCell(new PdfPCell(new Phrase(otherAnt2Int.toString(),getFont2())));
	
		table1.addCell(new PdfPCell(new Phrase("TOTAL EMPLEO MEDIO",getFont1())));
		
		Double totalAct = 0.0, totalAct2 = 0.0, totalAnt = 0.0, totalAnt2 = 0.0 ;
		totalAct = totalAct + otherAct1;
		totalAct2 = totalAct2 + otherAct2;
		totalAnt = totalAnt + otherAnt1;
		totalAnt2 = totalAnt2 + otherAnt2;
		for (Integer s : mapAct.keySet()) {
			if(s < 0 ){
				totalAct2 = totalAct2 + mapAct.get(s);
			}else totalAct = totalAct +mapAct.get(s);
		}
		
		for (Integer s : mapAnt.keySet()) {
			if(s < 0) {
				totalAnt2 = totalAnt2 +mapAnt.get(s);
			} else totalAnt = totalAnt +mapAnt.get(s);
		}
		
		table1.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalAct)),getFont1())));
		
		Integer totalAct2Int = totalAct2.intValue(); 
		table1.addCell(new PdfPCell(new Phrase(totalAct2Int.toString(),getFont1())));
		
		table1.addCell(new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalAnt)),getFont1())));
		
		Integer totalAnt2Int = totalAnt2.intValue();
		table1.addCell(new PdfPCell(new Phrase(totalAnt2Int.toString(),getFont1())));
		
		table1.addCell(emptyCell());
		
		PdfPCell c = new PdfPCell(table1);
		c.setColspan(2);
		table.addCell(c);
		
		return table;
	}
	
	private static PdfPTable getSalariedStaffGC(JSONObject json){
		Integer yearAct = Integer.parseInt(json.getString("year"));
		Integer yearAnt = yearAct - 1;
		JSONArray arrayAct = json.getJSONArray("contract_act");
		JSONArray arrayAnt = json.getJSONArray("contract_ant");
		HashMap<String, Double> mapAct = new HashMap<>();
		HashMap<String, Double> mapAnt = new HashMap<>();
		for (int i = 0; i < arrayAct.length(); i++) {
			JSONObject js = arrayAct.getJSONObject(i);
			String qg = js.getString("quotation_group");
			Double d = js.getDouble("fixed") + js.getDouble("unfixed");
			Double d2 = js.getDouble("end_fixed") + js.getDouble("end_unfixed");
			if(mapAct.containsKey(qg)){
 				mapAct.put(qg, mapAct.get(qg) + AonMathUtils.round(d));
 				mapAct.put(qg+2, mapAct.get(qg+2) + AonMathUtils.round(d2));

			} else {
				mapAct.put(qg, AonMathUtils.round(d));
				mapAct.put(qg+2, AonMathUtils.round(d2));
			}
		}
		for (int i = 0; i < arrayAnt.length(); i++) {
			JSONObject js = arrayAnt.getJSONObject(i);
			String qg = js.getString("quotation_group");
			Double d = js.getDouble("fixed") + js.getDouble("unfixed");
			Double d2 = js.getDouble("end_fixed") + js.getDouble("end_unfixed");
			if(mapAnt.containsKey(qg)){
 				mapAnt.put(qg, AonMathUtils.round(mapAnt.get(qg) + d));
 				mapAnt.put(qg+2, AonMathUtils.round(mapAnt.get(qg+2) + d2));
			} else {
				mapAnt.put(qg, AonMathUtils.round(d));
				mapAnt.put(qg+2, AonMathUtils.round(d2));
			}
		}
		
		PdfPTable table = new PdfPTable(2);
		table.setWidthPercentage(100);
		PdfPCell empresa = new PdfPCell(new Phrase("PERSONAL ASALARIADO POR GRUPOS DE COTIZACIÓN",getFont1()));
		empresa.setBackgroundColor(BaseColor.LIGHT_GRAY);
		table.addCell(empresa);
		
		table.addCell(emptyCell());

		PdfPTable table1 = new PdfPTable(5);
		table1.setWidthPercentage(100);
		float[] medidaCeldas1 = {3.8f, 0.55f, 0.55f, 0.55f, 0.55f};
		try {
			table1.setWidths(medidaCeldas1);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		
		String x ="Número medio de personas empleadas en el curso del ejercicio, por grupos de contización";
		table1.addCell(new PdfPCell(new Phrase(x,getFont1())));
		
		PdfPCell media = new PdfPCell(new Phrase("Media" ,getFont2()));
		media.setHorizontalAlignment(Element.ALIGN_CENTER);
		media.setBorder(PdfPCell.NO_BORDER);
		
		PdfPCell endEj = new PdfPCell(new Phrase("31/12",getFont2()));
		endEj.setHorizontalAlignment(Element.ALIGN_CENTER);
		endEj.setBorder(PdfPCell.NO_BORDER);
		
		PdfPTable tableX1 = new PdfPTable(2);
		PdfPCell ejAct = new PdfPCell(new Phrase("EJERCICIO "+ yearAct,getFont1()));
		ejAct.setColspan(2);
		tableX1.addCell(ejAct);
		tableX1.addCell(media);
		tableX1.addCell(endEj);
		PdfPCell x111 = new PdfPCell(tableX1);
		x111.setColspan(2);
		table1.addCell(x111);
		
		PdfPCell ejAnt = new PdfPCell(new Phrase("EJERCICIO " + yearAnt,getFont1()));
		ejAnt.setColspan(2);
		PdfPTable tableX2 = new PdfPTable(2);
		tableX2.addCell(ejAnt);
		tableX2.addCell(media);
		tableX2.addCell(endEj);
		PdfPCell x22 = new PdfPCell(tableX2);
		x22.setColspan(2);
		table1.addCell(x22);
		
		table1.addCell(stringCell("Ingenieros y Licenciados.Personal de alta dirección no incluido en el artículo 1.3.c) del Estatuto de los Trabajadores"));
		
		Double d = mapAct.containsKey("01") ? mapAct.get("01") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		Integer i = mapAct.containsKey("012") ? mapAct.get("012").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("01") ? mapAnt.get("01") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("012") ? mapAnt.get("012").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		table1.addCell(stringCell("Ingenieros Técnicos, Peritos y Ayudantes Titulados"));
		
		d = mapAct.containsKey("02") ? mapAct.get("02") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAct.containsKey("022") ? mapAct.get("022").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("02") ? mapAnt.get("02") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("022") ? mapAnt.get("022").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		table1.addCell(stringCell("Jefes Administrativos y de Taller"));
		
		d = mapAct.containsKey("03") ? mapAct.get("03") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAct.containsKey("032") ? mapAct.get("032").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("03") ? mapAnt.get("03") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("032") ? mapAnt.get("032").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		table1.addCell(stringCell("Ayudantes no Titulados"));
		
		d = mapAct.containsKey("04") ? mapAct.get("04") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAct.containsKey("042") ? mapAct.get("042").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("04") ? mapAnt.get("04") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("042") ? mapAnt.get("042").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		table1.addCell(stringCell("Oficiales Administrativos"));
		
		d = mapAct.containsKey("05") ? mapAct.get("05") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAct.containsKey("052") ? mapAct.get("052").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("05") ? mapAnt.get("05") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("052") ? mapAnt.get("052").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));

		table1.addCell(stringCell("Subalternos"));
		
		d = mapAct.containsKey("06") ? mapAct.get("06") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAct.containsKey("062") ? mapAct.get("062").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("06") ? mapAnt.get("06") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("062") ? mapAnt.get("062").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		table1.addCell(stringCell("Auxiliares Administrativos"));
		
		d = mapAct.containsKey("07") ? mapAct.get("07") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAct.containsKey("072") ? mapAct.get("072").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("07") ? mapAnt.get("07") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("072") ? mapAnt.get("072").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		table1.addCell(stringCell("Oficiales de primera y segunda"));
		
		d = mapAct.containsKey("08") ? mapAct.get("08") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAct.containsKey("082") ? mapAct.get("082").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("08") ? mapAnt.get("08") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("082") ? mapAnt.get("082").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
	
		table1.addCell(stringCell("Oficiales de tercera y Especialistas"));
		
		d = mapAct.containsKey("09") ? mapAct.get("09") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAct.containsKey("092") ? mapAct.get("092").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("09") ? mapAnt.get("09") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("092") ? mapAnt.get("092").intValue() : 0;
		PdfPCell ejAnt92 = new PdfPCell(new Phrase(i.toString(),getFont2()));
		table1.addCell(ejAnt92);
		
		table1.addCell(stringCell("Peones"));
		
		d = mapAct.containsKey("10") ? mapAct.get("10") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAct.containsKey("102") ? mapAct.get("102").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("10") ? mapAnt.get("10") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("102") ? mapAnt.get("102").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		table1.addCell(stringCell("Trabajadores menores de dieciocho años, cualquiera que sea su categoría profesional"));
		
		d = mapAct.containsKey("11") ? mapAct.get("11") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAct.containsKey("112") ? mapAct.get("112").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		d = mapAnt.containsKey("11") ? mapAnt.get("11") : 0.0;
		table1.addCell(new PdfPCell(new Phrase(d.toString(),getFont2())));
		
		i = mapAnt.containsKey("112") ? mapAnt.get("112").intValue() : 0;
		table1.addCell(new PdfPCell(new Phrase(i.toString(),getFont2())));
		
		String xN ="TOTAL EMPLEO MEDIO";
		table1.addCell(new PdfPCell(new Phrase(xN,getFont1())));
		
		Double totalAct = 0.0, totalAnt = 0.0, totalAct2 = 0.0, totalAnt2 = 0.0;
		for (String s : mapAct.keySet()) {
			if(s.length() == 3){
				totalAct2 = totalAct2 +mapAct.get(s);
			} else totalAct = totalAct +mapAct.get(s);
		}
	
		for (String s : mapAnt.keySet()) {
			if(s.length() == 3){
				totalAnt2 = totalAnt2 +mapAnt.get(s);
			} else totalAnt = totalAnt +mapAnt.get(s);
		}
			
		PdfPCell ejActN = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalAct)),getFont1()));
		table1.addCell(ejActN);
		
		Integer totalAct2Int = totalAct2.intValue();
		PdfPCell ejActN2 = new PdfPCell(new Phrase(totalAct2Int.toString(),getFont1()));
		table1.addCell(ejActN2);
		
		PdfPCell ejAntN = new PdfPCell(new Phrase(Double.toString(AonMathUtils.round(totalAnt)),getFont1()));
		table1.addCell(ejAntN);
		
		Integer totalAnt2Int = totalAnt2.intValue();
		PdfPCell ejAntN2 = new PdfPCell(new Phrase(totalAnt2Int.toString(),getFont1()));
		table1.addCell(ejAntN2);
		
		table1.addCell(emptyCell());
		
		PdfPCell c = new PdfPCell(table1);
		c.setColspan(2);
		table.addCell(c);
		
		return table;
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
	
	private static Font getTitleFont(){
		Font font = new Font();
		font.setSize(16);
		font.setStyle(Font.BOLD);
		return font;
	}
	

}
