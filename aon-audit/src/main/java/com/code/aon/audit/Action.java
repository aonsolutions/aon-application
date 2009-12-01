package com.code.aon.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;


/**
 * Transfer Object that represents the user.
 * 
 * @author Consulting & Development. Aimar Tellitu - 28-ago-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "ACTION", schema = "AUDIT")
@SequenceGenerator(name="ACTION_GENERATOR", sequenceName="SEQ_ACTION",allocationSize=1)
@org.hibernate.annotations.Table( appliesTo = "ACTION", indexes = { @Index(name="IDX_ACTION", columnNames={"NAME","APPLICATION"})})
public class Action implements ITransferObject {

	private static final long serialVersionUID = -7135601793952520234L;

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "ACTION_GENERATOR")
	@Column(name = "ID", nullable = false)
    private Integer id;

	@Column(name = "NAME", nullable = false, length = 64)
	@Index(name="IDX_ACTION_NAME")
    private String name;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="APPLICATION", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_ACTION_APPLICATION")
	private Application application;

    /**
     * The empty constructor.
     */
    public Action() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public Action(Integer id) {
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
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the application.
	 * 
	 * @return the application
	 */
	public Application getApplication() {
		return application;
	}

	/**
	 * Sets the application.
	 * 
	 * @param application the new application
	 */
	public void setApplication(Application application) {
		this.application = application;
	}
 
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Action o = (Action) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.application, o.application)
				.append(this.name, o.name)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(application)
			.append(id)
			.append(name)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}