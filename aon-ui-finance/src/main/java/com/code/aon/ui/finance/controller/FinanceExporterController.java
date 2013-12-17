package com.code.aon.ui.finance.controller;

import java.io.Serializable;

import javax.faces.event.ActionEvent;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.finance.BasicExporter;
import com.code.aon.ui.finance.event.FinanceSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class FinanceExporterController extends BasicController implements IFinanceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceExporterController.class.getName());
	
	private boolean payment;
	
	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	
	public boolean isPayroll() {
		return false;
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		FinanceSearchListener searchListener = (FinanceSearchListener)AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_SEARCH_LISTENER_NAME);
		FinanceStatus[] defaultFinanceStatus = {FinanceStatus.RETURNED, FinanceStatus.PAID};
		searchListener.setFinanceStatuses(defaultFinanceStatus);
	}

	public void onStart( ActionEvent event ) {
		ExporterController ec = (ExporterController) AonUtil.getRegisteredBean(IFinanceConstants.EXPORTER_CONTROLLER_NAME);
		BasicExporter exporter = ec.start(); 
		obtainData( exporter );
		ec.finish();
	}
	
	private void exportFinance( BasicExporter exporter, Finance finance ) {
		LogPanelController log = LogPanelController.getInstance();
		String key = finance.isPayment() ? ICommonMessages.FINANCE_PAYMENT_EXPORT : ICommonMessages.FINANCE_CHARGE_EXPORT;
		String reference = finance.isEmptyInvoice() ? finance.getDocumentNumber() : finance.getReferenceCode();
		log.info(AonUtil.getMessage(key, exporter.getType().getName(AonUtil.getCurrentLocale()), reference) );
		try {
			exporter.init(finance);
			exporter.write();
		} catch ( Throwable e ) {
			String errorKey = finance.isPayment() ? ICommonMessages.FINANCE_PAYMENT_EXPORT_ERROR : ICommonMessages.FINANCE_CHARGE_EXPORT_ERROR;
			String msg = AonUtil.getMessage(errorKey, reference, e.getMessage());
			LOGGER.error(msg, e);
			log.error(msg);			
		}		
	}
	
	private void obtainData( BasicExporter exporter ) {
		LogPanelController log = LogPanelController.getInstance();
		boolean initTransState = HibernateUtil.mustBeginTransaction();
		boolean initSessionState = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		HibernateUtil.setCloseSession(false);
		HibernateUtil.setBeginTransaction(false);
		try {
			Session session = HibernateUtil.getSession(sessionName);
			for( Serializable id : getCheckList() ) {
				Finance finance = (Finance) session.get(Finance.class, id);
				exportFinance(exporter, finance);
			}			
		} catch (Throwable t ) {
		    try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException e) {
				LOGGER.error(e.getMessage(), e);
			}		    
		    log.error(AonUtil.getMessage(ICommonMessages.FINANCE_FINANCES_EXPORT_ERROR, t.getMessage()));
		} finally {
			log.finish();
			HibernateUtil.closeSession(sessionName);
			if (initTransState != HibernateUtil.mustBeginTransaction()) {
				HibernateUtil.setBeginTransaction(initTransState);
			}
			if (initSessionState != HibernateUtil.mustCloseSession()) {
				HibernateUtil.setCloseSession(initSessionState);
			}
		}		
	}
	
}