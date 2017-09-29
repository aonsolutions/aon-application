package com.code.aon.ui.finance.controller;

import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_FOOTER_TEXT;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_DIR_STAFF;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_DISCOUNT;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_DOMAIN;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_LOGO;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_SELLER_NAME;
import static com.code.aon.common.enumeration.AppParam.POS_INVOICE_PRINT_TRADENAME;
import static com.code.aon.ui.common.ICommonMessages.FOOTER_TEXT_CONTENT_MSG_KEY_PREFIX;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.company.enumeration.ReportPrintOption;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PosInvoiceParamsController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(PosInvoiceParamsController.class.getName());
	
	private Map<AppParam, ApplicationParameter> params;
	
	private RegistryAttachment footerText;

	public boolean isPrintLogo() {
		return Boolean.valueOf(params.get(POS_INVOICE_PRINT_LOGO).getValue()).booleanValue();
	}

	public void setPrintLogo(boolean printLogo) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_LOGO);
		param.setValue(String.valueOf(printLogo));
		params.put(POS_INVOICE_PRINT_LOGO, param );
	}

	public boolean isPrintTradename() {
		return Boolean.valueOf(params.get(POS_INVOICE_PRINT_TRADENAME).getValue()).booleanValue();
	}

	public void setPrintTradename(boolean printTradename) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_TRADENAME);
		param.setValue(String.valueOf(printTradename));
		params.put(POS_INVOICE_PRINT_TRADENAME, param );
	}

	public ReportPrintOption getPrintDirStaff() {
		return obtainReportPrintOption(params.get(POS_INVOICE_PRINT_DIR_STAFF).getValue());
	}

	public void setPrintDirStaff(ReportPrintOption printDirStaff) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_DIR_STAFF);
		param.setValue(String.valueOf(printDirStaff.ordinal()));
		params.put(POS_INVOICE_PRINT_DIR_STAFF, param );
	}

	public ReportPrintOption getPrintSellerName() {
		return obtainReportPrintOption(params.get(POS_INVOICE_PRINT_SELLER_NAME).getValue());
	}

	public void setPrintSellerName(ReportPrintOption printSellerName) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_SELLER_NAME);
		param.setValue(String.valueOf(printSellerName.ordinal()));
		params.put(POS_INVOICE_PRINT_SELLER_NAME, param );
	}

	public boolean isPrintDomain() {
		return Boolean.valueOf(params.get(POS_INVOICE_PRINT_DOMAIN).getValue()).booleanValue();
	}

	public void setPrintDomain(boolean printDomain) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_DOMAIN);
		param.setValue(String.valueOf(printDomain));
		params.put(POS_INVOICE_PRINT_DOMAIN, param );
	}
	
	public boolean isPrintDiscount() {
		return Boolean.valueOf(params.get(POS_INVOICE_PRINT_DISCOUNT).getValue()).booleanValue();
	}
	
	public void setPrintDiscount(boolean printDiscount) throws ManagerBeanException {
		ApplicationParameter param = obtainApplicationParameter(POS_INVOICE_PRINT_DISCOUNT);
		param.setValue(String.valueOf(printDiscount));
		params.put(POS_INVOICE_PRINT_DISCOUNT, param );
	}
	
	private void initFooterText() throws ManagerBeanException {
		this.footerText = null;
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		Company company = companyController.obtainCompany();
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.POS_INVOICE_FOOTER_TEXT);
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			this.footerText = (RegistryAttachment) list.get(0);
		}
		if ( this.footerText == null ) {
			this.footerText = new RegistryAttachment();
			this.footerText.setRegistry(company);
			this.footerText.setMimeType(MimeType.MIME_TXT);
			this.footerText.setRegistryAttachmentType(RegistryAttachmentType.POS_INVOICE_FOOTER_TEXT);
			ApplicationParameter appParam = AppParamUtil.getParameter(POS_INVOICE_FOOTER_TEXT);
			if ( appParam != null ) {
				setFooterText(appParam.getValue());
				AppParamUtil.removeParameter(POS_INVOICE_FOOTER_TEXT);
			} else {
				setFooterText(AonUtil.getMessage(FOOTER_TEXT_CONTENT_MSG_KEY_PREFIX));
			}
			updateFooterText();
		}
	}

	private void updateFooterText() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryAttachment.class);
		bean.insertOrUpdate(this.footerText);
	}

	public String getFooterText() {
		byte[] data = this.footerText.getData();
		if (! ArrayUtils.isEmpty(data) ) {
			return new String(data);
		}
		return "";
	}

	public void setFooterText(String footerText) throws ManagerBeanException {
		byte[] data = StringUtils.isEmpty(footerText) ? null : footerText.getBytes();
		this.footerText.setData(data);
	}

	public void load() {
		params = new HashMap<AppParam, ApplicationParameter>();
		try {
			params.put(POS_INVOICE_PRINT_LOGO, obtainApplicationParameter(POS_INVOICE_PRINT_LOGO));
			params.put(POS_INVOICE_PRINT_TRADENAME, obtainApplicationParameter(POS_INVOICE_PRINT_TRADENAME));
			params.put(POS_INVOICE_PRINT_DIR_STAFF, obtainApplicationParameter(POS_INVOICE_PRINT_DIR_STAFF));
			params.put(POS_INVOICE_PRINT_SELLER_NAME, obtainApplicationParameter(POS_INVOICE_PRINT_SELLER_NAME));
			params.put(POS_INVOICE_PRINT_DOMAIN, obtainApplicationParameter(POS_INVOICE_PRINT_DOMAIN));
			params.put(POS_INVOICE_PRINT_DISCOUNT, obtainApplicationParameter(POS_INVOICE_PRINT_DISCOUNT));
			initFooterText();
		} catch (ManagerBeanException e) {
			String msg = "No se han podido iniciar correctamente lo parámetros.";
			LOGGER.error(msg);
			AonUtil.addErrorMessage(msg);
		}		
	}
	
	public void onInit(ActionEvent event){
		load();
	}
	
	public void onAccept(ActionEvent event) throws ManagerBeanException{
		for(ApplicationParameter param: params.values()) {
			AppParamUtil.insertParameter(param);
		}
		updateFooterText();
	}
	
	public ApplicationParameter obtainApplicationParameter(AppParam appParam) throws ManagerBeanException{
		ApplicationParameter param = AppParamUtil.getParameter(appParam);
		if ( param == null ) {
			param = new ApplicationParameter();
			param.setName(appParam.getValue());
			param.setValue("");
		}
		return param;
	}
	
	private ReportPrintOption obtainReportPrintOption(String value) {
		for(ReportPrintOption o: ReportPrintOption.values()){
			if(StringUtils.isNotBlank(value) && o.ordinal() == Integer.parseInt(value)){
				return o;
			}
		}
		return null;
	}
	
}
