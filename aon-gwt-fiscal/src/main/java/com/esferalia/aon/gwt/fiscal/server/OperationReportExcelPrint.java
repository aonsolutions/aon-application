package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;

import com.esferalia.aon.gwt.finance.server.AbsExcelAction;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.FiscalParameters;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.AppParamDAO;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "OperationReport Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/OperationReportExcelPrint" })
public class OperationReportExcelPrint extends HttpServlet {

	private static final long serialVersionUID = 3092550705365462359L;	
	
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
	private OperationParams params;
	private String titular = "";
		
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {			
			String operationParams = req.getParameter("operationParams");
			String domainName = req.getParameter("domainName");			
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			
			JSONParser parser = new JSONParser();
			JSONObject jsonParams =  (JSONObject) parser.parse(operationParams);
			
			params = new OperationParams();
			
			Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN);
			params.setDomain(domain.intValue());
			
			Long activity = (Long) jsonParams.get(IRequestParamsNames.ACTIVITY);
			if (activity != null) {
				params.setActivity(activity.intValue());	
			}
			
			String activityDescription = (String) jsonParams.get(IRequestParamsNames.ACTIVITY_DESCRIPTION);
			if (activityDescription != null) {
				params.setActivityDescription(activityDescription);	
			}			
			
			String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
			if (AonStringUtils.isNotBlank(fromDate)) {
				params.setFromDate( FORMATTER.parse(fromDate));			
			}
			
			String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
			if (AonStringUtils.isNotBlank(toDate)) {
				params.setToDate( FORMATTER.parse(toDate));			
			}
			
			Long expenses = (Long) jsonParams.get(IRequestParamsNames.EXPENSES);
			if (expenses != null) {
				params.setExpenses(expenses==1);
			}
			
			Long irpf = (Long) jsonParams.get(IRequestParamsNames.IRPF);
			if (irpf != null) {
				params.setIrpf(irpf==1);
			}
			
