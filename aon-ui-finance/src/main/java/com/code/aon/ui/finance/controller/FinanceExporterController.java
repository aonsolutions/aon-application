package com.code.aon.ui.finance.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.faces.controller.LogPanelController;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.finance.BasicExporter;
import com.code.aon.ui.finance.event.FinanceSearchListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceExporterController extends BasicController implements IFinanceController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceExporterController.class.getName());
	
	private boolean payment;
	
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
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		FinanceSearchListener searchListener = (FinanceSearchListener)AonUtil.getRegisteredBean(IFinanceConstants.FINANCE_SEARCH_LISTENER_NAME);
		FinanceStatus[] defaultFinanceStatus = {FinanceStatus.RETURNED, FinanceStatus.PAID};
		searchListener.setFinanceStatuses(defaultFinanceStatus);
		searchListener.setExportMode(true);
	}

	public void onStart( ActionEvent event ) {
		ExporterController ec = (ExporterController) AonUtil.getRegisteredBean(IFinanceConstants.EXPORTER_CONTROLLER_NAME);
		BasicExporter exporter = ec.start(); 
		obtainData( exporter );
		ec.setDataMap(exporter.getDataMap());		
		ec.finish();
	}
	
	private void exportFinance( BasicExporter exporter, Finance finance ) {
		LogPanelController log = LogPanelController.getInstance();
		String reference = finance.isEmptyInvoice() ? finance.getDocumentNumber() : finance.getReferenceCode();
		try {
			exporter.init(finance);
			if ( exporter.hasAccountData() ) {
				String key = finance.isPayment() ? ICommonMessages.FINANCE_PAYMENT_EXPORT : ICommonMessages.FINANCE_CHARGE_EXPORT;
				log.info(AonUtil.getMessage(key, exporter.getType().getName(AonUtil.getCurrentLocale()), reference) );
				exporter.write();	
			} else {
				String errorKey = finance.isPayment() ? ICommonMessages.FINANCE_PAYMENT_NO_DATA : ICommonMessages.FINANCE_CHARGE_NO_DATA;
				String msg = AonUtil.getMessage(errorKey, reference);
				log.error(msg);							
			}
		} catch ( Throwable e ) {
			String errorKey = finance.isPayment() ? ICommonMessages.FINANCE_PAYMENT_EXPORT_ERROR : ICommonMessages.FINANCE_CHARGE_EXPORT_ERROR;
			String msg = AonUtil.getMessage(errorKey, reference, e.getMessage());
			LOGGER.error(msg, e);
			log.error(msg);			
		}		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<FinanceTracking> getRecordableFinanceTrackings( Finance finance ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_TRACKING_RECORDED), Boolean.FALSE);
		criteria.addNullExpression(bean.getFieldName(IEntityAlias.FINANCE_TRACKING_BANK_STATEMENT_LINK));
		String typeAlias = bean.getFieldName(IEntityAlias.FINANCE_TRACKING_TYPE);
		Expression expr1 = ExpressionUtilities.getEqualExpression(typeAlias, FinanceTrackingType.PAID);
		Expression expr2 = ExpressionUtilities.getEqualExpression(typeAlias, FinanceTrackingType.RETURNED);
		criteria.addExpression(ExpressionUtilities.getOrExpression(expr1, expr2));
		return (List) bean.getList(criteria);
	}
	
	public AccountEntryFinanceWriter getWriter() {
		if (writer == null) {
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}
	
	private void recordFinanceTracking( String sessionName, FinanceTracking financeTracking ) {
		LogPanelController log = LogPanelController.getInstance();
		String id = financeTracking.getFinance().getDocumentNumber() + " - " + financeTracking.getDescription();
		try {
			String key = financeTracking.getFinance().isPayment() ? ICommonMessages.FINANCE_PAYMENT_RECORD : ICommonMessages.FINANCE_CHARGE_RECORD;
			log.info(AonUtil.getMessage(key, id) );
			HibernateUtil.beginTransaction(sessionName);
			getWriter().recordFinanceTracking(financeTracking);
			HibernateUtil.getSession(sessionName).merge(financeTracking);
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);			
		} catch (Throwable e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				LOGGER.error(e.getMessage(), e);
			}			
			String key = financeTracking.getFinance().isPayment() ? ICommonMessages.FINANCE_PAYMENT_RECORD_ERROR : ICommonMessages.FINANCE_CHARGE_RECORD_ERROR;
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
			for( Serializable id : getCheckList() ) {
				Finance finance = (Finance) session.get(Finance.class, id);
				for( FinanceTracking financeTracking : getRecordableFinanceTrackings(finance) ) {
					recordFinanceTracking(sessionName, financeTracking);
				}
			}
			HibernateUtil.closeSession(sessionName);
			session = HibernateUtil.getSession(sessionName);
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