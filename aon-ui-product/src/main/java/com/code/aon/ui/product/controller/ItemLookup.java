package com.code.aon.ui.product.controller;

import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.richfaces.lookup.inputText.JoinProperty;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.product.Item;
import com.code.aon.product.ItemSupplier;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class ItemLookup extends RichLookupBean {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ItemLookup.class);

	private ItemPricesManager pricesManager;
	
	public ItemPricesManager getPricesManager() {
		if (pricesManager == null) {
			pricesManager = new ItemPricesManager();
		}
		return pricesManager;
	}

	public void onPurchasePriceChanged(ValueChangeEvent event) {
		getPricesManager().onPurchasePriceChanged((Item)this.getTo(), event.getNewValue());
	}

	public void onProfitChanged(ValueChangeEvent event) {
		getPricesManager().onProfitChanged((Item)this.getTo(), event.getNewValue());
	}

	public void onPriceChanged(ValueChangeEvent event) {
		getPricesManager().onPriceChanged((Item)this.getTo(), event.getNewValue());
	}

	public void onSalesPriceChanged(ValueChangeEvent event) {
		getPricesManager().onSalesPriceChanged((Item)this.getTo(), event.getNewValue());
	}

	@Override
	protected void updateCriteria(List<JoinProperty> joinProperties) throws ManagerBeanException {
		super.updateCriteria(joinProperties);
		if (! joinProperties.isEmpty()) {
			Object code = getCode(joinProperties);
			if ( hasSuppliers(code) ) {
				addSuplierSudQuery(code);
			}			
		}
	}

	private Object getCode(List<JoinProperty> joinProperties) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		JoinProperty jp = joinProperties.get(0);
		return jp.getValue(ctx);
	}
	
	
	private boolean hasSuppliers( Object code ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ItemSupplier.class);			
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_CODE), code);
		return bean.getCount(criteria) > 0;
	}

	public void addSuplierSudQuery( Object code ) {
		try {	
			IManagerBean bean = BeanManager.getManagerBean(ItemSupplier.class);			
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_CODE), code);
			String idAlias = bean.getFieldName(IEntityAlias.ITEM_SUPPLIER_ITEM_ID);
			ProjectionList pl = new ProjectionList( Projection.property(idAlias) );
			Expression subExp = ExpressionUtilities.getSubQueryExpression(ItemSupplier.class, criteria, pl);
			Expression inExp = ExpressionUtilities.getInExpression(getController().getFieldName(IEntityAlias.ITEM_ID), subExp);
			getController().getCriteria().addOrExpression(inExp);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}		
	}

	
}