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

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.commercial.Target;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;


/**
 * Transfer Object that represents the Target Profile.
 * 
 * @author Consulting & Development. Aimar Tellitu - 24-sep-2008
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name = "target_profile")
public class TargetProfile extends ValueQuestionHolder {

	private static final long serialVersionUID = -2073655594982258016L;
	
	@ManyToOne (fetch=FetchType.EAGER)
    @JoinColumn( name="target", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_TARGET_PROFILE_TARGET")
	@Index(name = "IDX_TARGET_PROFILE_TARGET")
	private Target target;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_update", nullable = false)
    private Date lastUpdate;	
	
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TargetProfile o = (TargetProfile) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(getDate(), o.getDate())
				.append(getNumber(), o.getNumber())				
				.append(getQuestion(), o.getQuestion())
				.append(getText(), o.getText())				
				.append(lastUpdate, o.lastUpdate)
				.append(target, o.target)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(getDate())
			.append(getNumber())
			.append(getId())	
			.append(getQuestion())			
			.append(getText())		
			.append(lastUpdate)
			.append(target)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
	
}