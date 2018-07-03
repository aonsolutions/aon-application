package com.code.aon.ui.product.event;

import static com.code.aon.ui.common.ICommonMessages.ITEM_COMPOSITION_SERIALIZABLE_ERROR;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.ItemComposition;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.ItemCompositionController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemCompositionControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ItemCompositionController controller = (ItemCompositionController)event.getController();
		ItemComposition itemComposition = (ItemComposition)controller.getTo();

		try {
			itemComposition.setSequence(calculateNextSequence((Item)controller.getMasterController().getTo()));
			itemComposition.setQuantity(1.0);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		checkSerializable(event);
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		checkSerializable(event);
	}
	
	private void checkSerializable(ControllerEvent event) throws ControllerListenerException {
		ItemCompositionController controller = (ItemCompositionController)event.getController();
		ItemComposition itemComposition = (ItemComposition)controller.getTo();
		Item item = (Item)controller.getMasterController().getTo();
		try {
			if (!item.getProduct().isManufactured() && itemComposition.getCompositionItem().getProduct().isSerializable()
					&& !itemComposition.getCompositionItem().isWildCard()) {
				throw new ControllerListenerException(AonUtil.getMessage(ITEM_COMPOSITION_SERIALIZABLE_ERROR));
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

	private	Integer calculateNextSequence(Item item) throws ManagerBeanException {
		IManagerBean itemCompositionBean = BeanManager.getManagerBean(ItemComposition.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(itemCompositionBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_ITEM_ID), item.getId());
		Projection projection = Projection.max(itemCompositionBean.getFieldName(IEntityAlias.ITEM_COMPOSITION_SEQUENCE));
		Object value = itemCompositionBean.getUniqueResult(projection, criteria);
		return (value != null) ? ((Integer)value) + 1 : 1;
	}

}