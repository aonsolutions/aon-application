package com.code.aon.fiscal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.fiscal.dao.IFiscalAlias;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.ql.Criteria;

@Entity
@Table(name = "fs_vat")
public class VatTax implements ITransferObject, IConfidentialable {
	
	private static final long serialVersionUID = 5692053383866684819L;

    private Integer id;
	private Integer year;
	private Period period;
	private String comments;
	private VatTaxStatus status;
	private SecurityLevel securityLevel;
	private boolean complementary;
	private boolean replacement;
	private boolean taxRefundRegistry;
	private Integer number;
	
	private Boolean replaced;
	
    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(nullable = false)
    public Integer getYear() {
        return year;
    }
    public void setYear(Integer year) {
        this.year = year;
    }
    @Transient
    public boolean isAnual() {
    	return (getPeriod() == Period.YEAR);
    }
    
    @Column(name = "period")
    public Period getPeriod() {
        return period;
    }
    public void setPeriod(Period period) {
        this.period = period;
    }

    @Column(name="comments")
	@Lob
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Column(name="status")
    public VatTaxStatus getStatus() {
        return status;
    }
    public void setStatus(VatTaxStatus status) {
        this.status = status;
    }

	@Column(name="security_level")
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}

	@Transient
	public boolean isConfidential() {
		return SecurityLevel.CONFIDENTIAL == getSecurityLevel();
	}
	@Transient
	public void setConfidential(boolean confidential) {
		setSecurityLevel(confidential ? SecurityLevel.CONFIDENTIAL : SecurityLevel.OFFICIAL);
	}

	@Column(name="complementary")
	public boolean isComplementary() {
		return complementary;
	}
	public void setComplementary(boolean complementary) {
		this.complementary = complementary;
	}

	@Column(name="replacement")
	public boolean isReplacement() {
		return replacement;
	}
	public void setReplacement(boolean replacement) {
		this.replacement = replacement;
	}
	
	@Transient
	public boolean isExtraDeclaration() {
		return (isComplementary() || isReplacement() );
	}

	@Column(name="tax_refund_registry")
	public boolean isTaxRefundRegistry() {
		return taxRefundRegistry;
	}
	public void setTaxRefundRegistry(boolean taxRefundRegistry) {
		this.taxRefundRegistry = taxRefundRegistry;
	}
	
    @Column(name = "number")
    public Integer getNumber() {
        return number;
    }
    public void setNumber(Integer number) {
        this.number = number;
    }

	@Transient
	public boolean isFinished() {
		return getStatus() == VatTaxStatus.FINISHED;
	}
	
    @Transient
    public boolean isReplaced() {
    	if (replaced == null) {
	    	try {
	    		IManagerBean bean = BeanManager.getManagerBean(VatTax.class);
	    		Criteria c = new Criteria();
	    		c.addEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_YEAR), getYear());
	    		c.addEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_PERIOD), getPeriod());
	    		c.addEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_REPLACEMENT), true);
	    		if (isReplacement()) {
	    			c.addGreaterThanExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_NUMBER), getNumber());	
	    		}
	    		replaced = (bean.getCount(c) > 0);
	    	} catch (ManagerBeanException e) {
	    		replaced = false;
			}
    	}
    	return replaced;
    }

    @Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final VatTax o = (VatTax) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.year, o.year)			
				.append(this.period, o.period)
				.append(this.comments, o.comments)			
				.append(this.status, o.status)
				.append(this.securityLevel, o.securityLevel)
				.append(this.complementary, o.complementary)
				.append(this.replacement, o.replacement)
				.append(this.taxRefundRegistry, o.taxRefundRegistry)
				.append(this.number, o.number)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.year)			
			.append(this.period)
			.append(this.comments)			
			.append(this.status)
			.append(this.securityLevel)
			.append(this.complementary)
			.append(this.replacement)
			.append(this.taxRefundRegistry)
			.append(this.number)				
			.toHashCode();
	}	

	@Override
	public String toString() {
		return PojoToStringBuilder.reflectionToString(this);
	}

}