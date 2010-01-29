package com.code.aon.ui.supplier.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.supplier.SupplierSegment;
import com.code.aon.supplier.dao.ISupplierAlias;
import com.code.aon.supplier.enumeration.SupplierStatus;

public class SupplierCollectionsController {

	private List<SelectItem> supplierStatuses;
	
    /**
     * Gets the supplier statuses.
     * 
     * @return the supplier statuses
     */
    public List<SelectItem> getSupplierStatuses() {
    	if ( this.supplierStatuses == null ) {
	        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	        supplierStatuses = new LinkedList<SelectItem>();
	        for( SupplierStatus type : SupplierStatus.values() ) {
	            String name = type.getName(locale); 
	            SelectItem item = new SelectItem(type, name);
	            supplierStatuses.add( item );
	        }
    	}
        return supplierStatuses;
    }
    
    public SupplierSegment getSupplierSegment() {
    	return null;
    }
    
    public void setSupplierSegment( SupplierSegment supplierSegment ) {
    }
    
    @SuppressWarnings("unchecked")
    public List<SelectItem> getSupplierSegments() throws ManagerBeanException {
		List<SelectItem>supplierSegments = new LinkedList<SelectItem>();
		IManagerBean supplierSegmentBean = BeanManager.getManagerBean(SupplierSegment.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(supplierSegmentBean.getFieldName(ISupplierAlias.SUPPLIER_SEGMENT_DESCRIPTION));
		Iterator iter = supplierSegmentBean.getList(criteria).iterator();
		while(iter.hasNext()){
			SupplierSegment segment = (SupplierSegment)iter.next();
			SelectItem item = new SelectItem(segment, segment.getDescription());
			supplierSegments.add(item);
		}
		return supplierSegments;
	}
}