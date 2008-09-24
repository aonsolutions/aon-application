package com.code.aon.marketing;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;

import com.code.aon.commercial.Target;


/**
 * Transfer Object that represents the Target Profile.
 * 
 * @author Consulting & Development. Aimar Tellitu - 24-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "target_profile")
public class TargetProfile extends ValueHolder {

	private static final long serialVersionUID = -2073655594982258016L;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="target", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_TARGET_PROFILE_TARGET")
	private Target target;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_update", nullable = false)
    private Date lastUpdate;	
	
    /**
     * The empty constructor.
     */
    public TargetProfile() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public TargetProfile(Integer id) {
    	super(id);
    }

    /**
     * Gets the last update.
     * 
     * @return the last update
     */
    public Date getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * Sets the last update.
	 * 
	 * @param lastUpdate the new last update
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Gets the target.
	 * 
	 * @return the target
	 */
	public Target getTarget() {
		return target;
	}

	/**
	 * Sets the target.
	 * 
	 * @param target the new target
	 */
	public void setTarget(Target target) {
		this.target = target;
	}
	
}