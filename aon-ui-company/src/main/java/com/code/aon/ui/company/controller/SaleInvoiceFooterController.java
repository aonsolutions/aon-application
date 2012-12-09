package com.code.aon.ui.company.controller;

import static com.code.aon.ui.company.controller.CompanyParentController.PRINT_SALE_INVOICE_FOOTER;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.IAttachment;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.registry.controller.RegistryAttachController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class SaleInvoiceFooterController extends RegistryAttachController {
	
	private static final String BASE_NAME = "com.code.aon.company.i18n.messages";
	
	private static final String MSG_KEY_PREFIX = "company_saleInvoice_footer_LOPD";
	
	private boolean show;
	
	private String text;

	

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isShow() {
		return show;
	}
	
	@Override
	public void initializeModel() {
		try {
//			getCriteria().addEqualExpression(IEntityAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE, getType());
			getCriteria().addEqualExpression(this.getFieldName(IEntityAlias.REGISTRY_ATTACHMENT_DESCRIPTION), CompanyParentController.PRINT_SALE_INVOICE_FOOTER);
			
			super.initializeModel();
			
			if(getModel().getRowCount()==0){
				this.onReset(null);
				setText("");
			} else {
				this.onSelectFirst(null);
				setText(new String(((RegistryAttachment)this.getTo()).getData()));
			}
		} catch (ManagerBeanException e) {
			// 
		} 
	}
	
	@Override
	public void onAccept(ActionEvent event) {
		((RegistryAttachment)this.getTo()).setData(getText().getBytes());
		((RegistryAttachment)this.getTo()).setDescription(CompanyParentController.PRINT_SALE_INVOICE_FOOTER);
		super.onAccept(event);
	}
	
	public void createLOPD(ActionEvent event) {
		Company company = (Company) this.getMasterController().getTo();
		String companyName = company.getName();
		String companyFullAddress = "";
		try {
			companyFullAddress = company.getDefaultAddress().getFullAddress();
		} catch (ManagerBeanException e) {
			// NOTHING TO DO
		}
		setText(AonUtil.getMessage(ICompanyConstants.BUNDLE_NAME, MSG_KEY_PREFIX, companyName, companyFullAddress));
	}
	

	@Override
	public void onSelect(ActionEvent event) {
		String value = AppParamUtil.getValue(PRINT_SALE_INVOICE_FOOTER);
		this.show = StringUtils.equals(value, Boolean.TRUE.toString());
	}
	
	@SuppressWarnings("unchecked")
	private Set<String> getCurrentValues() throws ManagerBeanException {
		Set<String> values = new HashSet<String>();
		for( IAttachment attach : (List<IAttachment>) getModel().getWrappedData() ) {
			values.add( attach.getDescription() );
		}
		return values;
	}
	
	
}
