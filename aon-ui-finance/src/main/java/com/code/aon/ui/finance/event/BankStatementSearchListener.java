package com.code.aon.ui.finance.event;

import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementReliability;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;

public class BankStatementSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String lotNumber;
	private Date fromDate;
	private Date toDate;
	private StatementConcept commonConcept;
	private Boolean payment;
	private String amount;
	private String description;
	private String comments;
	private StatementReliability[] statementReliabilities;
	private Boolean confidential;
	private StatementStatus[] statementStatuses;
	
	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
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

	public StatementConcept getCommonConcept() {
		return commonConcept;
	}

	public void setCommonConcept(StatementConcept commonConcept) {
		this.commonConcept = commonConcept;
	}

	public Boolean getPayment() {
		return payment;
	}

	public void setPayment(Boolean payment) {
		this.payment = payment;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public StatementReliability[] getStatementReliabilities() {
		return statementReliabilities;
	}

	public void setStatementReliabilities(StatementReliability[] statementReliabilities) {
		this.statementReliabilities = statementReliabilities;
	}
	
	public Boolean getConfidential() {
		return confidential;
	}

	public void setConfidential(Boolean confidential) {
		this.confidential = confidential;
	}

	public StatementStatus[] getStatementStatuses() {
		return statementStatuses;
	}

	public void setStatementStatuses(StatementStatus[] statementStatuses) {
		this.statementStatuses = statementStatuses;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		initData();

		StatementStatus[] defaultStatementStatus = {StatementStatus.PENDING, StatementStatus.CHECKED};
		setStatementStatuses(defaultStatementStatus);
	}

	public void initData() throws ManagerBeanException {
		setLotNumber(null);
		setFromDate(null);
		setToDate(null);
		setCommonConcept(null);
		setPayment(null);
		setAmount(null);
		setDescription(null);
		setComments(null);
		setStatementReliabilities(new StatementReliability[0]);
		setConfidential(null);
		setStatementStatuses(new StatementStatus[0]);
	}

	private boolean hasMetaCharacters(String value) {
		return StringUtils.contains(value, '*')
			|| StringUtils.contains(value, ':')
			|| StringUtils.contains(value, '_')
			|| StringUtils.contains(value, '<')
			|| StringUtils.contains(value, '>')
			|| StringUtils.contains(value, '=')
		;
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (StringUtils.isNotEmpty(getLotNumber())) {
			criteria.addExpression(getFieldName(IEntityAlias.BANK_STATEMENT_LOT_NUMBER), getLotNumber());			
		}
		if (getFromDate() != null) {
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_OPERATION_DATE), getFromDate());			
		}
		if (getToDate() != null) {
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_OPERATION_DATE), getToDate());			
		}
		if (getCommonConcept() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_COMMON_CONCEPT), getCommonConcept());			
		}
		if (getPayment() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_PAYMENT), getPayment());			
		}
		if (StringUtils.isNotEmpty(getAmount())) {
			criteria.addExpression(getFieldName(IEntityAlias.BANK_STATEMENT_AMOUNT), getAmount());			
		}
		if (StringUtils.isNotEmpty(getDescription())) {
			String field = getFieldName(IEntityAlias.BANK_STATEMENT_DESCRIPTION);
			if (hasMetaCharacters(getDescription())) {
				criteria.addExpression(field, getDescription());			
			} else {
				String con = "%" + getDescription() + "%";
				Expression expression = ExpressionUtilities.getLikeExpression(field, con);
				criteria.addExpression(expression);
			}
		}
		if (StringUtils.isNotEmpty(getComments())) {
			criteria.addExpression(getFieldName(IEntityAlias.BANK_STATEMENT_COMMENTS), getComments());			
		}
		if (!ArrayUtils.isEmpty(getStatementReliabilities())) {
			String reliability = getController().resolveAlias(IEntityAlias.BANK_STATEMENT_RELIABILITY);
			addEnumToCriteria(criteria, reliability, getStatementReliabilities());
		}
		if (getConfidential() != null) {
			SecurityLevel securityLevel = (getConfidential().booleanValue()) ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL;
			criteria.addEqualExpression(getFieldName(IEntityAlias.BANK_STATEMENT_SECURITY_LEVEL), securityLevel);			
		}
		if (!ArrayUtils.isEmpty(getStatementStatuses())) {
			String status = getController().resolveAlias(IEntityAlias.BANK_STATEMENT_STATUS);
			addEnumToCriteria(criteria, status, getStatementStatuses());
		}
		criteria.addOrder(getFieldName(IEntityAlias.BANK_STATEMENT_REGISTRY_BANK_ID));
		criteria.addOrder(getFieldName(IEntityAlias.BANK_STATEMENT_OPERATION_DATE));
	}

}