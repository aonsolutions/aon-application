package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a brand.
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 * 
 */
@Entity
@Table(name="brand")
public class Brand implements ITransferObject {

	private static final long serialVersionUID = -9063450093952827806L;

	/**
     * Unique key.
     */
	
    private Integer id;

    /**
     * Brand's name.
     */
    private String name;

    /**
     * Returns the unique key.
     * 
     * @return Unique key.
     */
    @Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }

    /**
     * Asigns the unique key.
     * 
     * @param id
     *            Unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Retrun this brand's name.
     * 
     * @return brand's name.
     */
    @Column(length=64, nullable=false)
    public String getName() {
        return name;
    }

    /**
     * Asigns brand's name.
     * 
     * @param name
     *            brand's name.
     */
    public void setName(String name) {
        this.name = name;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Brand) {
			Brand o = (Brand) obj;
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