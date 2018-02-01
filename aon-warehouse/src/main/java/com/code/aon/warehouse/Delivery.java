package com.code.aon.warehouse;

import java.text.SimpleDateFormat;
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
import org.apache.commons.lang.time.DateUtils;
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
import com.code.aon.customer.Customer;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.tas.ProjectTas;
import com.code.aon.warehouse.enumeration.DeliveryStatus;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.DeliveryDB;

@Entity
@Table(name="delivery", uniqueConstraints = @UniqueConstraint(columnNames={"series", "number"}))
public class Delivery extends DeliveryDB implements IHeaderObject, ICalculableContainer, IBankAccountContainer, IPayMethod, IScopable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
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
		return getIssueTime();
	}

    @Transient
	public Customer getInvoicingCustomer() {
		return getCustomer().getInvoicingCustomer();
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
		return getStatus() == DeliveryStatus.INVOICED;
	}

	@Transient
	public ITransferObject getSpecificProject() throws ManagerBeanException {
		if (getProject() != null && getProject().isTas()) {
			return (ProjectTas)BeanManager.getManagerBean(ProjectTas.class).get(getProject().getId());
		}
		return null;
	}

	@Transient
	public List<ITransferObject> getDetailList() {
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

	@Transient
	public String getStatusModificationHour(){
		if(this.getStatusModificationDate()!=null){
			return new SimpleDateFormat("HH").format(this.getStatusModificationDate());
		}
		return null;
	}
	public void setStatusModificationHour(String hour){
		if(this.getStatusModificationDate()!=null && StringUtils.isNotEmpty(hour)){
			this.setStatusModificationDate(DateUtils.setHours(this.getStatusModificationDate(), Integer.parseInt(hour)));
		}
	}

	@Transient
	public String getStatusModificationMinute(){
		if(this.getStatusModificationDate()!=null){
			return new SimpleDateFormat("mm").format(this.getStatusModificationDate());
		}
		return null;
	}
	public void setStatusModificationMinute(String minute){
		if(this.getStatusModificationDate()!=null && StringUtils.isNotEmpty(minute)){
			this.setStatusModificationDate(DateUtils.setMinutes(this.getStatusModificationDate(), Integer.parseInt(minute)));
		}
	}

}