package com.esferalia.aon.gwt.stat.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;

import com.esferalia.aon.gwt.stat.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.stat.StatData;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.StatType;
import com.esferalia.aon.occam.api.model.stat.invoice.InvoiceChartType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Stat Table Report (excel)", urlPatterns = { "/aon_gwt_stat/StatTableExcel",
																"/aon_gwt_aio/StatTableExcel" })
public class StatTableExcelServlet extends HttpServlet {

	private static final long serialVersionUID = 2715328290565919810L;
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

	private class TableExcelAction extends AbsExcelAction {
		
		private StatData<String, String, Double> result;
		private LinkedHashMap<String, Integer> rowMap;
		private LinkedHashMap<String, Integer> colMap;
		private StatParams params;

		public TableExcelAction(StatData<String, String, Double> result,LinkedHashMap<String, Integer> rowMap ,LinkedHashMap<String, Integer> colMap, StatParams params) {
			this.result = result;
			this.rowMap = rowMap;
			this.colMap = colMap;
			this.params = params;
		}
		
		@Override
		protected void headerRow() {
			for (String rowKey : result.getMap().keySet()) {
				if (!rowMap.containsKey(rowKey)) {
					rowKey = AonStringUtils.substringBefore(rowKey, "=");
					rowMap.put(rowKey, (rowMap.size() + 2));
				}
				for (String colKey : result.getMap().get(rowKey).keySet()) {
					if (!colMap.containsKey(colKey)) {
						colKey = AonStringUtils.substringBefore(colKey, "=");
						colMap.put(colKey, (((colMap.size() + 1) * 2) - 1) );
					}
				}
			}
			Row titleRow = sheet.createRow(0);
			InvoiceChartType chartType = InvoiceChartType.values()[params.getChartType()];
			CellUtil.createCell(titleRow, 0, "Datos gr\u00E1fico (" + chartType.getDescription() + ")" , headerCellStyle);
			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, (colMap.size() * 2) ));
			
			Row headerRow = sheet.createRow(1);
			CellUtil.createCell(headerRow, 0, "" , headerCellStyle);
			for (String colKey : colMap.keySet()) {
				int idx = colMap.get(colKey);
				CellUtil.createCell(headerRow, idx, colKey , headerCellStyle);
				sheet.setColumnWidth(idx, 12*256);
				idx++;
				CellUtil.createCell(headerRow, idx, "% Total" , headerCellStyle);
				sheet.setColumnWidth(idx, 12*256);
			}
			
			XSSFCellStyle catCellStyle = (XSSFCellStyle) workbook.createCellStyle();
			catCellStyle.setVerticalAlignment( VerticalAlignment.CENTER);
			catCellStyle.setFont(boldFont);
			
			for (String rowKey : rowMap.keySet()) {
				int idx = rowMap.get(rowKey);
				Row colRow = sheet.createRow(idx);
				CellUtil.createCell(colRow, 0, rowKey, catCellStyle);	
			}
			
