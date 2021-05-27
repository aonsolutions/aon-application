package com.code.aon.finance;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IScopable;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryDocument;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.FinanceDB;

@Entity
@Table(name = "finance")
public class Finance extends FinanceDB implements IBankAccountContainer, IScopable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Finance.class.getName());

	private RegistryDocument registryFullDocument;
	private boolean skipCheckPosShift;

	private Set<FinanceBatchDetail> batchDetails = new HashSet<FinanceBatchDetail>();

	public Finance() {
		setDueDate(new Date());
		setSkipCheckPosShift(false);
	}
    
	public void setAmount(double amount) {
		super.setAmount(CommonUtil.round(amount));
	}
	
	public void setExpenses(double expenses) {
		super.setExpenses(CommonUtil.round(expenses));
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
	public boolean isSkipCheckPosShift() {
		return skipCheckPosShift;
	}
	public void setSkipCheckPosShift(boolean skipCheckPosShift) {
		this.skipCheckPosShift = skipCheckPosShift;
	}

	@OneToMany(mappedBy = "finance")
	public Set<FinanceBatchDetail> getBatchDetails() {
		return this.batchDetails;
	}
	public void setBatchDetails(Set<FinanceBatchDetail> batchDetails) {
		this.batchDetails = batchDetails;
	}

	@Transient
	public boolean isPending() {
		return FinanceStatus.PENDING == getFinanceStatus();
	}
	@Transient
	public boolean isBatched() {
		return FinanceStatus.BATCHED == getFinanceStatus();
	}
	@Transient
	public boolean isReturned() {
		return FinanceStatus.RETURNED == getFinanceStatus();
	}
	@Transient
	public boolean isPaid() {
		return FinanceStatus.PAID == getFinanceStatus();
	}
	@Transient
	public boolean isSettled() {
		return FinanceStatus.SETTLED == getFinanceStatus();
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
		if (getBankAccount() != null && !StringUtils.isBlank(getBankAccount().getBban())) {
			sb.append(getBankAccount().toString());
			sb.append(" ");
		}
		if (!StringUtils.isBlank(getBic())) {
			sb.append("[");
			sb.append(getBic());
			sb.append("] ");
		}
		if (!StringUtils.isBlank(getBankAlias())) {
			sb.append(getBankAlias());
		}
		return sb.toString(); 
	}

	@Transient
	public double getTotalAmount(){
		return CommonUtil.round(getAmount() + getExpenses());
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

}