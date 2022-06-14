package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.in.payroll.pdf.jooq.JooqModificationFormBuilder;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;

//http://ayudat.aonsolutions.net:8080/aon-aio/aon_gwt_payroll/modification_form/

@SuppressWarnings("serial")
@WebServlet(name = "Modification-Form", 
	urlPatterns = { 
			"/aon_gwt_aio/modification_form/*",
			"/aon_gwt_payroll/modification_form/*" 
	})
public class ModificationFormServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType(MimeType.MIME_PDF.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\"formulario_modificacion.pdf\";");
		try (OutputStream os = resp.getOutputStream()) {
//			req.setCharacterEncoding("utf-8");	//DESCOMENTAR EN CASO DE EXPERIMENTAR PROBLEMAS CON LA CODIFICACIÓN DE CARACTERES
			int contractId = AonNumberUtils.toint(req.getParameter("id"));
			String title = req.getParameter("title");
			String info = req.getParameter("info");
			Date date = AonDateUtils.parse(req.getParameter("date"), AonDateUtils.SIMPLE_DATE_FORMAT);
			String domainName = req.getParameter("domain_name");
			String login = req.getParameter("user");
			int domainId = AonNumberUtils.toint(req.getParameter("domain_id"));
			if (contractId > 0) {
				JooqModificationFormBuilder.createModificationForm(os, domainName, domainId, login, contractId, title, info, date);
				os.flush();
			}
		} catch (IOException | CanNotCreatePdfException e) {
			e.printStackTrace();
		}
	}
}
