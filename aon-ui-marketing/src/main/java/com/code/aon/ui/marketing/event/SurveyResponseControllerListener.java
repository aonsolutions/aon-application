package com.code.aon.ui.marketing.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.ActionTarget;
import com.code.aon.marketing.SurveyResponse;
import com.code.aon.marketing.dao.IMarketingAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class SurveyResponseControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		SurveyResponse response = (SurveyResponse) event.getController().getTo();
		try {
			Criteria criteria = new Criteria();
			IManagerBean bean = BeanManager.getManagerBean(ActionTarget.class);
			String id = bean.getFieldName(IMarketingAlias.ACTION_TARGET_SURVEY_RESPONSE_ID);
			criteria.addEqualExpression( id, response.getId() );
			List<ITransferObject> list = bean.getList(criteria);
			for( ITransferObject to : list ) {
				ActionTarget at = (ActionTarget) to;
				at.setSurveyResponse(null);
				bean.update(at);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

}
