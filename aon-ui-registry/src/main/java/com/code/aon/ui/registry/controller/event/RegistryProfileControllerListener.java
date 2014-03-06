package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.QuestionValue;
import com.code.aon.registry.RegistryProfile;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RegistryProfileController;

public class RegistryProfileControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryProfileController tpc = (RegistryProfileController) event.getController();
		tpc.setQuestionValueId(null);
		tpc.resetQuestionValues();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		RegistryProfileController tpc = (RegistryProfileController) event.getController();
		RegistryProfile tp = (RegistryProfile) tpc.getTo();
		tpc.refreshQuestionValues(tp.getQuestion());
		tpc.setQuestionValueId(tpc.getQuestionValueId(tp));
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		updateValue(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		updateValue(event);		
	}
	
	private void updateValue( ControllerEvent event ) throws ControllerListenerException {
		RegistryProfileController tpc = (RegistryProfileController) event.getController();
		if ( tpc.getQuestionValueId() != null ) {
			RegistryProfile tp = (RegistryProfile) tpc.getTo();
			try {
				QuestionValue qv = (QuestionValue) BeanManager.getManagerBean(QuestionValue.class).get(tpc.getQuestionValueId());
				if ( qv != null ) {
					qv.copyValues(tp);
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e.getMessage(),e );
			}			
		}
	}
	
}