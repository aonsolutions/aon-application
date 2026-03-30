package com.esferalia.aon.gwt.finance.server;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.finance.FBatch;
import com.esferalia.aon.occam.api.model.type.MimeType;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "FBatch Payment Report (Excel)", urlPatterns = { "/ms/api/fbatchPaymentReport"})
public class FBatchPaymentReportServlet extends HttpServlet {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		try {
			
			String domainName = req.getParameter( IRequestParamsNames.DOMAIN_NAME);
			Integer domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			String user = req.getParameter( IRequestParamsNames.USER);
			Integer fbatchId = Integer.parseInt(req.getParameter("fbatch"));

			FBatch fbatch = AON.getFBatch(domainName, domainId, user, fbatchId);
			
			FBatchPaymentExcelAction action = new FBatchPaymentExcelAction();
			
			action.setFbatch(fbatch);
			action.initialize("RELACION REMESA BANCARIA", false);
			action.fbatchInfo();
			action.headerRow();
			
			fbatch.getBatchDetails().stream().map(detail -> detail.getFinance()).forEach(action);

			String fileName = fbatch.getDescription() + DATE_FORMAT.format(fbatch.getIssueDate()) ;
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""+ fileName + ".xlsx\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		} 
	}

}
