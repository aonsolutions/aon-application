package com.code.aon.ui.cvitae.event;

import java.util.Iterator;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.cvitae.Curriculum;
import com.code.aon.cvitae.Evaluate;
import com.code.aon.cvitae.EvaluateSummary;
import com.code.aon.ui.cvitae.controller.CurriculumController;
import com.code.aon.ui.cvitae.controller.EvaluateController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class EvaluateControllerListener extends ControllerAdapter {

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CurriculumController curriculumController = 
			(CurriculumController)AonUtil.getController( CurriculumController.MANAGER_BEAN_NAME );
		Curriculum cv = (Curriculum)curriculumController.getTo();
		((EvaluateSummary)event.getController().getTo()).setCurriculum( cv );
	}
	
	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanAdded(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		EvaluateController ec = (EvaluateController) event.getController();
		try {
			saveTypes( ec );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		EvaluateController ec = (EvaluateController) event.getController();
		try {
			IManagerBean bean = BeanManager.getManagerBean( Evaluate.class );
			List types = (List) ec.getTypes().getWrappedData();
			for (Iterator iter = types.iterator(); iter.hasNext();) {
				Evaluate e = (Evaluate) iter.next();
				bean.remove( e );
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
		ec.onReset(null);
	}

	/* (non-Javadoc)
	 * @see com.code.aon.ui.form.event.ControllerAdapter#afterBeanUpdated(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		EvaluateController ec = (EvaluateController) event.getController();
		try {
			saveTypes( ec );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	private void saveTypes(EvaluateController ec) throws ManagerBeanException {
		Integer score = 0;
		List types = (List) ec.getTypes().getWrappedData();
		IManagerBean bean = BeanManager.getManagerBean( Evaluate.class );
		for (Iterator iter = types.iterator(); iter.hasNext();) {
			Evaluate e = (Evaluate) iter.next();
			if ( e.getId() == null )
				bean.insert(e);
			else 
				bean.update(e);
			score += e.getValue().ordinal();			
		}
		ec.setScore(score);
	}

}
