package com.code.aon.ui.registry.controller.event;

import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.BankAccount;
import com.code.aon.config.PayMethod;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.RegistryPayMethod;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.IRegistryConstants;
import com.code.aon.ui.registry.controller.RegistryCollectionsController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PersonPayMethodFormListener extends RegistryPayMethodFormListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public boolean isShowCompanyBanks() {
		return false;
	}
	
	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		RegistryCollectionsController c = (RegistryCollectionsController)AonUtil.getRegisteredBean(IRegistryConstants.COLLECTIONS_CONTROLLER_NAME);
		Registry registry = getRegistryPayMethod().getRegistry();
		return c.getActiveRegistryBanks(registry);
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanSelected(event);
		loadPayMethod(event);
	}
	
	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		super.afterBeanUpdated(event);
		loadPayMethod(event);
	}
	
	private void loadPayMethod(ControllerEvent event) throws ControllerListenerException {
		try {
			Person person = (Person) event.getController().getTo();
			
			IManagerBean rPayBean = BeanManager.getManagerBean(RegistryPayMethod.class);
			Criteria rPayBeanCriteria = new Criteria();
			rPayBeanCriteria.addEqualExpression(rPayBean.getFieldName(IEntityAlias.REGISTRY_PAY_METHOD_REGISTRY_ID), person.getId());
			Iterator<?> iter = rPayBean.getList(rPayBeanCriteria).iterator();
			if(iter.hasNext()){
				RegistryPayMethod pm = (RegistryPayMethod)iter.next(); 
				setRegistryPayMethod(pm);
				if (getRegistryBank() == null) {
					pm.setRegistryBank( new RegistryBank() );	
				}
				if (getRegistryBank().getBankAccount() == null) {
					pm.getRegistryBank().setBankAccount( new BankAccount() );
				}
			}else{
				setRegistryPayMethod(new RegistryPayMethod());
				getRegistryPayMethod().setRegistry(person.getRegistry());
				getRegistryPayMethod().setPayment(new PayMethod());
				getRegistryPayMethod().setRegistryBank( new RegistryBank() );
				getRegistryBank().setRegistry(person.getRegistry());
				getRegistryBank().setBankAccount( new BankAccount() );
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private Registry getRegistry( ControllerEvent event ) {
		IController controller = event.getController();
		return ((IRegistry) controller.getTo()).getRegistry();
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			updateRegistryLines( getRegistry(event) );
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}

	@Override
	protected void updateRegistryLines(Registry registry) throws ManagerBeanException {
		if(getRegistryBank()!=null && getRegistryBank().getId()!=null){
			super.updateRegistryBank(registry);
		}
		super.updateRegistryPayMethod(registry);
	}	

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		super.beforeBeanAdded(event);
	}
	

}
