package com.code.aon.ui.accounting.controller;

import java.io.Serializable;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Enterprise;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryBank;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
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
	private Date fromDate;
	private Date toDate;
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

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
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

	private void reset() throws ManagerBeanException {
		setToDate(null);
		setFromDate(null);
		setConcept(null);
		setEnterprise((Enterprise)BeanManager.getManagerBean(Enterprise.class).createNewTo());
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
	
}
