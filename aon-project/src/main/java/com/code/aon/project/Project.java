package com.code.aon.project;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;
import com.code.aon.company.Enterprise;
import com.code.aon.company.IEnterprise;
import com.code.aon.registry.Registry;

@Entity
@Table(name="project")
public class Project implements ITransferObject, IEnterprise {

	private static final long serialVersionUID = -4323833285796236116L;

	private Integer id;
	private Enterprise enterprise;
	private Registry registry;
	private ProjectType projectType;
	private String name;
    private String alias;
	private Date date;
	private boolean tas;
	private boolean commercial;
	private boolean dossier;
	private boolean active;

	public Project() {
		this.date = new Date();
		this.active = true;
	}

	@Id
	@GeneratedValue
	@Column(nullable=false)
    public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	@ManyToOne
    @JoinColumn(name="enterprise", nullable = false, updatable = false )
    @ForeignKey(name = "FK_PROJECT_ENTERPRISE")
    @Index(name = "IDX_PROJECT_ENTERPRISE")    
	public Enterprise getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}	

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="registry", nullable = false)
    @ForeignKey(name="FK_PROJECT_REGISTRY")
    @Index(name="IDX_PROJECT_REGISTRY")  
    public Registry getRegistry() {
        return registry;
    }
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="project_type")
    @ForeignKey(name="FK_PROJECT_PROJECT_TYPE")
    @Index(name="IDX_PROJECT_PROJECT_TYPE")  
    public ProjectType getProjectType() {
        return projectType;
    }
    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    @Column(nullable = false, length = 64)
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	
	@Column(length = 32)
	public String getAlias() {
		return alias;
	}
	public void setAlias(String alias) {
		this.alias = alias;
	}	
	
	@Temporal(TemporalType.DATE)
	@Column(nullable=false)
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public boolean isTas() {
		return tas;
	}
	public void setTas(boolean tas) {
		this.tas = tas;
	}	
	
	public boolean isCommercial() {
		return commercial;
	}
	public void setCommercial(boolean commercial) {
		this.commercial = commercial;
	}	
	
	public boolean isDossier() {
		return dossier;
	}
	public void setDossier(boolean dossier) {
		this.dossier = dossier;
	}	
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}

	@Transient
	public boolean isExtended() {
		return (isCommercial() || isTas());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Project o = (Project) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.enterprise, o.enterprise)
				.append(this.registry, o.registry)
				.append(this.projectType, o.registry)
				.append(this.alias, o.alias)
				.append(this.commercial, o.commercial)
				.append(this.date, o.date)
				.append(this.dossier, o.dossier)
				.append(this.name, o.name)
				.append(this.tas, o.tas)
				.append(this.active, o.active)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(enterprise)
			.append(registry)
			.append(projectType)
			.append(alias)
			.append(commercial)
			.append(date)
			.append(dossier)
			.append(name)
			.append(id)			
			.append(tas)
			.append(active)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}