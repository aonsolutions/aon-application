package com.code.aon.ui.customer.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the address maintenance of the customer.
 */
public class CustomerAddressController extends BasicController {
	
	/** CustomerMedia Controller name. */
	private static final String CUSTOMER_MEDIA_CONTROLLER_NAME = "customerMedia";
	
	/**
     * On reset. Sends a cancel to the media controller to avoid having both controllers editing
     * 
     * @param event the event
     */
    @Override
    public void onReset(ActionEvent event) {
    	IController mediaController = AonUtil.getController(CUSTOMER_MEDIA_CONTROLLER_NAME);
    	if(mediaController != null){
    		mediaController.onCancel(event);
    	}

        super.onReset(event);
    }

    /**
     * On select. Sends a cancel to the media controller to avoid having both controllers editing
     * 
     * @param event the event
     */
    @Override
    public void onSelect(ActionEvent event) {
    	IController mediaController = AonUtil.getController(CUSTOMER_MEDIA_CONTROLLER_NAME);
    	if(mediaController != null){
    		mediaController.onCancel(event);
    	}

        super.onSelect(event);
    }
    
    /**
     * Retrieves the whole <code>GeoZone</code> object when the lookup field changes
     * 
     * @param event the event
     * 
     * @throws ManagerBeanException the manager bean exception
     */
    @SuppressWarnings("unchecked")
    public void onChangeGeoZone(ValueChangeEvent event) throws ManagerBeanException {
    	if(event.getNewValue() != null){
    		IManagerBean geoZoneBean = BeanManager.getManagerBean(GeoZone.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(geoZoneBean.getFieldName(IGeoZoneAlias.GEO_ZONE_ID), event.getNewValue());
    		Iterator iter = geoZoneBean.getList(criteria).iterator();
    		if(iter.hasNext()){
    			((RegistryAddress)this.getTo()).setGeozone((GeoZone)iter.next());
    		}
    	}
    }
}