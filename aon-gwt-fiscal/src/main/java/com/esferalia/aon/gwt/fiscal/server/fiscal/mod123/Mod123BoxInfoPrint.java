package com.esferalia.aon.gwt.fiscal.server.fiscal.mod123;

import java.io.IOException;
import java.util.stream.Stream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.irpf.IrpfBreakdownExcelAction;
import com.esferalia.aon.occam.api.fiscal.MODEL123;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod123Key;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Mod123 Box Info Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model123BoxInfoPrint" })
public class Mod123BoxInfoPrint extends HttpServlet {

	private static final long serialVersionUID = 1105648961423707713L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod123"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod123 mod123 = MODEL123.get(occam,id);

			String keyString = req.getParameter("mod123Box");
			Mod123Key key = Mod123Key.valueOf(keyString);
			String fileName = "MOD123_" + AonStringUtils.defaultIfBlank(key.getBoxCode(), keyString);
			IrpfBreakdownExcelAction action = new IrpfBreakdownExcelAction();
			action.initialize(fileName);
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			Stream<IrpfBreakdown> stream = MODEL123.getInfo(occam, mod123, key); 
			stream.forEach(action);						
			action.finalize(resp.getOutputStream());
			stream.close();
			resp.flushBuffer();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	
}
