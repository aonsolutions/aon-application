package com.code.aon.project;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;

@Entity
@Table(name="activity_type")
public class ActivityType implements ITransferObject {
	
	private static final long serialVersionUID = 4810421114437119111L;

	private Integer id;
	
	private String description;
	
	private DossierType dossierType;
	
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
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@ManyToOne
	@JoinColumn(name="dossier_type")
	@ForeignKey(name = "FK_ACTIVITY_TYPE_DOSSIER_TYPE")
	@Index(name = "IDX_ACTIVITY_TYPE_DOSSIER_TYPE")					
	public DossierType getDossierType() {
		return dossierType;
	}

	public void setDossierType(DossierType dossierType) {
		this.dossierType = dossierType;
	}

	@Override
    public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
    }

	@Override
	public int hashCode() {
		return new HashCodeBuilder().
			append(description).append(dossierType).
			append(id).toHashCode();
	}

}