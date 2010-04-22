package com.code.aon.commercial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;

import com.code.aon.commercial.enumeration.TargetItemStatus;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.product.Item;

/**
 * Transfer Object that represents a Target Item.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-jul-2008 
 */
@Entity
@Table(name="target_item")
public class TargetItem implements ITransferObject {
	
	private static final long serialVersionUID = 1899215254008158426L;

	/** The id. */
	private Integer id;
	
	/** The target. */
	private Target target;
	
	/** The item. */
	private Item item;
	
	/** The status. */
	private TargetItemStatus status;

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the target.
	 * 
	 * @return the target
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="target", nullable=false , updatable=false)
	@ForeignKey(name = "FK_TARGET_ITEM_TARGET")
	public Target getTarget() {
		return target;
	}

	/**
	 * Sets the target.
	 * 
	 * @param target the target
	 */
	public void setTarget(Target target) {
		this.target = target;
	}

	/**
	 * Gets the item.
	 * 
	 * @return the item
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="item", nullable=false )
	@ForeignKey(name = "FK_TARGET_ITEM_ITEM")
	public Item getItem() {
		return item;
	}

	/**
	 * Sets the item.
	 * 
	 * @param item the item
	 */
	public void setItem(Item item) {
		this.item = item;
	}

	/**
	 * Gets the status.
	 * 
	 * @return the status
	 */
	@Column(nullable=false)
	public TargetItemStatus getStatus() {
		return status;
	}

	/**
	 * Sets the status.
	 * 
	 * @param status the new status
	 */
	public void setStatus(TargetItemStatus status) {
		this.status = status;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TargetItem o = (TargetItem) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.item, o.item)				
				.append(this.status, o.status)
				.append(this.target, o.target)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)			
			.append(item)
			.append(status)
			.append(target)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}