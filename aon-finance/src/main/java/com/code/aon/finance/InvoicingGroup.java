package com.code.aon.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.registry.Registry;

/**
 * The Class InvoicingGroup.
 */
@Entity
@Table(name="invoicing_group")
public class InvoicingGroup implements ITransferObject {

	
	private static final long serialVersionUID = -7948345500064583985L;

	/** The id. */
	private Integer id;
	
	/** The parent. */
	private Registry parent;
	

	/**
	 * Gets the id.
	 * 
	 * @return the id
	 */
	@Id
	@Column(nullable=false)
	@GeneratedValue
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
	 * Gets the parent.
	 * 
	 * @return the parent
	 */
    @ManyToOne
    @JoinColumn(name="parent", nullable = false)
	public Registry getParent() {
		return parent;
	}

	/**
	 * Sets the parent.
	 * 
	 * @param parent the parent
	 */
	public void setParent(Registry parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return super.equals(obj);
		}
		if (obj instanceof InvoicingGroup) {
			InvoicingGroup o = (InvoicingGroup) obj;
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
	
}