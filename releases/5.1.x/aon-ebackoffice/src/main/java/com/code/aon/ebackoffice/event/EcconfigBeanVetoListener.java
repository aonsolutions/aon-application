package com.code.aon.ebackoffice.event;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ebackoffice.util.EmailUtils;

public class EcconfigBeanVetoListener extends ManagerBeanVetoListenerAdapter {


	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
					
		if(!EmailUtils.validateEmailAddress(((Ecconfig)evt.getTo()).getEmail())){
    		throw new ManagerBeanVetoListenerException(
			"El email es incorrecto.");
		}
		
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
		
		if(!EmailUtils.validateEmailAddress(((Ecconfig)evt.getTo()).getEmail())){
    		throw new ManagerBeanVetoListenerException(
			"El email es incorrecto.");
		}	
		
	}

	/*public static void checkActiveConfiguration(ITransferObject to)
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
	}*/

}
