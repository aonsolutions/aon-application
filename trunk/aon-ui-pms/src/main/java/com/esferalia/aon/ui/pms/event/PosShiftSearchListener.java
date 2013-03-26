package com.esferalia.aon.ui.pms.event;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.finance.Pos;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;

public class PosShiftSearchListener extends ControllerSearchListener {

	private Hotel hotel;
	private Pos pos;
	private Date startTimeFrom;
	private Date startTimeTo;
	private Date endTimeFrom;
	private Date endTimeTo;
	
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
	
	public Date getStartTimeFrom() {
		return startTimeFrom;
	}

	public void setStartTimeFrom(Date startTimeFrom) {
		this.startTimeFrom = startTimeFrom;
	}

	public Date getStartTimeTo() {
		return startTimeTo;
	}

	public void setStartTimeTo(Date startTimeTo) {
		this.startTimeTo = startTimeTo;
	}

	public Date getEndTimeFrom() {
		return endTimeFrom;
	}

	public void setEndTimeFrom(Date endTimeFrom) {
		this.endTimeFrom = endTimeFrom;
	}

	public Date getEndTimeTo() {
		return endTimeTo;
	}

	public void setEndTimeTo(Date endTimeTo) {
		this.endTimeTo = endTimeTo;
	}
	
	private boolean skipBeforeYesterdayTimeLimit;
	
	public boolean isSkipBeforeYesterdayTimeLimit() {
		return skipBeforeYesterdayTimeLimit;
	}

	public void setSkipBeforeYesterdayTimeLimit(boolean skipBeforeYesterdayTimeLimit) {
		this.skipBeforeYesterdayTimeLimit = skipBeforeYesterdayTimeLimit;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setHotel(null);
		setPos(null);
		setStartTimeFrom(null);
		setStartTimeTo(null);
		setEndTimeFrom(null);
		setEndTimeTo(null);
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_POS_WORK_PLACE_ID), getHotel().getWorkPlace().getId());			
		}
		if (getPos() != null && getPos().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_POS_ID), getPos().getId());			
		}
		checkDates();
		if(getStartTimeFrom()!=null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_START_TIME), getStartTimeFrom());
		}
		if(getEndTimeFrom()!=null){
			criteria.addGreaterThanOrEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_END_TIME), getEndTimeFrom());
		}
		if(getStartTimeTo()!=null){
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_START_TIME), getStartTimeTo());
		}
		if(getEndTimeTo()!=null){
			criteria.addLessThanOrEqualExpression(getFieldName(IEntityAlias.POS_SHIFT_END_TIME), getEndTimeTo());
		}
	}
	
	private void checkDates() {
		if(AonUtil.getRoleManager().isConfig() && !AonUtil.getRoleManager().isAdmin() && !AonUtil.getRoleManager().isSaleOperator()){
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			if(getStartTimeFrom()!=null && getStartTimeFrom().after(DateUtils.addDays(cal.getTime(), -1))){
				setStartTimeFrom(DateUtils.addDays(cal.getTime(), -1));
			}
			if(getEndTimeFrom()!=null && getEndTimeFrom().after(DateUtils.addDays(cal.getTime(), -1))){
				setEndTimeFrom(DateUtils.addDays(cal.getTime(), -1));
			}
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			if(getStartTimeTo()!=null && getStartTimeTo().after(DateUtils.addDays(cal.getTime(), -1))){
				setStartTimeTo(DateUtils.addDays(cal.getTime(), -1));
			}
			if(getEndTimeTo()!=null && getEndTimeTo().after(DateUtils.addDays(cal.getTime(), -1))){
				setEndTimeTo(DateUtils.addDays(cal.getTime(), -1));
			}
		}
	}

	public List<SelectItem> getHotelPosList() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		if(getHotel()!=null && getHotel().getId()!=null){
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
			for(Object to: sqlQuery.list()){
				Pos pos = (Pos) BeanManager.getManagerBean(Pos.class).get((Integer)(((Object[])to)[0]));
				SelectItem item = new SelectItem(pos, pos.getName());
				item.setEscape(false);
				list.add(item);
			}
		}
		return list;
	}

}