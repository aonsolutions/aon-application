package com.code.aon.commercial;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents the Commercial Activity.
 * 
 * @author Consulting & Development. Aimar Tellitu - 18-jul-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="commercial_activity")
public class CommercialActivity implements ITransferObject {

	private static final long serialVersionUID = 7866038967575102415L;

	/** The id. */
    private Integer id;

    /** The name. */
    private String name;

    /**
     * The empty constructor.
     */
    public CommercialActivity() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public CommercialActivity(Integer id) {
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
		if (obj instanceof CommercialActivity) {
			CommercialActivity o = (CommercialActivity) obj;
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