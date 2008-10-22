package com.code.aon.ui.commercial.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.commercial.CommercialSegment;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the target maintenance.
 */
public class TargetController extends BasicController {
	
	/** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(TargetController.class.getName());
	
	private List<SelectItem> segments;
	
	private String selectedTab;
	
	public List<SelectItem> getSegments() {
		return segments;
	}

	@SuppressWarnings("unchecked")
	public void refreshSegments() throws ManagerBeanException {
		segments = new LinkedList<SelectItem>();
		IManagerBean segmentBean = BeanManager.getManagerBean(CommercialSegment.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(segmentBean.getFieldName(ICommercialAlias.COMMERCIAL_SEGMENT_NAME));
		Iterator<ITransferObject> iter = segmentBean.getList(criteria).iterator();
		while(iter.hasNext()){
			CommercialSegment segment = (CommercialSegment)iter.next();
			SelectItem item = new SelectItem(segment.getId(), segment.getName());
			segments.add(item);
		}
	}
	
	public void tabChanged( ValueChangeEvent event ) {
		Object controllerName = event.getOldValue();
		if ( controllerName != null ) {
			IController controller = AonUtil.getController((String) controllerName);
			if ( controller != null ) {
				controller.onCancel(null);	
			}
		}
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}
	
}