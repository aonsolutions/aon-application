package com.esferalia.aon.pms;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.esferalia.aon.entity.master.PosShiftDB;

@Entity
@Table(name="pos_shift")
public class PosShift extends PosShiftDB {

	private static final long serialVersionUID = 1L;
	
	private Set<PosShiftCount> posShiftCount = new HashSet<PosShiftCount>();
	private Double finalAmount;
	
	public void setFinalAmount(Double finalAmount) {
		this.finalAmount = finalAmount;
	}
	
	@OneToMany(mappedBy = "posShift", cascade={CascadeType.ALL})
	public Set<PosShiftCount> getPosShiftCount() {
		return posShiftCount;
	}
	public void setPosShiftCount(Set<PosShiftCount> posShiftCount) {
		this.posShiftCount = posShiftCount;
	}
	
	@Transient
	public Double getFinalAmount(){
		String sqlSelect = "SELECT sum(PosShiftCount.amount)"
				+ " FROM pos_shift_count as PosShiftCount"
				+ " WHERE  PosShiftCount.pos_shift = " + getId();
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query sqlQuery = session.createSQLQuery(sqlSelect);
		finalAmount = (Double)sqlQuery.list().get(0);
		return finalAmount;
	}

}
