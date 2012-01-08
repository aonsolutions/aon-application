package com.code.aon.warehouse;

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

import com.code.aon.common.BeanManager;
import com.code.aon.common.IHeaderObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.PayMethod;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.tas.ProjectTas;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.DeliveryDB;

@Entity
@Table(name="delivery", uniqueConstraints = @UniqueConstraint(columnNames={"series", "number"}))
public class Delivery extends DeliveryDB implements IHeaderObject, ICalculableContainer, IBankAccountContainer, IPayMethod {
	
	private static final long serialVersionUID = 5865460388758611455L;
	private static final String DELIM = " ";
	private static final Logger LOGGER = LoggerFactory.getLogger(Delivery.class.getName());

    private int[] paymentDaysArray;
	private Set<DeliveryDetail> lines = new HashSet<DeliveryDetail>();

	public Delivery() {
		setIssueTime( new Date());
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

	@OneToMany(mappedBy = "delivery", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<DeliveryDetail> getLines() {
		return this.lines;
	}
	public void setLines(Set<DeliveryDetail> lines) {
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
		return getIssueTime();
	}

	@Transient
	public DiscountExpression getDiscountExpression() {
		return new DiscountExpression("0.0");
	}

	@Transient
	public PayMethod getPayment() {
		return getPayMethod();
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
	public ITransferObject getSpecificProject() throws ManagerBeanException {
		if (getProject() != null && getProject().isTas()) {
			return (ProjectTas)BeanManager.getManagerBean(ProjectTas.class).get(getProject().getId());
		}
		return null;
	}

	@Transient
	public List<?> getDetailList() {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), getId());
			return deliveryDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining deliveryDetail list", e);
		}
		return null;
	}
	
	@Transient
	public List<?> getOrderedDetailList() {
		try {
			IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_DELIVERY_ID), getId());
			criteria.addOrder(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_LINE));
			return deliveryDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining deliveryDetail orderedList", e);
		}
		return null;
	}
}