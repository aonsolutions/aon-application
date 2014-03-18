package com.code.aon.purchase;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.PayMethod;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.purchase.enumeration.PurchaseDocumentType;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.PurchaseDB;

@Entity
@Table(name="purchase", uniqueConstraints = @UniqueConstraint(columnNames={"supplier", "series", "number"}))
public class Purchase extends PurchaseDB implements IHeaderObject, ICalculableContainer, IBankAccountContainer, IPayMethod, IConfidentialable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final String DELIM = " ";
	private static final Logger LOGGER = LoggerFactory.getLogger(Purchase.class.getName());
	
    private int[] paymentDaysArray;
	private Set<PurchaseDetail> lines = new HashSet<PurchaseDetail>();

	public Purchase() {
		setIssueDate( new Date());
	}

    public void setPaymentDays(String paymentDays) {
        super.setPaymentDays( paymentDays );
        StringTokenizer strTknzr = new StringTokenizer(getPaymentDays(),DELIM);
    	int[] values = new int[strTknzr.countTokens()];
    	for (int i = 0; i < values.length; i++){
    		values[i] = Integer.parseInt(strTknzr.nextToken());
    	}    	
        this.paymentDaysArray = values;
    }

	@OneToMany(mappedBy = "purchase", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<PurchaseDetail> getLines() {
		return this.lines;
	}
	public void setLines(Set<PurchaseDetail> lines) {
		this.lines = lines;
	}

    @Transient
    public String getReferenceCode() {
    	String referenceCode = StringUtils.leftPad(Integer.toString(getNumber()), 6, "0");
		if (!StringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }

    @Transient
    public int[] getPaymentDaysArray() {
    	return paymentDaysArray;
    }

	@Transient
	public Date getDate() {
		return getIssueDate();
	}
	@Transient
	public PayMethod getPayment() {
		return getPayMethod();
	}
	@Transient
	public boolean isItemReturn() {
		return getDocumentType()==PurchaseDocumentType.ITEM_RETURN;
	}
	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	
	@Transient
	public boolean isServed() {
		return getStatus() == PurchaseStatus.SERVED;
	}
	
	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	@SuppressWarnings("rawtypes")
	@Transient
	public List getDetailList() {
		try {
			IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(purchaseDetailBean.getFieldName(IEntityAlias.PURCHASE_DETAIL_PURCHASE_ID), getId());
			return purchaseDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining purchaseDetail list", e);
		}
		return null;
	}

}