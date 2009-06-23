package com.code.aon.ui.finance.event;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.finance.Finance;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.controller.SalesInvoicingController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.FormUtil;

/**
 * SalesFinance controller's veto listener 
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class SalesFinanceControllerVetoListener extends ControllerAdapter{
	
	/**
	 * The class logger
	 */
	private Logger LOGGER = Logger.getLogger(SalesFinanceControllerVetoListener.class.getName());
	
	/**
	 * SalesInvoicing controller's name
	 */
	private static final String SALES_INVOICING_CONTROLLER_NAME = "salesInvoicing";

	/**
	 * Completes Finance transfer object before add
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		SalesInvoicingController salesInvoicingController = (SalesInvoicingController)FormUtil.getController(SALES_INVOICING_CONTROLLER_NAME);
		loadPayMethodData(((Finance)event.getController().getTo()));
		((Finance)event.getController().getTo()).setRegistry(((Invoice)salesInvoicingController.getTo()).getRegistry());
		((Finance)event.getController().getTo()).setBank(null);
		((Finance)event.getController().getTo()).setPayment(false);
		((Finance)event.getController().getTo()).setFinanceStatus(FinanceStatus.PENDING);
	}

	/**
	 * Completes Finance transfer object before update
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		SalesInvoicingController salesInvoicingController = (SalesInvoicingController)FormUtil.getController(SALES_INVOICING_CONTROLLER_NAME);
		loadPayMethodData(((Finance)event.getController().getTo()));
		((Finance)event.getController().getTo()).setRegistry(((Invoice)salesInvoicingController.getTo()).getRegistry());
		((Finance)event.getController().getTo()).setBank(null);
		((Finance)event.getController().getTo()).setPayment(false);
	}
	
	/**
	 * Calculates Finance's amount and assigns it to the transfer object
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeBeanCreated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			SalesInvoicingController salesInvoicingController = (SalesInvoicingController)FormUtil.getController(SALES_INVOICING_CONTROLLER_NAME);
			double total = salesInvoicingController.getToInvoiceTotalPrice();
			LinesController financeController = (LinesController)event.getController();
			Iterator iter = ((List)financeController.getModel().getWrappedData()).iterator();
			while(iter.hasNext()){
				Finance finance = (Finance)iter.next();
				total = total -  finance.getAmount();
			}
			((Finance)event.getController().getTo()).setAmount(round(total,2));
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "error beforeBeanCreated SalesFinanceControllerVetoListener", e);
		}
	}
	
	/**
	 * Assigns PayMethod object
	 * 
	 * @param finance Finance
	 */
	private void loadPayMethodData(Finance finance) {
		try {
			IManagerBean payMethodBean = BeanManager.getManagerBean(PayMethod.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(payMethodBean.getFieldName(IConfigAlias.PAY_METHOD_ID), finance.getPayMethod().getId());
			Iterator iter = payMethodBean.getList(criteria).iterator();
			if(iter.hasNext()){
				finance.setPayMethod((PayMethod)iter.next());
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error loading payMethod data for finance with id= " + finance.getId(), e);
		}
	}
	
	/**
	 * Returns a value rounded with required precision
	 * 
	 * @param value value to round
	 * @param precision precision of the round 
	 * @return value rounded
	 */
	private double round(double value, int precision) {
        double decimal = Math.pow(10, precision);
        return Math.round(decimal*value) / decimal;
    }
}
