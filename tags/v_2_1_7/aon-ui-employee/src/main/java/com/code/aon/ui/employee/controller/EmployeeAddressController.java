package com.code.aon.ui.employee.controller;

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

public class EmployeeAddressController extends BasicController {
	
	private static final long serialVersionUID = -4654786512841650734L;
	static final String MANAGER_BEAN_NAME = "employeeAddress";

	@Override
    public void onReset(ActionEvent event) {
        IController mediaController = AonUtil.getController( EmployeeMediaController.MANAGER_BEAN_NAME );
        mediaController.onCancel(event);
        super.onReset(event);
    }

    @Override
    public void onSelect(ActionEvent event) {
        IController mediaController = AonUtil.getController( EmployeeMediaController.MANAGER_BEAN_NAME );
        mediaController.onCancel(event); 
        super.onSelect(event);
    }

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