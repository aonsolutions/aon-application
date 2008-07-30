package com.code.aon.ui.commercial.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.commercial.CommercialActivity;
import com.code.aon.commercial.CommercialTracking;
import com.code.aon.commercial.dao.ICommercialAlias;
import com.code.aon.commercial.enumeration.CommercialTrackingStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Seller;
import com.code.aon.ui.form.BasicController;

/**
 * Controller used in the offer maintenance.
 */
public class CommercialTrackingController extends BasicController {
	
	private Date lastDate;
	
	private Seller lastSeller;
	
	private boolean nextAction;
	
	private CommercialTracking next;
	
	private List<SelectItem> activities;

	public Date getLastDate() {
		return lastDate;
	}

	public void setLastDate(Date lastDate) {
		this.lastDate = lastDate;
	}

	public Seller getLastSeller() {
		return lastSeller;
	}

	public void setLastSeller(Seller lastSeller) {
		this.lastSeller = lastSeller;
	}

	public List<SelectItem> getActivities() {
		return activities;
	}	

	@SuppressWarnings("unchecked")
	public void refreshActivities() throws ManagerBeanException {
		activities = new LinkedList<SelectItem>();
		IManagerBean activityBean = BeanManager.getManagerBean(CommercialActivity.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(activityBean.getFieldName(ICommercialAlias.COMMERCIAL_ACTIVITY_NAME));
		Iterator<ITransferObject> iter = activityBean.getList(criteria).iterator();
		while(iter.hasNext()){
			CommercialActivity activity = (CommercialActivity)iter.next();
			SelectItem item = new SelectItem(activity, activity.getName());
			activities.add(item);
		}
	}

	public boolean isNextAction() {
		return nextAction;
	}

	public void setNextAction(boolean nextAction) {
		this.nextAction = nextAction;
	}

	public CommercialTracking getNext() {
		return next;
	}

	public void setNext(CommercialTracking next) {
		this.next = next;
	}	

	public void nextActionChanged( ValueChangeEvent event ) {
		Boolean value = (Boolean) event.getNewValue();
		if ( value ) {
			this.next = new CommercialTracking();
			if ( getLastDate() != null ) {
				this.next.setDate( getLastDate() );	
			}
			this.next.setStatus(CommercialTrackingStatus.PENDING);
		} else {
			this.next = null;
		}
	}
	
}