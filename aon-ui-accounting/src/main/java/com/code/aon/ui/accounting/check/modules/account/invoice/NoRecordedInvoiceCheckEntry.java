package com.code.aon.ui.accounting.check.modules.account.invoice;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckEntryAdapter;
import com.code.aon.ui.util.AonUtil;


public class NoRecordedInvoiceCheckEntry extends CheckEntryAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private boolean fixed = false;
	private String fixLabel = "Marcar como no Contabilizada";

	@Override
	public boolean isFixed() {
		return fixed;
	}


	@Override
	public boolean isFixAvailable() {
		return true;
	}

	@Override
	public String getFixActionLabel() {
		return fixLabel;
	}
	
	@Override
	public void onFix(ActionEvent event) throws AonCheckException{
		try {
			IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
			Invoice invoice = (Invoice) getTo();
			invoice = (Invoice) bean.get(invoice.getId());
			invoice.setStatus(InvoiceStatus.PENDING);
			invoice.setDefaultTaxInfo(false);
			invoice.setUpdateEnabled(false);
			bean.update(invoice);
			fixed = true;
		} catch (ManagerBeanException e) {
			String message = "No se pudo desmarcar la factura. [" + e.getMessage() + "]";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}
	
}
