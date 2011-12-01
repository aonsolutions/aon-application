package com.code.aon.groupware;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.Enterprise;
import com.code.aon.company.IEnterprise;
import com.code.aon.config.User;
import com.code.aon.groupware.enumeration.TaskHolderType;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;

@Entity
@Table(name="task_holder")
@PrimaryKeyJoinColumn(name="registry")
public class TaskHolder implements ITransferObject, IRegistry, IEnterprise {
	
	private static final long serialVersionUID = 2340873810468156830L;

	private Integer id;
	private Registry registry;
	private Enterprise enterprise;
    private TaskHolderType type;
    private CostProfile costProfile;
    private User user;
    private boolean active;
	
	public TaskHolder() {
		this.type = TaskHolderType.INTERNAL;
		this.active = true;
	}

	@Id
	@Column(name="registry")
	@GeneratedValue(generator="registry_id")
	@GenericGenerator(name="registry_id", strategy="foreign", parameters = {
			@Parameter(name="property", value="registry")})
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@OneToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false )
    @ForeignKey(name = "FK_TASK_ENTERPRISE")
    @Index(name = "IDX_TASK_ENTERPRISE")
    @Override
	public Enterprise getEnterprise() {
		return enterprise;
	}
    @Override
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}	

    @OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public TaskHolderType getType() {
		return type;
	}

	public void setType(TaskHolderType type) {
		this.type = type;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@ManyToOne
	@JoinColumn(name="user_id")
    @ForeignKey(name = "FK_TASK_HOLDER_USER")
    @Index(name = "IDX_TASK_HOLDER_USER")
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	@ManyToOne
	@JoinColumn(name="cost_profile")
    @ForeignKey(name = "FK_TASK_HOLDER_COST_PROFILE")
    @Index(name = "IDX_TASK_HOLDER_COST_PROFILE")
	public CostProfile getCostProfile() {
		return costProfile;
	}
	public void setCostProfile(CostProfile costProfile) {
		this.costProfile = costProfile;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final TaskHolder o = (TaskHolder) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.active, o.active)
				.append(this.registry, o.registry)
				.append(this.enterprise, o.enterprise)
				.append(this.type, o.type)
				.append(this.costProfile, o.costProfile)
				.append(this.user, o.user)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.active)
			.append(this.id)			
			.append(this.registry)			
			.append(this.enterprise)
			.append(this.type)
			.append(this.costProfile)
			.append(this.user)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}

}