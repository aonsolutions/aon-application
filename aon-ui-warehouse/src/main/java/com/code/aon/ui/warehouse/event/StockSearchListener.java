package com.code.aon.ui.warehouse.event;

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
import com.code.aon.product.ProductCategory;
import com.code.aon.product.enumeration.ProductStatus;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListenerEx;
import com.esferalia.aon.entity.IEntityAlias;

public class StockSearchListener extends ControllerSearchListenerEx {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Tag EMPTY_TAG = new Tag();
	
	private ProductStatus[] statuses;
	
	private ProductType[] types;
	
	private Tag[] tags;
	
	private ProductCategory category;
	
	public ProductStatus[] getStatuses() {
		return statuses;
	}

	public void setStatuses(ProductStatus[] statuses) {
		this.statuses = statuses;
	}

	public ProductType[] getTypes() {
		return types;
	}

	public void setTypes(ProductType[] types) {
		this.types = types;
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
	
	public ProductCategory getCategory() {
		return category;
	}

	public void setCategory(ProductCategory category) {
		this.category = category;
	}	

	@Override
	protected void init() throws ManagerBeanException {
		setStatuses( new ProductStatus[]{ProductStatus.ACTIVE} );
		setTypes( new ProductType[0] );
		setTags( new Tag[]{EMPTY_TAG} );
		setCategory( (ProductCategory) BeanManager.getManagerBean(ProductCategory.class).createNewTo() );
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		if (!ArrayUtils.isEmpty(getStatuses())) {
			String alias = getController().resolveAlias("Stock_item_product_status");
			addEnumToCriteria(criteria, alias, getStatuses());
		}
		if (!ArrayUtils.isEmpty(getTypes())) {
			String alias = getController().resolveAlias("Stock_item_product_type");
			addEnumToCriteria(criteria, alias, getTypes());
		}
		if ( getTagsSize() > 0 ) {
			addEnumToCriteria(criteria, "Stock.item.product.tags.tag.id", getTagsIds().toArray());	
		}
		if (getCategory() != null && getCategory().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("Stock_item_product_category<id"), getCategory().getId());
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
    	criteria.addEqualExpression(tagBean.getFieldName(IEntityAlias.TAG_TYPE), TagType.PRODUCT );
    	criteria.addOrder(tagBean.getFieldName(IEntityAlias.TAG_NAME));
    	for( ITransferObject to : tagBean.getList(criteria) ) {
    		Tag tag = (Tag) to;
    		SelectItem item = new SelectItem(tag, tag.getName());
    		tags.add(item);
    	}
    	return tags;
    }    

}