package com.esferalia.aon.ui.pms.event;

import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Allotment;
import com.esferalia.aon.pms.AllotmentItem;
import com.esferalia.aon.ui.pms.controller.AllotmentController;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;

public class AllotmentControllerListener extends ControllerAdapter implements IPmsConstants {

	private Item[] items;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		controller.setItems(null);
		controller.setItem(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		try {
			obtainAllotmentItems((Allotment)controller.getTo());
			controller.setItems(items);
			controller.setItem(null);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		Allotment allotment = (Allotment)controller.getTo();
		if (allotment.getEndDate().before(allotment.getStartDate())) {
			throw new ControllerListenerException("Las fechas de Inicio y Fin del Periodo son incorrectas.");
		}

		items = (Item[])ArrayUtils.removeElement(controller.getItems(), null);
		try {
			if (ArrayUtils.getLength(items) == 0) {
				List<SelectItem> hotelRoomItems = controller.getHotelRoomItems();
				for (int i=0; i<hotelRoomItems.size(); i++) {
					items = ((Item[])ArrayUtils.add(items, hotelRoomItems.get(i).getValue()));
				}
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		verifyAllotmentOverlap(allotment);
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		try {
			insertAllotmentItems((Allotment)controller.getTo());
			controller.setItems(items);
			controller.setItem(null);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		Allotment allotment = (Allotment)controller.getTo();
		if (allotment.getEndDate().before(allotment.getStartDate())) {
			throw new ControllerListenerException("Las fechas de Inicio y Fin del Periodo son incorrectas.");
		}

		items = (Item[])ArrayUtils.removeElement(controller.getItems(), null);
		try {
			if (ArrayUtils.getLength(items) == 0) {
				List<SelectItem> hotelRoomItems = controller.getHotelRoomItems();
				for (int i=0; i<hotelRoomItems.size(); i++) {
					items = ((Item[])ArrayUtils.add(items, hotelRoomItems.get(i).getValue()));
				}
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
		verifyAllotmentOverlap(allotment);
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		AllotmentController controller = (AllotmentController)event.getController();
		try {
			removeAllotmentItems((Allotment)controller.getTo());
			insertAllotmentItems((Allotment)controller.getTo());
			controller.setItems(items);
			controller.setItem(null);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		Allotment allotment = (Allotment)event.getController().getTo();
		try {
			removeAllotmentItems(allotment);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private void obtainAllotmentItems(Allotment allotment) throws ManagerBeanException {
		items = null;
		IManagerBean allotmentItemBean = BeanManager.getManagerBean(AllotmentItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ALLOTMENT_ID), allotment.getId());
		for (ITransferObject ito : allotmentItemBean.getList(criteria)) {
			AllotmentItem allotmentItem = (AllotmentItem)ito;
			items = (Item[])ArrayUtils.add(items, allotmentItem.getItem());
		}
	}

	private void verifyAllotmentOverlap(Allotment allotment) throws ControllerListenerException {
		try {
			IManagerBean allotmentItemBean = BeanManager.getManagerBean(AllotmentItem.class);
			for (Item item : items) {
				Criteria criteria = new Criteria();
				if (allotment.getId() != null) {
					criteria.addNotEqualExpression(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ALLOTMENT_ID), allotment.getId());
				}
				criteria.addEqualExpression(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ALLOTMENT_HOTEL_ID), allotment.getHotel().getId());
				criteria.addEqualExpression(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ALLOTMENT_AGENCY_ID), allotment.getAgency().getId());
				criteria.addLessThanOrEqualExpression(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ALLOTMENT_START_DATE), allotment.getEndDate());
				criteria.addGreaterThanOrEqualExpression(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ALLOTMENT_END_DATE), allotment.getStartDate());
				criteria.addEqualExpression(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ITEM_ID), item.getId());
				if (allotmentItemBean.getCount(criteria) > 0) {
					items = (Item[])ArrayUtils.removeElement(items, item);
				}
			}

			if (ArrayUtils.getLength(items) == 0) {
				throw new ControllerListenerException("Ya existen Cupos definidos para las Habitaciones en ese Periodo.");
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	private void insertAllotmentItems(Allotment allotment) throws ManagerBeanException {
		IManagerBean allotmentItemBean = BeanManager.getManagerBean(AllotmentItem.class);
		for (Item item : items) {
			AllotmentItem allotmentItem = new AllotmentItem();
			allotmentItem.setAllotment(allotment);
			allotmentItem.setItem(item);
			allotmentItemBean.insert(allotmentItem);
		}
	}

	private void removeAllotmentItems(Allotment allotment) throws ManagerBeanException {
		IManagerBean allotmentItemBean = BeanManager.getManagerBean(AllotmentItem.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(allotmentItemBean.getFieldName(IEntityAlias.ALLOTMENT_ITEM_ALLOTMENT_ID), allotment.getId());
		for (ITransferObject ito : allotmentItemBean.getList(criteria)) {
			allotmentItemBean.remove(ito);
		}
	}

}