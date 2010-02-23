package com.code.aon.audit;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.config.User;


/**
 * Transfer Object that represents the Favorite Options.
 * 
 * @author Consulting & Development. Aimar Tellitu - 15-feb-2010
 * @since 1.0
 * @version 1.0
 */
@Entity
@Table(name="action_denied")
public class ActionDenied implements ITransferObject {

	private static final long serialVersionUID = 3797118579358664083L;

	@Id
	@GeneratedValue
	@Column(nullable = false)
    private Integer id;
    
	@ManyToOne
    @JoinColumn( name="user_id", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_ACTION_DENIED_USER_ID")
	@Index(name = "IDX_ACTION_DENIED_USER_ID")
	private User user;

	@ManyToOne
    @JoinColumn( name="action_id", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_ACTION_DENIED_ACTION_ID")
	@Index(name = "IDX_ACTION_DENIED_ACTION_ID")
	private Action action;

    /**
     * The empty constructor.
     */
    public ActionDenied() {
    }

    /**
     * The constructor using the id.
     * 
     * @param id the id
     */
    public ActionDenied(Integer id) {
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
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 * 
	 * @param user the new user
	 */
	public void setUser(User user) {
		this.user = user;
	}    
	
	/**
	 * Gets the action.
	 * 
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * Sets the action.
	 * 
	 * @param action the new action
	 */
	public void setAction(Action action) {
		this.action = action;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final ActionDenied o = (ActionDenied) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.action, o.action)
				.append(this.user, o.user)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(action)
			.append(id)
			.append(user)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}	
}
