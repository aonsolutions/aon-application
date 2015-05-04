package com.code.aon.ui.company.controller;

import static com.code.aon.ui.common.ICommonMessages.COMPANY_SALE_INVOICE_FOOTER_LOPD;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.enumeration.SaleInvoiceTemplate;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.audit.AuditManager;
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
		if ( StringUtils.isEmpty(text) ){
			CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
			SaleInvoiceTemplate template = controller.getSaleInvoiceTemplate();
			try {
				if ( template == SaleInvoiceTemplate.GTA && isGarageDomainType()){
					setText(obtainGtaLOPD());
				} else if ( template == SaleInvoiceTemplate.HOTEL && isHotelDomainType()){
					setText(obtainHotelLOPD());
				}
			} catch (ManagerBeanException e) {
				setText(null);
			}
		}
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
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
	
	public String getHotelDefaultText() {
		try {
			return obtainHotelLOPD();
		} catch (ManagerBeanException e) {
			String msg = "Se ha producido un error de lectura. Vuelva a intentarlo pasados unos segundos.";
			LOGGER.error(msg, e);
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public boolean isGarageDomainType() {
		return isDomainType(Module.GARAGE);
	}
	
	public boolean isHotelDomainType() {
		return isDomainType(Module.HOTEL);
	}
	
	public boolean isDomainType(Module module) {
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(ConfigConstants.DOMAIN_SWITCHER);
		Integer domainId = ds.getDomainId();
		if ( domainId != null ) {
			Integer appId = AonUtil.getAuthPrincipal().getApplicationId();
			try {
				return AuditManager.hasModule(domainId, appId, module);
			} catch (Throwable th) {
				return false;
			}	
		}	
		return false;
	}
	
	public boolean isGarageSaleInvoiceTemplate() {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		return controller.getSaleInvoiceTemplate() == SaleInvoiceTemplate.GTA;
	}
	
	public boolean isHotelSaleInvoiceTemplate() {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		return controller.getSaleInvoiceTemplate() == SaleInvoiceTemplate.HOTEL;
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
	
	private String obtainHotelLOPD() throws ManagerBeanException {
		CompanyController controller = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		RegistryAddress address = controller.obtainAddress();
		Company company = controller.obtainCompany();
		String nameParam = company.getName();
		nameParam += StringUtils.isNotBlank(company.getAlias())?" ("+company.getAlias()+")":"";
		String addressParam = address.getZip();
		addressParam += ", " + address.getCity();
		addressParam += ", " + address.getFullAddress();
		return AonUtil.getMessage(ICommonMessages.HOTEL_FOOTER_TEXT, nameParam, addressParam);
	}
	
}
