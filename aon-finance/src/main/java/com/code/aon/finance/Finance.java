package com.code.aon.finance;


import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryDocument;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.FinanceDB;

@Entity
@Table(name = "finance")
public class Finance extends FinanceDB implements IBankAccountContainer, IScopable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(Finance.class.getName());

	private RegistryDocument registryFullDocument;

    public Finance() {
		setDueDate(new Date());
	}
    
	@Transient
	public List<ITransferObject> getGroupedList() {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IEntityAlias.FINANCE_FINANCE_GROUP_ID), getId());
			criteria.addOrder(financeBean.getFieldName(IEntityAlias.FINANCE_DUE_DATE));
	        return financeBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining grouped finance list", e);
		}
		return null;
	}

	@Transient
	public double getTotalAmount(){
		return getAmount() + getExpenses();
	}
	
	@Transient
	public RegistryDocument getRegistryFullDocument() {
		if (registryFullDocument == null) {
			registryFullDocument = new RegistryDocument();
		}
		registryFullDocument.setDocument(getRegistryDocument());
		registryFullDocument.setType(getRegistryDocumentType());
		registryFullDocument.setCountry(getRegistryDocumentCountry());
		return registryFullDocument;
	}
	@Transient
	public boolean isValidRegistryDocument() {
		return getRegistryFullDocument().isValid();
	}
	@Transient
	public boolean isRegistryDocumentValidable() {
		return getRegistryFullDocument().isValidable();
	}
	
	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	@Transient
	public boolean isEmptyInvoice() {
		return (getInvoice() == null || getInvoice().getId() == null);
	}

	@Transient
	public String getReferenceCode() {
		return (!isEmptyInvoice()) ? getInvoice().getReferenceCode() : null;
	}

	@Transient
	public String getDocumentNumber() {
		return (!isEmptyInvoice()) ? getInvoice().getDocumentNumber() : getConcept();
	}

	@Transient
	public boolean isNegotiableDocument(){
		return (getPayMethod() != null && getPayMethod().getType() == PayMethodType.NEGOTIABLE_DOCUMENT);
	}

	@Transient
	public String getBankDescription() {
		StringBuilder sb = new StringBuilder();
		if (getBank() != null && !StringUtils.isEmpty(getBank().getName()))  {
			sb.append(StringUtils.abbreviate(getBank().getName(), 30));
			sb.append(" ");
		}
		if (getBankAccount() != null) {
			sb.append("[");
			sb.append(getBankAccount().toString());
			sb.append("]");
		}
		return sb.toString(); 
	}

}