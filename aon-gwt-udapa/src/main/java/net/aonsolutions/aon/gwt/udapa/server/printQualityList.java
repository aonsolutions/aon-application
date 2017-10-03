package net.aonsolutions.aon.gwt.udapa.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.json.JSONException;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.DataResponseProperties;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;
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

import net.aonsolutions.aon.gwt.udapa.shared.quality.CleanAptitude;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Destiny;
import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetCode;

@SuppressWarnings("serial")
@WebServlet(name = "pruebawwwwww", urlPatterns = {"/aon_gwt_aio/download_udapa_quality_list/*"})
public class printQualityList extends HttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(printQualityList.class.getName());

	Map<String, String[]> filterMap;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.log(Level.INFO, "Print Quality List - GET METHOD");

		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain");
		String login = parameters.get("login");
		Domain domain = AON.getDomain(domainName, 1, login, f->f.getNameProperty().eq(domainName));	
		String option = parameters.get("option");
		Boolean isList = "list".equals(option);

		File file = null;
		if(isList){
			filterMap = SecurityUtils.getInstance().getParametersMap(req.getPathInfo().substring(1));
			LinkedList<DataResponse> drList = AON.getDataResponseStream(domain.getName(), domain.getId(), login, DataResponseSource.QUALITY,
					f -> dataResponseFilter(domain, filterMap, f)) 
				.collect(Collectors.toCollection(LinkedList::new));

			String type = parameters.get("type");
			if("excel".equals(type)){
				byte[] data = createExcel(domain, login, drList);
				giveBackData(resp, data, "quality.xls");
			} else {
				file = createPdf(domain, login, drList);
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
		}
	}
	
	public static Filter dataResponseFilter(Domain domain, Map<String, String[]> filterMap, DataResponseProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId()).and(f.getSourceProperty().eq(DataResponseSource.QUALITY.value()));
		
		if(filterMap.containsKey("from")){
			String from = filterMap.get("from")[0];
			Date d = new Date(Long.parseLong(from));
			Filter fDate = f.getIssueDateProperty().ge(AonDateUtils.toSql(d));
			filter = filter.and(fDate);
		}
		
		if(filterMap.containsKey("to")){
			String to = filterMap.get("to")[0];
			Date d = new Date(Long.parseLong(to));
			Filter fDate = f.getIssueDateProperty().le(AonDateUtils.toSql(d));
			filter = filter.and(fDate);
		}
		
		if(filterMap.containsKey("number")){
			Filter fnumber = f.getNumberProperty().eq(filterMap.get("number")[0]);
			for(Integer i = 1; i < filterMap.get("number").length ; i++){
				fnumber = fnumber.or(f.getNumberProperty().eq(filterMap.get("number")[i]));
			}
			filter = filter.and(fnumber);
		} 
		
		if(filterMap.containsKey("source")){
			Filter fsourceValue = f.getDetailVariableProperty().eq("source").and(f.getDetailValueProperty().like(filterMap.get("source")[0] + "@%"));				
			for(Integer i = 1; i < filterMap.get("source").length ; i++){
				fsourceValue = fsourceValue.or(f.getDetailVariableProperty().eq("source").and(f.getDetailValueProperty().like(filterMap.get("source")[i] + "@%")));
			}
			filter = filter.and(fsourceValue);
		}

		return filter;
	}
	
	
	public static void giveBackData(HttpServletResponse resp, byte[] data, String name) throws ServletException, IOException{
		Integer length = data.length;
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
	        
		resp.addHeader("Content-Disposition","attachment; filename=\""+name +"\"");
		resp.setContentType("application/msexcel");
		
		if (length > 0 && length <= Integer.MAX_VALUE)
        	resp.setContentLength((int)length);
        ServletOutputStream out = resp.getOutputStream();
        resp.setBufferSize(32768);
        int bufSize = resp.getBufferSize();
        byte[] buffer = new byte[bufSize];
        BufferedInputStream bis = new BufferedInputStream(bais,bufSize);
        int bytes;
        while ((bytes = bis.read(buffer, 0, bufSize)) >= 0)
        	out.write(buffer, 0, bytes);
    
        bis.close();
        bais.close();
        out.flush();
        out.close();
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}
	
	public File createPdf(Domain domain, String login, LinkedList<DataResponse> drList) {
		File archivoPDF = null;
		try {
			archivoPDF = File.createTempFile("quality", "pdf");
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}

		Document document = new Document(PageSize.A4.rotate());
		try {
			PdfWriter.getInstance(document, new FileOutputStream(archivoPDF));
			document.open();
			writeDocument(domain, login, document, drList);	
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
	
	private Boolean hasSupplier(String[] suppliers, String supplier) {
		LinkedList<String> list = new LinkedList<>(Arrays.asList(suppliers));
		return list.contains(supplier);
	}
	
	private Integer cont;
	public byte[] createExcel(Domain domain, String login, LinkedList<DataResponse> drList) {
		byte[] data = null; 
		try {
			ByteArrayOutputStream archivo = new ByteArrayOutputStream();
			HSSFWorkbook libro = new HSSFWorkbook();		
			
			CellStyle style = getStyle(libro);
			CellStyle style2 = getStyle2(libro);
			CellStyle style3 = getStyle3(libro);
			
			HSSFSheet hoja = libro.createSheet("Calidad");
			
			Row fila = hoja.createRow(0);
			
			boldCell(libro, fila, style, 0, "Codigo");
			boldCell(libro, fila, style, 1, "Fecha");
			boldCell(libro, fila, style, 2, "Proveedor");
			boldCell(libro, fila, style, 3, "Destino");
			boldCell(libro, fila, style, 4, "Transporte");
			boldCell(libro, fila, style, 5, "Cantidad");
			boldCell(libro, fila, style, 6, "Product");
			boldCell(libro, fila, style, 7, "Precio");	
			boldCell(libro, fila, style, 8, "<45");
			boldCell(libro, fila, style, 9, "45-50");
			boldCell(libro, fila, style, 10, ">80");
			boldCell(libro, fila, style, 11, "Sin Calibrar");
			boldCell(libro, fila, style, 12, "Tierra");
			boldCell(libro, fila, style, 13, "Defectos");
			boldCell(libro, fila, style, 14, "Lavado");
			boldCell(libro, fila, style, 15, "Total Defectos");
			boldCell(libro, fila, style, 16, "Merma");
			
			cont = 1;
			
			drList.stream().forEach(r -> {
				Map<String, String> map = AON.getDataResponseDetailStream(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(r.getId()))
						.collect(Collectors.toMap(DataResponseDetail::getDataVariable, DataResponseDetail::getDataValue));
				map = UdapaImpl.compute((HashMap<String, String>) map);
				if(!map.containsKey(QualitySheetCode.UFQDP1.getName()) || (map.containsKey(QualitySheetCode.UFQDP1.getName()) && 
						!Destiny.SIEMBRA.equals(Destiny.values()[Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName())) > 0 ? Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName()))-1 : 0]))){
					Optional<IncomeDetail> incomeDetail = AON.getIncomeDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(r.getSourceId()));
					if(incomeDetail.isPresent()){
						Optional<Income> income = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(incomeDetail.get().getIncome().getId()));
						if(income.isPresent() && (!filterMap.containsKey("supplier") || hasSupplier(filterMap.get("supplier"), income.get().getSupplier().toString()))) {
							CarrierPacking carrierPacking = null; 
							if(income.get().getCarrierPacking() != null){
								carrierPacking = AON.getCarrierPacking(domain.getName(), domain.getId(), login, income.get().getCarrierPacking());
							}
							Double a = map.containsKey(QualitySheetCode.UFQCC021.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC021.getName())) : 0.0;
							Double b = map.containsKey(QualitySheetCode.UFQCC041.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC041.getName())) : 0.0;
							Double c = map.containsKey(QualitySheetCode.UFQCC061.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC061.getName())) : 0.0;
							Double d = map.containsKey(QualitySheetCode.UFQCC081.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC081.getName())) : 0.0;
							Double e = (map.containsKey(QualitySheetCode.UFQCC101.getName()) && isPropaco(map)) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC101.getName())) : 0.0;
							Double f = map.containsKey(QualitySheetCode.UFQCD111.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCD111.getName())) : 0.0;
						
							Double merma = a + b + c + d + e + f; 
							
							if(Double.parseDouble(incomeDetail.get().getDiscountExpression()) > 0.0){
								merma = Double.parseDouble(incomeDetail.get().getDiscountExpression());
							}
					
							Row row = hoja.createRow(cont++);
							
						
							
							cell(libro, row, style3, 0, r.getCode());
							cell(libro, row, style3, 1, AonDateUtils.simpleFormat(r.getResponseDate()));
							cell(libro, row, style3, 2, income.isPresent() ? income.get().getSupplierName() : "-");
							cell(libro, row, style3, 3, map.containsKey(QualitySheetCode.UFQDP1.getName()) && !"0".equals(map.get(QualitySheetCode.UFQDP1.getName()))? Destiny.values()[Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName())) - 1].getName() : "-");
							cell(libro, row, style3, 4, carrierPacking != null ? carrierPacking.getNumberPlate() : "-");
							cell(libro, row, style2, 5, incomeDetail.isPresent() ? incomeDetail.get().getQuantity() : 0.0);
							cell(libro, row, style3, 6, incomeDetail.isPresent() ? incomeDetail.get().getDescription() : "-");
							cell(libro, row, style2, 7, incomeDetail.isPresent() ? incomeDetail.get().getPrice() : 0.0);
							cell(libro, row, style2, 8, a);
							cell(libro, row, style2, 9, b);
							cell(libro, row, style2, 10, c);
							cell(libro, row, style2, 11, d);
							if(isPropaco(map)){
								cell(libro, row, style2, 12, e);
							} else cell(libro, row, style2, 12,"(" + e + ")->");
							cell(libro, row, style2, 13, f);
							cell(libro, row, style2, 14, map.containsKey(QualitySheetCode.UFQAC6.getName()) && !map.get(QualitySheetCode.UFQAC6.getName()).equals("0")
								? CleanAptitude.values()[Integer.parseInt(map.get(QualitySheetCode.UFQAC6.getName())) - 1].getName(): "");

							Integer rowIndex = row.getRowNum() + 1;
							String formula = "SUM(H" + rowIndex + ",J" + rowIndex + ",L" + rowIndex + ",M" + rowIndex + ")"; 
							cellFormula(libro, row, style2, 15, formula);

							cell(libro, row, style2, 16, merma);
						}
					}
				}	
			});
			
			HSSFSheet hoja2 = libro.createSheet("Calidad Siembra");
		
			Row fila2 = hoja2.createRow(0);
			
		    boldCell(libro, fila2, style, 0, "Codigo");
			boldCell(libro, fila2, style, 1, "Fecha");
			boldCell(libro, fila2, style, 2, "Proveedor");
			boldCell(libro, fila2, style, 3, "Transporte");
			boldCell(libro, fila2, style, 4, "Cantidad");
			boldCell(libro, fila2, style, 5, "Product");
			boldCell(libro, fila2, style, 6, "Precio");	
			boldCell(libro, fila2, style, 7, "25-40");
			boldCell(libro, fila2, style, 8, "28-35");
			boldCell(libro, fila2, style, 9, "35-45");
			boldCell(libro, fila2, style, 10, "40-50");
			boldCell(libro, fila2, style, 11, "45-50");
			boldCell(libro, fila2, style, 12, "45-55");
			boldCell(libro, fila2, style, 13, "50-55");
			boldCell(libro, fila2, style, 14, ">55");

			
			boldCell(libro, fila2, style, 15, "Sin Calibrar");
			boldCell(libro, fila2, style, 16, "Tierra");
			boldCell(libro, fila2, style, 17, "Defectos");
			boldCell(libro, fila2, style, 18, "Lavado");
			boldCell(libro, fila2, style, 19, "Merma");
			
			cont = 1;
			
			drList.stream().forEach(r -> {
				Map<String, String> map = AON.getDataResponseDetailStream(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(r.getId()))
						.collect(Collectors.toMap(DataResponseDetail::getDataVariable, DataResponseDetail::getDataValue));
				map = UdapaImpl.compute((HashMap<String, String>) map);
				if(map.containsKey(QualitySheetCode.UFQDP1.getName()) && Destiny.SIEMBRA.equals(Destiny.values()[Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName())) > 0 ? Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName()))-1 : 0])){
					Optional<IncomeDetail> incomeDetail = AON.getIncomeDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(r.getSourceId()));
					if(incomeDetail.isPresent()){
						Optional<Income> income = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(incomeDetail.get().getIncome().getId()));
						if(income.isPresent() && (!filterMap.containsKey("supplier") || hasSupplier(filterMap.get("supplier"), income.get().getSupplier().toString()))) {
							CarrierPacking carrierPacking = null; 
							if(income.get().getCarrierPacking() != null){
								carrierPacking = AON.getCarrierPacking(domain.getName(), domain.getId(), login, income.get().getCarrierPacking());
							}
							Double a = map.containsKey(QualitySheetCode.UFQCC161.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC161.getName())) : 0.0;
							Double b = map.containsKey(QualitySheetCode.UFQCC031.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC031.getName())) : 0.0;
							Double c = map.containsKey(QualitySheetCode.UFQCC051.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC051.getName())) : 0.0;
							Double d = map.containsKey(QualitySheetCode.UFQCC171.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC171.getName())) : 0.0;
							Double e = map.containsKey(QualitySheetCode.UFQCC041.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC041.getName())) : 0.0;
							Double f = map.containsKey(QualitySheetCode.UFQCC181.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC181.getName())) : 0.0;
							Double g = map.containsKey(QualitySheetCode.UFQCC091.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC091.getName())) : 0.0;
							Double h = map.containsKey(QualitySheetCode.UFQCC111.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC111.getName())) : 0.0;
							Double i = map.containsKey(QualitySheetCode.UFQCC081.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC081.getName())) : 0.0;
							Double j = map.containsKey(QualitySheetCode.UFQCC101.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC101.getName())) : 0.0;
							Double k = map.containsKey(QualitySheetCode.UFQCD111.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCD111.getName())) : 0.0;

							Double merma = a + b + c + d + e + f + g + h + i + k; 
					
							if(Double.parseDouble(incomeDetail.get().getDiscountExpression()) > 0.0){
								merma = Double.parseDouble(incomeDetail.get().getDiscountExpression());
							}
							
							Row row = hoja2.createRow(cont++);
						
							cell(libro, row, style3, 0, r.getCode());
							cell(libro, row, style3, 1, AonDateUtils.simpleFormat(r.getResponseDate()));
							cell(libro, row, style3, 2, income.isPresent() ? income.get().getSupplierName() : "-");
							cell(libro, row, style3, 3, carrierPacking != null ? carrierPacking.getNumberPlate() : "-");
							cell(libro, row, style2, 4, incomeDetail.isPresent() ? incomeDetail.get().getQuantity() : 0.0);
							cell(libro, row, style3, 5, incomeDetail.isPresent() ? incomeDetail.get().getDescription() : "-");
							cell(libro, row, style2, 6, incomeDetail.isPresent() ? incomeDetail.get().getPrice() : 0.0);
							cell(libro, row, style2, 7, a);
							cell(libro, row, style2, 8, b);
							cell(libro, row, style2, 9, c);
							cell(libro, row, style2, 10, d);
							cell(libro, row, style2, 11, e);
							cell(libro, row, style2, 12, f);
							cell(libro, row, style2, 13, g);
							cell(libro, row, style2, 14, h);
							cell(libro, row, style2, 15, i);
							cell(libro, row, style2, 16, "(" + j +")->");
							cell(libro, row, style2, 17, k);
							cell(libro, row, style3, 18, map.containsKey(QualitySheetCode.UFQAC6.getName()) && !map.get(QualitySheetCode.UFQAC6.getName()).equals("0")
									? CleanAptitude.values()[Integer.parseInt(map.get(QualitySheetCode.UFQAC6.getName())) - 1].getName(): "");
							cell(libro, row, style2, 19, merma);
						}
					}
				}	
			});
			
	        for(int i = 0; i < 20; i++) {
	            hoja.autoSizeColumn(i);
	            
	            hoja2.autoSizeColumn(i);
	        }
		
			libro.write(archivo);

			data = archivo.toByteArray();
			archivo.close();
	        libro.close();
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return data;
	}
	
	
	private void writeDocument(Domain domain, String login, Document document, LinkedList<DataResponse> drList) throws DocumentException, JSONException, ParseException {
		PdfPTable p = new PdfPTable(1);
		p.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		p.setWidthPercentage(100);
		
		PdfPTable t = new PdfPTable(1);
		t.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		t.setWidthPercentage(100);
		t.addCell(new Paragraph("Resumen Ficha Calidad", getTitleFont()));
		
		p.addCell(t);
		
		p.addCell(getSeparator());
		
		PdfPTable tA = new PdfPTable(16);
		float[] medidaCeldas = {0.75f, 0.75f, 1.25f, 1f, 0.75f, 1f, 0.75f, 0.5f, 0.5f, 0.5f, 1f, 0.5f, 0.75f, 0.75f, 0.75f, 0.75f};
		try {
			tA.setWidths(medidaCeldas);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		//tA.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		tA.setWidthPercentage(100);
		
		tA.addCell(boldCell("Codigo"));
		tA.addCell(boldCell("Fecha"));
		tA.addCell(boldCell("Proveedor"));
		tA.addCell(boldCell("Transporte"));
		tA.addCell(boldCell("Cantidad"));
		tA.addCell(boldCell("Producto"));
		tA.addCell(boldCell("Precio"));
		tA.addCell(boldCell("<45"));
		tA.addCell(boldCell("45-50"));
		tA.addCell(boldCell(">80"));
		tA.addCell(boldCell("Sin Calibrar"));
		tA.addCell(boldCell("Tierra"));
		tA.addCell(boldCell("Defectos"));
		tA.addCell(boldCell("Lavado"));
		tA.addCell(boldCell("Total"));
		tA.addCell(boldCell("Merma"));
		
		p.addCell(tA);
		
		p.addCell(getSeparator());

		drList.stream().forEach(r -> {
			Map<String, String> map = AON.getDataResponseDetailStream(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(r.getId()))
			.collect(Collectors.toMap(DataResponseDetail::getDataVariable, DataResponseDetail::getDataValue));
			map = UdapaImpl.compute((HashMap<String, String>) map);
			if(!map.containsKey(QualitySheetCode.UFQDP1.getName()) || (map.containsKey(QualitySheetCode.UFQDP1.getName()) &&
 					!Destiny.SIEMBRA.equals(Destiny.values()[Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName())) > 0 ? Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName()))-1 : 0]))){
				Optional<IncomeDetail> incomeDetail = AON.getIncomeDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(r.getSourceId()));
				if(incomeDetail.isPresent()){
					Optional<Income> income = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(incomeDetail.get().getIncome().getId()));
					if(income.isPresent() && (!filterMap.containsKey("supplier") || hasSupplier(filterMap.get("supplier"), income.get().getSupplier().toString()))) {
						CarrierPacking carrierPacking = null; 
						if(income.get().getCarrierPacking() != null){
							carrierPacking = AON.getCarrierPacking(domain.getName(), domain.getId(), login, income.get().getCarrierPacking());
						}
		
						Double a = map.containsKey(QualitySheetCode.UFQCC021.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC021.getName())) : 0.0;
						Double b = map.containsKey(QualitySheetCode.UFQCC041.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC041.getName())) : 0.0;
						Double c = map.containsKey(QualitySheetCode.UFQCC061.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC061.getName())) : 0.0;
						Double d = map.containsKey(QualitySheetCode.UFQCC081.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC081.getName())) : 0.0;
						Double e = (map.containsKey(QualitySheetCode.UFQCC101.getName()) && isPropaco(map)) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC101.getName())) : 0.0;
						Double f = map.containsKey(QualitySheetCode.UFQCD111.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCD111.getName())) : 0.0;
					
						Double total = a + c +(isPropaco(map) ? e : 0.0) + f;
						Double merma = a + b + c + d + e + f; 
				
						if(Double.parseDouble(incomeDetail.get().getDiscountExpression()) > 0.0){
							merma = Double.parseDouble(incomeDetail.get().getDiscountExpression());
						}
					
						PdfPTable ta = new PdfPTable(16);
						try {
							ta.setWidths(medidaCeldas);
						} catch (DocumentException e1) {
							LOGGER.log(Level.SEVERE, e1.getMessage());
						}
						ta.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
						ta.setWidthPercentage(100);	
					
						ta.addCell(cell(r.getCode()));
						ta.addCell(cell(AonDateUtils.simpleFormat(r.getResponseDate())));
						ta.addCell(cell(income.isPresent() ? income.get().getSupplierName() : "-"));
						ta.addCell(cell(carrierPacking != null ? carrierPacking.getNumberPlate() : "-"));
						ta.addCell(cell(incomeDetail.isPresent() ? Double.toString(incomeDetail.get().getQuantity()) : "0.0"));
						ta.addCell(cell(incomeDetail.isPresent() ? incomeDetail.get().getDescription() : "-"));
						ta.addCell(cell(incomeDetail.isPresent() ? incomeDetail.get().getPrice().toString() : "0.0"));
						ta.addCell(cell(a.toString()));
						ta.addCell(cell(b.toString()));
						ta.addCell(cell(c.toString()));  
						ta.addCell(cell(d.toString()));
						ta.addCell(cell(isPropaco(map) ? e.toString() : "(" + e + ")->"));
						ta.addCell(cell(f.toString()));
						ta.addCell(cell(map.containsKey(QualitySheetCode.UFQAC6.getName()) && !map.get(QualitySheetCode.UFQAC6.getName()).equals("0")
								? CleanAptitude.values()[Integer.parseInt(map.get(QualitySheetCode.UFQAC6.getName())) - 1].getName(): "")); 
						
						ta.addCell(cell(total.toString()));
						ta.addCell(cell(merma.toString()));
						
						p.addCell(ta);
					}
				}
			}	
		});
		
		p.addCell(getSeparator());
		
		PdfPTable tB = new PdfPTable(20);
		float[] medidaCeldasB = {0.7f, 0.85f, 1f, 1f, 0.75f, 1f, 0.6f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 1f, 0.6f, 0.75f, 0.6f, 0.6f};
		try {
			tB.setWidths(medidaCeldasB);
		} catch (DocumentException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
		//tB.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
		tB.setWidthPercentage(100);
		tB.addCell(boldCell("Codigo"));
		tB.addCell(boldCell("Fecha"));
		tB.addCell(boldCell("Proveedor"));
		tB.addCell(boldCell("Transporte"));
		tB.addCell(boldCell("Cantidad"));
		tB.addCell(boldCell("Producto"));
		tB.addCell(boldCell("Precio"));
		tB.addCell(boldCell("25-40"));
		tB.addCell(boldCell("28-35"));
		tB.addCell(boldCell("35-45"));
		tB.addCell(boldCell("40-50"));
		tB.addCell(boldCell("45-50"));
		tB.addCell(boldCell("45-55"));
		tB.addCell(boldCell("50-55"));
		tB.addCell(boldCell(">55"));
		tB.addCell(boldCell("Sin Calibrar"));
		tB.addCell(boldCell("Tierra"));
		tB.addCell(boldCell("Defectos"));
		tB.addCell(boldCell("Lavado"));
		tB.addCell(boldCell("Merma"));
	
		p.addCell(tB);

		p.addCell(getSeparator());

		drList.stream().forEach(r -> {
			Map<String, String> map = AON.getDataResponseDetailStream(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(r.getId()))
			.collect(Collectors.toMap(DataResponseDetail::getDataVariable, DataResponseDetail::getDataValue));
			map = UdapaImpl.compute((HashMap<String, String>) map);
			if(map.containsKey(QualitySheetCode.UFQDP1.getName()) && Destiny.SIEMBRA.equals(Destiny.values()[Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName())) > 0 ? Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName()))-1 : 0])){
				Optional<IncomeDetail> incomeDetail = AON.getIncomeDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(r.getSourceId()));
				if(incomeDetail.isPresent()){
					Optional<Income> income = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(incomeDetail.get().getIncome().getId()));
					if(income.isPresent() && (!filterMap.containsKey("supplier") || hasSupplier(filterMap.get("supplier"), income.get().getSupplier().toString()))) {
						CarrierPacking carrierPacking = null; 
						if(income.get().getCarrierPacking() != null){
							carrierPacking = AON.getCarrierPacking(domain.getName(), domain.getId(), login, income.get().getCarrierPacking());
						}	
				
						Double a = map.containsKey(QualitySheetCode.UFQCC161.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC161.getName())) : 0.0;
						Double b = map.containsKey(QualitySheetCode.UFQCC031.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC031.getName())) : 0.0;
						Double c = map.containsKey(QualitySheetCode.UFQCC051.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC051.getName())) : 0.0;
						Double d = map.containsKey(QualitySheetCode.UFQCC171.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC171.getName())) : 0.0;
						Double e = map.containsKey(QualitySheetCode.UFQCC041.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC041.getName())) : 0.0;
						Double f = map.containsKey(QualitySheetCode.UFQCC181.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC181.getName())) : 0.0;
						Double g = map.containsKey(QualitySheetCode.UFQCC091.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC091.getName())) : 0.0;
						Double h = map.containsKey(QualitySheetCode.UFQCC111.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC111.getName())) : 0.0;
						Double i = map.containsKey(QualitySheetCode.UFQCC081.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC081.getName())) : 0.0;
						Double j = map.containsKey(QualitySheetCode.UFQCC101.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCC101.getName())) : 0.0;
						Double k = map.containsKey(QualitySheetCode.UFQCD111.getName()) ? Double.parseDouble(map.get(QualitySheetCode.UFQCD111.getName())) : 0.0;
						
						Double merma = a + b + c + d + e + f + g + h + i + k; 
						
						if(Double.parseDouble(incomeDetail.get().getDiscountExpression()) > 0.0){
							merma = Double.parseDouble(incomeDetail.get().getDiscountExpression());
						}	
						
						PdfPTable tb = new PdfPTable(20);
						try {
							tb.setWidths(medidaCeldasB);
						} catch (DocumentException e1) {
							LOGGER.log(Level.SEVERE, e1.getMessage());
						}
						tb.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
						tb.setWidthPercentage(100);	
						
						tb.addCell(cell(r.getCode()));
						tb.addCell(cell(AonDateUtils.simpleFormat(r.getResponseDate())));
						tb.addCell(cell(income.isPresent() ? income.get().getSupplierName() : "-"));
						tb.addCell(cell(carrierPacking != null ? carrierPacking.getNumberPlate() : "-"));
						tb.addCell(cell(incomeDetail.isPresent() ? Double.toString(incomeDetail.get().getQuantity()) : "0.0"));
						tb.addCell(cell(incomeDetail.isPresent() ? incomeDetail.get().getDescription() : "-"));
						tb.addCell(cell(incomeDetail.isPresent() ? incomeDetail.get().getPrice().toString() : "0.0"));
						tb.addCell(cell(a.toString()));
						tb.addCell(cell(b.toString()));
						tb.addCell(cell(c.toString()));
						tb.addCell(cell(d.toString()));
						tb.addCell(cell(e.toString()));
						tb.addCell(cell(f.toString()));
						tb.addCell(cell(g.toString()));
						tb.addCell(cell(h.toString()));
						tb.addCell(cell(i.toString()));
						tb.addCell(cell("(" + j + ")->"));
						tb.addCell(cell(k.toString()));
						tb.addCell(cell(map.containsKey(QualitySheetCode.UFQAC6.getName()) && !map.get(QualitySheetCode.UFQAC6.getName()).equals("0")
								? CleanAptitude.values()[Integer.parseInt(map.get(QualitySheetCode.UFQAC6.getName())) - 1].getName(): ""));
						tb.addCell(cell(merma.toString()));
					
						p.addCell(tb);
					}
				}	
			}
		});
		
		document.add(p);
	}
    
	private static Paragraph getSeparator(){
		Paragraph separator = new Paragraph();
		LineSeparator line = new LineSeparator();
        line.setOffset(5);
        separator.add(line);
        return separator;
	}

	// -------------------- EXCEL UTILS
	
	private Cell boldCell(HSSFWorkbook libro, Row row, CellStyle style, Integer index, String str) {
		Cell cell = row.createCell(index);
		cell.setCellValue(str);
		row.setHeightInPoints(16);
		cell.setCellStyle(style);	
		return cell;
	}
	
	private Cell cell(HSSFWorkbook libro, Row row, CellStyle style, Integer index, String str) {
		Cell cell = row.createCell(index);
		cell.setCellValue(str);
		row.setHeightInPoints(16);
		cell.setCellStyle(style);	
		return cell;
	}
	
	private Cell cell(HSSFWorkbook libro, Row row, CellStyle style, Integer index, Double dbl) {
		Cell cell = row.createCell(index);
		cell.setCellValue(dbl);
		row.setHeightInPoints(16);
		cell.setCellStyle(style);	
		return cell;
	}
	
	private Cell cellFormula(HSSFWorkbook libro, Row row, CellStyle style, Integer index, String formula) {
		Cell cell = row.createCell(index);
		cell.setCellFormula(formula);
		row.setHeightInPoints(16);
		cell.setCellStyle(style);	
		return cell;
	}
	
	private CellStyle getStyle(HSSFWorkbook libro){		
		CellStyle style = libro.createCellStyle();
		HSSFFont font = libro.createFont();
		font.setFontHeightInPoints((short)12);
		font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		style.setFont(font);
		style.setAlignment(CellStyle.ALIGN_CENTER);
		style.setBorderBottom(CellStyle.BORDER_MEDIUM); 
		return style;
	}
	
	private CellStyle getStyle2(HSSFWorkbook libro){	
		CellStyle style2 = libro.createCellStyle();
		HSSFFont font2 = libro.createFont();
		font2.setFontHeightInPoints((short)12);
		style2.setFont(font2);
		style2.setAlignment(CellStyle.ALIGN_RIGHT);
		style2.setBorderBottom(CellStyle.BORDER_THIN);
		style2.setBorderRight(CellStyle.BORDER_THIN);
		style2.setBorderLeft(CellStyle.BORDER_THIN);
		return style2;
	}
	
	private CellStyle getStyle3(HSSFWorkbook libro){	
		CellStyle style3 = libro.createCellStyle();
     	HSSFFont font2 = libro.createFont();
     	font2.setFontHeightInPoints((short)12);
		style3.setFont(font2);
		style3.setAlignment(CellStyle.ALIGN_LEFT);
		style3.setBorderBottom(CellStyle.BORDER_THIN);
		style3.setBorderRight(CellStyle.BORDER_THIN);
		style3.setBorderLeft(CellStyle.BORDER_THIN);
		style3.setBorderTop(CellStyle.BORDER_THIN);	
		return style3;
	}
	
	// -------------------- PDF UTILS
	
	private static PdfPCell boldCell(String str) {
		PdfPCell cell = new PdfPCell(new Phrase(str,getFont1()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	private static PdfPCell cell(String str) {
		PdfPCell cell = new PdfPCell(new Phrase(str,getFont2()));
		cell.setBorder(PdfPCell.NO_BORDER);
		return cell;
	}
	
	private static Font getTitleFont(){
		Font font = new Font();
		font.setSize(16);
		font.setStyle(Font.BOLD);
		return font;
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

	
	
	private static Boolean isPropaco(Map<String, String> map) {
		return map.containsKey(QualitySheetCode.UFQDP1.getName()) && 
			(map.get(QualitySheetCode.UFQDP1.getName()).equals(Integer.toString(Destiny.BASERRI.ordinal() + 1))
			|| map.get(QualitySheetCode.UFQDP1.getName()).equals(Integer.toString(Destiny.EUSKOLABEL.ordinal() + 1)));
	}

}
