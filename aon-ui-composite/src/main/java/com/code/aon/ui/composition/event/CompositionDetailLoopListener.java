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
import com.code.aon.ql.Criteria;
import com.code.aon.ui.composition.controller.CompositionController;
import com.code.aon.ui.composition.controller.CompositionDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CompositionDetailLoopListener extends ControllerAdapter {
	
	private static final String COMPOSITION_CONTROLLER_NAME = "composition";
	
	private static final Logger LOGGER = Logger.getLogger(CompositionDetailLoopListener.class.getName());
	
    /**
     * Before bean added. Checks if there are possible loops produced in composition, and if so it throws an exception.
     * 
     * @param event
     * @throws ControllerListenerException
     */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CompositionDetail compositionDetail = (CompositionDetail)event.getController().getTo();
		CompositionController compositionController = (CompositionController)AonUtil.getController(COMPOSITION_CONTROLLER_NAME);
		if(compositionDetail.getItem().getProduct().isComposition()){
			Composition childComposition = obtainItemComposition(compositionDetail.getItem());
			if(checkloop((Composition)compositionController.getTo(),childComposition)){
				CompositionDetailController compositionDetailController = (CompositionDetailController)event.getController();
				compositionDetailController.onCancel(null);
				throw new ControllerListenerException("Composition loop detected");
			}
		}
	}
	
    /**
     * Before bean updated. Checks if there are possible loops produced in composition, and if so it throws an exception.
     * 
     * @param event
     * @throws ControllerListenerException
     */
	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CompositionDetail compositionDetail = (CompositionDetail)event.getController().getTo();
		CompositionController compositionController = (CompositionController)AonUtil.getController(COMPOSITION_CONTROLLER_NAME);
		if(compositionDetail.getItem().getProduct().isComposition()){
			Composition childComposition = obtainItemComposition(compositionDetail.getItem());
			if(checkloop((Composition)compositionController.getTo(),childComposition)){
				CompositionDetailController compositionDetailController = (CompositionDetailController)event.getController();
				compositionDetailController.onCancel(null);
				compositionDetailController.onSearch(null);
				throw new ControllerListenerException("Composition loop detected");
			}
		}
	}

	/**
     * Obtains item composition.
     * 
	 * @param item
	 * @return Composition
	 */
	private Composition obtainItemComposition(Item item) {
		try {
			IManagerBean compositionBean = BeanManager.getManagerBean(Composition.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(compositionBean.getFieldName(ICompositionAlias.COMPOSITION_ITEM), item.getId());
			Iterator iter = compositionBean.getList(criteria).iterator();
			if(iter.hasNext()){
				return (Composition)iter.next();
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error obtaining composition for item with id= " + item.getId(), e);
		}
		return null;
	}

    /**
     * Checks if there are possible loops produced in composition.
     * 
	 * @param composition
	 * @param childComposition
	 * @return boolean
	 */
	private boolean checkloop(Composition composition, Composition childComposition) {
		try {
			if(childComposition.getItem().getId().equals(composition.getItem().getId())){
				return true;
			}
			IManagerBean compositionDetailBean = BeanManager.getManagerBean(CompositionDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(compositionDetailBean.getFieldName(ICompositionAlias.COMPOSITION_DETAIL_COMPOSITION_ID), childComposition.getId());
			Iterator iter = compositionDetailBean.getList(criteria).iterator();
			while(iter.hasNext()){
				CompositionDetail compositionDetail = (CompositionDetail)iter.next();
				if(compositionDetail.getItem().getId().equals(composition.getItem().getId())){
					return true;
				}
				if(compositionDetail.getItem().getProduct().isComposition()){
					return checkloop(composition, obtainItemComposition(compositionDetail.getItem()));
				}
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error checking composition loop", e);
		}
		return false;
	}
}
