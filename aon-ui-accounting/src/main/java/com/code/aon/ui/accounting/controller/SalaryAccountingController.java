package com.code.aon.ui.accounting.controller;

import static com.code.aon.ui.common.ICommonMessages.ACCOUNTING_SALARY_CONCEPT;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.accounting.Period;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.AccountEntry;

public class SalaryAccountingController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryAccountingController.class.getName());
	
	private Enterprise enterprise;
	private Month month;
	private Period period;
	private String concept;
	private RegistryBank registryBank;
	
	private String navigationKey;
	
	public void onReset(ActionEvent event) {
		try {
			reset();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public Period getPeriod() {
		return period;
	}

	public void setPeriod(Period period) {
		this.period = period;
	}

	public String getConcept() {
		return concept;
	}

	public void setConcept(String concept) {
		this.concept = concept;
	}

	public RegistryBank getRegistryBank() {
		return registryBank;
	}

	public void setRegistryBank(RegistryBank registryBank) {
		this.registryBank = registryBank;
	}

	private Enterprise getCurrentDomainEnterprise() throws ManagerBeanException {
		if (! DomainManager.isDomainManagementAvailable() ) {
			IManagerBean bean = BeanManager.getManagerBean(Enterprise.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_DOMAIN), DomainManager.getCurrentDomain());
			List<ITransferObject> list = bean.getList(criteria);
			if (! list.isEmpty() ) {
				return (Enterprise) bean.getList(criteria).get(0);
			}			
		}
		return (Enterprise)BeanManager.getManagerBean(Enterprise.class).createNewTo();
	}
	
	private void reset() throws ManagerBeanException {
		Calendar calendar = Calendar.getInstance();
		setMonth(Month.getMonthByValue(calendar.get(Calendar.MONTH)));
		setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		updateConcept();
		setEnterprise(getCurrentDomainEnterprise());
		setRegistryBank((RegistryBank)BeanManager.getManagerBean(RegistryBank.class).createNewTo());
	}

	public String accept() {
		return navigationKey;
	}
	
	public void onAccept(ActionEvent event) {
		this.navigationKey = "accountEntry_form";
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		AONContext salaryCTX = AONContext.getAONContext(domainName, domainId);
		try {
			Integer bankId = (registryBank != null) ? registryBank.getId() : null;
			int year = CommonUtil.getYear(getPeriod().getInitiationDate());
			Date fromDate = CommonUtil.getDate(year, month.getValue(), 1);
			Date toDate = CommonUtil.getMonthLastDay(fromDate);
			AccountEntry entry = AON.insertSalaryEntry(
					salaryCTX,
					enterprise.getId(),
					fromDate,
					toDate,
					concept,
					bankId);
			if ( (entry != null) && (entry.getId() != null) ) {
				loadAccountEntryController(entry);
			}
		} catch (Throwable e) {
			this.navigationKey = null;
			LOGGER.error(">>>> onAccept",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			salaryCTX.finalize();
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

	private void updateConcept() {
		Locale locale = AonUtil.getCurrentLocale();
		String message = AonUtil.getMessage(ACCOUNTING_SALARY_CONCEPT, getMonth().getName(locale));
		setConcept(message);
	}

	public void onUpdateConcept( ActionEvent event ) {
		updateConcept();
	}
	
}
