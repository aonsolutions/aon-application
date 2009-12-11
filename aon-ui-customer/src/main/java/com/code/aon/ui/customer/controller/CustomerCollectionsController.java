package com.code.aon.ui.customer.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.CustomerSegment;
import com.code.aon.customer.dao.ICustomerAlias;
import com.code.aon.customer.enumeration.CustomerStatus;
import com.code.aon.ql.Criteria;

public class CustomerCollectionsController {

	private List<SelectItem> customerStatuses;
	
	/**
	 * Gets the customer statuses.
	 * 
	 * @return the customer statuses
	 */
	public List<SelectItem> getCustomerStatuses() {
		if ( customerStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			customerStatuses = new LinkedList<SelectItem>();
			for( CustomerStatus status : CustomerStatus.values() ) {
				String name = status.getName(locale);
				SelectItem item = new SelectItem(status, name);
				customerStatuses.add(item);
			}			
		}
		return customerStatuses;
	}

    public CustomerSegment getCustomerSegment() {
    	return null;
    }
    
    public void setCustomerSegment( CustomerSegment customerSegment ) {
    }
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getCustomerSegments() throws ManagerBeanException {
		List<SelectItem>customerSegments = new LinkedList<SelectItem>();
		IManagerBean customerSegmentBean = BeanManager.getManagerBean(CustomerSegment.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(customerSegmentBean.getFieldName(ICustomerAlias.CUSTOMER_SEGMENT_DESCRIPTION));
		Iterator iter = customerSegmentBean.getList(criteria).iterator();
		while(iter.hasNext()){
			CustomerSegment segment = (CustomerSegment)iter.next();
			SelectItem item = new SelectItem(segment, segment.getDescription());
			customerSegments.add(item);
		}
		return customerSegments;
	}
}