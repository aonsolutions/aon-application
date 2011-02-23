package com.code.aon.company;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
import com.code.aon.config.IScopable;
import com.code.aon.config.Scope;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryAttachment;
import com.esferalia.aon.calendar.Calendar;

/**
 * Transfer Object that represents a Enterprise.
 */
@Entity
@Table(name="enterprise")
@PrimaryKeyJoinColumn(name="registry")
public class Enterprise implements ITransferObject, IRegistry, IScopable {

	private static final long serialVersionUID = -6717039049819608334L;
	
	private Integer id;
	
	private Registry registry;
	
	private Scope scope;
	
	private Calendar calendar;
	
	private Set<EnterpriseActivity> activities = new HashSet<EnterpriseActivity>();
	
	private Set<RegistryAttachment> documents = new HashSet<RegistryAttachment>();
	
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

	@OneToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@org.hibernate.annotations.Cascade(value = org.hibernate.annotations.CascadeType.SAVE_UPDATE)
	@PrimaryKeyJoinColumn 
	public Registry getRegistry() {
		return registry;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}
	
	@ManyToOne
    @JoinColumn(name="scope", nullable=false)
    @ForeignKey(name = "FK_ENTERPRISE_SCOPE")
    @Index(name = "IDX_ENTERPRISE_SCOPE")
	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}	
	
	@OneToMany(mappedBy = "enterprise", cascade={CascadeType.REMOVE})
	public Set<EnterpriseActivity> getActivities() {
		return activities;
	}

	public void setActivities(Set<EnterpriseActivity> activities) {
		this.activities = activities;
	}

	@ManyToOne
    @JoinColumn( name="calendar")	
	@ForeignKey(name = "FK_ENTERPRISE_CALENDAR")
	@Index(name = "FK_ENTERPRISE_CALENDAR")
	public Calendar getCalendar() {
		return calendar;
	}
	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	@OneToMany(mappedBy = "registry", cascade={CascadeType.REMOVE})
	public Set<RegistryAttachment> getDocuments() {
		return documents;
	}
	
	public void setDocuments(Set<RegistryAttachment> documents) {
		this.documents = documents;
	}	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Enterprise o = (Enterprise) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.registry, o.registry)
				.append(this.scope, o.scope)
				.append(this.calendar, o.calendar)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.append(registry)
			.append(scope)
			.append(calendar)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}
