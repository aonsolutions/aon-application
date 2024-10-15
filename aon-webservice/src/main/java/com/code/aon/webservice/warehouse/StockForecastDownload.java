package com.code.aon.webservice.warehouse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.SecurityUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.impl.jooq.dao.StatDAO;
import com.esferalia.aon.watson.server.AonDateUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet(name = "stockForecastProjection", urlPatterns = { "/aon_gwt_aio/ms/download_stockForecast/*" })
public class StockForecastDownload extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HashMap<String, String> parameters = SecurityUtils.getInstance().getParameters(req.getPathInfo().substring(1));
		String domainName = parameters.get("domain"), login = parameters.get("login");
		Domain domain = AON.getDomain(domainName, 1, login, f -> f.getNameProperty().eq(domainName));
		Map<String, String[]> filterMap = SecurityUtils.getInstance().getParametersMap(req.getPathInfo().substring(1));

		HSSFWorkbook book = createXls(domain, login, filterMap);
		
        ByteArrayOutputStream archivo = new ByteArrayOutputStream();
        book.write(archivo);  
        byte[] data = archivo.toByteArray();
        archivo.close();
        book.close();
        String name = "aprovisionamiento-" + dateFormat.format(new Date());
        Utils.giveBackData(resp, data , name + ".xls");
	}

	private HSSFWorkbook createXls(Domain domain, String login, Map<String, String[]> filterMap) {
		HSSFWorkbook book = new HSSFWorkbook();
		HSSFSheet sheet = book.createSheet("Aprovisionamiento");
		
		CellStyle style1 = createStyle1(book);
		CellStyle style2 = createStyle2(book); 
		CellStyle style3 = createStyle3(book); 
		
		int rowCount = 0;
		Row headerRow1 = createRow(sheet, rowCount++, style1);
//		fillHeaderRow(book, headerRow1, style1);

		Row headerRow2 = createRow(sheet, rowCount++, style2);
		fillHeaderRow(book, headerRow2, style2);

		createContent(sheet, rowCount, style3, domain, login, filterMap);

		for (Integer i = 0; i < sheet.getPhysicalNumberOfRows(); i++)
			sheet.autoSizeColumn(i);

		return book;
	}


	/* **************************************
	 * XLS METHODS
	 * **************************************/
	private CellStyle createStyle1(HSSFWorkbook book) {
		CellStyle style = book.createCellStyle();
		Font font = book.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.MEDIUM);
		return style;
	}

	private CellStyle createStyle2(HSSFWorkbook book) {
		CellStyle style = book.createCellStyle();
		Font font = book.createFont();
		font.setFontHeightInPoints((short) 12);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.RIGHT);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		return style;
	}

	private CellStyle createStyle3(HSSFWorkbook book) {
		CellStyle style = book.createCellStyle();
		Font font = book.createFont();
		font.setFontHeightInPoints((short) 12);
		style.setFont(font);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		return style;
	}
	
	private void fillHeaderRow(HSSFWorkbook book, Row row, CellStyle style) {
		row.setHeightInPoints(16);
		int cellIndex = 0;
		row.getCell(cellIndex++).setCellValue("Producto");
		row.getCell(cellIndex++).setCellValue("Consumo");
		row.getCell(cellIndex++).setCellValue("Consumo/Dia");
		row.getCell(cellIndex++).setCellValue("Acopio");
		row.getCell(cellIndex++).setCellValue("Stock");
		row.getCell(cellIndex++).setCellValue("Pte.Recibir");
		row.getCell(cellIndex++).setCellValue("Pte.Servir");
		row.getCell(cellIndex++).setCellValue("Propuesta");
	}

	private Row createRow(HSSFSheet sheet, int rownum, CellStyle style) {
		Row row = sheet.createRow(rownum);
		int cellIndex = 0;
		createCell(row, cellIndex++, style);
		createCell(row, cellIndex++, style);
		createCell(row, cellIndex++, style);
		createCell(row, cellIndex++, style);
		createCell(row, cellIndex++, style);
		createCell(row, cellIndex++, style);
		createCell(row, cellIndex++, style);
		createCell(row, cellIndex++, style);
		return row;
	}
	
	private void createCell(Row row, int cellindex, CellStyle style) {
		Cell cell = row.createCell(cellindex);
    	cell.setCellStyle(style);
	}
	
	private void createContent(HSSFSheet sheet, int rowCount, CellStyle style3, Domain domain, String login, Map<String, String[]> filterMap) {
		Date from = new Date(Long.parseLong(filterMap.get(MSG.FROM)[0]));
		Date to = new Date(Long.parseLong(filterMap.get(MSG.TO)[0]));
		long daysCount = AonDateUtils.getDaysBetweenDates(from, to);
        Integer accumulationDays = 1;
		if(filterMap.containsKey(MSG.FROM) && filterMap.containsKey("accumulation_days")) {
			accumulationDays = Integer.parseInt(filterMap.get("accumulation_days")[0]);	
		}
		
		StatData<Integer, String, Double> stat = WarehouseServlet.getStockForecastStatData(domain, login, filterMap);
		
        Integer[] productIds = stat.getMap().keySet().toArray(new Integer[stat.getMap().keySet().size()]);
    	Map<Integer, Product> productMap = new HashMap<>();

    	AON.getProductStream(domain, login,  f->f.getIdProperty().in(productIds))
    		.forEach(product -> productMap.put(product.getId(), product));
    
        for(Integer productId: productIds) {
			if(stat.getMap().containsKey(productId)){
				Product product = productMap.get(productId);
				String productName = product.getCode()+" / "+product.getName();
				Double quantity = new Double(stat.get(productId, StatDAO.PRODUCT_OUTPUTS));
				Double dailyQuantity = quantity / daysCount;
				Double accumulation = dailyQuantity * accumulationDays;
				Double stock = stat.get(productId, StatDAO.PRODUCT_STOCK)!=null?stat.get(productId, StatDAO.PRODUCT_STOCK):0.0;
				Double pendingPurchases = stat.get(productId, StatDAO.PRODUCT_PENDING_PURCHASES)!=null?stat.get(productId, StatDAO.PRODUCT_PENDING_PURCHASES):0.0;
				Double pendingSales = stat.get(productId, StatDAO.PRODUCT_PENDING_SALES)!=null?stat.get(productId, StatDAO.PRODUCT_PENDING_SALES):0.0;
				Double proposal = accumulation - stock - pendingPurchases + pendingSales;
				
				int cellIndex = 0;
				Row row = createRow(sheet, rowCount++, style3);
				row.getCell(cellIndex++).setCellValue(productName);
				row.getCell(cellIndex++).setCellValue(quantity);
				row.getCell(cellIndex++).setCellValue(dailyQuantity);
				row.getCell(cellIndex++).setCellValue(accumulation);
				row.getCell(cellIndex++).setCellValue(stock);
				row.getCell(cellIndex++).setCellValue(pendingPurchases);
				row.getCell(cellIndex++).setCellValue(pendingSales);
				row.getCell(cellIndex++).setCellValue(proposal);
			}
		}
	}
	
}
