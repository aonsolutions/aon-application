package com.code.aon.ui.geozone.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.GeoTree;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class GeotreeParentControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		IController geotreeParentController = event.getController();
		try {
			Criteria criteria = geotreeParentController.getCriteria();
			criteria.addNullExpression(geotreeParentController.getManagerBean().getFieldName(IGeoZoneAlias.GEO_TREE_PARENT));
			geotreeParentController.setCriteria(criteria);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		GeoTree geotree = (GeoTree)event.getController().getTo();
		geotree.setParent(null);
	}

    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		GeoTree geotree = (GeoTree)event.getController().getTo();
		geotree.setParent(null);
    }

}