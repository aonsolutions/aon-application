package com.code.gbp;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.ITransferObject;
import com.code.gbp.enumeration.IncidenceSource;

@Entity
@Table(name="incidence")
public class Incidence implements ITransferObject {

	private Integer id;
	
	private Date incidenceDate;
	
	private IncidenceType incidenceType;
	
	private String description;
	
	private String detail;
	
	private Supplier supplier;
	
	private Campaign campaign;
	
	private IncidenceSource source;
	
	private Integer sourceId;

	
	@Id
	@GeneratedValue
	@Column(nullable=false)
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Column(name="incidence_date", nullable=false)
	public Date getIncidenceDate() {
		return incidenceDate;
	}

	public void setIncidenceDate(Date incidenceDate) {
		this.incidenceDate = incidenceDate;
	}

	@ManyToOne
	@JoinColumn( name="incidence_type", nullable=false )
	public IncidenceType getIncidenceType() {
		return incidenceType;
	}

	public void setIncidenceType(IncidenceType incidenceType) {
		this.incidenceType = incidenceType;
	}

	@Column(length=64)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(length=65535)
	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	@ManyToOne
	@JoinColumn( name="supplier", nullable=false )
	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	@ManyToOne
	@JoinColumn( name="campaign", nullable=false )
	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@Column( nullable=false )
	public IncidenceSource getSource() {
		return source;
	}

	public void setSource(IncidenceSource source) {
		this.source = source;
	}

	@Column( name="source_id", nullable=false )
	public Integer getSourceId() {
		return sourceId;
	}

	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}
	
	@Transient
	public String getShortDetail() {
		if (detail != null &&
				detail.length()>SHORT_DESC_LENGTH)
			return detail.substring(0,SHORT_DESC_LENGTH)+"...";
		return detail;
	}

	private int SHORT_DESC_LENGTH = 64; 
}