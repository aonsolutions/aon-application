package com.code.aon.ui.warehouse.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.Product;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class StockSearchListener extends ControllerSearchListener {

	private Item item;
	
	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	@Override
	protected void init() throws ManagerBeanException {
		Item item = new Item();
		item.setProduct(new Product() );
		setItem( item );
	}
	
	@Override
	protected void completeCriteria() throws ManagerBeanException, ExpressionException {
		Criteria criteria = getController().getCriteria();
		if ( (getItem() != null) && (getItem().getId() != null) ) {
			String field = getController().getFieldName(IWarehouseAlias.STOCK_ITEM_ID);
			criteria.addEqualExpression(field, getItem().getId());			
		}			
	}

}