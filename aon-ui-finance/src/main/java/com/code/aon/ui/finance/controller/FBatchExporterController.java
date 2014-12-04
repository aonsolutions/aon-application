package com.code.aon.ui.finance.controller;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.ActionEvent;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.finance.BasicExporter;
import com.code.aon.ui.finance.event.FBatchSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class FBatchExporterController extends BasicController implements IFinanceController  {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FBatchExporterController.class.getName());
	
	private boolean payment;
	
	private Date recordDate;
	
	private AccountEntryFinanceWriter writer;

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}	
	
	public boolean isPayroll() {
		return false;
	}	
	
	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		FBatchSearchListener searchListener = (FBatchSearchListener)AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_BATCH_SEARCH_LISTENER_NAME);
		searchListener.setStatus(FinanceBatchStatus.RECORDED);
		setRecordDate(null);
	}
	
	public boolean isRecorded() {
		FBatchSearchListener searchListener = (FBatchSearchListener)AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_BATCH_SEARCH_LISTENER_NAME);
		return searchListener.getStatus() == FinanceBatchStatus.RECORDED;
	}
	
	public void onStart( ActionEvent event ) {
		ExporterController ec = (ExporterController) AonUtil.getRegisteredBean(IFinanceConstants.EXPORTER_CONTROLLER_NAME);
		BasicExporter exporter = ec.start(); 
		obtainData( exporter );
		ec.setDataMap(exporter.getDataMap());		
		ec.finish();
	}
	
	private void exportFBatch( BasicExporter exporter, FinanceBatch fBatch ) {
		LogPanelController log = LogPanelController.getInstance();
		String reference = fBatch.getDescription() + " (" + fBatch.getId() + ")";
		try {
			exporter.init(fBatch);
			if ( exporter.hasAccountData() ) {
				String key = fBatch.isPayment() ? ICommonMessages.FINANCE_FBATCH_PAYMENT_EXPORT : ICommonMessages.FINANCE_FBATCH_CHARGE_EXPORT;
				log.info(AonUtil.getMessage(key, exporter.getType().getName(AonUtil.getCurrentLocale()), reference) );
				exporter.write();	
			} else {
				String errorKey = fBatch.isPayment() ? ICommonMessages.FINANCE_FBATCH_PAYMENT_NO_DATA : ICommonMessages.FINANCE_FBATCH_CHARGE_NO_DATA;
				String msg = AonUtil.getMessage(errorKey, reference);
				log.error(msg);							
			}
		} catch ( Throwable e ) {
			String errorKey = fBatch.isPayment() ? ICommonMessages.FINANCE_FBATCH_PAYMENT_EXPORT_ERROR : ICommonMessages.FINANCE_FBATCH_CHARGE_EXPORT_ERROR;
			String msg = AonUtil.getMessage(errorKey, reference, e.getMessage());
			LOGGER.error(msg, e);
			log.error(msg);			
		}		
	}	
	
	private AccountEntryFinanceWriter getWriter() {
		if (writer == null) {
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}
	
	private void recordFinanceBatch( String sessionName, FinanceBatch fBatch ) {
		LogPanelController log = LogPanelController.getInstance();
		String id = fBatch.getDescription() + "(" + fBatch.getId() + ")";
		try {
			String key = fBatch.isPayment() ? ICommonMessages.FINANCE_FBATCH_PAYMENT_RECORD : ICommonMessages.FINANCE_FBATCH_CHARGE_RECORD;
			log.info(AonUtil.getMessage(key, id) );
			HibernateUtil.beginTransaction(sessionName);
			getWriter().recordFBatch(fBatch, getRecordDate());
			HibernateUtil.getSession(sessionName).merge(fBatch);
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);			
		} catch (Throwable e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				LOGGER.error(e.getMessage(), e);
			}			
			String key = fBatch.isPayment() ? ICommonMessages.FINANCE_FBATCH_PAYMENT_RECORD_ERROR : ICommonMessages.FINANCE_FBATCH_CHARGE_RECORD_ERROR;
			String msg = AonUtil.getMessage(key, id, e.getMessage());
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
			if ( ! isRecorded() ) {			
				for( Serializable id : getCheckList() ) {
					FinanceBatch fbatch = (FinanceBatch) session.get(FinanceBatch.class, id);
					recordFinanceBatch(sessionName, fbatch);
				}
			}
			HibernateUtil.closeSession(sessionName);			
			session = HibernateUtil.getSession(sessionName);
			for( Serializable id : getCheckList() ) {
				FinanceBatch fBatch = (FinanceBatch) session.get(FinanceBatch.class, id);
				exportFBatch(exporter, fBatch);
			}			
		} catch (Throwable t ) {
		    try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException e) {
				LOGGER.error(e.getMessage(), e);
			}		    
		    log.error(AonUtil.getMessage(ICommonMessages.FINANCE_FBATCH_EXPORT_ERROR, t.getMessage()));
		} finally {
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