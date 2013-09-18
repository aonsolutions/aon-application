package com.code.aon.ui.company.controller;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_BUNDLE;
import static com.code.aon.ui.common.ICommonMessages.COMPANY_SALE_INVOICE_FOOTER_LOPD;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CompanySaleInvoiceFooterController extends LinesController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanySaleInvoiceFooterController.class.getName());
	
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void createLOPD(ActionEvent event) {
		Company company = (Company) this.getMasterController().getTo();
		String companyName = "";
		String companyFullAddress = "";
		try {
			companyName = company.getName();
			companyFullAddress = company.getDefaultAddress().getFullAddress();
		} catch (ManagerBeanException e) {
			String msg = "Se ha priducido un error de lectura. Vuela a intentarlo pasados unos segundos.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		setText(AonUtil.getMessage(COMPANY_BUNDLE, COMPANY_SALE_INVOICE_FOOTER_LOPD, companyName, companyFullAddress));
	}
	
}
