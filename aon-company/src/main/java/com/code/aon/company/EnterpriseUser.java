package com.code.aon.company;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;

@Entity
@Table(name="user")
public class EnterpriseUser implements ITransferObject, IRegistry {
	
	private static final long serialVersionUID = -151638379810317997L;

	private Integer id;
	private String name;
	private String login;
	private String password;
	private boolean active;
    private Enterprise enterprise; 	
    private Registry registry; 	
	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@Column(length=64, nullable=false)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(length=16,nullable=false)
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}

	@Column(length = 128)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(nullable=false)
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@OneToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false )
    @ForeignKey(name = "FK_USER_ENTERPRISE")
    @Index(name = "IDX_USER_ENTERPRISE")    
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}	

	@Override
	@OneToOne
    @JoinColumn(name="registry", updatable = false )
    @ForeignKey(name = "FK_USER_REGISTRY")
    @Index(name = "IDX_USER_REGISTRY")    
	public Registry getRegistry() {
		return this.registry;
	}

	@Override
	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final EnterpriseUser o = (EnterpriseUser) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.enterprise, o.enterprise)				
				.append(this.login, o.login)				
				.append(this.name, o.name)			
				.append(this.password, o.password)
				.append(this.registry, o.registry)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(active)
			.append(enterprise)
			.append(id)		
			.append(login)		
			.append(name)		
			.append(password)
			.append(registry)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}