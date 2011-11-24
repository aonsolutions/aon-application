package com.code.aon.ui.document.event;

import static com.code.aon.project.dao.IProjectAlias.PROJECT_ENTERPRISE_ID;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.document.controller.IEnterpriseController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the EnterpriseController
 * 
 */
public class EnterpriseProjectListener extends ControllerAdapter {

	private IEnterpriseController enterpriseController;
	private boolean addInSearh;
	
	public EnterpriseProjectListener(IEnterpriseController enterpriseController, boolean addInSearch) {
		this.enterpriseController = enterpriseController;
		this.addInSearh = addInSearch;
	}

	public EnterpriseProjectListener(IEnterpriseController enterpriseController) {
		this( enterpriseController, true );
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Project project = (Project) event.getController().getTo();
		project.setEnterprise(enterpriseController.getEnterprise());
	}

	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		if ( addInSearh ) {
			Enterprise enterprise = enterpriseController.getEnterprise(); 
			if ((enterprise != null) && (enterprise.getId() != null)) {
				try {
					Criteria criteria = event.getController().getCriteria();
					String enterpriseId = event.getController().getFieldName(PROJECT_ENTERPRISE_ID);
					criteria.addEqualExpression(enterpriseId, enterprise.getId());						
				} catch (ManagerBeanException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				}
			}			
		}
	}
	
}