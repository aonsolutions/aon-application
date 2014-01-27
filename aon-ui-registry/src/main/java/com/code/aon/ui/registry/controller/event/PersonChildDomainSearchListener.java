package com.code.aon.ui.registry.controller.event;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.Domain;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;


public class PersonChildDomainSearchListener extends RegistrySearchListener {

	@Override
	public void beforeModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		super.beforeModelInitialized(event);
		try {
			completeCriteria( this.getController().getCriteria() );
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido construir el filtro de empresas";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		} catch (ExpressionException e) {
			String msg = "No se ha podido construir el filtro de empresas";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria(criteria);
		if(DomainManager.isDomainManagementAvailable()){
			criteria.setSkipDomainFilter( true );
			criteria.addInExpression("Person.domain", getCurrentChildDomainIds());
		}
	}
	
	private List<Integer> getCurrentChildDomainIds(){
		List<Integer> idList = new LinkedList<Integer>();
		if( DomainManager.isDomainManagementAvailable() ){
			try {
				IManagerBean bean = BeanManager.getManagerBean(Domain.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_PARENT_ID), DomainManager.getCurrentDomain());
				List<ITransferObject> list = bean.getList(criteria);
				for(ITransferObject to: list){
					idList.add(((Domain)to).getId());
				}
			} catch (ManagerBeanException e) {
				String msg = "No se ha podido construir el filtro de empresas";
				AonUtil.addErrorMessage(msg);
				AonUtil.addErrorMessage(e.getMessage());
				throw new AbortProcessingException(e);
			}
		} 
		return idList;
	}
	
}