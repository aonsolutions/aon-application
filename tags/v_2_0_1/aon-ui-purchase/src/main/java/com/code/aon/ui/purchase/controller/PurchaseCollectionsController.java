package com.code.aon.ui.purchase.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.purchase.SupplierSegment;
import com.code.aon.purchase.dao.IPurchaseAlias;
import com.code.aon.purchase.enumeration.PurchaseDetailStatus;
import com.code.aon.purchase.enumeration.PurchaseStatus;
import com.code.aon.purchase.enumeration.SupplierStatus;
import com.code.aon.ql.Criteria;

/**
 * Controller used to get Collections related with clasess in <code>com.code.aon.purchase</code>
 * 
 * @author Consulting & Development. Joseba Urkiri - 21-dic-2005
 */
public class PurchaseCollectionsController {
	
    /**
     * Gets the purchase statuses.
     * 
     * @return the purchase statuses
     */
    public List<SelectItem> getPurchaseStatuses() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        for( PurchaseStatus type : PurchaseStatus.values() ) {
            String name = type.getName(locale); 
            SelectItem item = new SelectItem(type, name);
            types.add( item );
        }
        return types;
    }
	
    /**
     * Gets the purchase detail statuses.
     * 
     * @return the purchase detail statuses
     */
    public List<SelectItem> getPurchaseDetailStatuses() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        for( PurchaseDetailStatus type : PurchaseDetailStatus.values() ) {
            String name = type.getName(locale); 
            SelectItem item = new SelectItem(type, name);
            types.add( item );
        }
        return types;
    }
    
    /**
     * Gets the supplier statuses.
     * 
     * @return the supplier statuses
     */
    public List<SelectItem> getSupplierStatuses() {
        Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
	    LinkedList<SelectItem> types = new LinkedList<SelectItem>();
        for( SupplierStatus type : SupplierStatus.values() ) {
            String name = type.getName(locale); 
            SelectItem item = new SelectItem(type, name);
            types.add( item );
        }
        return types;
    }
    
    @SuppressWarnings("unchecked")
    public List<SelectItem> getSupplierSegments() throws ManagerBeanException {
		List<SelectItem>supplierSegments = new LinkedList<SelectItem>();
		IManagerBean supplierSegmentBean = BeanManager.getManagerBean(SupplierSegment.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(supplierSegmentBean.getFieldName(IPurchaseAlias.SUPPLIER_SEGMENT_DESCRIPTION));
		Iterator iter = supplierSegmentBean.getList(criteria).iterator();
		while(iter.hasNext()){
			SupplierSegment segment = (SupplierSegment)iter.next();
			SelectItem item = new SelectItem(segment.getId(), segment.getDescription());
			supplierSegments.add(item);
		}
		return supplierSegments;
	}
}