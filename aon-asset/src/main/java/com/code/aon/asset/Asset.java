package com.code.aon.asset;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;


@Entity
@Table(name = "asset")
public class Asset implements ITransferObject {

	private static final long serialVersionUID = -8397672466944000748L;

	/**
	 * The ID of this asset
	 */
	private Integer id;

	/**
	 * The name of the asset.
	 */
	private String name;
	
	/**
	 * The description of the asset.
	 */
	private String description;
	
	/**
	 * Gets the ID of this asset.
	 * 
	 * @return The ID of this asset
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="id", nullable=false, length=4)
	public Integer getId() {
		return id;
	}

	/**
	 * Sets the ID of this asset.
	 * 
	 * @param id
	 *            The ID of this asset.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the name of this asset.
	 * 
	 * @return The name of this asset
	 */
	@Column(name="name", nullable = false, length = 10)
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this asset.
	 * 
	 * @param description
	 *            The name of this asset.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Gets the description of this asset.
	 * 
	 * @return The description of this asset
	 */
	@Column(name="description", nullable = false, length = 64)
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description of this asset.
	 * 
	 * @param description
	 *            The description of this asset.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Asset o = (Asset) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.name,o.name)
				.append(this.description,o.description)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(name)
			.append(description)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}