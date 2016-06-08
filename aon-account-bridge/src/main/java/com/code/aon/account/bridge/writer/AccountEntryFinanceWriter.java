package com.code.aon.account.bridge.writer;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.bridge.AccountEntryBankStatement;
import com.code.aon.account.bridge.AccountEntryFinanceBatch;
import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.PayMethodTypeDetail;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceBatchType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountEntryFinanceWriter implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final String CHARGE = "Cobro";
	private static final String PAYMENT = "Pago";
	private static final String REFUND = "Abono";
	private static final String RETURN = "Dev.";
	private static final String INVOICE_ABRV = "Fra";
	private static final String ENTRY = "Asiento: ";
	
	private AccountBridgeUtil accountBridgeUtil;
	private AccountingUtil accountingUtil;

	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}

	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

	/* FINANCE BATCH */

	@SuppressWarnings("unchecked")
	public AccountEntry recordFBatch(FinanceBatch fBatch, Date paymentDate) throws ManagerBeanException {
		FinanceRecordingTo recordingTo = new FinanceRecordingTo();
		recordingTo.setType((fBatch.isPayment()) ? AccountEntryType.PAYMENT : AccountEntryType.COLLECTION);
		recordingTo.setDate((paymentDate!=null) ? paymentDate : fBatch.getIssueDate());
		recordingTo.setPaymentAccount(obtainPaymentAccount(fBatch.getRegistryBank(), null));
		recordingTo.setBalancingConcept(fBatch.getDescription());
		recordingTo.setSecurityLevel((fBatch.getSecurityLevel()==null) ? SecurityLevel.OFFICIAL : fBatch.getSecurityLevel());
		List <?> details = fBatch.getDetailList();
		recordingTo.setFBatchDetailList((List<FinanceBatchDetail>)details);
		return recordFBatch(recordingTo, fBatch, null);
	}

	public AccountEntry recordFBatch(FinanceRecordingTo recordingTo, FinanceBatch fBatch, AccountEntry entry) throws ManagerBeanException {
		if (entry == null) {
			entry = createAccountEntry(recordingTo);
		}
		insertFBatchDetails(recordingTo, entry);
		if (entry != null) {
	        insertAccountEntryFinanceBatch(entry, fBatch);

	        IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
	        IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
	        IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
	        for (ITransferObject ito : fBatch.getDetailList()) {
	            FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)ito;
	            fBatchDetail.setStatus(FinanceStatus.PAID);
	            fBatchDetailBean.update(fBatchDetail);

	            fBatchDetail.getFinance().setFinanceStatus(FinanceStatus.PAID);
	            financeBean.update(fBatchDetail.getFinance());

	            FinanceTrackingWriter.addFinanceTracking(fBatchDetail.getFinance(), entry.getEntryDate(), FinanceTrackingType.PAID, 
	            		ENTRY + entry.getId(), fBatch.getRegistryBank(), null, fBatchDetail.getFinance().getTotalAmount(), true);
	        }

	        fBatch.setFinanceBatchStatus(FinanceBatchStatus.RECORDED);
	        fBatchBean.update(fBatch);
		}
		return entry;
	}

	private AccountEntry insertFBatchDetails(FinanceRecordingTo recordingTo, AccountEntry entry) throws ManagerBeanException {
		double balancingAmount = 0.0;
		// Primer Apunte
		for (FinanceBatchDetail fbatchDetail : recordingTo.getFBatchDetailList()) {
			balancingAmount += fbatchDetail.getAmount();
			insertFBatchDetailEntryDetail(recordingTo, entry, fbatchDetail);
		}

		// Segundo Apunte
		if (recordingTo.getAccountMap() != null) {
			for (Account account : recordingTo.getAccountMap().keySet()) {
				double amount = recordingTo.getAccountMap().get(account);
				balancingAmount += amount;
	
				AccountEntryDetail detail = new AccountEntryDetail();
				detail.setAccountEntry(entry);
				detail.setAccount(account);
				detail.setBalancingAccount(recordingTo.getPaymentAccount());
				detail.setConcept(recordingTo.getBalancingConcept());
				if (entry.getType().equals(AccountEntryType.COLLECTION)) {
					detail.setCredit(amount);
				} else {
					detail.setDebit(amount);
				}
				detail.setDocumentNumber(null);

				IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
				accountEntryDetailBean.insert(detail);
			}
		}
		balancingAmount = CommonUtil.round(balancingAmount);

		// Tercer Apunte
		insertFBatchDetailLastEntryDetail(recordingTo, entry, balancingAmount);
		return entry;
	}

	private void insertFBatchDetailEntryDetail(FinanceRecordingTo recordingTo, AccountEntry entry, FinanceBatchDetail fbatchDetail) 
		throws ManagerBeanException {
		Account registryAccount = null;
		if (!fbatchDetail.getFinance().isPayment()) {
			registryAccount = getAccountBridgeUtil().obtainCustomerAccount(fbatchDetail.getFinance().getRegistry());
		} else {
			if (!fbatchDetail.getFinance().isPrepayment()) {
				if (!fbatchDetail.getFinance().isPayroll()) {
					registryAccount = getAccountBridgeUtil().obtainSupplierAccount(fbatchDetail.getFinance().getRegistry());
					if (registryAccount == null) {
						registryAccount = getAccountBridgeUtil().obtainCreditorAccount(fbatchDetail.getFinance().getRegistry());
					}
				} else {
					registryAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC);
				}
			} else {
				registryAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PREPAYMENT_ACC);
				if (registryAccount == null) {
					registryAccount = getAccountBridgeUtil().obtainCreditorAccount(fbatchDetail.getFinance().getRegistry());
				}
			}
		}

		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(registryAccount);
		detail.setBalancingAccount(recordingTo.getPaymentAccount());
		detail.setConcept(obtainConcept(fbatchDetail.getFinance(), fbatchDetail.getAmount(), fbatchDetail.getFinanceBatch()));
		if (!fbatchDetail.getFinance().isPayment()) {
			detail.setCredit(fbatchDetail.getAmount());
		} else {
			detail.setDebit(fbatchDetail.getAmount());
		}
		detail.setDocumentNumber(fbatchDetail.getFinance().getDocumentNumber());

		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		accountEntryDetailBean.insert(detail);
	}
	
	private void insertFBatchDetailLastEntryDetail(FinanceRecordingTo recordingTo, AccountEntry entry, double amount) throws ManagerBeanException {
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(recordingTo.getPaymentAccount());
		detail.setBalancingAccount(null);
		detail.setConcept(recordingTo.getBalancingConcept());
		if (entry.getType().equals(AccountEntryType.COLLECTION)) {
			detail.setDebit(amount);
		} else{
			detail.setCredit(amount);
		}
		detail.setDocumentNumber(null);

		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		accountEntryDetailBean.insert(detail);
	}
	
	public AccountEntryFinanceBatch insertAccountEntryFinanceBatch(AccountEntry entry, FinanceBatch fBatch) throws ManagerBeanException {
		IManagerBean accountEntryFinanceBatchBean = BeanManager.getManagerBean(AccountEntryFinanceBatch.class);
		AccountEntryFinanceBatch accEntryBatch = new AccountEntryFinanceBatch();
		accEntryBatch.setAccountEntry(entry);
		accEntryBatch.setFinanceBatch(fBatch);
		return (AccountEntryFinanceBatch) accountEntryFinanceBatchBean.insert(accEntryBatch);
	}

	public boolean canRemoveAccountEntryFinanceBatch(FinanceBatch fBatch) throws ManagerBeanException {
        IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ID), fBatch.getId());
        criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.RETURNED);
        return (fBatchDetailBean.getCount(criteria) == 0);
	}

	public void removeAccountEntryFinanceBatch(FinanceBatch fBatch) throws ManagerBeanException {
		removeAccountEntryFinanceBatch(fBatch, true);
	}

	public void removeAccountEntryFinanceBatch(FinanceBatch fBatch, boolean removeAccountEntry) throws ManagerBeanException {
		IManagerBean accountEntryFinanceBatchBean = BeanManager.getManagerBean(AccountEntryFinanceBatch.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryFinanceBatchBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_BATCH_FINANCE_BATCH_ID), fBatch.getId());
		for (ITransferObject ito : accountEntryFinanceBatchBean.getList(criteria)) {
			AccountEntryFinanceBatch accountEntryFinanceBatch = (AccountEntryFinanceBatch)ito;
			if (removeAccountEntry) {
				getAccountingUtil().checkPeriod(accountEntryFinanceBatch.getAccountEntry());
				accountEntryFinanceBatchBean.remove(accountEntryFinanceBatch);
				removeAccountEntryDetails(accountEntryFinanceBatch.getAccountEntry());
				removeAccountEntry(accountEntryFinanceBatch.getAccountEntry());
			} else {
				accountEntryFinanceBatchBean.remove(accountEntryFinanceBatch);
			}
		}

        IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
        IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
        for (ITransferObject ito : fBatch.getDetailList()) {
            FinanceBatchDetail fbatchDetail = (FinanceBatchDetail)ito;
            fbatchDetail.setStatus(FinanceStatus.BATCHED);
            fBatchDetailBean.update(fbatchDetail);

            fbatchDetail.getFinance().setFinanceStatus(FinanceStatus.BATCHED);
            financeBean.update(fbatchDetail.getFinance());

            FinanceTrackingWriter.removeLastTrackingByType(fbatchDetail.getFinance(), FinanceTrackingType.PAID);
        }
        
        IManagerBean fBatchBean = BeanManager.getManagerBean(FinanceBatch.class);
        fBatch.setFinanceBatchStatus((fBatch.getFinanceBatchType() == FinanceBatchType.NONE) ? FinanceBatchStatus.TODO : FinanceBatchStatus.DONE);
        fBatchBean.update(fBatch);
	}


	/* FINANCE */
	public AccountEntry recordFinance(Finance finance, RegistryBank registryBank, PayMethodTypeDetail payMethodTypeDetail, Date paymentDate) 
		throws ManagerBeanException {
		List<Finance> list = new LinkedList<Finance>();
		list.add(finance);

		FinanceRecordingTo recordingTo = new FinanceRecordingTo();
		recordingTo.setType((finance.isPayment()) ? AccountEntryType.PAYMENT : AccountEntryType.COLLECTION);
		recordingTo.setDate(paymentDate);
		recordingTo.setPaymentAccount(obtainPaymentAccount(registryBank, payMethodTypeDetail));
		recordingTo.setSecurityLevel(finance.getSecurityLevel());
		recordingTo.setFinanceList(list);
		return recordFinances(recordingTo, null);
	}

	public AccountEntry recordFinances(FinanceRecordingTo recordingTo, AccountEntry entry) throws ManagerBeanException {
		if (entry == null) {
			entry = createAccountEntry(recordingTo);
		}
		insertFinanceEntryDetails(recordingTo, entry);
		return entry;
	}

	private void insertFinanceEntryDetails(FinanceRecordingTo recordingTo, AccountEntry entry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Account registryAccount = null;
		String concept = null;
		String documentNumber = null;
		double balancingAmount = 0;
		// Primer Apunte
		for (Finance finance: recordingTo.getFinanceList()) {
			if (!finance.isPayment()) {
				registryAccount = getAccountBridgeUtil().obtainCustomerAccount(finance.getRegistry());
			} else {
				if (!finance.isPrepayment()) {
					if (!finance.isPayroll()) {
						registryAccount = getAccountBridgeUtil().obtainSupplierAccount(finance.getRegistry());
						if (registryAccount == null) {
							registryAccount = getAccountBridgeUtil().obtainCreditorAccount(finance.getRegistry());
						}
					} else {
						registryAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC);
					}
				} else {
					registryAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PREPAYMENT_ACC);
					if (registryAccount == null) {
						registryAccount = getAccountBridgeUtil().obtainCreditorAccount(finance.getRegistry());
					}
				}
			}
			concept = obtainConcept(finance, finance.getTotalAmount(), null);
			documentNumber = finance.getDocumentNumber();
			balancingAmount += finance.getTotalAmount() * obtainBalancingFactor(finance.isPayment(), entry.getType());

			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccountEntry(entry);
			detail.setAccount(registryAccount);
			detail.setBalancingAccount(recordingTo.getPaymentAccount());
			detail.setConcept(concept);
			if (!finance.isPayment()) {
				detail.setCredit(finance.getTotalAmount());
			} else {
				detail.setDebit(finance.getTotalAmount());
			}
			detail.setDocumentNumber(documentNumber);
			accountEntryDetailBean.insert(detail);
		}

		// Segundo Apunte
		if (recordingTo.getAccountMap() != null) {
			for (Account account : recordingTo.getAccountMap().keySet()) {
				double amount = recordingTo.getAccountMap().get(account);
				balancingAmount += amount;
	
				AccountEntryDetail detail = new AccountEntryDetail();
				detail.setAccountEntry(entry);
				detail.setAccount(account);
				detail.setBalancingAccount(recordingTo.getPaymentAccount());
				detail.setConcept((StringUtils.isEmpty(recordingTo.getBalancingConcept())) ? concept : recordingTo.getBalancingConcept());
				if (entry.getType().equals(AccountEntryType.COLLECTION)) {
					detail.setCredit(amount);
				} else {
					detail.setDebit(amount);
				}
				detail.setDocumentNumber((recordingTo.getFinanceList().size()==1) ? documentNumber : null);
				accountEntryDetailBean.insert(detail);
			}
		}
		balancingAmount = CommonUtil.round(balancingAmount);

		// Tercer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(recordingTo.getPaymentAccount());
		detail.setBalancingAccount((recordingTo.getFinanceList().size()==1) ? registryAccount : null);
		detail.setConcept((StringUtils.isEmpty(recordingTo.getBalancingConcept())) ? concept : recordingTo.getBalancingConcept());
		if (entry.getType().equals(AccountEntryType.COLLECTION)) {
			detail.setDebit(balancingAmount);
		} else {
			detail.setCredit(balancingAmount);
		}
		detail.setDocumentNumber((recordingTo.getFinanceList().size()==1) ? documentNumber : null);
		accountEntryDetailBean.insert(detail);
	}


	/* FINANCE RETURN */
	public AccountEntry returnFinance(Finance finance, RegistryBank registryBank, PayMethodTypeDetail payMethodTypeDetail, Date returnDate) 
		throws ManagerBeanException {
		List<Finance> list = new LinkedList<Finance>();
		list.add(finance);

		FinanceRecordingTo recordingTo = new FinanceRecordingTo();
		recordingTo.setType((finance.isPayment()) ? AccountEntryType.RETURNED_PAYMENT : AccountEntryType.RETURNED_COLLECTION);
		recordingTo.setDate(returnDate);
		recordingTo.setPaymentAccount(obtainPaymentAccount(registryBank, payMethodTypeDetail));
		recordingTo.setSecurityLevel((finance.getSecurityLevel()==null) ? SecurityLevel.OFFICIAL : finance.getSecurityLevel());
		recordingTo.setFinanceList(list);
		return returnFinances(recordingTo, null);
	}

	private AccountEntry returnFinances(FinanceRecordingTo recordingTo, AccountEntry entry) throws ManagerBeanException {
		if (entry == null) {
			entry = createAccountEntry(recordingTo);
		}
		insertReturnFinanceEntryDetails(recordingTo, entry);
		return entry;
	}

	private void insertReturnFinanceEntryDetails(FinanceRecordingTo recordingTo, AccountEntry entry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Account registryAccount = null;
		String concept = null;
		String documentNumber = null;
		double balancingAmount = 0;
		// Primer Apunte
		for (Finance finance: recordingTo.getFinanceList()) {
			if (!finance.isPayment()) {
				registryAccount = getAccountBridgeUtil().obtainCustomerAccount(finance.getRegistry());
			} else {
				if (!finance.isPrepayment()) {
					if (!finance.isPayroll()) {
						registryAccount = getAccountBridgeUtil().obtainSupplierAccount(finance.getRegistry());
						if (registryAccount == null) {
							registryAccount = getAccountBridgeUtil().obtainCreditorAccount(finance.getRegistry());
						}
					} else {
						registryAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC);
					}
				} else {
					registryAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PREPAYMENT_ACC);
					if (registryAccount == null) {
						registryAccount = getAccountBridgeUtil().obtainCreditorAccount(finance.getRegistry());
					}
				}
			}
			concept = obtainReturnConcept(finance);
			documentNumber = finance.getDocumentNumber();
			balancingAmount += finance.getTotalAmount() * obtainBalancingFactor(finance.isPayment(), entry.getType());

			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccountEntry(entry);
			detail.setAccount(registryAccount);
			detail.setBalancingAccount(recordingTo.getPaymentAccount());
			detail.setConcept(concept);
			if (!finance.isPayment()) {
				detail.setDebit(finance.getTotalAmount());
			} else {
				detail.setCredit(finance.getTotalAmount());
			}
			detail.setDocumentNumber(documentNumber);
			accountEntryDetailBean.insert(detail);
		}

		// Segundo Apunte
		if (recordingTo.getAccountMap() != null) {
			for (Account account : recordingTo.getAccountMap().keySet()) {
				double amount = recordingTo.getAccountMap().get(account);
				balancingAmount += amount;
	
				AccountEntryDetail detail = new AccountEntryDetail();
				detail.setAccountEntry(entry);
				detail.setAccount(account);
				detail.setBalancingAccount(recordingTo.getPaymentAccount());
				detail.setConcept((StringUtils.isEmpty(recordingTo.getBalancingConcept())) ? concept : recordingTo.getBalancingConcept());
				if (entry.getType().equals(AccountEntryType.RETURNED_COLLECTION)) {
					detail.setDebit(amount);
				} else {
					detail.setCredit(amount);
				}
				detail.setDocumentNumber((recordingTo.getFinanceList().size()==1) ? documentNumber : null);
				accountEntryDetailBean.insert(detail);
			}
		}
		balancingAmount = CommonUtil.round(balancingAmount);

		// Tercer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(recordingTo.getPaymentAccount());
		detail.setBalancingAccount((recordingTo.getFinanceList().size()==1) ? registryAccount : null);
		detail.setConcept((StringUtils.isEmpty(recordingTo.getBalancingConcept())) ? concept : recordingTo.getBalancingConcept());
		if (entry.getType().equals(AccountEntryType.RETURNED_COLLECTION)) {
			detail.setCredit(balancingAmount);
		} else {
			detail.setDebit(balancingAmount);
		}
		detail.setDocumentNumber((recordingTo.getFinanceList().size()==1) ? documentNumber : null);
		accountEntryDetailBean.insert(detail);
	}


	/* FINANCE TRACKING */
	public AccountEntry recordFinanceTracking(FinanceTracking tracking) throws ManagerBeanException {
		List<FinanceTracking> list = new LinkedList<FinanceTracking>();
		list.add(tracking);

		FinanceRecordingTo recordingTo = new FinanceRecordingTo();
		if (tracking.getType() != FinanceTrackingType.RETURNED) {
			recordingTo.setType((tracking.getFinance().isPayment()) ? AccountEntryType.PAYMENT : AccountEntryType.COLLECTION);
		} else {
			recordingTo.setType((tracking.getFinance().isPayment()) ? AccountEntryType.RETURNED_PAYMENT : AccountEntryType.RETURNED_COLLECTION);
		}
		recordingTo.setDate(tracking.getTrackingDate());
		recordingTo.setPaymentAccount(obtainPaymentAccount(tracking.getRegistryBank(), tracking.getPayMethodTypeDetail()));
		recordingTo.setSecurityLevel(tracking.getFinance().getSecurityLevel());
		recordingTo.setFinanceTrackingList(list);
		return recordFinanceTrackings(recordingTo, null);
	}

	public AccountEntry recordFinanceTrackings(FinanceRecordingTo recordingTo, AccountEntry entry) throws ManagerBeanException {
		if (entry == null) {
			entry = createAccountEntry(recordingTo);
		}
		if (recordingTo.getType() == AccountEntryType.PAYMENT || recordingTo.getType() == AccountEntryType.COLLECTION) {
			insertFinanceTrackingEntryDetails(recordingTo, entry);
		} else {
			insertReturnFinanceTrackingEntryDetails(recordingTo, entry);
		}
		if (entry != null) {
			IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
			for (FinanceTracking tracking: recordingTo.getFinanceTrackingList()) {
				tracking.setDescription(ENTRY + entry.getId());
				tracking.setRecorded(true);
				trackingBean.update(tracking);

				insertAccountEntryFinanceTracking(entry, tracking);
			}
		}
		return entry;
	}

	private void insertFinanceTrackingEntryDetails(FinanceRecordingTo recordingTo, AccountEntry entry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Account registryAccount = null;
		String concept = null;
		String documentNumber = null;
		double balancingAmount = 0;
		// Primer Apunte
		for (FinanceTracking tracking: recordingTo.getFinanceTrackingList()) {
			if (!tracking.getFinance().isPayment()) {
				registryAccount = getAccountBridgeUtil().obtainCustomerAccount(tracking.getFinance().getRegistry());
			} else {
				if (!tracking.getFinance().isPrepayment()) {
					if (!tracking.getFinance().isPayroll()) {
						registryAccount = getAccountBridgeUtil().obtainSupplierAccount(tracking.getFinance().getRegistry());
						if (registryAccount == null) {
							registryAccount = getAccountBridgeUtil().obtainCreditorAccount(tracking.getFinance().getRegistry());
						}
					} else {
						registryAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC);
					}
				} else {
					registryAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PREPAYMENT_ACC);
					if (registryAccount == null) {
						registryAccount = getAccountBridgeUtil().obtainCreditorAccount(tracking.getFinance().getRegistry());
					}
				}
			}
			concept = obtainConcept(tracking.getFinance(), tracking.getAmount(), null);
			documentNumber = tracking.getFinance().getDocumentNumber();
			balancingAmount += tracking.getAmount() * obtainBalancingFactor(tracking.getFinance().isPayment(), entry.getType());

			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccountEntry(entry);
			detail.setAccount(registryAccount);
			detail.setBalancingAccount(recordingTo.getPaymentAccount());
			detail.setConcept(concept);
			if (!tracking.getFinance().isPayment()) {
				detail.setCredit(tracking.getAmount());
			} else {
				detail.setDebit(tracking.getAmount());
			}
			detail.setDocumentNumber(documentNumber);
			accountEntryDetailBean.insert(detail);
		}

		// Segundo Apunte
		if (recordingTo.getAccountMap() != null) {
			for (Account account : recordingTo.getAccountMap().keySet()) {
				double amount = recordingTo.getAccountMap().get(account);
				balancingAmount += amount;
	
				AccountEntryDetail detail = new AccountEntryDetail();
				detail.setAccountEntry(entry);
				detail.setAccount(account);
				detail.setBalancingAccount(recordingTo.getPaymentAccount());
				detail.setConcept((StringUtils.isEmpty(recordingTo.getBalancingConcept())) ? concept : recordingTo.getBalancingConcept());
				if (entry.getType().equals(AccountEntryType.COLLECTION)) {
					detail.setCredit(amount);
				} else {
					detail.setDebit(amount);
				}
				detail.setDocumentNumber((recordingTo.getFinanceTrackingList().size()==1) ? documentNumber : null);
				accountEntryDetailBean.insert(detail);
			}
		}
		balancingAmount = CommonUtil.round(balancingAmount);

		// Tercer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(recordingTo.getPaymentAccount());
		detail.setBalancingAccount((recordingTo.getFinanceTrackingList().size()==1) ? registryAccount : null);
		detail.setConcept((StringUtils.isEmpty(recordingTo.getBalancingConcept())) ? concept : recordingTo.getBalancingConcept());
		if (entry.getType().equals(AccountEntryType.COLLECTION)) {
			detail.setDebit(balancingAmount);
		} else {
			detail.setCredit(balancingAmount);
		}
		detail.setDocumentNumber((recordingTo.getFinanceTrackingList().size()==1) ? documentNumber : null);
		accountEntryDetailBean.insert(detail);
	}

	private void insertReturnFinanceTrackingEntryDetails(FinanceRecordingTo recordingTo, AccountEntry entry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Account registryAccount = null;
		String concept = null;
		String documentNumber = null;
		double balancingAmount = 0;
		// Primer Apunte
		for (FinanceTracking tracking: recordingTo.getFinanceTrackingList()) {
			if (!tracking.getFinance().isPayment()) {
				registryAccount = getAccountBridgeUtil().obtainCustomerAccount(tracking.getFinance().getRegistry());
			} else {
				if (!tracking.getFinance().isPrepayment()) {
					if (!tracking.getFinance().isPayroll()) {
						registryAccount = getAccountBridgeUtil().obtainSupplierAccount(tracking.getFinance().getRegistry());
						if (registryAccount == null) {
							registryAccount = getAccountBridgeUtil().obtainCreditorAccount(tracking.getFinance().getRegistry());
						}
					} else {
						registryAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC);
					}
				} else {
					registryAccount = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PREPAYMENT_ACC);
					if (registryAccount == null) {
						registryAccount = getAccountBridgeUtil().obtainCreditorAccount(tracking.getFinance().getRegistry());
					}
				}
			}
			concept = obtainReturnConcept(tracking.getFinance());
			documentNumber = tracking.getFinance().getDocumentNumber();
			balancingAmount += tracking.getAmount() * obtainBalancingFactor(tracking.getFinance().isPayment(), entry.getType());

			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccountEntry(entry);
			detail.setAccount(registryAccount);
			detail.setBalancingAccount(recordingTo.getPaymentAccount());
			detail.setConcept(concept);
			if (!tracking.getFinance().isPayment()) {
				detail.setDebit(tracking.getAmount());
			} else {
				detail.setCredit(tracking.getAmount());
			}
			detail.setDocumentNumber(documentNumber);
			accountEntryDetailBean.insert(detail);
		}

		// Segundo Apunte
		if (recordingTo.getAccountMap() != null) {
			for (Account account : recordingTo.getAccountMap().keySet()) {
				double amount = recordingTo.getAccountMap().get(account);
				balancingAmount += amount;
	
				AccountEntryDetail detail = new AccountEntryDetail();
				detail.setAccountEntry(entry);
				detail.setAccount(account);
				detail.setBalancingAccount(recordingTo.getPaymentAccount());
				detail.setConcept((StringUtils.isEmpty(recordingTo.getBalancingConcept())) ? concept : recordingTo.getBalancingConcept());
				if (entry.getType().equals(AccountEntryType.RETURNED_COLLECTION)) {
					detail.setDebit(amount);
				} else {
					detail.setCredit(amount);
				}
				detail.setDocumentNumber((recordingTo.getFinanceTrackingList().size()==1) ? documentNumber : null);
				accountEntryDetailBean.insert(detail);
			}
		}
		balancingAmount = CommonUtil.round(balancingAmount);

		// Tercer Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(recordingTo.getPaymentAccount());
		detail.setBalancingAccount((recordingTo.getFinanceTrackingList().size()==1) ? registryAccount : null);
		detail.setConcept((StringUtils.isEmpty(recordingTo.getBalancingConcept())) ? concept : recordingTo.getBalancingConcept());
		if (entry.getType().equals(AccountEntryType.RETURNED_COLLECTION)) {
			detail.setCredit(balancingAmount);
		} else {
			detail.setDebit(balancingAmount);
		}
		detail.setDocumentNumber((recordingTo.getFinanceTrackingList().size()==1) ? documentNumber : null);
		accountEntryDetailBean.insert(detail);
	}

	public AccountEntryFinanceTracking insertAccountEntryFinanceTracking(AccountEntry entry, FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean accountEntryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		AccountEntryFinanceTracking accEntryTracking = new AccountEntryFinanceTracking();
		accEntryTracking.setAccountEntry(entry);
		accEntryTracking.setFinanceTracking(tracking);
		return (AccountEntryFinanceTracking) accountEntryFinanceTrackingBean.insert(accEntryTracking);
	}

	public void removeAccountEntryFinanceTracking(FinanceTracking tracking) throws ManagerBeanException {
		removeAccountEntryFinanceTracking(tracking, true);
	}

	public void removeAccountEntryFinanceTracking(FinanceTracking tracking, boolean removeAccountEntry) throws ManagerBeanException {
		IManagerBean accountEntryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryFinanceTrackingBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_ID), tracking.getId());
		for (ITransferObject ito : accountEntryFinanceTrackingBean.getList(criteria)) {
			AccountEntryFinanceTracking accountEntryFinanceTracking = (AccountEntryFinanceTracking)ito;
			if (removeAccountEntry) {
				getAccountingUtil().checkPeriod(accountEntryFinanceTracking.getAccountEntry());
				accountEntryFinanceTrackingBean.remove(accountEntryFinanceTracking);
				removeAccountEntryDetails(accountEntryFinanceTracking.getAccountEntry());
				removeAccountEntry(accountEntryFinanceTracking.getAccountEntry());
			} else {
				accountEntryFinanceTrackingBean.remove(accountEntryFinanceTracking);
			}
		}
	}


	/* BANK STATEMENT */
	public AccountEntry recordBankStatementLinks(FinanceRecordingTo recordingTo, boolean payment, double amount) throws ManagerBeanException {
		AccountEntry entry = createAccountEntry(recordingTo);

		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Account registryAccount = null;
		// Primer Apunte
		for (Account account : recordingTo.getAccountMap().keySet()) {
			registryAccount = account;

			AccountEntryDetail detail = new AccountEntryDetail();
			detail.setAccountEntry(entry);
			detail.setAccount(account);
			detail.setBalancingAccount(recordingTo.getPaymentAccount());
			detail.setConcept(recordingTo.getBalancingConcept());
			if (!payment) {
				detail.setCredit(recordingTo.getAccountMap().get(account));
			} else {
				detail.setDebit(recordingTo.getAccountMap().get(account));
			}
			detail.setDocumentNumber(null);
			accountEntryDetailBean.insert(detail);
		}

		// Segundo Apunte
		AccountEntryDetail detail = new AccountEntryDetail();
		detail.setAccountEntry(entry);
		detail.setAccount(recordingTo.getPaymentAccount());
		detail.setBalancingAccount((recordingTo.getAccountMap().size()==1) ? registryAccount : null);
		detail.setConcept(recordingTo.getBalancingConcept());
		if (!payment) {
			detail.setDebit(amount);
		} else {
			detail.setCredit(amount);
		}
		detail.setDocumentNumber(null);
		accountEntryDetailBean.insert(detail);

		return entry;
	}

	public AccountEntryBankStatement insertAccountEntryBankStatement(AccountEntry entry, BankStatement statement) throws ManagerBeanException {
		IManagerBean accountEntryBankStatementBean = BeanManager.getManagerBean(AccountEntryBankStatement.class);
		AccountEntryBankStatement accEntryStatement = new AccountEntryBankStatement();
		accEntryStatement.setAccountEntry(entry);
		accEntryStatement.setBankStatement(statement);
		return (AccountEntryBankStatement) accountEntryBankStatementBean.insert(accEntryStatement);
	}

	public void removeAccountEntryBankStatement(BankStatement statement) throws ManagerBeanException {
		removeAccountEntryBankStatement(statement, true);
	}

	public void removeAccountEntryBankStatement(BankStatement statement, boolean removeAccountEntry) throws ManagerBeanException {
		IManagerBean accountEntryBankStatementBean = BeanManager.getManagerBean(AccountEntryBankStatement.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryBankStatementBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_BANK_STATEMENT_BANK_STATEMENT_ID), statement.getId());
		for (ITransferObject ito : accountEntryBankStatementBean.getList(criteria)) {
			AccountEntryBankStatement accountEntryBankStatement= (AccountEntryBankStatement)ito;
			if (removeAccountEntry) {
				getAccountingUtil().checkPeriod(accountEntryBankStatement.getAccountEntry());
				accountEntryBankStatementBean.remove(accountEntryBankStatement);
				removeAccountEntryDetails(accountEntryBankStatement.getAccountEntry());
				removeAccountEntry(accountEntryBankStatement.getAccountEntry());
			} else {
				accountEntryBankStatementBean.remove(accountEntryBankStatement);
			}
		}
	}

	/* COMMON METHODS */
	private AccountEntry createAccountEntry(FinanceRecordingTo to) throws ManagerBeanException {
		Period period = (to.getPeriod()!=null && to.getPeriod().getId()!=null) ? to.getPeriod() : getAccountingUtil().obtainPeriod(to.getDate());
		SecurityLevel securityLevel = (to.getSecurityLevel()==null) ? SecurityLevel.OFFICIAL : to.getSecurityLevel();

		AccountEntry entry = new AccountEntry();
		entry.setAccountPeriod(period);
		entry.setEntryDate(to.getDate());
		entry.setType(to.getType());
		entry.setJournal(null);
		entry.setSecurityLevel(securityLevel);
		entry.setComments(to.getComments());

		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		return (AccountEntry)accountEntryBean.insert(entry);
	}

	public Account obtainPaymentAccount(RegistryBank registryBank, PayMethodTypeDetail payMethodTypeDetail) throws ManagerBeanException {
		Account account = null;
		if (registryBank != null) {
			account = getAccountBridgeUtil().obtainRBankAccount(registryBank);
		} else if (payMethodTypeDetail != null) {
			account = payMethodTypeDetail.getAccount();
		}
		return (account!=null) ? account : getAccountingUtil().obtainCashAccount();
	}

	private String obtainConcept(Finance finance, double total, FinanceBatch fbatch) {
		String prefix = (total < 0) ? REFUND : (!finance.isPayment()) ? CHARGE : PAYMENT;
		prefix = prefix + " " + (!finance.isPayroll() ? INVOICE_ABRV + ": " : "");
		String concept = (!finance.isEmptyInvoice()) ? finance.getInvoice().getReferenceCode() : finance.getConcept();
		String fbatchConcept = (fbatch != null) ? (" (R:" + fbatch.getId() + ")") : "";
		return StringUtils.abbreviate(prefix + concept + fbatchConcept, 32);
	}

	private String obtainReturnConcept(Finance finance) {
		String prefix = RETURN + " " + INVOICE_ABRV + ": ";
		String concept = (!finance.isEmptyInvoice()) ? finance.getInvoice().getReferenceCode() : finance.getConcept();
		return StringUtils.abbreviate(prefix + concept, 32);
	}

	private double obtainBalancingFactor(boolean payment, AccountEntryType entryType) {
		boolean entryPayment = (entryType == AccountEntryType.PAYMENT || entryType == AccountEntryType.RETURNED_PAYMENT);
		if (payment == entryPayment) {
			return 1;
		} else {
			return -1;
		}
	}

	private void removeAccountEntryDetails(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
		for (ITransferObject ito : accountEntryDetailBean.getList(criteria)) {
			AccountEntryDetail accEntryDetail = (AccountEntryDetail)ito;
			accountEntryDetailBean.remove(accEntryDetail);
		}
	}

	private void removeAccountEntry(AccountEntry accountEntry) throws ManagerBeanException {
		BeanManager.getManagerBean(AccountEntry.class).remove(accountEntry);
	}

}