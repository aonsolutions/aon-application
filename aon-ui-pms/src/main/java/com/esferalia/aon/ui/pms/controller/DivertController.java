package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationDivert;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class DivertController extends BasicController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DivertController.class);
	
	private boolean showDivertWindow;
	private IControllerListener divertFilter;
	
	public boolean isShowDivertWindow() {
		return showDivertWindow;
	}
	
	public void setShowDivertWindow(boolean showDivertWindow) {
		this.showDivertWindow = showDivertWindow;
	}
	
	public IControllerListener getDivertedReservationFilter() {
		if ( this.divertFilter == null ) {
			this.divertFilter = new DivertFilter();
		}
		return this.divertFilter;
	}
	
	public List<SelectItem> getAvailableHotels() throws ManagerBeanException{
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ACTIVE), true);
		criteria.addNotEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_SCOPE_ID), getWorkPlaceScopeToExclude());
		criteria.addOrder(hotelBean.getFieldName(IEntityAlias.HOTEL_WORK_PLACE_DESCRIPTION));
		for (ITransferObject ito : hotelBean.getList(criteria)) {
			Hotel hotel = (Hotel)ito;
			SelectItem item = new SelectItem(hotel, hotel.getWorkPlace().getDescription());
			list.add(item);
		}
		return list;
	}
	
	private Integer getWorkPlaceScopeToExclude() {
		// Id del ambito de hoteles externos
		return 107;
	}

	private static class DivertFilter extends ControllerAdapter {

		@Override
		public void beforeModelSearched(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {					
				for(Integer i: getPendingReservationDiverts()){
					controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_ID), i);
				}
				controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.BLOCKED);
				controller.getCriteria().addNotEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.CANCELLED);
				if(! AonUtil.getRoleManager().isSaleOperator()){
					controller.getCriteria().addGreaterThanOrEqualExpression(controller.getFieldName(IEntityAlias.PROJECT_RESERVATION_END_DATE), new Date());
				}
			} catch (ManagerBeanException e) {
				LOGGER.error("Error filtering reservation", e);
			}
		}
		private List<Integer> getPendingReservationDiverts() throws ManagerBeanException {
			List<Integer> list = new LinkedList<Integer>();
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservationDivert.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_DIVERT_STATUS), ReservationDivertStatus.PENDING);
			for(ITransferObject to: bean.getList(criteria)){
				ProjectReservationDivert d = (ProjectReservationDivert) to;
				list.add(d.getProjectReservation().getId());
			}
			return list;
		}
		
	}
	
}