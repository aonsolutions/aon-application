package com.code.aon.ui.fiscal.controller;

import java.util.List;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDeclaration;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.dao.IFiscalAlias;
import com.code.aon.fiscal.enumeration.Administration;
import com.code.aon.fiscal.enumeration.VatTaxDeclarationStatus;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.form.LinesController;

public class VatTaxDeclarationController extends LinesController {
	public static final String BEAN_NAME = "vatTaxDeclaration";
	
	public void calculate(VatTaxDeclaration dec) {
		VatTaxController master = (VatTaxController) getMasterController();
		dec.setQuota(CommonUtil.round( getDiference(master) * dec.getPercent() / 100 ));
		double t = 0.0;
		if (dec.getResult() > 0){
			dec.setDeposit(dec.getResult());	
			dec.setCompensate(0.0);
			dec.setPayBack(0.0);
			t = dec.getDeposit();
		} else {
			dec.setDeposit(0.0);
			t = CommonUtil.round(dec.getResult() * -1);
			if (dec.isCompensable()) {
				dec.setCompensate(t);
				dec.setPayBack(0.0);
			} else {
				dec.setCompensate(0.0);
				dec.setPayBack(t);
			}
		}
		
		dec.setTotalTaxDebt( CommonUtil.round( t + dec.getExtraCharge() + dec.getDelayInterest()
				- dec.getPreviousDeposit() - dec.getPreviousPayBack() )); 
	}

	public boolean isListEnabled() {
		try {
			return (getModel().getRowCount() > 1);
		} catch (ManagerBeanException e) {
			return true;
		}
	}
	
	private double getDiference(VatTaxController master) {
		double dif = 0.0;
		for (VatTaxDetail detail:master.getSummary()) {
			if (detail.getKey() == VatTaxKey.DF) {
				dif = detail.getQuota();
				break;
			}
		}
		return dif;
	}

	public void calculate(ActionEvent event) {
		VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
		calculate(dec);
	}
	
	public void changeCompensate(ActionEvent event) {
		VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
		dec.setCompensable( !dec.isCompensable() );
		calculate(dec);
	}

	public void tryAutomaticCreation() throws ManagerBeanException {
		VatTaxController master = (VatTaxController) getMasterController();
		VatTax vatTax = (VatTax) master.getTo();
		VatTax previousVatTax = getPreviousVatTax(vatTax);
		if (previousVatTax == null) {
			Administration adm = master.getFiscalParams().getDefaultAdministration();
			createDeclaration(vatTax,adm,100.0,null);
		} else {
			duplicateDeclaration(vatTax,previousVatTax);
		}
		onCancel(null);	
	}

	private void duplicateDeclaration(VatTax vatTax,VatTax previousVatTax) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( getManagerBean().getFieldName(IFiscalAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), previousVatTax.getId());
		List<ITransferObject> list = getManagerBean().getList(criteria);
		for (ITransferObject to: list ) {
			VatTaxDeclaration dec = (VatTaxDeclaration) to;
			createDeclaration(vatTax,dec.getAdministration(),dec.getPercent(),dec.getRegistryBank());
		}
		
	}

	private void createDeclaration(VatTax vatTax,Administration adm, double percent,RegistryBank rBank) throws ManagerBeanException {
		onReset(null);
		VatTaxDeclaration dec = (VatTaxDeclaration) getTo();
		dec.setAdministration(adm);
		dec.setPercent(percent);
		dec.setStatus( VatTaxDeclarationStatus.PENDING );
		fillPreviousData(vatTax,dec);
		calculate(dec);
		accept(null);
		dec.setRegistryBank(null);	
	}

	private void fillPreviousData(VatTax vatTax,VatTaxDeclaration dec) throws ManagerBeanException {
		Criteria criteria = new Criteria();
		criteria.addExpression( ExpressionUtilities.getNotEqualExpression(getManagerBean().getFieldName(IFiscalAlias.VAT_TAX_DECLARATION_VAT_TAX_ID), vatTax.getId()));
		criteria.addEqualExpression( getManagerBean().getFieldName(IFiscalAlias.VAT_TAX_DECLARATION_VAT_TAX_YEAR), vatTax.getYear());
		criteria.addEqualExpression( getManagerBean().getFieldName(IFiscalAlias.VAT_TAX_DECLARATION_VAT_TAX_PERIOD), vatTax.getPeriod());
		criteria.addEqualExpression( getManagerBean().getFieldName(IFiscalAlias.VAT_TAX_DECLARATION_ADMINISTRATION), dec.getAdministration());
		criteria.addOrder( getManagerBean().getFieldName(IFiscalAlias.VAT_TAX_DECLARATION_VAT_TAX_NUMBER), false);
		criteria.addOrder( getManagerBean().getFieldName(IFiscalAlias.VAT_TAX_DECLARATION_VAT_TAX_REPLACEMENT), false);
		System.out.println( criteria.toString() );
		List<ITransferObject> list = getManagerBean().getList(criteria);
		double pd = 0.0;
		double pp = 0.0;
		if (list != null && list.size() > 0){
			VatTaxDeclaration vtd = (VatTaxDeclaration) list .get(0);
			pd = vtd.getDeposit();
			pp = vtd.getPayBack();
		}
		dec.setPreviousDeposit(pd);
		dec.setPreviousPayBack(pp);
	}

	private VatTax getPreviousVatTax(VatTax vatTax) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(VatTax.class);
		Criteria criteria = new Criteria();
		criteria.addExpression( ExpressionUtilities.getNotEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_ID), vatTax.getId()));
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_YEAR), vatTax.getYear());
		criteria.addLessThanOrEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_PERIOD), vatTax.getPeriod());
		criteria.addOrder(bean.getFieldName(IFiscalAlias.VAT_TAX_YEAR), false);
		criteria.addOrder(bean.getFieldName(IFiscalAlias.VAT_TAX_PERIOD), false);
		List<ITransferObject> list = bean.getList(criteria);
		if (list != null && list.size() > 0) {
			return (VatTax) list.get(0); 
		}
		return null;
		
	}
}
