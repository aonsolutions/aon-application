package com.code.aon.ui.product.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.richfaces.lookup.inputText.JoinProperty;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryItem;
import com.code.aon.registry.enumeration.RegistryMode;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemLookup extends RichLookupBean implements IItemConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ItemLookup.class);

	public ProductCollectionsController getProductCollectionsController() {
		return (ProductCollectionsController)AonUtil.getRegisteredBean(PRODUCT_COLLECTIONS);
	}

	public ProductController getProductController() {
		return (ProductController)AonUtil.getRegisteredBean(PRODUCT);
	}

	public void onResetItem(ActionEvent event) throws ManagerBeanException {
		Item item = (Item)getTo();
		getProductController().updateItemData(item);
	}

	public void onSerializableChanged(ValueChangeEvent event) {
		Product product = ((Item)getTo()).getProduct();
		if (event.getNewValue() != null && (Boolean)event.getNewValue()) {
			product.setInventoriable(true);
		}
	}

	public void onPurchasePriceChanged(ValueChangeEvent event) {
		getProductController().onPurchasePriceChanged((Item)this.getTo(), event.getNewValue());
	}

	public void onProfitChanged(ValueChangeEvent event) {
		getProductController().onProfitChanged((Item)this.getTo(), event.getNewValue());
	}

	public void onPriceChanged(ValueChangeEvent event) {
		getProductController().onPriceChanged((Item)this.getTo(), event.getNewValue());
	}

	public void onSalesProfitChanged(ValueChangeEvent event) {
		getProductController().onSalesProfitChanged((Item)this.getTo(), event.getNewValue());
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		getProductController().onSalesPriceChanged((Item)this.getTo(), event.getNewValue());
	}

	@Override
	protected void updateCriteria(List<JoinProperty> joinProperties) throws ManagerBeanException {
		super.updateCriteria(joinProperties);
		if (!joinProperties.isEmpty()) {
			Object code = getCode(joinProperties);
			if (hasSuppliers(code)) {
				addSupplierSubQuery(code);
			}			
		}
	}

	private Object getCode(List<JoinProperty> joinProperties) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		JoinProperty jp = joinProperties.get(0);
		return jp.getValue(ctx);
	}
	
	private boolean hasSuppliers(Object code) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(RegistryItem.class);			
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_CODE), code);
		return bean.getCount(criteria) > 0;
	}

	private void addSupplierSubQuery(Object code) {
		try {	
			IManagerBean bean = BeanManager.getManagerBean(RegistryItem.class);			
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_TYPE), RegistryMode.SUPPLIER);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.REGISTRY_ITEM_CODE), code);
			String idAlias = bean.getFieldName(IEntityAlias.REGISTRY_ITEM_ITEM_ID);
			ProjectionList pl = new ProjectionList(Projection.property(idAlias));
			Expression subExp = ExpressionUtilities.getSubQueryExpression(RegistryItem.class, criteria, pl);
			Expression inExp = ExpressionUtilities.getInExpression(getController().getFieldName(IEntityAlias.ITEM_ID), subExp);
			getController().getCriteria().addOrExpression(inExp);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

}
