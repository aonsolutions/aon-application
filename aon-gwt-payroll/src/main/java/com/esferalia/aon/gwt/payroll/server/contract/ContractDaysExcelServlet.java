package com.esferalia.aon.gwt.payroll.server.contract;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.in.payroll.excel.contract.AnualDaysEntryExcel;
import com.esferalia.aon.in.payroll.excel.contract.ContractDaysExcelAction;
import com.esferalia.aon.in.payroll.excel.contract.WorkplaceDaysEntryExcel;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "Contract Days (Excel)", urlPatterns = { "/ms/api/contractDays/*"})
public class ContractDaysExcelServlet extends HttpServlet {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			
			String domainName = req.getParameter("domainName");
			Integer domainId = Integer.parseInt( req.getParameter("domainId") );
			Date startDate = DATE_FORMAT.parse( req.getParameter("startDate") );
			Date endDate = AonStringUtils.isBlank(req.getParameter("endDate")) ? AonDateUtils.getMonthLastDay(new Date()) : DATE_FORMAT.parse( req.getParameter("endDate") );
			
			ContractDaysExcelAction action = new ContractDaysExcelAction();
			
			try (Connection connection = AonServletUtils.getConnection(domainName)) {
				
				// Get dslContext for given connection
				AONContext ctx = new AONContext(connection);
				InformeMensualService service = new InformeMensualService(ctx.getDslContext(), domainId);
				
				List<WorkplaceDaysEntryExcel> informesCTs = service.generarInformeMensualCTs( AonDateUtils.toSql(startDate) , AonDateUtils.toSql(endDate) );
				
				informesCTs.forEach(informesCT -> {
					
					String clearWorkplaceName = cleanSheetName(informesCT.getDescription());
					
					action.createSheet(clearWorkplaceName);
					
					action.setCTMeses( informesCT.getMeses() );
					
					action.headerRow(clearWorkplaceName, DATE_FORMAT.format(startDate), DATE_FORMAT.format(endDate));
					
					List<AnualDaysEntryExcel> informes = informesCT.getContracts();
					
					informes.forEach(action);
					
				});
				
				

			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
			
			String fileName = "Informe_D\u00edas_Contrato";
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""+ fileName + ".xlsx\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		} 
	}
	
	private String cleanSheetName(String sheetName) {
		if (sheetName == null) {
            return "";
        }

        // Eliminar tildes
        sheetName = sheetName.replaceAll("á", "a");
        sheetName = sheetName.replaceAll("é", "e");
        sheetName = sheetName.replaceAll("í", "i");
        sheetName = sheetName.replaceAll("ó", "o");
        sheetName = sheetName.replaceAll("ú", "u");
        sheetName = sheetName.replaceAll("Á", "A");
        sheetName = sheetName.replaceAll("É", "E");
        sheetName = sheetName.replaceAll("Í", "I");
        sheetName = sheetName.replaceAll("Ó", "O");
        sheetName = sheetName.replaceAll("Ú", "U");
        
        sheetName = sheetName.trim();

        // Reemplazar caracteres no alfanuméricos (como '/', espacios, etc.) por un guión bajo
        sheetName = sheetName.replaceAll("[^a-zA-Z0-9]", "_");

        // Limitar a 31 caracteres (el límite en nombres de hojas de Excel)
        if (sheetName.length() > 31) {
            sheetName = sheetName.substring(0, 31);
        }

        return sheetName;
    }
	

}
