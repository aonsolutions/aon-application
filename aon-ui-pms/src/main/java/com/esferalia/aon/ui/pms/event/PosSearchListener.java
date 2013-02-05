package com.esferalia.aon.ui.pms.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.PmsCollectionsController;

public class PosSearchListener extends ControllerSearchListener {

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
			criteria.addEqualExpression(getFieldName(IEntityAlias.POS_WORK_PLACE_ID), getHotel().getWorkPlace().getId());			
		} else {
			PmsCollectionsController collections = (PmsCollectionsController) AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			if (collections.getCurrentUserHotelsCount() > 0) {
				criteria.addInExpression(getFieldName(IEntityAlias.POS_WORK_PLACE_ID), collections.getCurrentUserHotelIds());
			}
		}
	}

}