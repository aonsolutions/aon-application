package com.esferalia.aon.pms;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.finance.Finance;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.PosShiftDB;

@Entity
@Table(name="pos_shift")
public class PosShift extends PosShiftDB {

	private static final long serialVersionUID = 1L;
	
	private Set<PosShiftCount> posShiftCount = new HashSet<PosShiftCount>();
	
	@OneToMany(mappedBy = "posShift", cascade={CascadeType.ALL})
	public Set<PosShiftCount> getPosShiftCount() {
		return posShiftCount;
	}
	public void setPosShiftCount(Set<PosShiftCount> posShiftCount) {
		this.posShiftCount = posShiftCount;
	}
	
	@Transient
	public double getFinalAmount() throws ManagerBeanException {
		IManagerBean posShiftCountBean = BeanManager.getManagerBean(PosShiftCount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(posShiftCountBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), getId());
		Projection projection = Projection.sum(posShiftCountBean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_AMOUNT));
		Object value = posShiftCountBean.getUniqueResult(projection, criteria);
		return (value == null) ? 0 : ((Double)value).doubleValue();
	}
	
	@Transient
	public Double getTotalCashAmount() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PosShiftCount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), this.getId());
		criteria.addEqualExpression("posShiftCount.payMethod.type", PayMethodType.CASH_BASIS);
		Projection projection = Projection.sum(bean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_AMOUNT));
		Double cashAmount = ((Double) bean.getUniqueResult(projection, criteria));
		return cashAmount!=null?cashAmount-this.getInitialAmount():null;
	}
	
	@Transient
	public Double getTotalInvoiceAmount() throws ManagerBeanException {
		if( !this.getPos().isInvoiceable() ){
			IManagerBean bean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.FINANCE_INVOICE_CREATION_DATE),this.getStartTime(), this.getEndTime()!=null?this.getEndTime():new Date());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_INVOICE_CREATION_USER), this.getUser().getLogin());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.FINANCE_PAY_METHOD_TYPE), PayMethodType.CASH_BASIS);
			Projection projection = Projection.sum(bean.getFieldName(IEntityAlias.FINANCE_AMOUNT));
			return (Double) bean.getUniqueResult(projection, criteria);
		}
		return null;
	}

}
