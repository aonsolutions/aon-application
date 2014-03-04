package com.esferalia.aon.ui.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;

public class StopSalesSearchListener extends ControllerSearchListener {

	private Hotel hotel;

	public Hotel getHotel() {
		return hotel;
	}

	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}

	@Override
	protected void init() throws ManagerBeanException {
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.STOP_SALES_HOTEL_ID), getHotel().getId());			
		}
	}

}