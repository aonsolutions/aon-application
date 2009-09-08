package com.code.aon.ebackoffice;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.commercial.Target;
import com.code.aon.common.ITransferObject;
import com.code.aon.ebackoffice.enumeration.TargetType;


/**
 * Transfer Object that represents a eCommerce Target.
 * 
 * @author Esferalia Networks. Ekain Aguirrezabal - 31/08/2009
 */
@Entity
@Table(name="ec_target")
public class Ectarget implements ITransferObject {
	
	/** The id. */
	private Integer id;
	/** The target. */
	private Target target;
	/** The login. */
	private String login;
	/** The password. */
	private String password;
	/** The type. */
	private TargetType type;
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
	@ForeignKey(name = "FK_ECTARGET_TARGET")
	@Index(name = "IDX_ECTARGET_TARGET")
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
	@Column(length=48, nullable = false)
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
	@Column(name = "type", length = 1)
	public TargetType getType() {
		return type;
	}
	public void setType(TargetType type) {
		this.type = type;
	}
	
	/**
	 * Gets the last access.
	 * 
	 * @return the last access
	 */
	@Column(name="last_access")
	@Temporal(TemporalType.DATE)
	public Date getLastAccess() {
		return lastAccess;
	}
	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}
	
	
	
}