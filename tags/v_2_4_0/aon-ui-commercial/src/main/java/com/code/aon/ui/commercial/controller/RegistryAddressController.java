package com.code.aon.ui.commercial.controller;

import java.util.Iterator;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoZone;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the address maintenance of the target.
 */
public class RegistryAddressController extends BasicController {
	
    /**
     * Retrieves the whole <code>GeoZone</code> object when the lookup field changes
     * 
     * @param event the event
     * 
     * @throws ManagerBeanException the manager bean exception
     */
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