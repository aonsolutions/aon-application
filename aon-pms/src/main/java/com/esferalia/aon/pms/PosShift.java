package com.esferalia.aon.pms;

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

}
