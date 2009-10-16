package com.code.aon.ui.commercial.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.model.SelectItem;

import com.code.aon.commercial.CommercialSegment;
import com.code.aon.commercial.Target;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryMedia;
import com.code.aon.ui.commercial.ICommercialMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

/**
 * Controller used in the target maintenance.
 */
public class TargetController extends BasicController {
	
	private List<SelectItem> segments;
	
	private ResourceBundle bundle;
	
	private String selectedTab;
	
	/** The phone. */
	private RegistryMedia phone;

	/** The fax. */
	private RegistryMedia fax;

	/** The email. */
	private RegistryMedia email;
	
		
	public RegistryMedia getPhone() {
		return phone;
	}

	public void setPhone(RegistryMedia phone) {
		this.phone = phone;
	}

	public RegistryMedia getFax() {
		return fax;
	}

	public void setFax(RegistryMedia fax) {
		this.fax = fax;
	}

	public RegistryMedia getEmail() {
		return email;
	}

	public void setEmail(RegistryMedia email) {
		this.email = email;
	}

	public TargetController() {
		setBundleName(ICommercialMessages.BUNDLE_KEY);
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

	public ResourceBundle getBundle() {
		return bundle;
	}

	public void setBundleName(String bundleName) {
		this.bundle = AonUtil.getResourceBundle(bundleName);
	}	
	
	
	public void obtainPhone() throws ManagerBeanException {	
		if (((Target)this.getTo()).getRegistry().getPhone()!=null)
		{
		setPhone(((Target)this.getTo()).getRegistry().getCellular());
		}
	}
	
	public void obtainFax() throws ManagerBeanException {
		if (((Target)this.getTo()).getRegistry().getFax()!=null)
		{		
		setFax(((Target)this.getTo()).getRegistry().getFax());
		}			
	}
	
	
	public void obtainEmail() throws ManagerBeanException {	
		if (((Target)this.getTo()).getRegistry().getEmail()!=null)
		{
		setEmail(((Target)this.getTo()).getRegistry().getEmail());
		}
	}
	
	
	
	
	
}