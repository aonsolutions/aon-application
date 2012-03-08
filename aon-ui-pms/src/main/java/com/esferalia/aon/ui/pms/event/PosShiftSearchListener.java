package com.esferalia.aon.ui.pms.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Pos;

public class PosShiftSearchListener extends ControllerSearchListener {

	private Hotel hotel;
	private Pos pos;
	
	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Pos getPos() {
		return pos;
	}

	public void setPos(Pos pos) {
		this.pos = pos;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setHotel(null);
		setPos(null);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_POS_WORK_PLACE_ID), getHotel().getWorkPlace().getId());			
		}
		if (getPos() != null && getPos().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_POS_ID), getPos().getId());			
		}
	}
	
	public List<SelectItem> getHotelPosList() throws ManagerBeanException {
		String sqlSelect = "SELECT Pos.*"
			+ " FROM pos as Pos"
			+ " LEFT JOIN pos_shift as PosShift on PosShift.pos = Pos.id"
			+ " WHERE " + DomainManager.getSQLWhereClause("Pos.domain")
			+ (getHotel() == null ? "" : " AND Pos.workplace = " + getHotel().getWorkPlace().getId()) 
			+ " GROUP BY Pos.name"
			+ " ORDER BY Pos.name"
			;
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query sqlQuery = session.createSQLQuery(sqlSelect);
		List<SelectItem> list = new LinkedList<SelectItem>();
		for(Object to: sqlQuery.list()){
			Pos pos = (Pos) BeanManager.getManagerBean(Pos.class).get((Integer)(((Object[])to)[0]));
			SelectItem item = new SelectItem(pos, pos.getName());
			list.add(item);
		}
		return list;
	}

}