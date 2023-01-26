package com.esferalia.aon.gwt.fiscal.server.irpf;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.type.MimeType;

@WebServlet(name = "IrpfReport Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/IrpfReportExcelPrint" })
public class IrpfReportExcelPrint extends HttpServlet {

	private static final long serialVersionUID = 2782900860290220524L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			String irpfParams = req.getParameter( IRequestParamsNames.IRPF_PARAMS );
			IRPFParams params = JsonParser.parseIRPFParams(irpfParams);
			params.setDomain(Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID)));
			params.setUser(req.getParameter(IRequestParamsNames.USER));
			params.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME));
			Occam occam = new Occam()
				.setDomainName(params.getDomainName())
				.setDomain(params.getDomain())
				.setUser(params.getUser());
			
			IrpfBreakdownExcelAction action = new IrpfBreakdownExcelAction( );
			action.initialize("IRPF");
			FISCAL.getIrpfBreakdown(occam, params)
				.forEach(action)						
			;
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"IRPF."+ MimeType.MS_EXCEL.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			
			resp.flushBuffer();
			
		} catch (ParseException | java.text.ParseException e) {
			e.printStackTrace();
		}

	}
	
}
