package com.code.aon.ui.groupware.event;

import java.util.Date;

import com.code.aon.common.AonVersion;
import com.code.aon.groupware.Campaign;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CampaignControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
    public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
        beforeBeanSaved(event);
    }

    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        beforeBeanSaved(event);
    }

    private void beforeBeanSaved(ControllerEvent event) throws ControllerListenerException {
        Date startDate = ((Campaign)event.getController().getTo()).getStartDate();
        Date endDate = ((Campaign)event.getController().getTo()).getEndDate();
        if (endDate.compareTo(startDate) < 0) {
            throw new ControllerListenerException("Fecha Inicio no puede ser posterior a Fecha Fin.");
        }
    }

}