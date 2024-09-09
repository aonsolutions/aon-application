package com.esferalia.aon.gwt.payroll.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import com.esferalia.aon.in.payroll.excel.EnterpriseContractExcel;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ContractParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
			
			String user = api.getData().getString("user");
			String description = api.getData().getString("description");
			
			Byte active = null;
			if(AonStringUtils.isNotBlank(api.getData().getString("active")))
				active = Byte.parseByte(api.getData().getString("active"));
			
			String tc2 = null;
			if(AonStringUtils.isNotBlank(api.getData().getString("tc2")))
				tc2 = api.getData().getString("tc2");
			
			Integer workplace = null;
			if(AonStringUtils.isNotBlank(api.getData().getString("workplace")))
				workplace = Integer.parseInt(api.getData().getString("workplace"));
			
			String fromData = api.getData().getString("from");
			Date from = AonStringUtils.isBlank(fromData) ? null : dateFormat.parse(fromData);
			
			String toData = api.getData().getString("to");
			Date to = AonStringUtils.isBlank(toData) ? null : dateFormat.parse(toData);
			
			ContractParams params = new ContractParams()
					.setDomainName(domainName)
					.setDomain(domain.getId())
					.setUser(user)
					.setDescription(description)
					.setActive(active)
					.setTc2(tc2)
					.setWorkplace(workplace)
					.setFrom(from)
					.setTo(to)
					.setOffset(0)
					.setLimit(Integer.MAX_VALUE);
			
			responseFile(resp, "Contratos", new FileInputStream(getEnterpriseContractsExcel(api, params)), MimeType.MS_EXCEL);
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		doGet(req, resp);
	}
	
	private File getEnterpriseContractsExcel(AonApiData api, ContractParams params) throws Exception {
		logger.info("[GET] ENTERPRISE CONTRACTS EXCEL");
		
		File file = File.createTempFile("Contratos Empresa", "");
	
		EnterpriseContractExcel.simpleEnterpriseContractGenerator(params, new FileOutputStream(file));
		
		return file;
	}
	
}
