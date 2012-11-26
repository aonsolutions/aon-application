package com.code.aon.ui.document.controller;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import org.alfresco.webservice.util.Constants;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.document.AlfrescoCategory;
import com.code.aon.ql.Criteria;

public class AlfrescoCategoryGroupController extends AlfrescoCategoryController {

	private Map<String, AlfrescoCategory> categoryMap;
	
	private List<SelectItem> categoryTree;
	
	public AlfrescoCategoryGroupController() {
		this.categoryTree = new LinkedList<SelectItem>();
		this.categoryMap = new HashMap<String, AlfrescoCategory>();
	}

	public List<SelectItem> getCategories() throws ManagerBeanException {
		if ( categoryTree.isEmpty() ) {
			updateCategories();
		}
		return categoryTree;
	}	
	
	public void resetCategories() {
		this.categoryTree.clear();
		this.categoryMap.clear();
	}
	
	private SelectItem getSelectItem( AlfrescoCategory category ) {
		String name = category.getName();
		SelectItem item = null;
		List<AlfrescoCategory> categories = category.getCategories();
		if ( categories.isEmpty() ) {
			item = new SelectItem(category, name);
		} else {
			SelectItem[] selectItems = new SelectItem[category.getCategories().size()];
			for( int i = 0; i < selectItems.length; i++ ) {
				AlfrescoCategory subCategory = category.getCategories().get(i);
				selectItems[i] = getSelectItem(subCategory);
			}
			item = new SelectItemGroup(name, name,false, selectItems);					
		}
		categoryMap.put(category.getId().getUuid(), category);
		return item;
	}
	
	private void updateCategories() throws ManagerBeanException {
    	Criteria criteria = new Criteria();
    	criteria.addOrder(getManagerBean().getFieldName(Constants.PROP_NAME));
    	for( ITransferObject to : getManagerBean().getList(criteria) ) {
    		AlfrescoCategory category = (AlfrescoCategory) to;
    		categoryTree.add(getSelectItem(category));
    	}		
	}
	
	public AlfrescoCategory getCategory( String uuid ) {
		return categoryMap.get(uuid);
	}
	
}
