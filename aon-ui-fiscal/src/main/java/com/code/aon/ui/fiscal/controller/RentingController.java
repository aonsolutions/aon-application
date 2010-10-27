package com.code.aon.ui.fiscal.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.fiscal.Renting;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.fiscal.enumeration.RentingStatus;
import com.code.aon.fiscal.renting.RentingProvider;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class RentingController extends BasicController {

	private RentingProvider provider;
	private FiscalParametersController fiscalParams;
	private Company company;

	private Company getCompany() {
		if (company == null) {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			this.company = companyController.obtainCompany(); 
		}
		return company;
	}

	public RentingProvider getProvider() {
		if (provider == null) {
			provider = new RentingProvider();
		}
		return provider;
	}

	public FiscalParametersController getFiscalParams() {
		if (fiscalParams == null) {
			fiscalParams = (FiscalParametersController) AonUtil.getRegisteredBean( FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
		}
		return fiscalParams;
	}

	public void initializeRenting() throws ManagerBeanException {
		Renting renting = (Renting) getTo();
		getProvider().initializeRenting(renting);
		getProvider().fillDeclared(renting);
		renting.calculate();
	}
	
	public void onFinish(ActionEvent event){
		Renting renting = (Renting) getTo();
		renting.setStatus(RentingStatus.FINISHED);
		accept(event);
	}
	public void onReopen(ActionEvent event){
		Renting renting = (Renting) getTo();
		renting.setStatus(RentingStatus.PENDING);
		accept(event);
	}

	public void onRecalculate(ActionEvent event ) {
		Renting renting = (Renting) getTo();
		renting.calculate();
	}
	
	public void downloadDisk(ActionEvent event) {
		Renting renting = (Renting) getTo();
		try {
			FacesContext ctx = FacesContext.getCurrentInstance();
			ExternalContext ec = ctx.getExternalContext();
			HttpServletResponse res = (HttpServletResponse) ec.getResponse();
			res.setContentType(MimeType.MIME_TXT.getName());
			String resourceName = "115A" +  renting.getYear() + renting.getPeriod() + ".txt"; 
			res.setHeader("Content-Disposition", "attachment; filename=\""+resourceName+"\";");
			res.setCharacterEncoding("iso-8859-1");
			String resource = "/com/code/aon/ui/fiscal/facelet/renting/115A0901.txt";
			InputStream input = VatTaxDeclaration.class.getResourceAsStream(resource);
			byte[] arr = IOUtils.toByteArray(input);
			String n = getCompany().getFullName();
			char[] name = n.toCharArray();
			int i = 50;
			for (int x=0;x < name.length; x++) {
				arr[i] = (byte) name[x];
				i++;
			}
			ByteArrayInputStream in = new ByteArrayInputStream(arr); 
			IOUtils.copy(in, res.getOutputStream());
			res.flushBuffer();
			ctx.responseComplete();
		} catch (IOException e) {
			AonUtil.addErrorMessage("El fichero no es correcto");
			throw new AbortProcessingException( e );
		}
		
	}

	public void initializeRentingDetail() throws ManagerBeanException {
		Renting renting = (Renting) getTo();
		getProvider().initializeRentingDetail(renting);
	}
	
}
