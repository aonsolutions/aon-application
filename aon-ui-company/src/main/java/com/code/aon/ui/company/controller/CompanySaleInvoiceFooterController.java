package com.code.aon.ui.company.controller;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_SALE_INVOICE_FOOTER_LOPD;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.config.controller.ConfigConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class CompanySaleInvoiceFooterController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanySaleInvoiceFooterController.class.getName());
	
	private String text;

	public String getText() {
		if ( StringUtils.isEmpty(text) && isGarageDomainType() && isGarageSaleInvoiceTemplate()) {
			createGtaLOPD();
		}
		return text;
	}
	
	public String getGtaDefaultText() {
		try {
			return obtainGtaLOPD();
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error de lectura. Vuelva a intentarlo pasados unos segundos.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public boolean isGarageDomainType() {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		return ds.getType() == DomainType.GARAGE;
	}
	
	public boolean isGarageSaleInvoiceTemplate() {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		return controller.getSaleInvoiceTemplate() == SaleInvoiceTemplate.GTA;
	}
	
	public void createLOPD(ActionEvent event) {
		Company company = (Company) this.getMasterController().getTo();
		String companyName = "";
		String companyFullAddress = "";
		try {
			companyName = company.getName();
			companyFullAddress = company.getDefaultAddress().getFullAddress();
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error de lectura. Vuelva a intentarlo pasados unos segundos.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
		setText(AonUtil.getMessage(COMPANY_SALE_INVOICE_FOOTER_LOPD, companyName, companyFullAddress));
	}
	
	private void createGtaLOPD() {
		try {
			setText(obtainGtaLOPD());
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error de lectura. Vuelva a intentarlo pasados unos segundos.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	private String obtainGtaLOPD() throws ManagerBeanException {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		RegistryAddress address = controller.obtainAddress();
		Company company = controller.obtainCompany();
		StringBuffer buffer = new StringBuffer();
		buffer.append(AonUtil.getMessage(ICommonMessages.GTA_FOOTER_TEXT_1)).append(" ");
		buffer.append(company.getName()).append(" ");
		buffer.append(AonUtil.getMessage(ICommonMessages.GTA_FOOTER_TEXT_2)).append("\n");
		buffer.append(AonUtil.getMessage(ICommonMessages.TAS_LEGAL_TEXT_1)).append(" ");
		buffer.append(company.getName()).append(" ");
		buffer.append(AonUtil.getMessage(ICommonMessages.TAS_LEGAL_TEXT_2)).append(" ");
		buffer.append(company.getName()).append(", ");
		buffer.append(address.getFullAddress()).append(" - ");
		buffer.append(address.getZip()).append(" ");
		buffer.append(address.getCity());
		buffer.append(" (").append(address.getGeozone().getName()).append(") ");
		buffer.append(AonUtil.getMessage(ICommonMessages.TAS_LEGAL_TEXT_3));
		return buffer.toString();
	}
	
}
