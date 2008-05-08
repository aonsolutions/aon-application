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
import com.code.aon.product.Item;
import com.code.aon.product.util.DiscountExpression;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.warehouse.IncomeDetail;

public class CompositionIncomeDetailListener extends ControllerAdapter {
	
    /** The LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CompositionIncomeDetailListener.class.getName());

    /**
     * Before bean added. Sets composition price equals zero in income, if composition price isPriceInDetails.
     * 
     * @param event
     * @throws ControllerListenerException
     */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Composition composition = obtainComposition(((IncomeDetail)event.getController().getTo()).getItem());
		if(composition != null && composition.isPriceInDetails()){
			((IncomeDetail)event.getController().getTo()).setPrice(0.0);
		}
	}
	
    /**
     * After bean added. Inserts composition details in income.
     * 
     * @param event
     * @throws ControllerListenerException
     */
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IncomeDetail incomeDetail = duplicateIncomeDetail((IncomeDetail)event.getController().getTo());
		try {
			Composition composition = obtainComposition(incomeDetail.getItem());
			if(composition != null){
				IManagerBean compositionDetailBean = BeanManager.getManagerBean(CompositionDetail.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(compositionDetailBean.getFieldName(ICompositionAlias.COMPOSITION_DETAIL_COMPOSITION_ID), composition.getId());
				Iterator iter = compositionDetailBean.getList(criteria).iterator();
				while(iter.hasNext()){
					CompositionDetail compositionDetail = (CompositionDetail)iter.next();
					((IncomeDetail)event.getController().getTo()).setItem(compositionDetail.getItem());
                    ((IncomeDetail)event.getController().getTo()).setDescription(compositionDetail.getDescription());
					((IncomeDetail)event.getController().getTo()).setDiscountExpression(new DiscountExpression("0.0"));
					((IncomeDetail)event.getController().getTo()).setQuantity(incomeDetail.getQuantity() * compositionDetail.getQuantity());
					if(composition.isPriceInDetails()){
						((IncomeDetail)event.getController().getTo()).setPrice(incomeDetail.getQuantity() * compositionDetail.getPrice());
					}else{
						((IncomeDetail)event.getController().getTo()).setPrice(0.0);
					}
                    event.getController().onAccept(null);
                    event.getController().onReset(null);
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error before bean added for incomeDetail with id= " + incomeDetail.getId(), e);
		}
		
	}
	
    /**
     * Obtains a composition using the given item.
     * 
     * @param item
     * @return Composition
     */
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
	private IncomeDetail duplicateIncomeDetail(IncomeDetail detail) {
		IncomeDetail incomeDetail = new IncomeDetail();
		incomeDetail.setId(detail.getId());
		incomeDetail.setIncome(detail.getIncome());
        incomeDetail.setItem(detail.getItem());
        incomeDetail.setDescription(detail.getDescription());
		incomeDetail.setPrice(detail.getPrice());
        incomeDetail.setDiscountExpression(detail.getDiscountExpression());
		incomeDetail.setQuantity(detail.getQuantity());
		return incomeDetail;
	}

}