			// Obtener NIF y Nombre de la Empresa			
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domainName, domainId, user);
				FiscalParameters p = AppParamDAO.getFiscalParameters(ctx);
				titular = p.getDocument()+ " - "+p.getName();
			} finally {
				if (ctx != null)
					ctx.close();
			}
			
			// "Listado de Compras y Gastos" o "Listado de Ventas e Ingresos"
			String filename = params.getExpenses() ? "Listado de Compras y Gastos" : "Listado de Ventas e Ingresos";
			
			ExcelAction action = new ExcelAction( );
			
			// Listado IVA
			params.setIrpf(false);
			action.initialize(filename.replace("Listado de ", "")+" IVA");			 
			FISCAL.getOperationBreakdown(domainName, user, domainId, params).forEach(action);
			action.summaryRows();
			
			// Listado IRPF
			params.setIrpf(true);  
			action.createSheet(filename.replace("Listado de ", "")+" IRPF");
			FISCAL.getOperationBreakdown(domainName, user, domainId, params).forEach(action);
			action.summaryRows();
			
			// Se estaba poniendo con extensión XLS, pero el archivo era Excel 2007
			//resp.setContentType(MimeType.MS_EXCEL.getName());
			//resp.setHeader("Content-disposition", "attachment; filename=\"Compras-Ventas."+ MimeType.MS_EXCEL.getExtension()+ "\";");
			
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""+filename+"."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			
			action.finalize(resp.getOutputStream());
			
			resp.flushBuffer();
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
	private class ExcelAction extends AbsExcelAction implements Consumer<OperationBreakdown>{
		
		private int rowsHeader = 0;
		
		private double sumBase = 0.0;
		private double sumQuota = 0.0;
		private double sumSurchargeQuota = 0.0;
		private double sumTotal = 0.0;
		Map<Double,Double[]> mapIvaSummary = new TreeMap<Double, Double[]>();
		Map<Double,Double[]> mapSurSummary = new TreeMap<Double, Double[]>();
		Map<String,Object[]> mapConceptSummary = new TreeMap<String, Object[]>();
		
		@Override
		protected void headerRow() {
			
			Font topHeaderFont = workbook.createFont();
			topHeaderFont.setBold(true);
			topHeaderFont.setFontHeightInPoints((short) 12);
			
			XSSFCellStyle topHeaderCellStyle = (XSSFCellStyle) workbook.createCellStyle();			
		    topHeaderCellStyle.setFont(topHeaderFont);
		    
		    // Título del Listado, Titular y Actividad
		    row = sheet.createRow(rowCount++);
			cellCount = 0;
		    CellUtil.createCell(row, cellCount, "Listado de "+sheet.getSheetName(), topHeaderCellStyle);
			
		    // Titular
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			CellUtil.createCell(row, cellCount, "Titular: "+ titular, topHeaderCellStyle);
		    
		    // Actividad
		    row = sheet.createRow(rowCount++);
			cellCount = 0;
			CellUtil.createCell(row, cellCount, "Actividad: "+params.getActivityDescription(), topHeaderCellStyle);
			
		    // Periodo
		    row = sheet.createRow(rowCount++);
			cellCount = 0;
			CellUtil.createCell(row, cellCount, "Periodo: Desde "+  FORMATTER.format(params.getFromDate()) + " hasta " + FORMATTER.format(params.getToDate()), topHeaderCellStyle);
			
			// Linea en blanco
			row = sheet.createRow(rowCount++);  
			
			// Cabeceras de las columnas
			row = sheet.createRow(rowCount++);
			cellCount = 0;

			CellUtil.createCell(row, cellCount, "ID", headerCellStyle);
			if (params.getIrpf())
				sheet.setColumnWidth(cellCount++, 5 * 256);
			else sheet.setColumnWidth(cellCount++, 10 * 256); // En el IVA lo ponemos mas ancho, por que al final irá la base del resumen por tipos en esa columna 

			CellUtil.createCell(row, cellCount, "FECHA", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			if (!params.getIrpf()) {
				CellUtil.createCell(row, cellCount, "FECHA IVA", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 15 * 256);
			}

			CellUtil.createCell(row, cellCount, "CONCEPTO", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);

			CellUtil.createCell(row, cellCount, "N\u00BA DOCUMENTO", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			CellUtil.createCell(row, cellCount, "TITULAR", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 50 * 256);

			CellUtil.createCell(row, cellCount, "BASE IMP.", headerCellStyle);
			sheet.setColumnWidth(cellCount++, 15 * 256);

			if (params.getIrpf()) {
				CellUtil.createCell(row, cellCount, "IMPUESTOS", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 15 * 256);
				
				CellUtil.createCell(row, cellCount, "TOTAL", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 15 * 256);

			}
			else {
				CellUtil.createCell(row, cellCount, "%IVA", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 15 * 256);
	
				CellUtil.createCell(row, cellCount, "CUOTA IVA", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 15 * 256);
	
				CellUtil.createCell(row, cellCount, "% REQ.", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 15 * 256);
	
				CellUtil.createCell(row, cellCount, "CUOTA REQ.", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 15 * 256);
				
				CellUtil.createCell(row, cellCount, "TOTAL FRA.", headerCellStyle);
				sheet.setColumnWidth(cellCount++, 15 * 256);				
			}
			
			// Almacenar cuantas filas tiene la cabecera, para rellenar la columna ID
			rowsHeader = rowCount;
			
			// Inicializar totales
			sumBase = 0.0;
			sumQuota = 0.0;
			sumSurchargeQuota = 0.0;
			sumTotal = 0.0;
			mapIvaSummary.clear();
			mapSurSummary.clear();
			mapConceptSummary.clear();
		}

		public void accept(OperationBreakdown op) {
			row = sheet.createRow(rowCount++);
			cellCount = 0;
			
			addCell(rowCount-rowsHeader);       // ID
			addCell(op.getEntryDate());         // FECHA
			
			if (!params.getIrpf())
				addCell(op.getTaxDate());       // FECHA IVA (SOLO IVA)
			
			addCell(op.getFullConcept());       // CONCEPTO
			addCell(op.getDocNumber());         // Nº DOCUMENTO
			addCell(op.getFullDocumentName());  // TITULAR			
			addCell(op.getBase());              // BASE IMP.
			
			if (params.getIrpf()) {
				addCell(op.getQuota() + op.getSurchargeQuota()); // IMPUESTOS (SOLO IRPF)
			}
			else {				
				addCell(op.getPercent());           // %IVA (SOLO IVA)
				addCell(op.getQuota());             // CUOTA IVA (SOLO IVA)
				addCell(op.getSurchargePercent());  // %REQ (SOLO IVA)
				addCell(op.getSurchargeQuota());    // CUOTA REQ (SOLO IVA)
			}			
			
			addCell(op.getTotal());			    // TOTAL FRA
			
			// Acumular totales y resumen por tipos de IVA, REQ (IVA) y Cuenta (IRPF)
			
			sumBase = sumBase + op.getBase();
			sumQuota = sumQuota + op.getQuota();
			sumSurchargeQuota = sumSurchargeQuota + op.getSurchargeQuota();
			sumTotal = sumTotal + op.getTotal();
			
			if (params.getIrpf()) { // IRPF
				Object[] indexIrpf = mapConceptSummary.get(op.getAccount());				 
				if (indexIrpf != null) {
					Object[] obj = new Object[2];	
					obj[0] = op.getAccountDescription();
					obj[1] = (double) indexIrpf[1] + op.getBase();
					mapConceptSummary.put(op.getAccount(), obj);
				} else {
					Object[] obj = new Object[2];
					obj[0] = op.getAccountDescription();
					obj[1] = op.getBase();
					mapConceptSummary.put(op.getAccount(), obj);
				}
			} else { // IVA
				Double[] indexIva = mapIvaSummary.get(op.getPercent());
				if (indexIva != null) {
					Double[] array = {0.0,0.0};	
					array[0] = indexIva[0] + op.getBase();
					array[1] = indexIva[1] + op.getQuota();
					mapIvaSummary.put(op.getPercent(), array);
				} else {
					Double[] array = {0.0,0.0};
					array[0] = op.getBase();
					array[1] = op.getQuota();
					mapIvaSummary.put(op.getPercent(), array);
				}
				
				// Solo se acumula el REQ si es distinto de cero
				if (op.getSurchargePercent() != 0) {
					Double[] indexSur = mapSurSummary.get(op.getSurchargePercent());
					if (indexSur != null) {
						Double[] array = {0.0,0.0};	
						array[0] = indexSur[0] + op.getBase();
						array[1] = indexSur[1] + op.getSurchargeQuota();
						mapSurSummary.put(op.getSurchargePercent(), array);
					} else {
						Double[] array = {0.0,0.0};	
						array[0] = op.getBase();
						array[1] = op.getSurchargeQuota();
						mapSurSummary.put(op.getSurchargePercent(), array);
					}
				}
			}			
		}
		
		public void createSheet(String name) {
			
			sheet = (SXSSFSheet) workbook.createSheet(name);		    
		    rowCount = 0;
		    cellCount = 0;
		    
		    headerRow();		    
		}
		
		private Cell addCell(String value, CellStyle style) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellStyle(style);
			cell.setCellValue(AonStringUtils.trimToEmpty( value ) );
			cell.setCellType(CellType.STRING);
			return cell;
		}
		
		private Cell addCell(Double number, CellStyle style) {
			Cell cell = row.createCell(cellCount++);
			cell.setCellStyle(style);
			cell.setCellValue(number!=null?number:0.0);
			cell.setCellType(CellType.NUMERIC);
			return cell;
		}
		
		public void summaryRows() {
			
			// Letra normal en negrita
			Font boldFont = workbook.createFont();			
			boldFont.setBold(true);
			
			// Estilos para textos en negrita
			CellStyle textBoldStyle = workbook.createCellStyle();
			textBoldStyle.setFont(boldFont);
						
			CellStyle centerBoldStyle = workbook.createCellStyle();
			centerBoldStyle.cloneStyleFrom(centerCellStyle);
			centerBoldStyle.setFont(boldFont);
			
			// Estilo para importes en negrita
			CellStyle decimalBoldStyle = workbook.createCellStyle();
			decimalBoldStyle.cloneStyleFrom(decimalStyle);
			decimalBoldStyle.setFont(boldFont);
			
			// Linea en blanco
		    row = sheet.createRow(rowCount++); 
		    
		    // Linea de Totales
		    row = sheet.createRow(rowCount++);
			cellCount = params.getIrpf() ? 4 : 5;
		    addCell("TOTAL",textBoldStyle);

		    addCell(sumBase,decimalBoldStyle);
		    
		    if (params.getIrpf()) {		    	
		    	addCell(sumQuota+sumSurchargeQuota,decimalBoldStyle);
		    }
		    else {
		    	cellCount++;
		    	addCell(sumQuota,decimalBoldStyle);
		    	cellCount++;
		    	addCell(sumSurchargeQuota,decimalBoldStyle);
		    }
		    
		    addCell(sumTotal,decimalBoldStyle);
		    
		    // Resumen por Cuenta (IRPF) o por tipos de IVA y tipos de REQ (IVA) 
		    row = sheet.createRow(rowCount++); // Línea en blanco
		    row = sheet.createRow(rowCount++);
		    if (params.getIrpf()) {
		    	cellCount = 1;
		    	addCell("RESUMEN POR CONCEPTO",textBoldStyle);
		    	row = sheet.createRow(rowCount++);
		    	cellCount = 1;
		    	addCell("CUENTA",centerBoldStyle);
		    	addCell("DESCRIPCIÓN",centerBoldStyle);
		    	addCell("TOTAL",centerBoldStyle);		    	
		    	for (Entry<String, Object[]> entry : mapConceptSummary.entrySet()) {
		    		row = sheet.createRow(rowCount++);
		    		cellCount = 1;		    		
		    		addCell(entry.getKey().toString());
		    		addCell(entry.getValue()[0] == null ? "" : entry.getValue()[0].toString()); 
		    		addCell((double) entry.getValue()[1]);
				}		    	
		    }
		    else {
		    	cellCount = 0;
		    	addCell("RESUMEN POR TIPOS DE IVA",textBoldStyle);
		    	row = sheet.createRow(rowCount++);
		    	cellCount = 0;
		    	addCell("BASE",centerBoldStyle);
		    	addCell("TIPO IVA",centerBoldStyle);
		    	addCell("CUOTA",centerBoldStyle);		    	
		    	for (Entry<Double, Double[]> entry : mapIvaSummary.entrySet()) {
		    		row = sheet.createRow(rowCount++);
		    		cellCount = 0;
					addCell((double) entry.getValue()[0]);
					addCell((double) entry.getKey());
					addCell((double) entry.getValue()[1]);
				}
		    	
		    	if (!mapSurSummary.isEmpty()) {
		    		row = sheet.createRow(rowCount++); // Linea en blanco
			    	row = sheet.createRow(rowCount++);
			    	cellCount = 0;
			    	addCell("RESUMEN POR TIPOS DE RECARGO DE EQUIVALENCIA",textBoldStyle);
			    	row = sheet.createRow(rowCount++);
			    	cellCount = 0;
			    	addCell("BASE",centerBoldStyle);
			    	addCell("TIPO REQ",centerBoldStyle);
			    	addCell("CUOTA",centerBoldStyle);
			    	for (Entry<Double, Double[]> entry : mapSurSummary.entrySet()) {
			    		row = sheet.createRow(rowCount++);
			    		cellCount = 0;			    		
						addCell((double) entry.getValue()[0]);
						addCell((double) entry.getKey());
						addCell((double) entry.getValue()[1]);
					}
		    	}		    	
		    }		    
		}		
		
	}
}
