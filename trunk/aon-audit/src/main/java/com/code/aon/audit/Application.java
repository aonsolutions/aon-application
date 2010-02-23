package com.code.aon.audit;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.Index;

import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;


/**
 * Transfer Object that represents the application.
 * 
 * @author Consulting & Development. Aimar Tellitu - 27-ago-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "application")
public class Application implements ITransferObject {

	private static final long serialVersionUID = 3375874695541393974L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;

	@Column(nullable = false, length = 64, unique = true)
	@Index(name="IDX_APPLICATION_NAME")
    private String name;
	
	@Column(name = "audit_level", nullable = false)
	private AuditLevel auditLevel;	
	
	@OneToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE,
			CascadeType.REMOVE }, mappedBy = "application")
	@org.hibernate.annotations.Cascade( {
			org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<Action> actions = new LinkedList<Action>();
	
    /**
     * The empty constructor.
     */
    public Application() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public Application(Integer id) {
        this.id = id;
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
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

	/**
	 * Gets the audit level.
	 * 
	 * @return the audit level
	 */
	public AuditLevel getAuditLevel() {
		return auditLevel;
	}

	/**
	 * Sets the audit level.
	 * 
	 * @param auditLevel the new audit level
	 */
	public void setAuditLevel(AuditLevel auditLevel) {
		this.auditLevel = auditLevel;
	}
    
	/**
	 * Gets the actions.
	 * 
	 * @return the actions
	 */
	public List<Action> getActions() {
		return this.actions;
	}
	
	/**
	 * Sets the actions.
	 * 
	 * @param addresses the actions
	 */
	public void setActions( List<Action> actions ) {
		this.actions = actions;
	}
 
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Application o = (Application) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.auditLevel, o.auditLevel)
				.append(this.name, o.name)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(auditLevel)
			.append(id)
			.append(name)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
    
}