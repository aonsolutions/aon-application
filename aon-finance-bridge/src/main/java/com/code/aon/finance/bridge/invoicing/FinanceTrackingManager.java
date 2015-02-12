package com.code.aon.finance.bridge.invoicing;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_TRACKING_FRACTIONED;
import static com.code.aon.ui.common.ICommonMessages.FINANCE_TRACKING_SETTLED;
import static com.code.aon.ui.common.ICommonMessages.PENDING;
import static com.code.aon.ui.common.ICommonMessages.TRACKING_RECORDED;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceTrackingManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(FinanceTrackingManager.class.getName());

	private Finance finance;
	private FinanceGenerator financeGenerator;
	private AccountEntryFinanceWriter writer;

	public FinanceTrackingManager(Finance finance) {
		this.finance = finance;
	}

	public FinanceGenerator getFinanceGenerator() {
		if (financeGenerator == null) {
			financeGenerator = new FinanceGenerator();
		}
		return financeGenerator;
	}

	private AccountEntryFinanceWriter getWriter() {
		if (writer == null){
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}

	public Finance pay(double amount, RegistryBank rBank, PayMethodTypeDetail pmTypeDetail, Date date, boolean record) throws ManagerBeanException {
		double financeAmount = finance.getAmount();
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			if (amount != finance.getTotalAmount()) {
				String message = AonUtil.getMessage(FINANCE_TRACKING_FRACTIONED, 1, 2);
				FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.FRACTIONED, message, finance.getTotalAmount());

				Finance fraction = getFinanceGenerator().duplicateFinance(finance, CommonUtil.round(finance.getTotalAmount() - amount));
				message = AonUtil.getMessage(FINANCE_TRACKING_FRACTIONED, 2, 2);
				FinanceTrackingWriter.addFinanceTracking(fraction, new Date(), FinanceTrackingType.FRACTIONED, message, finance.getTotalAmount());

				finance.setAmount(CommonUtil.round(amount - finance.getExpenses()));
			}

			AccountEntry entry = (record) ? recordPayment(rBank, pmTypeDetail, date) : null;
			String message = (record) ? AonUtil.getMessage(TRACKING_RECORDED) + " " + entry.getId() : AonUtil.getMessage(PENDING);
			FinanceTracking tracking = payFinance(message, rBank, pmTypeDetail, date, record);
			if (record) {
				getWriter().insertAccountEntryFinanceTracking(entry, tracking);
			}
			updateFinanceStatus(sessionName, FinanceStatus.PAID);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return finance;
		} catch (Exception e) {
			finance.setAmount(financeAmount);
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private AccountEntry recordPayment(RegistryBank rBank, PayMethodTypeDetail pmTypeDetail, Date date) throws ManagerBeanException {
		return getWriter().recordFinance(finance, rBank, pmTypeDetail, date);
	}

	private FinanceTracking payFinance(String message, RegistryBank rBank, PayMethodTypeDetail pmTypeDetail, Date date, boolean record) {
		FinanceTrackingType type = FinanceTrackingType.PAID;
		return FinanceTrackingWriter.addFinanceTracking(finance, date, type, message, rBank, pmTypeDetail, finance.getTotalAmount(), record);
	}

	private void updateFinanceStatus(String sessionName, FinanceStatus status) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		finance.setFinanceStatus(status);
		financeBean.restoreNullSubPOJOs(finance);
		finance = (Finance)HibernateUtil.getSession(sessionName).merge(finance);	
		finance = (Finance)financeBean.update(finance);
	}

	public Finance returnPay(double expenses, RegistryBank rBank, PayMethodTypeDetail pmTypeDetail, Date date, boolean record) throws ManagerBeanException {
		double financeExpenses = finance.getExpenses();
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			if (finance.getExpenses() != expenses) {
				finance.setExpenses(expenses);
			}

			AccountEntry entry = (record) ? recordReturn(rBank, pmTypeDetail, date) : null;
			String message = (record) ? AonUtil.getMessage(TRACKING_RECORDED) + " " + entry.getId() : AonUtil.getMessage(PENDING);
			FinanceTracking tracking = returnFinance(message, rBank, pmTypeDetail, date, record);
			if (record) {
				getWriter().insertAccountEntryFinanceTracking(entry, tracking);
			}
			returnFinanceBatchDetail(sessionName);

			updateFinanceStatus(sessionName, FinanceStatus.PAID);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return finance;
		} catch (Exception e) {
			finance.setExpenses(financeExpenses);
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private AccountEntry recordReturn(RegistryBank rBank, PayMethodTypeDetail pmTypeDetail, Date date) throws ManagerBeanException {
		return getWriter().returnFinance(finance, rBank, pmTypeDetail, date);
	}

	private FinanceTracking returnFinance(String message, RegistryBank rBank, PayMethodTypeDetail pmTypeDetail, Date date, boolean record) {
		FinanceTrackingType type = FinanceTrackingType.RETURNED;
		return FinanceTrackingWriter.addFinanceTracking(finance, date, type, message, rBank, pmTypeDetail, finance.getTotalAmount(), record);
	}

	private void returnFinanceBatchDetail(String sessionName) throws ManagerBeanException {
		IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.PAID);
		for (ITransferObject ito : fBatchDetailBean.getList(criteria)) {
			FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)ito;
			fBatchDetail.setStatus(FinanceStatus.RETURNED);
			fBatchDetail = (FinanceBatchDetail)HibernateUtil.getSession(sessionName).merge(fBatchDetail);
			fBatchDetailBean.update(fBatchDetail);
		}
	}

	public Finance settle() throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			String message = AonUtil.getMessage(FINANCE_TRACKING_SETTLED);
			settleFinance(message);
			updateFinanceStatus(sessionName, FinanceStatus.SETTLED);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);

			return finance;
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	private FinanceTracking settleFinance(String message) {
		return FinanceTrackingWriter.addFinanceTracking(finance, new Date(), FinanceTrackingType.SETTLED, message);
	}

	public void recordTracking(FinanceTracking tracking) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			if (tracking.getType() == FinanceTrackingType.PAID || tracking.getType() == FinanceTrackingType.RETURNED) {
				getWriter().recordFinanceTracking(tracking);
			}

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

	public void removeTracking(FinanceTracking tracking) throws ManagerBeanException {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			getWriter().removeAccountEntryFinanceTracking(tracking);
			BeanManager.getManagerBean(FinanceTracking.class).remove(tracking);

			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
		} catch (Exception e) {
			try {
				HibernateUtil.rollbackTransaction(sessionName);
			} catch (DAOException daoe) {
				String msg = "Unable to rollback transaction!";
				LOGGER.error(msg,daoe);
			}
			LOGGER.error(e.getMessage());
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}

}
