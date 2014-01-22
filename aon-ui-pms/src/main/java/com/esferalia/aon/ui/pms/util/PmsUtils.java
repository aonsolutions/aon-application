package com.esferalia.aon.ui.pms.util;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddInfo;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.PmsCollectionsController;

public class PmsUtils implements IPmsConstants {

	public static List<SelectItem> getRoomItems(Hotel hotel) throws ManagerBeanException {
		if (hotel != null && hotel.getId() != null) {
			return getHotelRoomItems(hotel);
		}
		PmsCollectionsController pmsCollections = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		return pmsCollections.getRoomItems();
	}

	public static List<SelectItem> getHotelRoomItems(Hotel hotel) throws ManagerBeanException {
		List<SelectItem> roomItems = new LinkedList<SelectItem>();
		if (hotel != null && hotel.getId() != null) {
			List<Integer> items = new LinkedList<Integer>();
			IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), hotel.getId());
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_ACTIVE), Boolean.TRUE);
			criteria.addOrder(roomBean.getFieldName(IEntityAlias.ROOM_ITEM_PRODUCT_NAME));
			for (ITransferObject ito : roomBean.getList(criteria)) {
				Item item = ((Room)ito).getItem();
				if (!items.contains(item.getId())) {
					items.add(item.getId());
	
					SelectItem roomItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
					roomItems.add(roomItem);
				}
			}
		}
		return roomItems;
	}

	public static boolean isAgencyUser() throws ManagerBeanException {
		IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), REQUEST_USER);
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE), UserUtils.getInstance().getLoggedUser().getLogin());
		return rAddInfoBean.getCount(criteria) > 0;
	}

	public static List<Integer> getUserAgencies() throws ManagerBeanException {
		List<Integer> userAgencies = new LinkedList<Integer>();
		IManagerBean rAddInfoBean = BeanManager.getManagerBean(RegistryAddInfo.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_ATTRIBUTE), REQUEST_USER);
		criteria.addEqualExpression(rAddInfoBean.getFieldName(IEntityAlias.REGISTRY_ADD_INFO_VALUE), UserUtils.getInstance().getLoggedUser().getLogin());
		for (ITransferObject ito : rAddInfoBean.getList(criteria)) {
			RegistryAddInfo rAddInfo = (RegistryAddInfo)ito;
			userAgencies.add(rAddInfo.getRegistry().getId());
		}
		return userAgencies;
	}

}
