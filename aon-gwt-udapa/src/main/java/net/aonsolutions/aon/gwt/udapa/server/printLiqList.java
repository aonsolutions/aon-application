package net.aonsolutions.aon.gwt.udapa.server;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.DataResponseProperties;
import com.esferalia.aon.occam.api.model.management.PurchaseDetail;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonMathUtils;

import net.aonsolutions.aon.gwt.udapa.client.Utils;
import net.aonsolutions.aon.gwt.udapa.shared.quality.Destiny;
import net.aonsolutions.aon.gwt.udapa.shared.quality.QualitySheetCode;

@SuppressWarnings("serial")
@WebServlet(name = "Download Udapa Liq List", urlPatterns = {"/aon_gwt_aio/download_udapa_liq_list/*"})
public class printLiqList extends HttpServlet{

	private static final Logger LOGGER  = Logger.getLogger(printLiqList.class.getName());

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

		if(isList){
			filterMap = SecurityUtils.getInstance().getParametersMap(req.getPathInfo().substring(1));
			LinkedList<DataResponse> drList = AON.getDataResponseStream(domain.getName(), domain.getId(), login, DataResponseSource.QUALITY,
					f -> dataResponseFilter(domain, filterMap, f)) 
				.collect(Collectors.toCollection(LinkedList::new));

			String type = parameters.get("type");
			if("excel".equals(type)){
				byte[] data = createExcel(domain, login, drList);
				giveBackData(resp, data, "quality.xls");
			}
		}
	}
	
	public static Filter dataResponseFilter(Domain domain, Map<String, String[]> filterMap, DataResponseProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId()).and(f.getSourceProperty().eq(DataResponseSource.QUALITY.value()))
				.and(f.getDetailVariableProperty().eq(QualitySheetCode.UFQDP1.getName()).and(
					f.getDetailValueProperty().eq(Integer.toString(Destiny.BASERRI.ordinal() +1))
					.or(f.getDetailValueProperty().eq(Integer.toString(Destiny.EUSKOLABEL.ordinal()+ 1)))
				));
		
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
			CellStyle style3 = getStyle3(libro);
			
			HSSFSheet hoja = libro.createSheet("PROPACO");
			
			Row fila = hoja.createRow(0);
			
			boldCell(libro, fila, style, 0, "Codigo");
			boldCell(libro, fila, style, 1, "Fecha");
			boldCell(libro, fila, style, 2, "Proveedor");
			
			boldCell(libro, fila, style, 3, "Cantidad");
			boldCell(libro, fila, style, 4, "Precio");
			boldCell(libro, fila, style, 5, "Importe");
			
			boldCell(libro, fila, style, 6, "Peso Bruto");
			boldCell(libro, fila, style, 7, "Variedad");
			boldCell(libro, fila, style, 8, "Destino");
			boldCell(libro, fila, style, 9, "% <45");
			boldCell(libro, fila, style, 10, "kgs peq");
			boldCell(libro, fila, style, 11, "% >80");
			boldCell(libro, fila, style, 12, "KG DE >80 >5%");	
			boldCell(libro, fila, style, 13, "% Tierra");
			boldCell(libro, fila, style, 14, "kgs tierra");
			boldCell(libro, fila, style, 15, "Kgs (peq+gordas+tierra)");
			boldCell(libro, fila, style, 16, "Kilos netos sin % dto");
			boldCell(libro, fila, style, 17, "% Defectos");
			boldCell(libro, fila, style, 18, "Prima Merma");
			boldCell(libro, fila, style, 19, "% Dto - prima");
			boldCell(libro, fila, style, 20, "Kgs Dto");
			
			boldCell(libro, fila, style, 21, "Kgs netos finales");
			boldCell(libro, fila, style, 22, "precio contrato");
			boldCell(libro, fila, style, 23, "p FONDO");
			boldCell(libro, fila, style, 24, "subidas a medias");
			boldCell(libro, fila, style, 25, "PRECIO LABEL");
			boldCell(libro, fila, style, 26, "PRECIO EB");
			boldCell(libro, fila, style, 27, "Euros Base");
			boldCell(libro, fila, style, 28, "Temp");
			boldCell(libro, fila, style, 29, "prima");
			boldCell(libro, fila, style, 30, "Color");
			boldCell(libro, fila, style, 31, "EUR. PRIMA LAV");
			boldCell(libro, fila, style, 32, "EUR. prima peq");
			boldCell(libro, fila, style, 33, "EUR. PEQ");
			boldCell(libro, fila, style, 34, "EUR. GOR");
			boldCell(libro, fila, style, 35, "EUR. netos");
			boldCell(libro, fila, style, 36, "EUR./Kg bruto");
			boldCell(libro, fila, style, 37, "EUR./Kg neto final");
			boldCell(libro, fila, style, 38, "EUR. TOTAL");
			boldCell(libro, fila, style, 39, "> 80 SIN BONIF");
			boldCell(libro, fila, style, 40, "DTO SIN BON");

			cont = 1;
			
			drList.stream().forEach(r -> {
				Map<String, String> map = new HashMap<>();
				for(DataResponseDetail drd : AON.getDataResponseDetailStream(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(r.getId()))
						.collect(Collectors.toCollection(LinkedList::new))){
					map.put(drd.getDataVariable(), drd.getDataValue());
				}
				
				if(!map.containsKey(QualitySheetCode.UFQDP1.getName()) || (map.containsKey(QualitySheetCode.UFQDP1.getName()) && 
						!Destiny.SIEMBRA.equals(Destiny.values()[Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName())) > 0 ? Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName()))-1 : 0]))){
					Optional<IncomeDetail> incomeDetail = AON.getIncomeDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(r.getSourceId()));
					if(incomeDetail.isPresent()){
						if(!map.containsKey(QualitySheetCode.UFQCC01.getName())) {
							map.put(QualitySheetCode.UFQCC01.getName(), Double.toString(incomeDetail.get().getQuantity()));
						}
						map = UdapaImpl.compute((HashMap<String, String>) map);
						Optional<Income> income = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(incomeDetail.get().getIncome().getId()));
						if(income.isPresent() && (!filterMap.containsKey("supplier") || hasSupplier(filterMap.get("supplier"), income.get().getSupplier().toString()))) {
							CarrierPacking carrierPacking = null; 
							if(income.get().getCarrierPacking() != null){
								carrierPacking = AON.getCarrierPacking(domain.getName(), domain.getId(), login, income.get().getCarrierPacking());
							}

							Row row = hoja.createRow(cont++);

							cell(libro, row, style3, 0, r.getCode());
							cell(libro, row, style3, 1, AonDateUtils.simpleFormat(r.getResponseDate()));
							cell(libro, row, style3, 2, income.isPresent() ? income.get().getSupplierName() : "-");
							cell(libro, row, style3, 3, incomeDetail.get().getQuantity());
							cell(libro, row, style3, 4, incomeDetail.get().getPrice());
							cell(libro, row, style3, 5, AonMathUtils.round(incomeDetail.get().getQuantity()* incomeDetail.get().getPrice()));
							
							// PESO BRUTO
							Double quantity = carrierPacking.getNet();
							cell(libro, row, style3, 6, quantity);
							
							// DESCRIPCIÓN PRODUCTO / VARIEDAD
							cell(libro, row, style3, 7, incomeDetail.get().getDescription());
							
							// DESTINO
							Destiny destiny = Destiny.values()[Integer.parseInt(map.get(QualitySheetCode.UFQDP1.getName())) - 1];
							cell(libro, row, style3, 8, destiny.name());
							
							// % PEQUEÑA
							String per_peq = map.containsKey(QualitySheetCode.UFQCC021.getName()) ? map.get(QualitySheetCode.UFQCC021.getName()) : "0.0";
							Double perPeq = Double.parseDouble(per_peq);
							cell(libro, row, style3, 9, perPeq);
							
							// KG PEQUEÑA
							Double kgPeq = (quantity * perPeq)/100;
							cell(libro, row, style3, 10, kgPeq);
							
							// % GORDA
							String per_gor = map.containsKey(QualitySheetCode.UFQCC061.getName()) ? map.get(QualitySheetCode.UFQCC061.getName()) : "0.0";
							Double perGor = Double.parseDouble(per_gor);
							cell(libro, row, style3, 11, perGor);

							// KG GORDA
							Double kgGor = perGor > 5 ? (quantity * (perGor-5))/100 : 0.0;
							cell(libro, row, style3, 12, kgGor);	
							
							// % TIERRA
							String per_ter = map.containsKey(QualitySheetCode.UFQCC101.getName()) ? map.get(QualitySheetCode.UFQCC101.getName()) : "0.0";
							Double perTer = Double.parseDouble(per_ter);
							cell(libro, row, style3, 13, perTer);

							// KG TIERRA
							Double kgTer = (quantity * perTer)/100;
							cell(libro, row, style3, 14, AonMathUtils.round(kgTer));
							
							// KGS (KG PEQUEÑA + KG GORDA + KG TIERRA)
							Double kgPeqGorTer = kgPeq + kgGor + kgTer;
							cell(libro, row, style3, 15, AonMathUtils.round(kgPeqGorTer));
							
							// KG NETO SIN DTO
							Double kgNetSin = quantity - kgPeqGorTer;
							cell(libro, row, style3, 16, AonMathUtils.round(kgNetSin));

							// % DEFECTOS
							String per_def = map.containsKey(QualitySheetCode.UFQCD111.getName()) ? map.get(QualitySheetCode.UFQCD111.getName()) : "0.0";
							Double perDef = Double.parseDouble(per_def);
							cell(libro, row, style3, 17, perDef);
							Double primaMerma = perDef < 7 ? 4 : 0.0;
							cell(libro, row, style3, 18, primaMerma);
							Double dtoPrima = perDef < 4 ? 0.0 : perDef - primaMerma;
							cell(libro, row, style3, 19, dtoPrima);
							Double kgDef = (kgNetSin * dtoPrima)/100;
							cell(libro, row, style3, 20, AonMathUtils.round(kgDef));
							
							// KG NETOS FINALES
							Double kgNet = kgNetSin - kgDef;
							cell(libro, row, style3, 21, AonMathUtils.round(kgNet));

							// PRECIO CONTRATO
							if(!map.containsKey("product_price")) {
								DataResponseDetail drd = new DataResponseDetail();
								drd.setDomain(domain.getId());
								drd.setDataResponse(r.getId());
								drd.setDataVariable("product_price");
								drd.setDataValue(incomeDetail.get().getPrice().toString());
								AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
								map.put("product_price", incomeDetail.get().getPrice() + "");
							}  else if(incomeDetail.get().getPurchaseDetail() != null) {
								PurchaseDetail pd = AON.getPurchaseDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(incomeDetail.get().getPurchaseDetail()));
								if(pd.getPrice() > 0.0) {
									DataResponseDetail drd = new DataResponseDetail();
									drd.setDomain( domain.getId());
									drd.setDataResponse(r.getId());
									drd.setDataVariable("product_price");
									drd.setDataValue(pd.getPrice() + "");
									AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
									map.put("product_price", pd.getPrice() + "");	
								}
							}
							String product_price =  map.containsKey("product_price") ? map.get("product_price") : incomeDetail.get().getPrice().toString();
							Double contractPrice = Double.parseDouble(product_price.replace(",", "."));
							cell(libro, row, style3, 22, contractPrice);
							
							// P FONDO
							String p_fondo = map.containsKey(QualitySheetCode.UFQC2.getName()) ?  map.get(QualitySheetCode.UFQC2.getName()) : "0.0";
							if("0.0".equals(p_fondo)){
								ApplicationParameter app = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.QUALITY_PFONDO);
								p_fondo = app != null ? app.getValue(): "0.0";
							} else {
								LinkedList<DataResponseDetail> dddd = AON.getDataResponseDetailStream(domain.getName(), domain.getId(), login, f -> 
								f.getDataResponseProperty().eq(r.getId()).and(f.getDataVariableProperty().eq(QualitySheetCode.UFQC2.getName())))
								.sorted((o1, o2) -> o2.getModificationDate().compareTo(o1.getModificationDate())).collect(Collectors.toCollection(LinkedList::new));
								if(dddd.size()> 1) {
									map.put(QualitySheetCode.UFQC2.getName(), dddd.get(0).getDataValue());
									p_fondo = dddd.get(0).getDataValue();
									for(Integer j = 1; j < dddd.size(); j++) {
										Integer n = j;
										AON.deleteDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(dddd.get(n).getId()));
									}
								}
							}
							Double pFondo = Double.parseDouble(p_fondo.replace(",", "."));
							cell(libro, row, style3, 23, pFondo);
							
							// SUBIDAS A MEDIAS
							Double z = pFondo > contractPrice ? (pFondo - contractPrice) * 0.55 : 0.0;
							cell(libro, row, style3, 24, z);
							
							// PRECIO
							Double price = contractPrice + z;
							if(Destiny.BASERRI.equals(destiny)) {
								price = price * 0.88;
							}
							cell(libro, row, style3, 25, Destiny.EUSKOLABEL.equals(destiny) ? price : 0.0);
							cell(libro, row, style3, 26, Destiny.BASERRI.equals(destiny) ? price : 0.0);
							cell(libro, row, style3, 27, price);
						
							// TEMPERATURA
							String tempStr = map.containsKey(QualitySheetCode.UFQAC1.getName()) ?  map.get(QualitySheetCode.UFQAC1.getName()) : "17.0";
							Double temp = Double.parseDouble(tempStr);
							cell(libro, row, style3, 28, temp);
				
							String transportDate =  map.get("transport_delivery_date");
							Date issueDate = new Date();
							if(transportDate != null && !"".equals(transportDate)
									&& !"-".equals(transportDate)){
								issueDate = Utils.parseDateTime(transportDate);
							}
							Date start = new Date((2019-1900), 8, 1);

							Boolean a = temp >= 8.0 && temp <= 16.0 && issueDate.compareTo(start) >= 0;
							Boolean b = temp >= 22.0 && temp <= 24.0 && issueDate.compareTo(start) >= 0;
							Boolean c = temp > 24.0 && issueDate.compareTo(start) >= 0;
							Boolean d = temp < 17.0 && issueDate.compareTo(start) < 0;
							
							Double tempVar = 1.0;
							if(a || d) tempVar = 1.03;
							else if(b) tempVar = 0.95;
							else if(c) tempVar = 0.85;
							// PRIMA
							Double prima = price * tempVar;
							cell(libro, row, style3, 29, AonMathUtils.round(prima,3));
							
							// COLOR
							String col = map.containsKey(QualitySheetCode.UFQAC8.getName()) ? map.get(QualitySheetCode.UFQAC8.getName()) : "0.0";
							Double color = "1".equals(col) || "1.0".equals(col) ? 0.003 : 0.0; 
							cell(libro, row, style3, 30, color);
							// PRIMA LAVADO
							cell(libro, row, style3, 31, color);

							// PRIMA PEQUEÑA
							Double primaPeq = Destiny.BASERRI.equals(destiny) ? 0.06 : 0.08;
							cell(libro, row, style3, 32, primaPeq);
							
							// EUROS PEQUEÑA
							Double eurosPeq = kgPeq * primaPeq;
							cell(libro, row, style3, 33, AonMathUtils.round(eurosPeq));

							// EUROS GORDA
							Double eurosGor = 0.7 * kgGor * prima;
							cell(libro, row, style3, 34, AonMathUtils.round(eurosGor));
							
							Double eurosNet = kgNet * (prima + color);
							cell(libro, row, style3, 35, AonMathUtils.round(eurosNet));				
							
							Double totalEuros = eurosPeq + eurosGor + eurosNet;
							Double eurosKgBruto2 = totalEuros / quantity;
							cell(libro, row, style3, 36, AonMathUtils.round(eurosKgBruto2,4));
							
							Double eurosKgNeto2 = totalEuros / kgNet;
							cell(libro, row, style3, 37, AonMathUtils.round(eurosKgNeto2,4));

							Double totalEuros2 =  AonMathUtils.round(kgNet) * AonMathUtils.round(eurosKgNeto2, 4);
							cell(libro, row, style3, 38, AonMathUtils.round(totalEuros2));
		
							Double sinBon802 = ((quantity - kgPeq - kgGor - kgTer) * perGor) /100;
							cell(libro, row, style3, 39, AonMathUtils.round(sinBon802));
							
							Double dtoSinBon2 = ((quantity - kgPeq - kgGor - kgTer) * perDef) /100;
							cell(libro, row, style3, 40, AonMathUtils.round(dtoSinBon2));
							
							if(!incomeDetail.get().getPrice().equals(AonMathUtils.round(eurosKgNeto2,4))
									|| !incomeDetail.get().getQuantity().equals(AonMathUtils.round(kgNet))) {							
								cell(libro, row, style3, 3, AonMathUtils.round(kgNet));
								cell(libro, row, style3, 4, AonMathUtils.round(eurosKgNeto2,4));
								cell(libro, row, style3, 5, AonMathUtils.round(AonMathUtils.round(kgNet)*AonMathUtils.round(eurosKgNeto2,4)));
								
								UdapaImpl.getInstance().updateIncomeDetail(domain.getName(), domain.getId(), AonMathUtils.round(eurosKgNeto2,4),
									AonMathUtils.round(kgNet), incomeDetail.get().getId());
							}	
						}
					}
				}	
			});
			
	        for(int i = 0; i < 20; i++) {
	            hoja.autoSizeColumn(i);
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
	

  

	// -------------------- EXCEL UTILS
	
	private Cell boldCell(HSSFWorkbook libro, Row row, CellStyle style, Integer index, String str) {
		Cell cell = row.createCell(index);
		cell.setCellValue(str);
		row.setHeightInPoints(40);
		cell.setCellStyle(style);	
		return cell;
	}
	
	private Cell cell(HSSFWorkbook libro, Row row, CellStyle style, Integer index, String str) {
		Cell cell = row.createCell(index);
		cell.setCellValue(str);
		row.setHeightInPoints(25);
		cell.setCellStyle(style);	
		return cell;
	}
	
	private Cell cell(HSSFWorkbook libro, Row row, CellStyle style, Integer index, Double dbl) {
		Cell cell = row.createCell(index);
		cell.setCellValue(dbl);
		row.setHeightInPoints(25);
		cell.setCellStyle(style);	
		return cell;
	}
	
	private CellStyle getStyle(HSSFWorkbook libro){		
		CellStyle style = libro.createCellStyle();
		style.setWrapText(true);
		HSSFFont font = libro.createFont();
		font.setFontHeightInPoints((short)12);
		font.setBold(true);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.MEDIUM); 
		return style;
	}
	
	private CellStyle getStyle3(HSSFWorkbook libro){	
		CellStyle style3 = libro.createCellStyle();
     	HSSFFont font2 = libro.createFont();
     	font2.setFontHeightInPoints((short)10);
		style3.setFont(font2);
		style3.setAlignment(HorizontalAlignment.LEFT);
		style3.setBorderBottom(BorderStyle.THIN);
		style3.setBorderRight(BorderStyle.THIN);
		style3.setBorderLeft(BorderStyle.THIN);
		style3.setBorderTop(BorderStyle.THIN);	
		return style3;
	}

}
