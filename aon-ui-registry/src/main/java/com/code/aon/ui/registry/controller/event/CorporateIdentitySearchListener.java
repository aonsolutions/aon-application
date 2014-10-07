package com.code.aon.ui.registry.controller.event;


import static com.code.aon.ui.registry.controller.CorporateIdentityController.DOMAIN_0;

import java.util.LinkedList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tag;
import com.code.aon.config.enumeration.TagType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.registry.Category;
import com.code.aon.ui.config.event.ScopeSearchListener;
import com.code.aon.ui.registry.controller.ICorporateIdentityController;
import com.esferalia.aon.entity.IEntityAlias;

public class CorporateIdentitySearchListener extends ScopeSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Category EMPTY_CATEGORY = new Category();
	
	private static final Tag EMPTY_TAG = new Tag();
	
	private Integer sizeFrom;
	
	private Integer sizeTo;
	
	private Category[] categories;
	
	private Tag[] tags;
	
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
	
	public Category[] getCategories() {
		if (ArrayUtils.isEmpty(categories)) {
			categories = new Category[]{EMPTY_CATEGORY};
		}
		return categories;
	}
	
	public void setCategories(Category[] categories) {
		this.categories = categories;
	}

	public int getCategoriesSize() {
		return ArrayUtils.getLength(categories);
	}
	
	public List<Integer> getCategoriesIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for( Category category : getCategories() ) {
			if ((category != null) && (category.getId() != null)) {
				ids.add(category.getId());
			}
		}
		return ids;
	}		
	
	public Tag[] getTags() {
		if (ArrayUtils.isEmpty(tags)) {
			tags = new Tag[]{EMPTY_TAG};
		}
		return tags;
	}

	public void setTags(Tag[] tags) {
		this.tags = tags;
	}

	public int getTagsSize() {
		return ArrayUtils.getLength(tags);
	}
	
	public List<Integer> getTagsIds() {
		List<Integer> ids = new LinkedList<Integer>();
		for( Tag tag : getTags() ) {
			if ((tag != null) && (tag.getId() != null)) {
				ids.add(tag.getId());
			}
		}
		return ids;
	}			
	
	public void onClear(ActionEvent event) throws ManagerBeanException {
		init();
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		super.init();
		setSizeFrom(null);
		setSizeTo(null);
		setCategories( new Category[]{EMPTY_CATEGORY} );
		setTags( new Tag[]{EMPTY_TAG} );
	}

	@Override
	protected void completeCriteria( Criteria criteria ) throws ManagerBeanException, ExpressionException {
		super.completeCriteria(criteria);
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
		this.categories = (Category[]) ArrayUtils.add(this.categories, EMPTY_CATEGORY);
	}
	
	public void onRemoveCategory(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.categories = (Category[]) ArrayUtils.remove(this.categories, index);
		if ( ArrayUtils.isEmpty(this.categories) ) {
			setCategories(new Category[]{EMPTY_CATEGORY});
		}
}		

	public void onAddTag(ActionEvent event) {
		this.tags = (Tag[]) ArrayUtils.add(this.tags, EMPTY_TAG);
	}
	
	public void onRemoveTag(ActionEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
		int index = Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("index"));		
		this.tags = (Tag[]) ArrayUtils.remove(this.tags, index);
		if ( ArrayUtils.isEmpty(this.tags) ) {
			setTags(new Tag[]{EMPTY_TAG});
		}
	}		
	 
    public List<SelectItem> getSelectableTags() throws ManagerBeanException {
    	List<SelectItem> tags = new LinkedList<SelectItem>();
    	IManagerBean tagBean = BeanManager.getManagerBean(Tag.class);
    	Criteria criteria = new Criteria();
    	ICorporateIdentityController cic = (ICorporateIdentityController) getController();
    	if ( cic.isServiconvenios() ) {
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_DOMAIN), DOMAIN_0);
    	}
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