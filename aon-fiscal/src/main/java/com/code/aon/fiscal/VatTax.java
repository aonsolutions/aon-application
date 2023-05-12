package com.code.aon.fiscal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.VatTaxStatus;
import com.esferalia.aon.entity.master.VatTaxDB;

@Entity
@Table(name="fs_vat")
public class VatTax extends VatTaxDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

//	private Boolean replaced;
	
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
    	return false;
//    	if (replaced == null) {
//	    	try {
//	    		IManagerBean bean = BeanManager.getManagerBean(VatTax.class);
//	    		Criteria c = new Criteria();
//	    		c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_YEAR), getYear());
//	    		c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_PERIOD), getPeriod());
//	    		c.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_REPLACEMENT), true);
//	    		if (isReplacement()) {
//	    			c.addGreaterThanExpression(bean.getFieldName(IEntityAlias.VAT_TAX_NUMBER), getNumber());	
//	    		}
//	    		replaced = (bean.getCount(c) > 0);
//	    	} catch (ManagerBeanException e) {
//	    		replaced = false;
//			}
//    	}
//    	return replaced;
    }

    @Transient
    public boolean isProrataEnabled() {
		return (CommonUtil.round(getProrata()) != 100);
	}
}
