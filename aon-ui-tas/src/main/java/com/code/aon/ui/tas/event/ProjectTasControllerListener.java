package com.code.aon.ui.tas.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Project;
import com.code.aon.tas.ProjectTas;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.tas.controller.ProjectTasController;

public class ProjectTasControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ProjectTasController controller = (ProjectTasController)event.getController();
		controller.initSeries();
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		ProjectTasController controller = (ProjectTasController)event.getController();
		ProjectTas projectTas = (ProjectTas)controller.getTo();
		try {
			IManagerBean projectBean = BeanManager.getManagerBean(Project.class);
			projectBean.remove(projectTas.getProject());
		} catch (ManagerBeanException e) {
			//controller.getManagerBean().restoreNullSubPOJOs(projectTas);
			//controller.getManagerBean().insert(projectTas);
			controller.setNew(true);
			controller.accept(null);

			throw new ControllerListenerException("No se puede borrar la Orden de Reparacion. Esta asociada a algún documento.");
		}
	}

}