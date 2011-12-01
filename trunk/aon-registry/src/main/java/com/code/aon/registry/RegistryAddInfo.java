package com.code.aon.registry;

import java.util.Date;

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

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

/**
 * Transfer Object that represents a Registry Segment.
 * 
 * @author Consulting & Development. Aimar Tellitu - 21-jul-2008 
 */
@Entity
@Table(name="raddinfo")
public class RegistryAddInfo implements ITransferObject {
	
	private static final long serialVersionUID = -2704634950055889788L;

	/** The id. */
	private Integer id;
	
	/** The registry. */
	private Registry registry;
	
	/** The attribute. */
	private String attribute;

	/** The value. */
	private String value;

    /** The value date. */
    private Date valueDate;

	/**
	 * The Constructor. Sets TODAY to valueDate.
	 */
	public RegistryAddInfo() {
		this.valueDate = new Date();
	}

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
	 * Gets the registry.
	 * 
	 * @return the registry
	 */
	@ManyToOne (fetch=FetchType.EAGER)
	@JoinColumn( name="registry", nullable=false , updatable=false)
	@ForeignKey(name = "FK_REGISTRY_ADDINFO_REGISTRY")
	public Registry getRegistry() {
		return registry;
	}

	/**
	 * Sets the registry.
	 * 
	 * @param registry the registry
	 */
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	/**
	 * Gets the attribute.
	 * 
	 * @return the attribute
	 */
	@Column(nullable=false)
	public String getAttribute() {
		return attribute;
	}

	/**
	 * Sets the attribute.
	 * 
	 * @param attribute the attribute
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	@Column(nullable=false)
	public String getValue() {
		return value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value the value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Gets the valueDate.
	 * 
	 * @return the valueDate
	 */
	@Column(name="value_date", nullable=false)
	public Date getValueDate() {
		return valueDate;
	}

	/**
	 * Sets the valueDate.
	 * 
	 * @param valueDate the valueDate
	 */
	public void setValueDate(Date valueDate) {
		this.valueDate = valueDate;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final RegistryAddInfo o = (RegistryAddInfo) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()			
				.append(this.valueDate, o.valueDate)
				.append(this.value, o.value)
				.append(this.attribute, o.attribute)
				.append(this.registry, o.registry)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)			
			.append(valueDate)
			.append(value)
			.append(attribute)
			.append(registry)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}