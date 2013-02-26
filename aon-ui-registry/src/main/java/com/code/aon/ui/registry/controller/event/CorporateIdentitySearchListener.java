package com.code.aon.ui.registry.controller.event;


import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tag;
import com.code.aon.config.enumeration.TagType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Category;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class CorporateIdentitySearchListener extends ControllerSearchListenerEx {

	private static final Category EMPTY_CATEGORY = new Category();
	
	private static final Tag EMPTY_TAG = new Tag();
	
	private Integer sizeFrom;
	
	private Integer sizeTo;
	
	private List<Category> categories;
	
	private List<Tag> tags;

	public Integer getSizeFrom() {
		return sizeFrom;
	}

	public void setSizeFrom(Integer sizeFrom) {
		this.sizeFrom = sizeFrom;
	}

	public Integer getSizeTo() {
		return sizeTo;
	}

	public void setSizeTo(Integer sizeTo) {
		this.sizeTo = sizeTo;
	}

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
	
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
		if (tags.isEmpty()) {
			tags.add(EMPTY_TAG);
		}				
	}

	public int getTagsSize() {
		return (tags != null) ? tags.size() : 0;
	}
	
	public List<Integer> getTagsIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for( Tag tag : tags ) {
			if ((tag != null) && (tag.getId() != null)) {
				ids.add(tag.getId());
			}
		}
		return ids;
	}		
	
	public void onClear(ActionEvent event) throws ManagerBeanException {
		reset();
	}
	
	protected void reset() throws ManagerBeanException {
		setSizeFrom(null);
		setSizeTo(null);
		setCategories( new LinkedList<Category>() );
		setTags( new LinkedList<Tag>() );	
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		reset();
	}

	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		if ( getSizeFrom() != null ) {
			criteria.addGreaterThanOrEqualExpression("RegistryAttachment.size", getSizeFrom() * 1024);
		}
		if ( getSizeTo() != null ) {
			criteria.addLessThanOrEqualExpression("RegistryAttachment.size", getSizeTo() * 1024);
		}
		if ( getCategoriesSize() > 0 ) {
			addEnumToCriteria(criteria, "RegistryAttachment.category<id", getCategoriesIds().toArray());	
		}
		if ( getTagsSize() > 0 ) {
			addEnumToCriteria(criteria, "RegistryAttachment.tags.tag.id", getTagsIds().toArray());	
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

	public void onAddTag(ActionEvent event) {
		getTags().add(EMPTY_TAG);
	}
	
	public void onRemoveTag(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		getTags().remove(index);
		if (getTags().isEmpty()) {
			getTags().add(EMPTY_TAG);
		}
	}		
	 
    public List<SelectItem> getSelectableTags() throws ManagerBeanException {
    	List<SelectItem> tags = new LinkedList<SelectItem>();
    	IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.RATTACH );
    	criteria.addOrder(tagBean.getFieldName(IEntityAlias.TAG_NAME));
    	for( ITransferObject to : tagBean.getList(criteria) ) {
    		Tag tag = (Tag) to;
    		SelectItem item = new SelectItem(tag, tag.getName());
    		tags.add(item);
    	}
    	return tags;
    }    
	
}