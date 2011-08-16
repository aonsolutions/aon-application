package com.esferalia.aon.payroll;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Index;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.enumeration.DisabilityLevel;
import com.esferalia.aon.payroll.enumeration.FamilySituation;

@Entity
@Table(name = "mod145")
public class Mod145 implements ITransferObject {
	
	private static final long serialVersionUID = 2294092287841868968L;

	private Integer id;
	private Contract contract;
	private Date date;
	private FamilySituation familySituation;
	private String spouseDocument;
	private DisabilityLevel disabilityLevel;
	private boolean dependence;
	private Date movingDate;
	private boolean labourProlongation;
	private Integer descendientCount;
	
	@Id
    @GeneratedValue
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    @ManyToOne
	@JoinColumn( name="contract", nullable = false, updatable = false )	
	@ForeignKey(name = "FK_MOD145_CONTRACT")
	@Index(name = "IDX_MOD145_CONTRACT")
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	@Temporal(TemporalType.DATE)
	@Column( name = "date" )
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	@Column( name = "family_situation")
	public FamilySituation getFamilySituation() {
		return familySituation;
	}
	public void setFamilySituation(FamilySituation familySituation) {
		this.familySituation = familySituation;
	}
	
	@Column(name = "spouse_document",length=16)
	public String getspouseDocument() {
		return spouseDocument;
	}
	public void setSpouseDocument(String spouseDocument) {
		this.spouseDocument = spouseDocument;
	}
	
	@Column( name = "disability_level")
	public DisabilityLevel getDisabilityLevel() {
		return disabilityLevel;
	}
	public void setDisabilityLevel(DisabilityLevel disabilityLevel) {
		this.disabilityLevel = disabilityLevel;
	}
	
	@Column( name = "dependence" )
	public boolean isDependence() {
		return dependence;
	}
	public void setDependence(boolean dependence) {
		this.dependence = dependence;
	}

	@Temporal(TemporalType.DATE)
	@Column( name = "moving_date" )
	public Date getMovingDate() {
		return movingDate;
	}
	public void setMovingDate(Date movingDate) {
		this.movingDate = movingDate;
	}
	
	@Column( name = "labour_prolongation" )
	public boolean isLabourProlongation() {
		return labourProlongation;
	}
	public void setLabourProlongation(boolean labourProlongation) {
		this.labourProlongation = labourProlongation;
	}
	
	@Column(name = "descendient_count", length = 2)
	public Integer getDescendientCount() {
		return descendientCount;
	}
	public void setDescendientCount(Integer descendientCount) {
		this.descendientCount = descendientCount;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final Mod145 o = (Mod145) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)			
				.append(this.date, o.date)			
				.append(this.familySituation, o.familySituation)			
				.append(this.spouseDocument, o.spouseDocument)			
				.append(this.disabilityLevel, o.disabilityLevel)			
				.append(this.dependence, o.dependence)			
				.append(this.movingDate, o.movingDate)			
				.append(this.labourProlongation, o.labourProlongation)			
				.append(this.descendientCount, o.descendientCount)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.contract)		
			.append(this.date)		
			.append(this.familySituation)		
			.append(this.spouseDocument)		
			.append(this.disabilityLevel)		
			.append(this.dependence)		
			.append(this.movingDate)		
			.append(this.labourProlongation)
			.append(this.descendientCount)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
