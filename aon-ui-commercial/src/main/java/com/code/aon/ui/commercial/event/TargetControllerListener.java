package com.code.aon.ui.commercial.event;

import java.io.Serializable;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.commercial.Target;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.SurveyResponse;
import com.code.aon.tas.ProjectTas;
import com.code.aon.ui.commercial.controller.ICommercialConstants;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class TargetControllerListener extends ControllerAdapter implements ICommercialConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Target target = (Target)event.getController().getTo();
		Serializable id = target.getId();
		String message1 = AonUtil.getMessage(ICommonMessages.COMMERCIAL_TARGET_NO_REMOVE);
		try {
			if ( target.isCustomer() ) {
				String message2 = AonUtil.getMessage(ICommonMessages.COMMERCIAL_TARGET_IS_CUSTOMER);
				throw new ControllerListenerException(message1 + " " + message2);
			}
			if ( FormUtil.hasReferences(Offer.class, id, IEntityAlias.OFFER_TARGET_ID) ) {
				String message2 = AonUtil.getMessage(ICommonMessages.REFERENCE_OFFER);
				throw new ControllerListenerException(message1 + " " + message2);
			}
			if ( FormUtil.hasReferences(ProjectCommercial.class, id, IEntityAlias.PROJECT_COMMERCIAL_TARGET_ID) ) {
				String message2 = AonUtil.getMessage(ICommonMessages.REFERENCE_PROJECT_COMMERCIAL);
				throw new ControllerListenerException(message1 + " " + message2);
			}
			if ( FormUtil.hasReferences(ActionTarget.class, id, IEntityAlias.ACTION_TARGET_TARGET_ID) ) {
				String message2 = AonUtil.getMessage(ICommonMessages.REFERENCE_ACTION_TARGET);
				throw new ControllerListenerException(message1 + " " + message2);
			}
			if ( FormUtil.hasReferences(SurveyResponse.class, id, IEntityAlias.SURVEY_RESPONSE_TARGET_ID) ) {
				String message2 = AonUtil.getMessage(ICommonMessages.REFERENCE_SURVEY_RESPONSE);
				throw new ControllerListenerException(message1 + " " + message2);
			}
			if ( FormUtil.hasReferences(ProjectTas.class, id, IEntityAlias.PROJECT_TAS_TARGET_ID) ) {
				String message2 = AonUtil.getMessage(ICommonMessages.REFERENCE_PROJECT_TAS);
				throw new ControllerListenerException(message1 + " " + message2);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
}