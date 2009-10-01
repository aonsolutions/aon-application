package com.code.aon.ui.commercial.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;

import com.code.aon.commercial.CommercialSegment;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the target maintenance.
 */
public class TargetController extends BasicController {
	
	private List<SelectItem> segments;
	
	private ResourceBundle bundle;
	
	private String selectedTab;
	
	private boolean showProfile;
	
	private boolean showSearchOnlyCustomers;
	
	private boolean showCommercialTracking;
	
	private boolean showProduct;
	
	private boolean showSeller;
	
	public TargetController() {
		this.showProduct = true;
		this.showSeller = true;
		setBundleName(ICommercialConstants.BUNDLE_NAME);
	}

	public List<SelectItem> getSegments() {
		return segments;
	}

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
	
	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public boolean isShowCommercialTracking() {
		return showCommercialTracking;
	}

	public void setShowCommercialTracking(boolean showCommercialTracking) {
		this.showCommercialTracking = showCommercialTracking;
	}

	public boolean isShowProfile() {
		return showProfile;
	}

	public void setShowProfile(boolean showProfile) {
		this.showProfile = showProfile;
	}

	public boolean isShowSearchOnlyCustomers() {
		return showSearchOnlyCustomers;
	}

	public void setShowSearchOnlyCustomers(boolean showSearchOnlyCustomers) {
		this.showSearchOnlyCustomers = showSearchOnlyCustomers;
	}

	public boolean isShowProduct() {
		return showProduct;
	}

	public void setShowProduct(boolean showProduct) {
		this.showProduct = showProduct;
	}

	public boolean isShowSeller() {
		return showSeller;
	}

	public void setShowSeller(boolean showSeller) {
		this.showSeller = showSeller;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundleName(String bundleName) {
		this.bundle = AonUtil.getResourceBundle(bundleName);
	}	
	
}