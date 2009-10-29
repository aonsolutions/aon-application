package com.code.aon.ui.finance.event;

import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the controller of any entity with series and number
 * 
 */
public class InvoiceHeaderControllerListener extends HeaderControllerListener {
	
	/**
	 * Obtains the series and the next number to use
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Invoice invoiceHeader = (Invoice)event.getController().getTo();
        if(invoiceHeader.getNumber() == 0) {
        	Criteria criteria = new Criteria();
        	criteria.addEqualExpression(IFinanceAlias.INVOICE_TYPE, invoiceHeader.getType());
        	invoiceHeader.setNumber(SeriesNumberUtil.obtainNumber(invoiceHeader.getSeries(), getTable(), criteria));
		}
    }

}
