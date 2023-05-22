package com.code.aon.warehouse;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

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
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.warehouse.enumeration.IncomeStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.IncomeDB;

@Entity
@Table(name="income", uniqueConstraints = @UniqueConstraint(columnNames={"supplier", "reference_code"}))
public class Income extends IncomeDB implements IHeaderObject, ICalculableContainer, IBankAccountContainer, IPayMethod, IScopable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
    private static final String DELIM = " ";
	private static final Logger LOGGER = LoggerFactory.getLogger(Income.class.getName());
    
    private int[] paymentDaysArray;
	private Set<IncomeDetail> lines = new HashSet<IncomeDetail>();

    public void setPaymentDays(String paymentDays) {
        super.setPaymentDays( paymentDays );
        StringTokenizer strTknzr = new StringTokenizer(getPaymentDays(),DELIM);
    	int[] values = new int[strTknzr.countTokens()];
    	for (int i = 0; i < values.length; i++){
    		values[i] = Integer.parseInt(strTknzr.nextToken());
    	}    	
        this.paymentDaysArray = values;
    }

	@OneToMany(mappedBy = "income", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<IncomeDetail> getLines() {
		return this.lines;
	}
	public void setLines(Set<IncomeDetail> lines) {
		this.lines = lines;
	}

	@Transient
	public Registry getRegistry() {
		return getSupplier().getRegistry();
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
    public int[] getPaymentDaysArray() {
    	return paymentDaysArray;
    }

	@Transient
	public boolean isInvoiced() {
		return getStatus() == IncomeStatus.INVOICED;
	}

	@Transient
	public List<ITransferObject> getDetailList() {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), getId());
			return incomeDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining incomeDetail list", e);
		}
		return null;
	}
	
	@Transient
	public List<ITransferObject> getOrderedDetailList() {
		try {
			IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_INCOME_ID), getId());
			criteria.addOrder(incomeDetailBean.getFieldName(IEntityAlias.INCOME_DETAIL_LINE));
			return incomeDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining incomeDetail orderedList", e);
		}
		return null;
	}
	
	@Transient
	public String getSeries() {
		if (StringUtils.contains(getReferenceCode(), "/")) {
			return StringUtils.substringBefore(getReferenceCode(), "/");
		}
		return null;
	}

	public void setSeries(String series) {

	}

	@Transient
	public int getNumber() {
		if (StringUtils.contains(getReferenceCode(), "/")) {
			String after = StringUtils.substringAfter(getReferenceCode(), "/");
			if (StringUtils.isNotEmpty(after)) {
				try {
					return Integer.parseInt(after);
				} catch (NumberFormatException e) {
					LOGGER.error("INCOME NO ES IHEADEROBJECT!", e);
				}
			}
		}
		return 0;
	}

	public void setNumber(int number) {
	}

}