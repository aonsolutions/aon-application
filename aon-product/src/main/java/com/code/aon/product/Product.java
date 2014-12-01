package com.code.aon.product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.annotations.Heritable;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.ProductDB;

@Entity
@Table(name="product", uniqueConstraints = @UniqueConstraint(columnNames="code"))
@Heritable
public class Product extends ProductDB implements IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Set<Item> items = new HashSet<Item>();
	private Set<ProductTag> tags = new HashSet<ProductTag>();

	@Transient
	public String getShortName() {
		return (getName().length() > 16) ? StringUtils.substring(getName(), 0, 16) : getName();
	}

	@Transient
	public boolean isWithholding() {
		return (getRetention() != null && getRetention().getId() != null);
	}

    @OneToMany(mappedBy="product")
	public Set<Item> getItems() {
		return this.items;
	}
	
	public void setItems( Set<Item> items ) {
		this.items = items;
	}
	
	@Transient
	public void addItems(Item item) {
		item.setProduct( this );
		this.items.add( item );
	}

	@OneToMany(mappedBy = "product", cascade={CascadeType.REMOVE})
	public Set<ProductTag> getTags() {
		return this.tags;
	}

	public void setTags( Set<ProductTag> tags ) {
		this.tags = tags;
	}

	@Transient
	public String getTagList() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProductTag.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.PRODUCT_TAG_PRODUCT_ID);
		criteria.addEqualExpression(alias, getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (!list.isEmpty()) {
			Set<String> tags = new TreeSet<String>();
			for(ITransferObject to : list) {
				ProductTag pt = (ProductTag) to;
				tags.add(pt.getTag().getName());
			}
			return StringUtils.join(tags, "| ");
		}
		return null;
	}	

	@Transient
	public String getFullName() throws ManagerBeanException {
		String tags = getTagList();
		if (! StringUtils.isEmpty(tags)) {
			return getName() + " (" + tags + ")";
		}
		return getName();
	}	
	
}