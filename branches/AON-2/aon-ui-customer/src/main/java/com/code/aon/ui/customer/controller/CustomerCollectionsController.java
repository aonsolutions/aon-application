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

	/**
	 * Gets the customer statuses.
	 * 
	 * @return the customer statuses
	 */
	public List<SelectItem> getCustomerStatuses() {
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		LinkedList<SelectItem> types = new LinkedList<SelectItem>();
		CustomerStatus[] cStatuses = CustomerStatus.values();
		for (int i = 0; i < cStatuses.length; i++) {
			CustomerStatus status = cStatuses[i];
			String name = status.getName(locale);
			SelectItem item = new SelectItem(status, name);
			types.add(item);
		}
		return types;
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
			SelectItem item = new SelectItem(segment.getId(), segment.getDescription());
			customerSegments.add(item);
		}
		return customerSegments;
	}
}