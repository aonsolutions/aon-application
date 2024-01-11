package com.esferalia.aon.gwt.payroll.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.payroll.shared.ContractParams;
import com.esferalia.aon.in.payroll.excel.EnterpriseContractExcel;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "ENTERPRISE-SERVLET", urlPatterns = { "/aon_gwt_payroll/enteprise_contracts/*"})
public class EnterpriseServlet extends AonApiHttpServlet {
	
	private static Logger logger = Logger.getLogger(EnterpriseServlet.class.getName());
	
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		try {
			AonApiData api = initialize(req, false);
			
			String domainName = api.getData().getString("domain");
			Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
			
			String inactiveData = api.getData().getString("inactive");
			Boolean inactive = Boolean.parseBoolean(inactiveData);
			
			String fromData = api.getData().getString("from");
			Date from = AonStringUtils.isBlank(fromData) ? null : dateFormat.parse(fromData);
			
			String toData = api.getData().getString("to");
			Date to = AonStringUtils.isBlank(toData) ? null : dateFormat.parse(toData);
			
			ContractParams params = new ContractParams();
			
			params.setFrom(from);
			params.setTo(to);
			
			params.setEmployee( api.getData().getString("employee") );
			params.setTc2( api.getData().getString("tc2") );
			params.setWorkplace( api.getData().getString("workplace") );
			
			responseFile(resp, "Contratos", new FileInputStream(getEnterpriseContractsExcel(api, domain.getId(), inactive, params)), MimeType.MS_EXCEL);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		doGet(req, resp);
	}
	
	private File getEnterpriseContractsExcel(AonApiData api, Integer domainId, Boolean inactive, ContractParams params) throws Exception {
		logger.info("[GET] ENTERPRISE CONTRACTS EXCEL");
		
		File file = File.createTempFile("Contratos Empresa", "");
	
		EnterpriseContractExcel.simpleEnterpriseContractGenerator(api.getDomain().getName(), api.getUser().getLogin(), domainId, inactive, params.getWorkplace(), params.getEmployee(), params.getTc2(), params.getFrom(), params.getTo(), new FileOutputStream(file));
		
		return file;
	}
	
}
