package com.code.aon.ebackoffice.event;

import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ebackoffice.Ectarget;
import com.code.aon.ebackoffice.util.EmailUtils;

public class EcconfigBeanVetoListener extends ManagerBeanVetoListenerAdapter {


	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
		
		if(((Ecconfig)evt.getTo()).isActive()){
			try {
				checkActiveConfiguration((Ecconfig)evt.getTo());
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    		
		}		
		
		if(!EmailUtils.validateEmailAddress(((Ecconfig)evt.getTo()).getEmail())){
    		throw new ManagerBeanVetoListenerException(
			"El email es incorrecto.");
		}
		
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
		
		if(((Ecconfig)evt.getTo()).isActive()){
			try {
				checkActiveConfiguration((Ecconfig)evt.getTo());
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
    		
		}
		
		if(!EmailUtils.validateEmailAddress(((Ecconfig)evt.getTo()).getEmail())){
    		throw new ManagerBeanVetoListenerException(
			"El email es incorrecto.");
		}	
		
	}

	public static void checkActiveConfiguration(ITransferObject to)
			throws ManagerBeanException {

		IManagerBean ecconfigBean = BeanManager.getManagerBean(Ecconfig.class);
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Ecconfig conf = (Ecconfig) to;

		List<ITransferObject> lista;
		lista = ecconfigBean.getList(null);

		for (ITransferObject rec : lista) {
			Ecconfig ecconf = (Ecconfig) rec;
			if (conf.getId() != ecconf.getId()) {
				ecconf.setActive(false);
				ecconfigBean.update(ecconf);
				;
			}

		}
	}

}
