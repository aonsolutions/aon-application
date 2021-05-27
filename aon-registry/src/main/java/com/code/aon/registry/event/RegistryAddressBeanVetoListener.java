package com.code.aon.registry.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.esferalia.aon.entity.IEntityAlias;

public class RegistryAddressBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
    public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	RegistryAddress to = (RegistryAddress) evt.getTo();
    	ensureAddressType( to );
    }

	@Override
    public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
    	RegistryAddress to = (RegistryAddress) evt.getTo();
    	ensureAddressType( to );
    }

    private void ensureAddressType(RegistryAddress to) throws ManagerBeanVetoListenerException {
    	try {
    		if (to.getAddressType() != AddressType.DELEGATION) { 
				IManagerBean bean = BeanManager.getManagerBean(RegistryAddress.class);
				Criteria c = new Criteria();
				c.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_REGISTRY_ID ), to.getRegistry().getId());
				if (to.getId() != null) {
					c.addNotEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ID ), to.getId());	
				}
				
		    	if (to.getAddressType() == null) {
	    			int count = bean.getCount(c);
	    			if (count>0) { 
	    				to.setAddressType(AddressType.DELEGATION);
	    			} else {
	    				to.setAddressType(AddressType.MAIN);
	    			}
		    	} else if (to.getAddressType() == AddressType.MAIN) {
	    			c.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ADDRESS_ADDRESS_TYPE ), AddressType.MAIN);
	    			for(ITransferObject updatableTo: bean.getList(c)){
	    				try {
	    					RegistryAddress ra = (RegistryAddress) updatableTo;
	    					ra.setAddressType(AddressType.DELEGATION);
	    					bean.update(ra);
	    				} catch (Exception e) {
	    					throw new ManagerBeanVetoListenerException("No se ha podido modificar a tipo principal.");  
	    				}
	    			}
	    		}
    		}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e);
		}
	}
}
