package com.code.aon.ui.finance.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

public class InvoiceExcelDetailledReport  implements ICollectionProvider{

	private String sourceControllerName;

	public String getSourceControllerName() {
		return sourceControllerName;
	}

	public void setSourceControllerName(String sourceControllerName) {
		this.sourceControllerName = sourceControllerName;
	}

	@Override
	public Collection<?> getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
		IController controller = FormUtil.getController(getSourceControllerName());
		if (controller == null) {
			throw new IllegalArgumentException("Controller [" + getSourceControllerName() + "] not valid");
		}
		Criteria criteria = controller.getCriteria();
		IManagerBean taxBean = BeanManager.getManagerBean(InvoiceTax.class);
		String alias1 = taxBean.getFieldName(IFinanceAlias.INVOICE_TAX_INVOICE_DETAIL_ID);
		String alias2 = taxBean.getFieldName(IFinanceAlias.INVOICE_TAX_TAX_TYPE);
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		List<ITransferObject> taxes = new LinkedList<ITransferObject>();
		List<ITransferObject> invoices = bean.getList(criteria);
		for (ITransferObject to : invoices) {
			Invoice invoice = (Invoice) to;
			for (InvoiceDetail detail : invoice.getLines()) {
				Criteria c = new Criteria();
				c.addEqualExpression(alias1, detail.getId());
				c.addOrder(alias2);
				taxes.addAll( taxBean.getList(c) );
			}
		}
		return taxes;
	}
}
