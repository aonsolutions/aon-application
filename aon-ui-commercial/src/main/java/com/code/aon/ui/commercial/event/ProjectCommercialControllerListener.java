package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.ProjectCommercial;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.project.Project;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class ProjectCommercialControllerListener extends ControllerAdapter {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		ProjectCommercial pc = (ProjectCommercial) event.getController().getTo();
		try {
			IManagerBean bean = BeanManager.getManagerBean(Project.class);
			bean.remove( pc.getProject() );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}