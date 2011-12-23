package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.VatTaxDB;

@Entity
@Table(name="fs_vat")
public class VatTax extends VatTaxDB {
	
	private static final long serialVersionUID = 1L;

	private Boolean replaced;
	
    @Transient
    public boolean isAnual() {
    	return (getPeriod() == Period.YEAR);
    }
	@Transient
	public boolean isExtraDeclaration() {
		return (isComplementary() || isReplacement() );
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
	    		c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_YEAR), getYear());
	    		c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_PERIOD), getPeriod());
	    		c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_REPLACEMENT), true);
	    		if (isReplacement()) {
	    			c.addGreaterThanExpression(bean.getFieldName(IEntityAlias.VAT_TAX_NUMBER), getNumber());	
	    		}
	    		replaced = (bean.getCount(c) > 0);
	    	} catch (ManagerBeanException e) {
	    		replaced = false;
			}
    	}
    	return replaced;
    }

}