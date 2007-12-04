package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.PayMethod;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.sales.controller.FeeInvoicingController;
import com.code.aon.ui.util.AonUtil;

public class FeeFinanceController extends LinesController {
	
	private static final Logger LOGGER = Logger.getLogger(FeeFinanceController.class.getName());

	private static final String FEE_INVOICING_CONTROLLER_NAME = "feeInvoicing";
	
	private Integer registryBankId;

	public Integer getRegistryBankId() {
		return registryBankId;
	}

	public void setRegistryBankId(Integer registryBankId) {
		this.registryBankId = registryBankId;
	}
	
	public boolean isModelToPaid() throws ManagerBeanException{
		return ((Finance)this.getModel().getRowData()).getFinanceStatus().equals(FinanceStatus.PAID);
	}
	
	@SuppressWarnings("unchecked")
	public void payMethodChanged(ValueChangeEvent event) throws ManagerBeanException{
		if(event.getNewValue() != null){
			IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(payMethodBean.getFieldName(IFinanceAlias.PAY_METHOD_ID), event.getNewValue());
			Iterator iter = payMethodBean.getList(criteria,0,1).iterator();
			if(iter.hasNext()){
				((Finance)this.getTo()).setPayMethod((PayMethod)iter.next());
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List getRegistryBanks(){
		List<SelectItem> rBanks = new LinkedList<SelectItem>();
		FeeInvoicingController feeInvoicingController = (FeeInvoicingController)AonUtil.getController(FEE_INVOICING_CONTROLLER_NAME);
		Invoice invoice = (Invoice)feeInvoicingController.getTo(); 
		try {
			IManagerBean rBankBean = BeanManager.getManagerBean(RegistryBank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rBankBean.getFieldName(IFinanceAlias.REGISTRY_BANK_REGISTRY_ID), invoice.getRegistry().getId());
			Iterator iter = rBankBean.getList(criteria).iterator();
			while(iter.hasNext()){
				RegistryBank rBank = (RegistryBank)iter.next();
				SelectItem item = new SelectItem(rBank.getId(), rBank.getBank().getName());
				rBanks.add(item);
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining Banks for registry with id=" + invoice.getRegistry().getId(), e);
		}
		return rBanks;
	}
}
