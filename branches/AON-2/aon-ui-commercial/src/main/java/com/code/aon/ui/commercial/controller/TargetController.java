package com.code.aon.ui.commercial.controller;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ILookupObject;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.menu.jsf.MenuEvent;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the target maintenance.
 */
public class TargetController extends BasicController {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TargetController.class.getName());
	
	/** REGISTRY_ADDRESS_ID. */
	private static final String REGISTRY_ADDRESS_ID = "raddress_id";
	
	/** REGISTRY_ADDRESS. */
	private static final String REGISTRY_ADDRESS = "raddress";
	
	/** REGISTRY_ADDRESS_CITY. */
	private static final String REGISTRY_ADDRESS_CITY = "city";
	
	/** TargetMedia Controller name. */
	private static final String TARGET_MEDIA_CONTROLLER_NAME = "targetMedia";
	
	/** TargetAddress Controller name. */
	private final static String TARGET_ADDRESS_CONTROLLER_NAME = "targetAddress";

    /**
     * Adds custom entries into the lookup map
     * 
     * @param ito the lookup object
     * @param map the map
     */
    @Override
	protected void customizeLookupMap(ILookupObject ito, Map<String, Object> map) {
		try {
			IManagerBean rAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID),map.get("Target_id"));
            criteria.addOrder(rAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS_TYPE));
            Iterator iter = rAddressBean.getList(criteria).iterator();
			if(iter.hasNext()){
				RegistryAddress rAddress = (RegistryAddress)iter.next();
				map.put(REGISTRY_ADDRESS_ID, rAddress.getId());
				map.put(REGISTRY_ADDRESS,((rAddress.getAddress() != null)?rAddress.getAddress():"") + " " + ((rAddress.getAddress2()!= null)?rAddress.getAddress2():"") + " " + ((rAddress.getAddress3()!=null)?rAddress.getAddress3():"") );
				map.put(REGISTRY_ADDRESS_CITY, rAddress.getCity() + " " + rAddress.getZip());
			}else{
				map.put(REGISTRY_ADDRESS, "" );
				map.put(REGISTRY_ADDRESS_CITY, "");
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error customizing lookup map",e);
		}
	}

    /**
     * On reset. Method launched by the menu
     * 
     * @param event the event
     */
    @SuppressWarnings("unused")
    public void onReset(MenuEvent event) {
        this.onReset((ActionEvent)event);
    }

    /**
     * On reset. Method launched by the menu
     * 
     * @param event the event
     */
    @Override
    public void onReset(ActionEvent event) {
        IController addressController = AonUtil.getController(TARGET_ADDRESS_CONTROLLER_NAME);
        addressController.onCancel(event);

        IController mediaController = AonUtil.getController(TARGET_MEDIA_CONTROLLER_NAME);
        mediaController.onCancel(event);

        super.onReset(event);
    }

    /**
     * On select. Sends a cancel to the media and address controllers to avoid having editing any of them
     * 
     * @param event the event
     */
    @Override
    public void onSelect(ActionEvent event) {
    	IController addressController = AonUtil.getController(TARGET_ADDRESS_CONTROLLER_NAME);
        addressController.onCancel(event);

        IController mediaController = AonUtil.getController(TARGET_MEDIA_CONTROLLER_NAME);
        mediaController.onCancel(event);

        super.onSelect(event);
    }
}