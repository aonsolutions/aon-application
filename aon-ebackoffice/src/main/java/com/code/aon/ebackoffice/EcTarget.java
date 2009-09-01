package com.code.aon.ebackoffice;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.commercial.Target;
import com.code.aon.common.ITransferObject;


/**
 * Transfer Object that represents a eCommerce Target.
 * 
 * @author Esferalia Networks. Ekain Agirrezabal - 31/08/2009
 */
@Entity
@Table(name="ec_target")
public class EcTarget implements ITransferObject {
	
	/** The id. */
	private Integer id;
	/** The target. */
	private Target target;
	/** The login. */
	private String login;
	/** The password. */
	private String password;
	/** The type. */
	private Integer type;
	/** The last_access. */
	private Date lastAccess;
	
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
	public void setId(Integer id) {
		this.id = id;
	}
	
	/**
	 * Gets the target.
	 * 
	 * @return the name
	 */
	@OneToOne (fetch=FetchType.EAGER)
	@JoinColumn(name="target", nullable=false)
	@ForeignKey(name = "EC_TARGET_FK")
	@Index(name = "TARGET")
	public Target getTarget() {
		return target;
	}
	public void setTarget(Target target) {
		this.target = target;
	}
	
	/**
	 * Gets the login.
	 * 
	 * @return the login
	 */
	@Column(length=32, nullable = false)
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	/**
	 * Gets the password.
	 * 
	 * @return the password
	 */
	@Column(length=32, nullable = false)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	@Column(length=32, nullable = false)
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	
	/**
	 * Gets the last access.
	 * 
	 * @return the last access
	 */
	@Column(name="last_access", nullable = false)
	@Temporal(TemporalType.DATE)
	public Date getLastAccess() {
		return lastAccess;
	}
	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EcTarget o = (EcTarget) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.target, o.target)
				.append(this.login, o.login)				
				.append(this.password, o.password)				
				.append(this.type, o.type)				
				.append(this.lastAccess, o.lastAccess)				
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)			
			.append(target)
			.append(login)				
			.append(password)				
			.append(type)				
			.append(lastAccess)
			.toHashCode();
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
	
}