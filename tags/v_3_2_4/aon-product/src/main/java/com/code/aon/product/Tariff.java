package com.code.aon.product;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;

/**
 * Transfer Object that represents a tariff
 * 
 * @author Consulting & Development. Eugenio Castellano - 31-ene-2005
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="tariff")
public class Tariff implements ITransferObject {

	private static final long serialVersionUID = 324027393235801481L;

	/**
     * Primary key.
     */
    private Integer id;

    /**
     * Name of the tariff.
     */
    private String name;

    /**
     * Returns unique key.
     * 
     * @return unique key.
     */
    @Id
    @GeneratedValue
    @Column(nullable=false)
    public Integer getId() {
        return id;
    }

    /**
     * Assigns unique key.
     * 
     * @param id
     *            Unique key.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Returns the name of the tariff.
     * 
     * @return name of thhe tariff.
     */
    @Column(length=32, nullable=false)
    public String getName() {
        return name;
    }

    /**
     * Assign the name for this tariff.
     * 
     * @param name
     *            name of the tariff.
     */
    public void setName(String name) {
        this.name = name;
    }

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
    		return super.equals(obj);
		}
		if (obj instanceof Tariff) {
			Tariff o = (Tariff) obj;
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