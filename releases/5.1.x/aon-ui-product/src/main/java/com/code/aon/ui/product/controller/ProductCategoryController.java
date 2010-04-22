package com.code.aon.ui.product.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.product.ProductCategory;
import com.code.aon.ui.form.LinesController;

public class ProductCategoryController extends LinesController {

	private ProductCategory category;

	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}

	@Override
	public void onSelect(ActionEvent arg0) {
		// TODO Auto-generated method stub		
		super.onSelect(arg0);
		ProductCategory p = new ProductCategory();
		p.setId(((ProductCategory)this.getTo()).getId());
		p.setName(((ProductCategory)this.getTo()).getName());
		this.category=p;
	}
	@Override
	public void onReset(ActionEvent arg0) {
		// TODO Auto-generated method stub
		this.setCategory(null);
		super.onReset(arg0);
	}
}