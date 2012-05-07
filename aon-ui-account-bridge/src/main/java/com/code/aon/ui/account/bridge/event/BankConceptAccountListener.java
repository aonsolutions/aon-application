package com.code.aon.ui.account.bridge.event;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.BankConceptAccount;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.BankConcept;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class BankConceptAccountListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		BankConcept bankConcept = (BankConcept)event.getController().getTo();
		bankConcept.setAccount(new Account());
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		BankConcept concept = (BankConcept)event.getController().getTo();
		try {
			IManagerBean conceptAccBean = BeanManager.getManagerBean(BankConceptAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(conceptAccBean.getFieldName(IEntityAlias.BANK_CONCEPT_ACCOUNT_BANK_CONCEPT_ID), concept.getId());
			Iterator<ITransferObject> iterator = conceptAccBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				BankConceptAccount conceptAccount = (BankConceptAccount)iterator.next();
				concept.setAccount(conceptAccount.getAccount());
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}

		if (concept.getAccount() == null) {
			concept.setAccount(new Account());
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BankConcept bankConcept = (BankConcept)event.getController().getTo();
		if (bankConcept.getAccount() != null && !StringUtils.isEmpty(bankConcept.getAccount().getCode())) {
			validateAccount(bankConcept.getAccount());
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		BankConcept bankConcept = (BankConcept)event.getController().getTo();
		if (bankConcept.getAccount() != null && !StringUtils.isEmpty(bankConcept.getAccount().getCode())) {
			insertBankConceptAccount(bankConcept, bankConcept.getAccount());
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		BankConcept bankConcept = (BankConcept)event.getController().getTo();
		if (bankConcept.getAccount() != null && !StringUtils.isEmpty(bankConcept.getAccount().getCode())) {
			validateAccount(bankConcept.getAccount());
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		BankConcept bankConcept = (BankConcept)event.getController().getTo();
		if (bankConcept.getAccount() != null && !StringUtils.isEmpty(bankConcept.getAccount().getCode())) {
			updateBankConceptAccount(bankConcept, bankConcept.getAccount());
		} else {
			removeBankConceptAccount(bankConcept);
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		BankConcept bankConcept = (BankConcept)event.getController().getTo();
		removeBankConceptAccount(bankConcept);
	}

	private void validateAccount(Account account) throws ControllerListenerException {
		if (account == null) {
			throw new ControllerListenerException("La Cuenta Contable no es válida.");
		}
		if (!account.isEntryEnabled()) {
			throw new ControllerListenerException("La Cuenta Contable " + account.getId() + " no permite apuntes.");
		}
	}

	private void insertBankConceptAccount(BankConcept concept, Account account) throws ControllerListenerException {
		BankConceptAccount conceptAccount = new BankConceptAccount();
		conceptAccount.setBankConcept(concept);
		conceptAccount.setAccount(account);
		try {
			IManagerBean conceptAccBean = BeanManager.getManagerBean(BankConceptAccount.class);
			conceptAccBean.insert(conceptAccount);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private void updateBankConceptAccount(BankConcept concept, Account account) throws ControllerListenerException {
		try {
			IManagerBean conceptAccBean = BeanManager.getManagerBean(BankConceptAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(conceptAccBean.getFieldName(IEntityAlias.BANK_CONCEPT_ACCOUNT_BANK_CONCEPT_ID), concept.getId());
			Iterator<ITransferObject> iterator = conceptAccBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				BankConceptAccount conceptAccount = (BankConceptAccount)iterator.next();
				conceptAccount.setAccount(account);
				conceptAccBean.update(conceptAccount);
			} else {
				insertBankConceptAccount(concept, account);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		
	}

	private void removeBankConceptAccount(BankConcept concept) throws ControllerListenerException {
		try {
			IManagerBean conceptAccBean = BeanManager.getManagerBean(BankConceptAccount.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(conceptAccBean.getFieldName(IEntityAlias.BANK_CONCEPT_ACCOUNT_BANK_CONCEPT_ID), concept.getId());
			Iterator<ITransferObject> iterator = conceptAccBean.getList(criteria).iterator();
			while (iterator.hasNext()) {
				BankConceptAccount conceptAccount = (BankConceptAccount)iterator.next();
				conceptAccBean.remove(conceptAccount);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
}