			sheet.autoSizeColumn(0);
		}
		
		public void accept(String rowKey, String colKey, Double value) {
			Integer rowIdx = rowMap.get(rowKey);
			Integer colIdx = colMap.get(colKey);
			Row valueRow = sheet.getRow(rowIdx);
			Cell cell = valueRow.getCell(colIdx);
			if (cell == null) {
				cell = valueRow.createCell(colIdx);
				cell.setCellValue(0.0);
				cell.setCellStyle(decimalStyle);
				cell.setCellType(CellType.NUMERIC);
			}
			double v = cell.getNumericCellValue() +  (value!=null?value:0.0);
			cell.setCellValue(v);
		}
		
		public void fillFormulas() {
			XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(workbook);
			Row totalRow = sheet.createRow(sheet.getLastRowNum() + 1);
			for (String colKey : colMap.keySet()) {
				int colIdx = colMap.get(colKey);
				Cell cell = totalRow.createCell(colIdx);
				cell.setCellStyle(totalCellStyle);
				cell.setCellType(CellType.FORMULA);
				CellReference ref = new CellReference(cell);
				String[] parts = ref.getCellRefParts();  // Returns the three parts of the cell reference, the Sheet name (or null if none supplied), 
														 // the 1 based row number, and the A based column letter. This will not include any markers 
														 //	for absolute references, so use formatAsString() to properly turn references into strings.
				String colId = parts[2];
				String totalRef = parts[2]+parts[1];
				String formula = "SUM("+colId + "3:" + colId + sheet.getLastRowNum() + ")";
				cell.setCellFormula(formula);
				CellValue cellValue = evaluator.evaluate(cell);
				cell.setCellValue(cellValue.getNumberValue());
				
				for (int i = 2; i < sheet.getLastRowNum(); i++) {
					Row curRow = sheet.getRow(i);
					Cell percentCell = curRow.createCell(colIdx+1);
					percentCell.setCellStyle(percentStyle);
					percentCell.setCellType(CellType.FORMULA);
					String percentFormula =  colId + (i+1) + "/" + totalRef;
					percentCell.setCellFormula(percentFormula);
					CellValue percentValue = evaluator.evaluate(percentCell);
					percentCell.setCellValue(percentValue.getNumberValue());
				}

			}
		}
	}; 

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);
			String user = req.getParameter(IRequestParamsNames.USER);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN));
			
			String statParams = req.getParameter(IRequestParamsNames.STAT_PARAMS);

			StatParams params = new StatParams();
			JSONParser parser = new JSONParser();
			JSONObject jsonParams = (JSONObject) parser.parse(statParams);

			Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN);
			params.setDomain(domain.intValue());
			String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
			if (AonStringUtils.isNotBlank(fromDate)) {
				params.setFrom( FORMATTER.parse(fromDate));			
			}
			String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
			if (AonStringUtils.isNotBlank(toDate)) {
				params.setTo( FORMATTER.parse(toDate));			
			}
			Long viewAmounts = (Long) jsonParams.get(IRequestParamsNames.VIEW_AMOUNTS);
			if (viewAmounts != null) {
				params.setViewAmounts(viewAmounts==1);
			}
			Long statType= (Long) jsonParams.get(IRequestParamsNames.STAT_TYPE);
			if (statType != null) {
				params.setStatType( StatType.safeValueOf(statType.intValue()));
			}
			Long chartType = (Long) jsonParams.get(IRequestParamsNames.CHART_TYPE);
			if (chartType!= null) {
				params.setChartType( chartType.byteValue());
			}
			Long registry = (Long) jsonParams.get(IRequestParamsNames.REGISTRY);
			if (registry != null) {
				params.setRegistry(registry.intValue());	
			}
			Long product = (Long) jsonParams.get(IRequestParamsNames.PRODUCT);
			if (product != null) {
				params.setProduct(product.intValue());	
			}
			
			JSONArray items = (JSONArray) jsonParams.get(IRequestParamsNames.FILTER_ITEMS);
			if (items != null) {
				LinkedList<StatFilterItem> list = new LinkedList<StatFilterItem>();
				for (int i = 0; i < items.size(); i++) {
					StatFilterItem item = new StatFilterItem();
					JSONObject a = (JSONObject) items.get(i);
					Long statFilterType = (Long) a.get(IRequestParamsNames.STAT_FILTER_TYPE);
					if (statFilterType != null) {
						item.setType( StatFilterType.safeValueOf(statFilterType.intValue()));
					}
					String id = (String) a.get(IRequestParamsNames.ID);
					if (id != null) {
						item.setId(id);	
					}
					String label = (String) a.get(IRequestParamsNames.LABEL);
					if (label != null) {
						item.setLabel(label);	
					}
					item.setSelected(true);
					list.add(item);
				}
				params.setFilterItems(list);
			}
			
			StatData<String, String, Double> result =  AON.getStatData(domainName,domainId,user,params);
			
			LinkedHashMap<String, Integer> rowMap = new LinkedHashMap<String, Integer>();
			LinkedHashMap<String, Integer> colMap = new LinkedHashMap<String, Integer>();
			
			InvoiceChartType ct = InvoiceChartType.values()[params.getChartType()];
			if (ct == InvoiceChartType.INVOICE_GEO_PROVINCE) {
				result =  transformGeoDataTable(result,params);	
			}
			
			TableExcelAction excelAction = new TableExcelAction(result,rowMap,colMap,params);
			
			String fileName = "Datos estad\u00EDsticos";
			excelAction.initialize(fileName);
			
			for (String rowKey : result.getMap().keySet()) {
				for (String colKey : result.getMap().get(rowKey).keySet()) {
					excelAction.accept(rowKey,colKey,result.getMap().get(rowKey).get(colKey));
				}
			}
			excelAction.fillFormulas();	
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xlsx\";");
			excelAction.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}


	private StatData<String, String, Double> transformGeoDataTable(StatData<String, String, Double> result, StatParams params) {
		StatData<String, String, Double> newResult = new StatData<String, String, Double>();
		
		String colLabel = params.isViewAmounts()? "Cantidad" : "Importe";
		LinkedHashMap<String, Double> map = result.getMap().get("CHART");
		for (String col : map.keySet()) {
			LinkedHashMap<String, Double> rowMap = newResult.getMap().get(col);
			if (rowMap == null) {
				rowMap = new LinkedHashMap<String, Double>();
				newResult.getMap().put(col, rowMap);
			}
			rowMap.put(colLabel, map.get(col));
		}
		return newResult;
	}
	
}
