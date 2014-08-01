package com.code.aon.ui.registry.controller;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.registry.Question;
import com.code.aon.registry.enumeration.QuestionType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * Controller used in the offer maintenance.
 */
public class QuestionController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private IControllerListener notInfoListener;
	
	public Question getQuestion() {
		return (Question) getTo();
	}
	
	public boolean isBoolean() {
		return getQuestion().getType() == QuestionType.BOOLEAN;
	}

	public boolean isDate() {
		return getQuestion().getType() == QuestionType.DATE;
	}

	public boolean isText() {
		return getQuestion().getType() == QuestionType.TEXT;
	}

	public boolean isNumber() {
		return getQuestion().getType() == QuestionType.NUMBER;
	}

	public IControllerListener getNotInfoListener() {
		if ( notInfoListener == null ) {
			notInfoListener = new NotInfoFilter();
		}
		return notInfoListener;
	}
	
	private static class NotInfoFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			try {
				String alias = controller.getFieldName(IEntityAlias.QUESTION_TYPE);
				controller.getCriteria().addNotEqualExpression(alias, QuestionType.INFO);
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e);
			} 
		}		
		
	}
	
}