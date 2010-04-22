package com.code.aon.ui.composition.event;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.composition.Composition;
import com.code.aon.composition.CompositionDetail;
import com.code.aon.composition.dao.ICompositionAlias;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CompositionInvoiceDetailListener extends ControllerAdapter {
	
    /** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CompositionInvoiceDetailListener.class.getName());
	
    /**
     * Before bean added. Sets composition price equals zero in invoice, if composition price isPriceInDetails.
     * 
     * @param event
     * @throws ControllerListenerException
     */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Composition composition = obtainComposition(((InvoiceDetail)event.getController().getTo()).getItem());
		if(composition != null && composition.isPriceInDetails()){
			((InvoiceDetail)event.getController().getTo()).setPrice(0.0);
		}
	}

    /**
     * After bean added. Inserts composition details in invoice.
     * 
     * @param event
     * @throws ControllerListenerException
     */
	@Override
	@SuppressWarnings("unchecked")
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		InvoiceDetail invoiceDetail = duplicateInvoiceDetail((InvoiceDetail)event.getController().getTo());
		try {
			Composition composition = obtainComposition(invoiceDetail.getItem());
			if(composition != null){
				IManagerBean compositionDetailBean = BeanManager.getManagerBean(CompositionDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(compositionDetailBean.getFieldName(ICompositionAlias.COMPOSITION_DETAIL_COMPOSITION_ID), composition.getId());
				Iterator iter = compositionDetailBean.getList(criteria).iterator();
				while(iter.hasNext()){
					CompositionDetail compositionDetail = (CompositionDetail)iter.next();
                    ((InvoiceDetail)event.getController().getTo()).setItem(compositionDetail.getItem());
                    ((InvoiceDetail)event.getController().getTo()).setDescription(compositionDetail.getDescription() + "( " + composition.getItem().getProduct().getName() + " )");
					((InvoiceDetail)event.getController().getTo()).setSourceId(null);
					((InvoiceDetail)event.getController().getTo()).setDiscountExpression(new DiscountExpression("0.0"));
					((InvoiceDetail)event.getController().getTo()).setQuantity(invoiceDetail.getQuantity() * compositionDetail.getQuantity());
					if(composition.isPriceInDetails()){
						((InvoiceDetail)event.getController().getTo()).setPrice(compositionDetail.getPrice());
					}else{
						((InvoiceDetail)event.getController().getTo()).setPrice(0.0);
					}
                    event.getController().onAccept(null);
                    event.getController().onReset(null);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error before bean added for invoiceDetail with id= " + invoiceDetail.getId(), e);
		}
		
	}
	
    /**
     * Obtains a composition using the given item.
     * 
     * @param item
     * @return Composition
     */
	@SuppressWarnings("unchecked")
	private Composition obtainComposition(Item item) {
		try {
			IManagerBean compositionBean = BeanManager.getManagerBean(Composition.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(compositionBean.getFieldName(ICompositionAlias.COMPOSITION_ITEM), item.getId());
			Iterator iter = compositionBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Composition)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining composition for item with id=" + item.getId(), e);
		}
		return null;
	}

    /** Duplicates delivery detail.
     * @param detail
     * @return DeliveryDetail
     */
	private InvoiceDetail duplicateInvoiceDetail(InvoiceDetail detail) {
		InvoiceDetail invoiceDetail = new InvoiceDetail();
		invoiceDetail.setId(detail.getId());
		invoiceDetail.setSourceId(detail.getSourceId());
		invoiceDetail.setDiscountExpression(detail.getDiscountExpression());
        invoiceDetail.setInvoice(detail.getInvoice());
		invoiceDetail.setItem(detail.getItem());
        invoiceDetail.setDescription(detail.getDescription());
		invoiceDetail.setPrice(detail.getPrice());
		invoiceDetail.setQuantity(detail.getQuantity());
		return invoiceDetail;
	}

}
