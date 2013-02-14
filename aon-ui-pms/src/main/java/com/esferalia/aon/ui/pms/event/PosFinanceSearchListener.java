package com.esferalia.aon.ui.pms.event;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.finance.PosShift;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.Shift;

public class PosFinanceSearchListener extends ControllerSearchListener {

	private Hotel hotel;
	private Date startDate;
	private Date endDate;
	private PayMethod payMethod;
	private Shift shift;

	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = DateUtils. addMilliseconds(endDate, 24*60*60*1000 - 1000);
	}

	public PayMethod getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(PayMethod payMethod) {
		this.payMethod = payMethod;
	}

	public Shift getShift() {
		return shift;
	}
	public void setShift(Shift shift) {
		this.shift = shift;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setStartDate(DateUtils.truncate(new Date(), Calendar.DATE));
		setEndDate(DateUtils.truncate(new Date(), Calendar.DATE));
		setPayMethod((PayMethod)BeanManager.getManagerBean(PayMethod.class).createNewTo());
		setShift(null);
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_PAYMENT), false);
		criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
		criteria.addBetweenExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_CREATION_DATE), getStartDate(), getEndDate());
		if (getPayMethod() != null && getPayMethod().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.FINANCE_PAY_METHOD_ID), getPayMethod().getId());
		}
		List<String> userList = getUserList();
		if (userList.size() > 0) {
			criteria.addInExpression(getFieldName(IEntityAlias.FINANCE_INVOICE_CREATION_USER), userList);
		}
		criteria.addEqualExpression("Finance.invoice<lines.workPlace.id", getHotel().getWorkPlace().getId());
	}

	private List<String> getUserList() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_POS_WORK_PLACE_ID), getHotel().getWorkPlace().getId());
		criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_START_TIME), getStartDate(), getEndDate());
		criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME), getStartDate(), getEndDate());
		if (getShift() != null) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_SHIFT), getShift());
		}

		List<String> list = new LinkedList<String>();
		for (ITransferObject to: bean.getList(criteria)) {
			PosShift posShift = (PosShift)to;
			list.add(posShift.getUser().getLogin());
		}
		return list;
	}

}