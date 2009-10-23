package com.code.aon.ui.geozone.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.geozone.dao.IGeoZoneAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class GeotreeChildControllerListener extends ControllerAdapter {

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		LinesController geotreeChildController = (LinesController)event.getController();
		try {
			Criteria criteria = geotreeChildController.getCriteria();
			criteria.addOrder(geotreeChildController.getManagerBean().getFieldName(IGeoZoneAlias.GEO_TREE_CHILD_NAME));
			geotreeChildController.setCriteria(criteria);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}