package com.code.aon.ui.accounting.controller.entry;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountBridgeUtil;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.SalaryEntry;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class SalaryEntryController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryEntryController.class.getName());

	private SalaryEntry entry;
	private String navigationKey;
	private AccountBridgeUtil accountBridgeUtil;
	
	private Account accDefaultSalary;
	private Account accDefaultSalaryInKind;
	private Account accDefaultAllowance;
	private Account accDefaultCompensation;
	private Account accDefaultCompanySocIns;
	private Account accSalaryChargedRet;
	private Account accSalaryChargedRetInKind;
	private Account accDefaultSocialInsurance;
	private Account accDefaultPendingSalary;

	private Account salaryAccount;
	private Account salaryInKindAccount;
	private Account allowanceAccount;
	private Account compensationAccount;
	private Account companySocInsAccount;
	private Account salaryChargedRetAccount;
	private Account salaryChargedRetInKindAccount;
	private Account socialInsuranceAccount;
	private Account pendingSalaryAccount;
	private Account rbankAccount;

	private DataModel model;

	private AccountBridgeUtil getAccountBridgeUtil() {
		if (accountBridgeUtil == null) {
			accountBridgeUtil = new AccountBridgeUtil();
		}
		return accountBridgeUtil;
	}
	
	public DataModel getModel() {
		return model;
	}
	
	public SalaryEntry getEntry() {
		return entry;
	}

	public void setEntry(SalaryEntry entry) {
		this.entry = entry;
	}

	public void onReset(ActionEvent event) {
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	public Account getSalaryAccount() {
		return salaryAccount;
	}
	public void setSalaryAccount(Account salaryAccount) {
		this.salaryAccount = salaryAccount;
	}
	public Account getSalaryInKindAccount() {
		return salaryInKindAccount;
	}
	public void setSalaryInKindAccount(Account salaryInKindAccount) {
		this.salaryInKindAccount = salaryInKindAccount;
	}

	public Account getAllowanceAccount() {
		return allowanceAccount;
	}
	public void setAllowanceAccount(Account allowanceAccount) {
		this.allowanceAccount = allowanceAccount;
	}
	public Account getCompensationAccount() {
		return compensationAccount;
	}
	public void setCompensationAccount(Account compensationAccount) {
		this.compensationAccount = compensationAccount;
	}
	public Account getCompanySocInsAccount() {
		return companySocInsAccount;
	}
	public void setCompanySocInsAccount(Account companySocInsAccount) {
		this.companySocInsAccount = companySocInsAccount;
	}
	public Account getSalaryChargedRetAccount() {
		return salaryChargedRetAccount;
	}
	public void setSalaryChargedRetAccount(Account salaryChargedRetAccount) {
		this.salaryChargedRetAccount = salaryChargedRetAccount;
	}
	public Account getSalaryChargedRetInKindAccount() {
		return salaryChargedRetInKindAccount;
	}
	public void setSalaryChargedRetInKindAccount(
			Account salaryChargedRetInKindAccount) {
		this.salaryChargedRetInKindAccount = salaryChargedRetInKindAccount;
	}
	public Account getSocialInsuranceAccount() {
		return socialInsuranceAccount;
	}
	public void setSocialInsuranceAccount(Account socialInsuranceAccount) {
		this.socialInsuranceAccount = socialInsuranceAccount;
	}
	public Account getPendingSalaryAccount() {
		return pendingSalaryAccount;
	}
	public void setPendingSalaryAccount(Account pendingSalaryAccount) {
		this.pendingSalaryAccount = pendingSalaryAccount;
	}
	public Account getRbankAccount() {
		return rbankAccount;
	}
	public void setRbankAccount(Account rbankAccount) {
		this.rbankAccount = rbankAccount;
	}

	private void reset() throws ManagerBeanException {
		resetDefaultSalary();
		resetDefaultSalaryInKind();
		resetAllowanceSalary();
		resetCompensation();
		resetSalaryChargedRetAccount();
		resetSalaryChargedRetInKindAccount();
		resetCompanySocInsAccount();
		resetSocialInsuranceAccount();
		accDefaultPendingSalary = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_PENDING_SALARY_ACC);
		model = null;

		setSalaryAccount(accDefaultSalary);
		setSalaryInKindAccount(accDefaultSalaryInKind);
		setAllowanceAccount(accDefaultAllowance);
		setCompensationAccount(accDefaultCompensation);
		setCompanySocInsAccount(accDefaultCompanySocIns);
		setSalaryChargedRetAccount(accSalaryChargedRet);
		setSalaryChargedRetInKindAccount(accSalaryChargedRetInKind);
		setSocialInsuranceAccount(accDefaultSocialInsurance);
		setPendingSalaryAccount(accDefaultPendingSalary);
		
		setEntry(new SalaryEntry());
		getEntry().setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		getEntry().setDate(new Date());
		getEntry().setSecurityLevel(SecurityLevel.OFFICIAL);
	}

	private void resetDefaultSalary() throws ManagerBeanException {
		accDefaultSalary = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_SALARY_ACC);
		if (accDefaultSalary == null) accDefaultSalary = new Account();
	}
	private void resetDefaultSalaryInKind() throws ManagerBeanException {
		accDefaultSalaryInKind = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_SALARY_IK_ACC);
		if (accDefaultSalaryInKind == null) accDefaultSalaryInKind = new Account();
	}
	private void resetAllowanceSalary() throws ManagerBeanException {
		accDefaultAllowance = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_ALLOWANCE_ACC);
		if (accDefaultAllowance == null) accDefaultAllowance = new Account();
	}
	private void resetCompensation() throws ManagerBeanException {
		accDefaultCompensation = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_COMPENSATION_ACC);
		if (accDefaultCompensation == null) accDefaultCompensation = new Account();
	}
	private void resetSalaryChargedRetAccount() throws ManagerBeanException {
		accSalaryChargedRet = AccountingUtil.obtainDefaultAccount(AppParam.ACC_SALARY_CHARGED_RET_ACC);	
		if (accSalaryChargedRet == null) accSalaryChargedRet = new Account();
	}
	private void resetSalaryChargedRetInKindAccount() throws ManagerBeanException {
		accSalaryChargedRetInKind = AccountingUtil.obtainDefaultAccount(AppParam.ACC_SALARY_CHARGED_RET_IK_ACC);	
		if (accSalaryChargedRetInKind == null) accSalaryChargedRetInKind = new Account();
	}
	private void resetCompanySocInsAccount() throws ManagerBeanException {
		accDefaultCompanySocIns = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC);
		if (accDefaultCompanySocIns == null) accDefaultCompanySocIns = new Account();
	}
	private void resetSocialInsuranceAccount() throws ManagerBeanException {
		accDefaultSocialInsurance = AccountingUtil.obtainDefaultAccount(AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC);
		if (accDefaultSocialInsurance == null) accDefaultSocialInsurance = new Account();
	}

	public String accept() {
		return navigationKey;
	}
	private AccountEntry getAccountEntry() {
		AccountEntry entry = new AccountEntry();
		entry.setEntryDate(getEntry().getDate());
		entry.setAccountPeriod(getEntry().getPeriod());
		entry.setType(AccountEntryType.SALARY);
		entry.setSecurityLevel(getEntry().getSecurityLevel());
		return entry;
	}
	
	public void onAccept(ActionEvent event) {
		// inicio transaccion
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// operaciones de la transaccion
				this.navigationKey = "accountEntry_form";
				IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
				AccountEntry entry = getAccountEntry();
				entry = (AccountEntry) entryBean.insert(entry);
				List<AccountEntryDetail> details = getAccountEntryDetails(entry);
				insertEntryDetails(details);
				// FIN operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
				loadAccountEntryController(entry);
			} catch (Exception e) {
				navigationKey = null;
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					String msg = "Unable to rollback transaction!";
					LOGGER.error(msg, e);
				}
				String msg = "Error on aon-accounting:  " + e.getMessage();
				LOGGER.error(msg, e);
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	private List<AccountEntryDetail> getAccountEntryDetails(AccountEntry entry) throws ManagerBeanException {
		
		if (getEntry().getGrossSalaryMonetary() != 0 && (getSalaryAccount() == null ||  getSalaryAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Remuneraciones monetarias'";
			AonUtil.addErrorMessage(msg);
			throw new ManagerBeanException(msg);
		}
		if (getEntry().getGrossSalaryInKind()   != 0 && (getSalaryInKindAccount() == null || getSalaryInKindAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Remuneraciones en especie'";		
			AonUtil.addErrorMessage(msg);
			throw new ManagerBeanException(msg);
		}
		if (getEntry().getAllowance() != 0 	&& (getAllowanceAccount() == null || getAllowanceAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Dietas'";
			AonUtil.addErrorMessage(msg);
			throw new ManagerBeanException(msg);
		}
		if (getEntry().getCompensation() != 0 && (getCompensationAccount() == null || getCompensationAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Indemnizaciones'";		
			AonUtil.addErrorMessage(msg);
			throw new ManagerBeanException(msg);
		}
		if (getEntry().getCompanySocialInsurance() != 0	&& (getCompanySocInsAccount() == null || getCompanySocInsAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'Seg.Social Empresa'";			
			AonUtil.addErrorMessage(msg);
			throw new ManagerBeanException(msg);
		}
		if (getEntry().getRetention() != 0	&& (getSalaryChargedRetAccount() == null || getSalaryChargedRetAccount().getId() == null)) {	
			String msg = "Debe indicar una cuenta contable para el valor 'I.R.P.F.'";
			AonUtil.addErrorMessage(msg);
			throw new ManagerBeanException(msg);
		}
		if (getEntry().getRetentionInKind() != 0 && (getSalaryChargedRetInKindAccount() == null || getSalaryChargedRetInKindAccount().getId() == null)) {
			String msg = "Debe indicar una cuenta contable para el valor 'I.R.P.F. en especie'";		
			AonUtil.addErrorMessage(msg);
			throw new ManagerBeanException(msg);
		}
		if (getEntry().getTotalSocialInsurance() != 0 && (getSocialInsuranceAccount() == null || getSocialInsuranceAccount().getId() == null)) {	
			String msg = "Debe indicar una cuenta contable para el valor 'Seg.Social Empleado'";
			AonUtil.addErrorMessage(msg);
			throw new ManagerBeanException(msg);
		}
		
		List<AccountEntryDetail> list = new LinkedList<AccountEntryDetail>();
		
		AccountEntryDetail detail = new AccountEntryDetail();
		if (getEntry().getGrossSalaryMonetary() != 0 
				&& getSalaryAccount() != null 
				&& getSalaryAccount().getId() != null) {
			detail.setAccount(getSalaryAccount());
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setDebit(getEntry().getGrossSalaryMonetary());
			list.add(detail);
		}

		if (getEntry().getGrossSalaryInKind() != 0 
				&& getSalaryInKindAccount() != null 
				&& getSalaryInKindAccount().getId() != null) {
			detail = new AccountEntryDetail();
			detail.setAccount(getSalaryInKindAccount());
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setDebit(getEntry().getGrossSalaryInKind());
			list.add(detail);
		}

		if (getEntry().getAllowance() != 0 
				&& getAllowanceAccount() != null 
				&& getAllowanceAccount().getId() != null) {
			detail = new AccountEntryDetail();
			detail.setAccount(getAllowanceAccount());
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setDebit(getEntry().getAllowance());
			list.add(detail);
		}

		if (getEntry().getCompensation() != 0 
				&& getCompensationAccount() != null 
				&& getCompensationAccount().getId() != null) {
			detail = new AccountEntryDetail();
			detail.setAccount(getCompensationAccount());
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setDebit(getEntry().getCompensation());
			list.add(detail);
		}

		if (getEntry().getCompanySocialInsurance() != 0 
				&& getCompanySocInsAccount() != null 
				&& getCompanySocInsAccount().getId() != null) {
			detail = new AccountEntryDetail();
			detail.setAccount(getCompanySocInsAccount());
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setDebit(getEntry().getCompanySocialInsurance());
			list.add(detail);
		}

		if (getEntry().getRetention() != 0 
				&& getSalaryChargedRetAccount() != null 
				&& getSalaryChargedRetAccount().getId() != null) {
			detail = new AccountEntryDetail();
			detail.setAccount(getSalaryChargedRetAccount());
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setCredit(getEntry().getRetention());
			list.add(detail);
		}

		if (getEntry().getRetentionInKind() != 0 
				&& getSalaryChargedRetInKindAccount() != null 
				&& getSalaryChargedRetInKindAccount().getId() != null) {
			detail = new AccountEntryDetail();
			detail.setAccount(getSalaryChargedRetInKindAccount());
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setCredit(getEntry().getRetentionInKind());
			list.add(detail);
		}

		if (getEntry().getTotalSocialInsurance() != 0 
				&& getSocialInsuranceAccount() != null 
				&& getSocialInsuranceAccount().getId() != null) {
			detail = new AccountEntryDetail();
			detail.setAccount(getSocialInsuranceAccount());
			detail.setAccountEntry(entry);
			detail.setBalancingAccount(null);
			detail.setConcept(getEntry().getConcept());
			detail.setCredit(getEntry().getTotalSocialInsurance());
			list.add(detail);
		}

		if (getEntry().getNetSalary() != 0) {
			detail = new AccountEntryDetail();
			Account account = null;
			if (getRbankAccount() != null) {
				account = getRbankAccount();
			} else {
				account = getPendingSalaryAccount();
			}
			if (account != null && account.getId() != null) {
				detail.setAccount(account);
				detail.setAccountEntry(entry);
				detail.setBalancingAccount(null);
				detail.setConcept(getEntry().getConcept());
				detail.setCredit(getEntry().getNetSalary());
				list.add(detail);
			}
		}
		
		return list;		
	}
	
	public void onRefresh(ActionEvent event) {
		try {
			AccountEntry entry = getAccountEntry();
			List<AccountEntryDetail> details = getAccountEntryDetails(entry);
			model = new SerializableListDataModel(details);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error al refrescar la vista previa.", e);
			model = null;
		}
	}
	public void onChangeBank(ActionEvent event) {
		try {
			if (getEntry().getRegistryBank() != null && getEntry().getRegistryBank().getId() != null) {
				rbankAccount = getAccountBridgeUtil().obtainRBankAccount(getEntry().getRegistryBank());
			} else {
				rbankAccount = null;
			}
			onRefresh(event);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error al identificar la cuenta contable del banco.", e);
			rbankAccount = null;
		}
	}
	
	private void insertEntryDetails(List<AccountEntryDetail> details) {
		try {
			IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
			for (AccountEntryDetail detail : details) {
				accountEntryDetailBean.insert(detail);	
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error al grabar el asiento de nómina.", e);
		}

	}

	private void loadAccountEntryController(AccountEntry entry) {
		try {
			AccountEntryController entryController = (AccountEntryController) FormUtil.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), entry.getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error cargando el asiento.", e);
		}
	}
	
	public double getTotalDebit() {
		double totalDebit = 0;
		if (getModel() != null) {
			@SuppressWarnings("unchecked")
			List<AccountEntryDetail> details = (List<AccountEntryDetail>) getModel().getWrappedData();
			for (AccountEntryDetail detail : details) {
				totalDebit = CommonUtil.round(totalDebit + detail.getDebit());	
			}
		}
		return totalDebit;
	}
	public double getTotalCredit() {
		double totalCredit = 0;
		if (getModel() != null) {
			@SuppressWarnings("unchecked")
			List<AccountEntryDetail> details = (List<AccountEntryDetail>) getModel().getWrappedData();
			for (AccountEntryDetail detail : details) {
				totalCredit = CommonUtil.round(totalCredit + detail.getCredit());	
			}
		}
		return totalCredit;
	}
	
	private boolean accountEquals(Account acc1,Account acc2) {
		if (acc1 == null && acc2 == null) return true;
		if (acc1 == null && acc2 != null) return false;	
		if (acc1 != null && acc2 == null) return false;	
		return ObjectUtils.equals(acc1.getId(), acc2.getId());
	}
	
	public boolean isSalaryAccountUpdatable() {
		return salaryAccount != null 
			&& salaryAccount.getId() != null
			&& !accountEquals(accDefaultSalary,salaryAccount);
	}
	public void onSameSalaryAccount(ActionEvent event){
		try {
			saveParam(AppParam.ACC_DEFAULT_SALARY_ACC, salaryAccount);
			resetDefaultSalary();
		} catch (ManagerBeanException e) {
			String m = "No se pudo modificar el valor por defecto";
			AonUtil.addErrorMessage(m);
			throw new AbortProcessingException(m,e);
		}
	}
	
	public boolean isSalaryInKindAccountUpdatable() {
		return salaryInKindAccount != null 
			&& salaryInKindAccount.getId() != null
			&& !accountEquals(accDefaultSalaryInKind,salaryInKindAccount);
	}
	public void onSameSalaryInKindAccount(ActionEvent event){
		try {
			saveParam(AppParam.ACC_DEFAULT_SALARY_IK_ACC, salaryInKindAccount);
			resetDefaultSalaryInKind();
		} catch (ManagerBeanException e) {
			String m = "No se pudo modificar el valor por defecto";
			AonUtil.addErrorMessage(m);
			throw new AbortProcessingException(m,e);
		}
	}

	public boolean isAllowanceAccountUpdatable() {
		return allowanceAccount != null 
			&& allowanceAccount.getId() != null
			&& !accountEquals(accDefaultAllowance,allowanceAccount);
	}
	public void onSameAllowanceAccount(ActionEvent event){
		try {
			saveParam(AppParam.ACC_DEFAULT_ALLOWANCE_ACC, allowanceAccount);
			resetAllowanceSalary();
		} catch (ManagerBeanException e) {
			String m = "No se pudo modificar el valor por defecto";
			AonUtil.addErrorMessage(m);
			throw new AbortProcessingException(m,e);
		}
	}

	public boolean isCompensationAccountUpdatable() {
		return compensationAccount != null 
			&& compensationAccount.getId() != null
			&& !accountEquals(accDefaultCompensation,compensationAccount);
	}
	public void onSameCompensationAccount(ActionEvent event){
		try {
			saveParam(AppParam.ACC_DEFAULT_COMPENSATION_ACC, compensationAccount);
			resetCompensation();
		} catch (ManagerBeanException e) {
			String m = "No se pudo modificar el valor por defecto";
			AonUtil.addErrorMessage(m);
			throw new AbortProcessingException(m,e);
		}
	}

	public boolean isSalaryChargedRetAccountUpdatable() {
		return salaryChargedRetAccount != null 
			&& salaryChargedRetAccount.getId() != null
			&& !accountEquals(accSalaryChargedRet,salaryChargedRetAccount);
	}
	public void onSameSalaryChargedRetAccount(ActionEvent event){
		try {
			saveParam(AppParam.ACC_SALARY_CHARGED_RET_ACC, salaryChargedRetAccount);
			resetSalaryChargedRetAccount();
		} catch (ManagerBeanException e) {
			String m = "No se pudo modificar el valor por defecto";
			AonUtil.addErrorMessage(m);
			throw new AbortProcessingException(m,e);
		}
	}
	
	public boolean isSalaryChargedRetInKindAccountUpdatable() {
		return salaryChargedRetInKindAccount != null 
			&& salaryChargedRetInKindAccount.getId() != null
			&& !accountEquals(accSalaryChargedRetInKind,salaryChargedRetInKindAccount);
	}
	public void onSameSalaryChargedRetInKindAccount(ActionEvent event){
		try {
			saveParam(AppParam.ACC_SALARY_CHARGED_RET_IK_ACC, salaryChargedRetInKindAccount);
			resetSalaryChargedRetInKindAccount();
		} catch (ManagerBeanException e) {
			String m = "No se pudo modificar el valor por defecto";
			AonUtil.addErrorMessage(m);
			throw new AbortProcessingException(m,e);
		}
	}

	public boolean isCompanySocInsAccountUpdatable() {
		return companySocInsAccount != null 
			&& companySocInsAccount.getId() != null
			&& !accountEquals(accDefaultCompanySocIns,companySocInsAccount);
	}
	public void onSameCompanySocInsAccount(ActionEvent event){
		try {
			saveParam(AppParam.ACC_DEFAULT_COMPANY_SOC_INS_ACC, companySocInsAccount);
			resetCompanySocInsAccount();
		} catch (ManagerBeanException e) {
			String m = "No se pudo modificar el valor por defecto";
			AonUtil.addErrorMessage(m);
			throw new AbortProcessingException(m,e);
		}
	}
	
	public boolean isSocialInsuranceAccountUpdatable() {
		return socialInsuranceAccount != null 
			&& socialInsuranceAccount.getId() != null
			&& !accountEquals(accDefaultSocialInsurance,socialInsuranceAccount);
	}
	public void onSameSocialInsuranceAccount(ActionEvent event){
		try {
			saveParam(AppParam.ACC_DEFAULT_SOCIAL_INSURANCE_ACC, socialInsuranceAccount);
			resetSocialInsuranceAccount();
		} catch (ManagerBeanException e) {
			String m = "No se pudo modificar el valor por defecto";
			AonUtil.addErrorMessage(m);
			throw new AbortProcessingException(m,e);
		}
	}
	
	private void saveParam(AppParam param, Account account) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME), param.getValue());
		List<ITransferObject> list = bean.getList(c);
		ApplicationParameter applicationParam = null;
		if (list != null && list.size() > 0) {
			applicationParam = (ApplicationParameter) list.get(0);
			applicationParam.setValue(Integer.toString( account.getId()));
			bean.update(applicationParam);
		} else {
			applicationParam = new ApplicationParameter();
			applicationParam.setName(param.getValue());	
			applicationParam.setValue(Integer.toString( account.getId()));
			bean.insert(applicationParam);
		}
	}

}
