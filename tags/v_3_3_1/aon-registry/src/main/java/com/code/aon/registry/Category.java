package com.code.aon.registry;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents the Category.
 * 
 * @author Consulting & Development. Aimar Tellitu - 03-ene-2006
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="category")
public class Category implements ITransferObject {

	private static final long serialVersionUID = -4682457922593197659L;

	/** The id. */
    private Integer id;

    /** The name. */
    private String name;

    /**
     * The empty constructor.
     */
    public Category() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public Category(Integer id) {
        this.id = id;
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
     * Gets the name.
     * 
     * @return the name
     */
    @Column(length=32, nullable = false)
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

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Category) {
			Category o = (Category) obj;
			if (o.getId() == null && id == null) {
				return super.equals(obj);	
			}
			if (ObjectUtils.equals(getId(), o.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
    public int hashCode() {
        return id != null ? this.getClass().hashCode() + id.hashCode() : super.hashCode();
    }

}