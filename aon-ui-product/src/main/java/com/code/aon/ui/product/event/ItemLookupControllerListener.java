package com.code.aon.ui.product.event;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.RichLookupController;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemLookupControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    @Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
    	RichLookupController controller = (RichLookupController)event.getController();
    	Item item = (Item)controller.getTo();
		if (item.getProduct().getId() != null && item.getProduct().isSerializable() && item.getProduct().isLotable()) {
			try {
				IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_ID), item.getProduct().getId());
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_STATUS), ProductStatus.DISCONTINUED);
				criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_SERIAL_NUMBER), item.getSerialNumber());
				if (StringUtils.isNotEmpty(item.getDetail())) {
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL), item.getDetail());
				} else {
					criteria.addNullExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL));
				}
				if (StringUtils.isNotEmpty(item.getDetail2())) {
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL2), item.getDetail2());
				} else {
					criteria.addNullExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL2));
				}
				if (StringUtils.isNotEmpty(item.getDetail3())) {
					criteria.addEqualExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL3), item.getDetail3());
				} else {
					criteria.addNullExpression(itemBean.getFieldName(IEntityAlias.ITEM_DETAIL3));
				}
				List<ITransferObject> itemList = itemBean.getList(criteria);
				if (!itemList.isEmpty()) {
					item = (Item)itemList.get(0);
					item.setStatus(ProductStatus.ACTIVE);
					item = (Item)itemBean.update(item);

					controller.setLookupTo(item);
					controller.setNevv(false);
					throw new ManagerBeanException("Se ha Activado el Lote Número: " + item.getSerialNumber() + " que estaba Descatalogado.");
				}
			} catch (ManagerBeanException e) {
	            throw new ControllerListenerException(e.getMessage(), e);
			}
		}
	}

}