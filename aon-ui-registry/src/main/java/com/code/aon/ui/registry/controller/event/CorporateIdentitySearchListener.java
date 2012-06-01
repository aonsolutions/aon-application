package com.code.aon.ui.registry.controller.event;


import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Category;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;

public class CorporateIdentitySearchListener extends ControllerSearchListenerEx {

	private static final Category EMPTY_CATEGORY = new Category();
	
	private List<Category> categories;

	public List<Category> getCategories() {
		return categories;
	}
	
	public void setCategories(List<Category> categories) {
		this.categories = categories;
		if (categories.isEmpty()) {
			categories.add(EMPTY_CATEGORY);
		}		
	}

	public int getCategoriesSize() {
		return (categories != null) ? categories.size() : 0;
	}
	
	public List<Integer> getCategoriesIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for( Category category : categories ) {
			if ((category != null) && (category.getId() != null)) {
				ids.add(category.getId());
			}
		}
		return ids;
	}		

	public void onClear(ActionEvent event) {
		reset();
	}
	
	private void reset() {
		setCategories( new LinkedList<Category>() );
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		reset();
	}

	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ( getCategoriesSize() > 0 ) {
			addEnumToCriteria(criteria, "RegistryAttachment.category<id", getCategoriesIds().toArray());	
		}
	}
	
	public void onAddCategory(ActionEvent event) {
		getCategories().add(EMPTY_CATEGORY);
	}
	
	public void onRemoveCategory(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		getCategories().remove(index);
		if (getCategories().isEmpty()) {
			getCategories().add(EMPTY_CATEGORY);
		}
	}		
	
}