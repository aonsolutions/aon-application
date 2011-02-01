package com.code.ui.gbp.event;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.gbp.Campaign;
import com.code.gbp.dao.IGBPAlias;

public class CampaignControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.CAMPAIGN_CODE), false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		checkDate(event);
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		checkDate(event);
	}
	
	private void checkDate(ControllerEvent event) throws ControllerListenerException{
		Campaign camp = (Campaign)event.getController().getTo();
		Date start = camp.getStartDate();
		Date end = camp.getEndDate();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.set(Calendar.YEAR, 1900);
		calendar.set(Calendar.MONTH, 0);
		calendar.set(Calendar.DATE, 1);
		if (start.before(calendar.getTime())){
			throw new ControllerListenerException("Start date can not be less than 1900");
		}
		if (end.before(calendar.getTime())){
			throw new ControllerListenerException("End date can not be less than 1900");
		}
	}
}
