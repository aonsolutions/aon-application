package com.code.aon.ui.document.event;


import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.project.Project;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.document.controller.IEnterpriseController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

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
// [EUKE]
//		project.setEnterprise(enterpriseController.getEnterprise());
		project.setDomain(enterpriseController.getEnterprise().getDomain());
// fin		
	}

	@Override
	public void afterEditSearch(ControllerEvent event) throws ControllerListenerException {
		if ( addInSearh ) {
			Enterprise enterprise = enterpriseController.getEnterprise(); 
			if ((enterprise != null) && (enterprise.getId() != null)) {
				try {
					Criteria criteria = event.getController().getCriteria();
// [EUKE]
//					String enterpriseId = event.getController().getFieldName(IEntityAlias.PROJECT_ENTERPRISE_ID);
//					criteria.addEqualExpression(enterpriseId, enterprise.getId());						
					String enterpriseId = event.getController().getFieldName(IEntityAlias.PROJECT_DOMAIN_ID);
					criteria.addEqualExpression(enterpriseId, enterprise.getDomain().getId());						
					// fin		
				} catch (ManagerBeanException e) {
					throw new ControllerListenerException(e.getMessage(), e);
				}
			}			
		}
	}
	
}