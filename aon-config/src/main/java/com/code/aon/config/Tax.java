package com.code.aon.config;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.TaxDB;

@Entity
@Table(name="tax")
@Heritable
public class Tax extends TaxDB implements IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Transient
	public boolean isVat() {
		return (getType() == TaxType.VAT);
	}

	@Transient
	public boolean isRetention() {
		return (getType() == TaxType.RETENTION);
	}

    @Transient
    public double getDatedPercentage(Date date) throws ManagerBeanException {
		if (date.before(getStartDate())) {
			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
	    	Criteria criteria = new Criteria();
	    	criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID), getId());
	    	criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_START_DATE), date);
	    	criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), date);
	    	Projection prjValue = Projection.property(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_VALUE));
			List<?> resultList = taxDetailBean.getList(new ProjectionList(prjValue), criteria);
			if (resultList.size() > 0) {
   				return (Double)resultList.get(0);
	    	}
		}
    	return getPercentage();
    }

}
