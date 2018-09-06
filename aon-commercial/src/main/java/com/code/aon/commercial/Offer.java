package com.code.aon.commercial;

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
import com.code.aon.commercial.enumeration.OfferStatus;
import com.code.aon.commercial.enumeration.OfferType;
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
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.OfferDB;

@Entity
@Table(name="offer", uniqueConstraints = @UniqueConstraint(columnNames={"series", "number", "version"}))
public class Offer extends OfferDB implements IHeaderObject, ICalculableContainer, IBankAccountContainer, IPayMethod, IScopable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
    private static final String DELIM = " ";
	private static final Logger LOGGER = LoggerFactory.getLogger(Offer.class.getName());
	
    private int[] paymentDaysArray;
	private Set<OfferDetail> lines = new HashSet<OfferDetail>();
	private Set<OfferAttachment> attachments = new HashSet<OfferAttachment>();	
	private Set<OfferTerm> terms = new HashSet<OfferTerm>();	
	
	public Offer() {
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

	@OneToMany(mappedBy = "offer", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<OfferDetail> getLines() {
		return this.lines;
	}

	public void setLines(Set<OfferDetail> lines) {
		this.lines = lines;
	}
	
	@OneToMany(mappedBy = "offer", cascade={CascadeType.REMOVE})
	public Set<OfferAttachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(Set<OfferAttachment> attachments) {
		this.attachments = attachments;
	}

	@OneToMany(mappedBy = "offer", cascade={CascadeType.REMOVE})
	@OrderBy("line")
	public Set<OfferTerm> getTerms() {
		return terms;
	}

	public void setTerms(Set<OfferTerm> terms) {
		this.terms = terms;
	}

    @Transient
    public String getReferenceCode() {
    	String referenceCode = StringUtils.leftPad(Integer.toString(getNumber()), SeriesNumberUtil.getNumberMinimumLength(), "0");
    	referenceCode += "/" + getVersion();
		if (!StringUtils.isEmpty(getSeries())) {
			referenceCode = getSeries() + "/" + referenceCode;
		}
    	return referenceCode;
    }

	@Transient
	public Registry getRegistry() {
		return getTarget().getRegistry();
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
	public List<ITransferObject> getDetailList() {
		try {
			IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(offerDetailBean.getFieldName(IEntityAlias.OFFER_DETAIL_OFFER_ID), getId());
			return offerDetailBean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error("Error obtaining offerDetail list", e);
		}
		return null;
	}
	
	@Transient
	public boolean isNormal() {
		return (OfferType.NORMAL == getType());
	}
	@Transient
	public boolean isInternet() {
		return (OfferType.INTERNET == getType());
	}
	@Transient
	public boolean isProforma() {
		return (OfferType.PROFORMA == getType());
	}
	@Transient
	public boolean isDealership() {
		return (OfferType.DEALERSHIP == getType());
	}
	@Transient
	public boolean isOtherOffer() {
		return (OfferType.OTHER == getType());
	}

	@Transient
	public boolean isPending() {
		return (OfferStatus.PENDING == getStatus());
	}
	@Transient
	public boolean isApproved() {
		return (OfferStatus.APPROVED == getStatus());
	}
	@Transient
	public boolean isRefused() {
		return (OfferStatus.REFUSED == getStatus());
	}
	@Transient
	public boolean isBlocked() {
		return (OfferStatus.BLOCKED == getStatus());
	}
	@Transient
	public boolean isInvoiced() {
		return (OfferStatus.INVOICED == getStatus());
	}

}