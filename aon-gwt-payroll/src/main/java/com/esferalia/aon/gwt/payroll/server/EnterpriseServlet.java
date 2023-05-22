package com.esferalia.aon.gwt.payroll.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.in.payroll.excel.EnterpriseContractExcel;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "ENTERPRISE-SERVLET", urlPatterns = { "/aon_gwt_payroll/enteprise_contracts/*"})
public class EnterpriseServlet extends AonApiHttpServlet {
	
	private static Logger logger = Logger.getLogger(EnterpriseServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		try {
			AonApiData api = initialize(req, false);
			
			String domainName = api.getData().getString("domain");
			Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
			
			responseFile(resp, "Contratos", new FileInputStream(getEnterpriseContractsExcel(api, domain.getId())), MimeType.MS_EXCEL);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		doGet(req, resp);
	}
	
	private File getEnterpriseContractsExcel(AonApiData api, Integer domainId) throws Exception {
		logger.info("[GET] ENTERPRISE CONTRACTS EXCEL");
		
		File file = File.createTempFile("Contratos Empresa", "");
	
		EnterpriseContractExcel.simpleEnterpriseContractGenerator(api.getDomain().getName(), api.getUser().getLogin(), domainId, new FileOutputStream(file));
		
		return file;
	}
	
}
