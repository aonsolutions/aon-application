package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Item;

@Entity
@Table(name="item_alternative")
public class ItemAlternative implements ITransferObject{
	
	private static final long serialVersionUID = -6415420632921077499L;

	private Integer id;
	private Item item;
	private Item alternativeItem;
	private Integer priority;

	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer primaryKey) {
		this.id = primaryKey;
	}
	
	@ManyToOne
	@JoinColumn(name="item",nullable=false)
    @ForeignKey(name = "FK_ITEM_ALTENATIVE_ITEM")
    @Index(name = "IDX_ITEM_ALTENATIVE_ITEM")
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	
    @ManyToOne
    @JoinColumn(name="alternative_item")
    @ForeignKey(name = "FK_ITEM_ALTENATIVE_ALTERNATIVE")
    @Index(name = "IDX_ITEM_ALTENATIVE_ALTERNATIVE")
    public Item getAlternativeItem() {
        return alternativeItem;
    }

    public void setAlternativeItem(Item alternativeItem) {
        this.alternativeItem = alternativeItem;
    }

	public Integer getPriority() {
		return priority;
	}
	public void setPriority(Integer priority) {
		this.priority = priority;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ItemAlternative o = (ItemAlternative) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.item, o.item)
				.append(this.alternativeItem, o.alternativeItem)
				.append(this.priority, o.priority)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)	
			.append(item)			
			.append(alternativeItem)
			.append(priority)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}