package com.esferalia.aon.gwt.fiscal.server.fiscal.mod421;

import java.io.IOException;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.fiscal.server.VatContextExcelAction;
import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Mod421 Box Info Print", urlPatterns = { "/aon_gwt_fiscal/ms/Model421BoxInfoPrint" })
public class Mod421BoxInfoPrint extends HttpServlet {

	private static final long serialVersionUID = 4470820406622713302L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod421"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod421 mod421 = MODEL421.get(occam,id);

			String keyString = req.getParameter("mod421Box");
			Mod421Key key = Mod421Key.valueOf(keyString);
			String fileName = "MOD421_" + AonStringUtils.defaultIfBlank(key.getBoxCode(), keyString);
			VatContextExcelAction action = new VatContextExcelAction();
			action.initialize(fileName);
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");
			Stream<VatContext> stream = MODEL421.getInfo(occam, mod421, key); 
			stream.forEach(action);						
			action.finalize(resp.getOutputStream());
			stream.close();
			resp.flushBuffer();
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	
}
