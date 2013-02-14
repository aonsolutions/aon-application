package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.QuestionValue;
import com.code.aon.commercial.TargetProfile;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.commercial.controller.TargetProfileController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class TargetProfileControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		TargetProfileController tpc = (TargetProfileController) event.getController();
		tpc.setQuestionValueId(null);
		tpc.resetQuestionValues();
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		TargetProfileController tpc = (TargetProfileController) event.getController();
		TargetProfile tp = (TargetProfile) tpc.getTo();
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
		TargetProfileController tpc = (TargetProfileController) event.getController();
		if ( tpc.getQuestionValueId() != null ) {
			TargetProfile tp = (TargetProfile) tpc.getTo();
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