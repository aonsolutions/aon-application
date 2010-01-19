package com.code.aon.ebackoffice.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.validator.EmailValidator;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ebackoffice.Ectarget;
import com.code.aon.ebackoffice.dao.IEbackofficeAlias;

public class EcconfigBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final Logger LOGGER = Logger
			.getLogger(EcconfigBeanVetoListener.class.getName());

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
