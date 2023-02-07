package com.esferalia.aon.gwt.fiscal.server.fiscal.mod115;

import java.io.IOException;
import java.util.stream.Stream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.irpf.IrpfBreakdownExcelAction;
import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Mod115 Box Info Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model115BoxInfoPrint" })
public class Mod115BoxInfoPrint extends HttpServlet {

	private static final long serialVersionUID = 1105648961423707713L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod115"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod115 mod115 = MODEL115.get(occam,id);

			String keyString = req.getParameter("mod115Box");
			Mod115Key key = Mod115Key.valueOf(keyString);
			String fileName = "MOD115_" + AonStringUtils.defaultIfBlank(key.getBoxCode(), keyString);
			IrpfBreakdownExcelAction action = new IrpfBreakdownExcelAction();
			action.initialize(fileName);
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			Stream<IrpfBreakdown> stream = MODEL115.getInfo(occam, mod115, key); 
			stream.forEach(action);						
			action.finalize(resp.getOutputStream());
			stream.close();
			resp.flushBuffer();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	
}
