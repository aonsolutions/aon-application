package com.esferalia.aon.payroll;

import java.util.Date;
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
@Table(name = "irpf_data")
public class IrpfData implements ITransferObject {
	
	private static final long serialVersionUID = 2294092287841868968L;

	private Integer id;
	private Contract contract;
	private Date startDate;
	private Date endDate;
	private FamilySituation familySituation;
	private String spouseDocument;
	private DisabilityLevel disabilityLevel;
	private boolean dependence;
	private Date movingDate;
	private boolean labourProlongation;
	private Integer descendientCount;
	private boolean fiscalExclusion;
	
	private Set<IrpfDataDescendients> descendients = new HashSet<IrpfDataDescendients>();
	private Set<IrpfDataAscendants> ascendants = new HashSet<IrpfDataAscendants>();
	
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
	@ForeignKey(name = "FK_IRPF_DATA_CONTRACT")
	@Index(name = "IDX_IRPF_DATA_CONTRACT")
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	@Temporal(TemporalType.DATE)
	@Column( name = "start_date" )
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	@Temporal(TemporalType.DATE)
	@Column( name = "end_date" )
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Column( name = "family_situation")
	public FamilySituation getFamilySituation() {
		return familySituation;
	}
	public void setFamilySituation(FamilySituation familySituation) {
		this.familySituation = familySituation;
	}
	
	@Column(name = "spouse_document",length=16)
	public String getSpouseDocument() {
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
	
	@Column( name = "fiscal_exclusion" )
	public boolean isFiscalExclusion() {
		return fiscalExclusion;
	}
	public void setFiscalExclusion(boolean fiscalExclusion) {
		this.fiscalExclusion = fiscalExclusion;
	}
	
	@OneToMany(mappedBy = "irpfData", cascade={CascadeType.REMOVE})
	public Set<IrpfDataDescendients> getDescendients() {
		return this.descendients;
	}
	public void setDescendients(Set<IrpfDataDescendients> descendients) {
		this.descendients = descendients;
	}
	
	@OneToMany(mappedBy = "irpfData", cascade={CascadeType.REMOVE})
	public Set<IrpfDataAscendants> getAscendants() {
		return this.ascendants;
	}
	public void setAscendants(Set<IrpfDataAscendants> ascendants) {
		this.ascendants = ascendants;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final IrpfData o =  (IrpfData) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)			
				.append(this.startDate, o.startDate)			
				.append(this.endDate, o.endDate)			
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
			.append(this.startDate)		
			.append(this.endDate)		
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
