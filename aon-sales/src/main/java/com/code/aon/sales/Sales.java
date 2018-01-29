package com.code.aon.sales;

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
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.config.IBankAccountContainer;
import com.code.aon.config.IPayMethod;
import com.code.aon.config.IScopable;
import com.code.aon.config.PayMethod;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.sales.enumeration.DocumentType;
import com.code.aon.sales.enumeration.SalesStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.SalesDB;

@Entity
@Table(name="sales", uniqueConstraints = @UniqueConstraint(columnNames={"series", "number"}))
public class Sales extends SalesDB implements IHeaderObject, ICalculableContainer, IBankAccountContainer, IPayMethod, IScopable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	private static final String DELIM = " ";
	private static final Logger LOGGER = LoggerFactory.getLogger(Sales.class);
	
    private int[] paymentDaysArray;
	private Set<SalesDetail> lines = new HashSet<SalesDetail>();
	
	public Sales() {
		setIssueDate( new Date() );
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

	@OneToMany(mappedBy = "sales", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<SalesDetail> getLines() {
		return this.lines;
	}
	public void setLines(Set<SalesDetail> lines) {
		this.lines = lines;
	}

    @Transient
    public String getReferenceCode() {
    	String referenceCode = StringUtils.leftPad(Integer.toString(getNumber()), SeriesNumberUtil.getNumberMinimumLength(), "0");
    	if (!StringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }

	@Transient
	public Registry getRegistry() {
		return getCustomer().getRegistry();
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
    public int[] getPaymentDaysArray() {
    	return paymentDaysArray;
    }

	@Transient
	public boolean isItemReturn() {
		return getDocumentType()==DocumentType.ITEM_RETURN;
	}
	
	@Transient
	public boolean isPending() {
		return (SalesStatus.PENDING == getStatus());
	}
	@Transient
	public boolean isServed() {
		return (SalesStatus.SERVED == getStatus());
	}
	@Transient
	public boolean isClosed() {
		return (SalesStatus.CLOSED == getStatus());
	}
	@Transient
	public boolean isBlocked() {
		return (SalesStatus.BLOCKED == getStatus());
	}
	@Transient
	public boolean isInvoiced() {
		return (SalesStatus.INVOICED == getStatus());
	}

	@Transient
	public List<ITransferObject> getDetailList() {
		try {
			IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_SALES_ID), getId());
			return salesDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining salesDetail list", e);
		}
		return null;
	}
	
}