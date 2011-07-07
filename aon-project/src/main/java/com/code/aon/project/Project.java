package com.code.aon.project;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.dao.hibernate.PojoToStringBuilder;

@Entity
@Table(name="project")
public class Project implements ITransferObject {

	private static final long serialVersionUID = -4323833285796236116L;

	private Integer id;
    private String name;
    private String alias;
	private Date date;
	private boolean tas;
	private boolean commercial;
	private boolean dossier;

	public Project() {
		this.date = new Date();
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Project o = (Project) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.alias, o.alias)
				.append(this.commercial, o.commercial)
				.append(this.date, o.date)
				.append(this.dossier, o.dossier)
				.append(this.name, o.name)
				.append(this.tas, o.tas)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(alias)
			.append(commercial)
			.append(date)
			.append(dossier)
			.append(name)
			.append(id)			
			.append(tas)
			.toHashCode();
	}

	@Override
	public String toString() {
		return new PojoToStringBuilder(this).toString();
	}
	
}