package com.code.aon.marketing;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Type;

import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents the template.
 * 
 * @author Consulting & Development. Aimar Tellitu - 22-jul-2011
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "mk_template")
public class Template implements ITransferObject {

	private static final long serialVersionUID = 4364711903758593403L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	@Column(nullable = false, length = 64)
    private String name;

	@Lob
	@Type(type="stringClob")
	@Column(nullable = false)
    private String data;	

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
    private Date creationDate;	
	
	@Column(nullable = false)
	private boolean active;
	
    /**
     * The empty constructor.
     */
    public Template() {
    	this.active = true;
    }

    /**
     * Gets the id.
     * 
     * @return the id
     */
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
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * Gets the creation date.
	 * 
	 * @return the creation date
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * Sets the creation date.
	 * 
	 * @param creationDate the new creation date
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	
	/**
	 * Checks if is active.
	 * 
	 * @return true, if is active
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Sets the active.
	 * 
	 * @param active the new active
	 */
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Template o = (Template) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.creationDate, o.creationDate)
				.append(this.data, o.data)				
				.append(this.name, o.name)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(creationDate)
			.append(data)
			.append(id)	
			.append(name)			
			.toHashCode();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).
			append("active", active).
			append("creationDate", creationDate).
			append("data", StringUtils.abbreviate(data, 64)).			
			append("id", id).
			append("name", name).
			toString();
	}
	
}