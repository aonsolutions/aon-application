package com.esferalia.aon.ui.pms.util;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.lang.math.NumberUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.config.util.AppParamUtil;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
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

	public static List<SelectItem> getServiceItems(Item roomItem) throws ManagerBeanException {
		if (roomItem != null && roomItem.getId() != null) {
			return getRoomServiceItems(roomItem);
		}
		PmsCollectionsController pmsCollections = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		return pmsCollections.getServiceItems();
	}

	public static List<SelectItem> getCurrentUserHotelRoomItems() throws ManagerBeanException {
		List<SelectItem> roomItems = new LinkedList<SelectItem>();
		PmsCollectionsController pmsCollections = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
		IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		criteria.addInExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), pmsCollections.getCurrentUserHotelIds());
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_ACTIVE), Boolean.TRUE);
		criteria.addOrder(roomBean.getFieldName(IEntityAlias.ROOM_ITEM_PRODUCT_CODE));
		ProjectionList projectionList = new ProjectionList(Projection.group(roomBean.getFieldName(IEntityAlias.ROOM_ITEM_ID)));
		for (Object obj : roomBean.getList(projectionList, criteria)) {
			Item item = (Item)itemBean.get((Integer)obj);
			SelectItem roomItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
			roomItems.add(roomItem);
		}
		return roomItems;
	}

	public static List<SelectItem> getHotelRoomItems(Hotel hotel) throws ManagerBeanException {
		List<SelectItem> roomItems = new LinkedList<SelectItem>();
		if (hotel != null && hotel.getId() != null) {
			List<Integer> items = new LinkedList<Integer>();
			IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), hotel.getId());
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_ACTIVE), Boolean.TRUE);
			criteria.addOrder(roomBean.getFieldName(IEntityAlias.ROOM_ITEM_PRODUCT_CODE));
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

	public static List<SelectItem> getRoomServiceItems(Item roomItem) throws ManagerBeanException {
		List<SelectItem> serviceItems = new LinkedList<SelectItem>();
		if (roomItem != null && roomItem.getId() != null) {
			int serviceCategory = NumberUtils.toInt(AppParamUtil.getValue(AppParam.PMS_SERVICE_CATEGORY));
			List<Integer> items = new LinkedList<Integer>();
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), serviceCategory);
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_STATUS), ProductStatus.ACTIVE);
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_COMPOSITION), Boolean.TRUE);
			criteria.addEqualExpression("Item.compositions.compositionItem.id", roomItem.getId());
			Projection prjId = Projection.property(itemBean.getFieldName(IEntityAlias.ITEM_ID));
			for (Object obj : itemBean.getList(new ProjectionList(prjId), criteria)) {
				items.add((Integer)obj);
			}

			criteria = new Criteria();
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CATEGORY_ID), serviceCategory);
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.ACTIVE);
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_STATUS), ProductStatus.ACTIVE);
			criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_COMPOSITION), Boolean.FALSE);
			for (Object obj : itemBean.getList(new ProjectionList(prjId), criteria)) {
				items.add((Integer)obj);
			}

			criteria = new Criteria();
			criteria.addInExpression(itemBean.getFieldName(IEntityAlias.ITEM_ID), items);
			criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_CODE));
			for (ITransferObject ito : itemBean.getList(criteria)) {
				Item item = (Item)ito;
				SelectItem serviceItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
				serviceItems.add(serviceItem);
			}
		}
		return serviceItems;
	}

	public static String getHotelName(int hotelId) throws ManagerBeanException {
		IManagerBean hotelBean = BeanManager.getManagerBean(Hotel.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(hotelBean.getFieldName(IEntityAlias.HOTEL_ID), hotelId);
		List<ITransferObject> hotelList = hotelBean.getList(criteria);
		return !hotelList.isEmpty() ? ((Hotel)hotelList.get(0)).getWorkPlace().getDescription() : null;
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
