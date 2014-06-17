package com.esferalia.aon.ui.calendar.controller;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.Holiday;
import com.esferalia.aon.entity.IEntityAlias;

public class HolidayController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HolidayController.class.getName());
	
	private Integer year;
	private IControllerListener excludeCurrentHoliday;
	
	public void setExcludeCurrentHoliday(IControllerListener excludeCurrentHoliday) {
		this.excludeCurrentHoliday = excludeCurrentHoliday;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	public void onChangeYear(ActionEvent event){
		try {
			refreshLines();
		} catch (ManagerBeanException e) {
			String msg = "error on onChangeYear";
			LOGGER.error(msg);
			throw new AbortProcessingException(e);
		}
	}
	
	private void refreshLines() throws ManagerBeanException{
		IController detail = (IController) AonUtil.getRegisteredBean(ICalendarConstants.HOLIDAY_DETAIL_CONTROLLER_NAME);
		detail.initializeModel();
	}
	
	public IControllerListener getExcludeCurrentHoliday() {
		if ( this.excludeCurrentHoliday == null ) {
			this.excludeCurrentHoliday = new ExcludeCurrentHolidayFilter();
		}
		return this.excludeCurrentHoliday;
	}

	private static class ExcludeCurrentHolidayFilter extends ControllerAdapter {

		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			IController hc = FormUtil.getController(ICalendarConstants.HOLIDAY_CONTROLLER_NAME);
			Holiday holiday = (Holiday) hc.getTo();
			if ( holiday!=null && holiday.getId()!=null ) {
				try {
					String alias = controller.getFieldName(IEntityAlias.HOLIDAY_ID);
					controller.getCriteria().addNotEqualExpression(alias, holiday.getId());
				} catch (ManagerBeanException e) {
					LOGGER.error("Error filtering current holiday", e);
				}
			}
		}
		
	}
	
}
