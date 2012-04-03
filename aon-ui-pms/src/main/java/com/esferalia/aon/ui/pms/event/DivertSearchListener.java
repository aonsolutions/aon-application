package com.esferalia.aon.ui.pms.event;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;

public class DivertSearchListener extends ControllerSearchListener {
	
	private ReservationDivertStatus[] reservationDivertStatuses;
	
	public ReservationDivertStatus[] getReservationDivertStatuses() {
		return reservationDivertStatuses;
	}
	public void setReservationDivertStatuses(
			ReservationDivertStatus[] reservationDivertStatuses) {
		this.reservationDivertStatuses = reservationDivertStatuses;
	}

	@Override
	protected void init() throws ManagerBeanException {
		ReservationDivertStatus[] defaultStatus = {ReservationDivertStatus.PENDING};
		setReservationDivertStatuses(defaultStatus);
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getReservationDivertStatuses())) {
			String status = getFieldName(IEntityAlias.PROJECT_RESERVATION_DIVERT_STATUS);
			addEnumToCriteria(criteria, status, getReservationDivertStatuses());
		}
		addRequestHotelsToCriteria();
	}

	private void addRequestHotelsToCriteria() throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria hotelCriteria = new Criteria();
		hotelCriteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), new Boolean(true));
		UserUtils.getInstance().addScopeFilterToCriteria(hotelCriteria, hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID));
		hotelCriteria.addOrder(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_DESCRIPTION));
		Expression expToAdd = null;
		for( ITransferObject to : hotelBean.getList(hotelCriteria) ) {
			Hotel hotel = (Hotel)to;
			if ( to != null ) {
				if ( expToAdd == null ) {
					expToAdd = ExpressionUtilities.getEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_DIVERT_REQUEST_HOTEL_ID), hotel.getId());				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_DIVERT_REQUEST_HOTEL_ID), hotel.getId());
					expToAdd = ExpressionUtilities.getOrExpression(expToAdd, exp);
				}
			}
		}
		if ( expToAdd != null ) {
			getController().getCriteria().addExpression(expToAdd);
		}
	}

}