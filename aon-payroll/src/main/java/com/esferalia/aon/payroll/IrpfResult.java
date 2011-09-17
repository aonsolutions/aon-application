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

/**
 * @author eagirrezabal
 *
 */
@Entity
@Table(name = "irpf_result")
public class IrpfResult implements ITransferObject {
	
	private static final long serialVersionUID = -1945344764101612409L;

	/**
	 * the id
	 */
	private Integer id;
	/**
	 * the contract
	 */
	private Contract contract;

	/**
	 * Fecha de entrada en vigor
	 */
	private Date effectiveDate;
	/**
	 * Base para calcular el tipo de retencin
	 */
	private Double baseIrpf;
	/**
	 * Mnimo personal y familiar para calcular el tipo de retencin
	 */
	private Double minimunPersonalFamily;
	/**
	 * Minoracin por pagos de prstamo para vivienda habitual
	 */
	private Double deductHomeLoanAmount;
	/**
	 * Deduccion Arttculo 80 bis LIRPF
	 */
	private Double deduct80Bis;
	/**
	 * Tipo retencin apliclabe
	 */
	private Double irpf;
	/**
	 * Importe anual de las retenciones e ingresos a cuenta
	 */
	private Double annualIrpf;
	/**
	 * Retribuciones anuales. Importe ntegro
	 */
	private Double annualRemuneration;
	/**
	 * Reducciones por irregularidad ( Art. 18.2 LIRPF). Importe
	 */
	private Double irregular18_2Reduction;
	/**
	 * Reducciones por irregularidad ( Art. 18.3: DD.TT 11 y 12 de la LIRPF). Importe
	 */
	private Double irregular18_3Reduction;
	/**
	 * Gastos deducibles. Importe anual
	 */
	private Double deducciblesExpenses;
	/**
	 * Reducciones por rendimiento del trabajo
	 */
	private Double workRemunerationReduction;
	/**
	 * Reducciones por prolongacin de la actividad
	 */
	private Double workProlongationReduction;
	/**
	 * Reducciones por movilidad geografica
	 */
	private Double workMovingReduction;
	/**
	 * Reducciones por discapacidad
	 */
	private Double workDisabilityReduction;
	/**
	 * Por ser pensionista de la s. social/cl. Pasivas o desempleado
	 */
	private Double socialSecurityPensioner;
	/**
	 * Por tener ms de dos descendientes con derecho a mnimo
	 */
	private Double twoOrMoreDescendentsMin;
	/**
	 * Pension compensatoria a favor del cnyuge. Importe anual
	 */
	private Double spousalSupport;
	/**
	 * Anualidades por alimentos en favor de los hijos. Importe anual
	 */
	private Double foodAnnuity;
	/**
	 * Mnimo personal
	 */
	private Double minimunPersonal;
	/**
	 * MNimo por descendientes
	 */
	private Double minimunAscendents;
	/**
	 * Mnimo por descendientes
	 */
	private Double minimunDescendents;
	/**
	 * Mnimo por discapacidad
	 */
	private Double minimunDisability;
	/**
	 * Descendientes computados menores de tres aos. Total
	 */
	private Integer descendentsMinor3Total;
	/**
	 * Descendientes computados menores de tres aos. Por entero
	 */
	private Integer descendentsMinor3Entirely;
	/**
	 * Resto de descendientes computados . Total
	 */
	private Integer descendentsRemainderTotal;
	/**
	 * Resto de descendientes computados . Por entero
	 */
	private Integer descendentsRemainderEntirely;
	/**
	 * Descendientes con discapacidad >= 33% y < 65%. Total
	 */
	private Integer descendents33_65Total;
	/**
	 * Descendientes con discapacidad >= 33% y < 65%. Por entero
	 */
	private Integer descendents33_65Entirely;
	/**
	 * Descendientes con discapacidad, movilidad reducida. Total
	 */
	private Integer descendentsMovingTotal;
	/**
	 * Descendientes con discapacidad, movilidad reducida. Por entero
	 */
	private Integer descendentsMovingEntirely;
	/**
	 * Descendientes con discapacidad > 65%. Total
	 */
	private Integer descendents65Total;
	/**
	 * Descendientes con discapacidad > 65%. Por entero
	 */
	private Integer descendents65Entirely;
	/**
	 * Detalle del cmputo de descendientes. Hijo 1 
	 */
	private Integer descendentsFirst;
	/**
	 * Detalle del cmputo de descendientes. Hijo 2
	 */
	private Integer descendentsSecond;
	/**
	 * Detalle del cmputo de descendientes. Hijo 3
	 */
	private Integer descendentsThird;
	/**
	 * Detalle del cmputo de descendientes. Hijo 4 y sucesivos. Total
	 */
	private Integer descendentsFourthSubsequentTotal;
	/**
	 * Detalle del cmputo de descendientes. Hijo 4 y sucesivos. Por entero
	 */
	private Integer descendentsFourthSubsequentEntirely;
	/**
	 * Ascendientes computados menores de 75 aos. Total
	 */
	private Integer ascendentsMinor75Total;
	/**
	 * Ascendientes computados menores de 75 aos. Por entero
	 */
	private Integer ascendentsMinor75Entirely;
	/**
	 * Ascendientes computados mayores de 75 aos. Total
	 */
	private Integer ascendentsMayor75Total;
	/**
	 * Ascendientes computados mayores de 75 aos. Por entero
	 */
	private Integer ascendentsMayor75Entirely;
	/**
	 * Ascendientes con discapacidad >= 33% y < 65%. Total
	 */
	private Integer ascendents33_65Total;
	/**
	 * Ascendientes con discapacidad >= 33% y < 65%. Por entero
	 */
	private Integer ascendents33_65Entirely;
	/**
	 * Ascendientes con discapacidad, movilidad reducida. Total
	 */
	private Integer ascendentsMovingTotal;
	/**
	 * Ascendientes con discapacidad, movilidad reducida. Por entero
	 */
	private Integer ascendentsMovingEntirely;
	/**
	 * Ascendientes con discapacidad > 65%. Total
	 */
	private Integer ascendents65Total;
	/**
	 * Ascendientes con discapacidad > 65%. Por entero
	 */
	private Integer ascendents65Entirely;

	
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
	@ForeignKey(name = "FK_IRPF_RESULT_CONTRACT")
	@Index(name = "IDX_IRPF_RESULT_CONTRACT")
	public Contract getContract() {
		return contract;
	}
	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	@Temporal(TemporalType.DATE)
	@Column( name = "effective_date", nullable=false )
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	
	@Column(name = "base_irpf", precision = 15, scale = 3)
	public Double getBaseIrpf() {
		return baseIrpf;
	}
	public void setBaseIrpf(Double baseIrpf) {
		this.baseIrpf = baseIrpf;
	}
	
	@Column(name = "minimun_personal_family", precision = 15, scale = 3)
	public Double getMinimunPersonalFamily() {
		return minimunPersonalFamily;
	}
	public void setMinimunPersonalFamily(Double minimunPersonalFamily) {
		this.minimunPersonalFamily = minimunPersonalFamily;
	}
	
	@Column(name = "deduct_home_loan_amount", precision = 15, scale = 3)
	public Double getDeductHomeLoanAmount() {
		return deductHomeLoanAmount;
	}
	public void setDeductHomeLoanAmount(Double deductHomeLoanAmount) {
		this.deductHomeLoanAmount = deductHomeLoanAmount;
	}
	
	@Column(name = "deduct_80_bis", precision = 15, scale = 3)
	public Double getDeduct80Bis() {
		return deduct80Bis;
	}
	public void setDeduct80Bis(Double deduct80Bis) {
		this.deduct80Bis = deduct80Bis;
	}
	
	@Column(name = "irpf", precision = 15, scale = 3)
	public Double getIrpf() {
		return irpf;
	}
	public void setIrpf(Double irpf) {
		this.irpf = irpf;
	}
	
	@Column(name = "annual_irpf", precision = 15, scale = 3)
	public Double getAnnualIrpf() {
		return annualIrpf;
	}
	public void setAnnualIrpf(Double annualIrpf) {
		this.annualIrpf = annualIrpf;
	}
	
	@Column(name = "annual_remuneration", precision = 15, scale = 3)
	public Double getAnnualRemuneration() {
		return annualRemuneration;
	}
	public void setAnnualRemuneration(Double annualRemuneration) {
		this.annualRemuneration = annualRemuneration;
	}
	
	@Column(name = "irregular_18_2_reduction", precision = 15, scale = 3)
	public Double getIrregular18_2Reduction() {
		return irregular18_2Reduction;
	}
	public void setIrregular18_2Reduction(Double irregular18_2Reduction) {
		this.irregular18_2Reduction = irregular18_2Reduction;
	}
	
	@Column(name = "irregular_18_3_reduction", precision = 15, scale = 3)
	public Double getIrregular18_3Reduction() {
		return irregular18_3Reduction;
	}
	public void setIrregular18_3Reduction(Double irregular18_3Reduction) {
		this.irregular18_3Reduction = irregular18_3Reduction;
	}
	
	@Column(name = "deduccibles_expenses", precision = 15, scale = 3)
	public Double getDeducciblesExpenses() {
		return deducciblesExpenses;
	}
	public void setDeducciblesExpenses(Double deducciblesExpenses) {
		this.deducciblesExpenses = deducciblesExpenses;
	}
	
	@Column(name = "work_remuneration_reduction", precision = 15, scale = 3)
	public Double getWorkRemunerationReduction() {
		return workRemunerationReduction;
	}
	public void setWorkRemunerationReduction(Double workRemunerationReduction) {
		this.workRemunerationReduction = workRemunerationReduction;
	}
	
	@Column(name = "work_prolongation_reduction", precision = 15, scale = 3)
	public Double getWorkProlongationReduction() {
		return workProlongationReduction;
	}
	public void setWorkProlongationReduction(Double workProlongationReduction) {
		this.workProlongationReduction = workProlongationReduction;
	}
	
	@Column(name = "work_moving_reduction", precision = 15, scale = 3)
	public Double getWorkMovingReduction() {
		return workMovingReduction;
	}
	public void setWorkMovingReduction(Double workMovingReduction) {
		this.workMovingReduction = workMovingReduction;
	}
	
	@Column(name = "work_disability_reduction", precision = 15, scale = 3)
	public Double getWorkDisabilityReduction() {
		return workDisabilityReduction;
	}
	public void setWorkDisabilityReduction(Double workDisabilityReduction) {
		this.workDisabilityReduction = workDisabilityReduction;
	}
	
	@Column(name = "social_security_pensioner", precision = 15, scale = 3)
	public Double getSocialSecurityPensioner() {
		return socialSecurityPensioner;
	}
	public void setSocialSecurityPensioner(Double socialSecurityPensioner) {
		this.socialSecurityPensioner = socialSecurityPensioner;
	}
	
	@Column(name = "two_or_more_descendents_min", precision = 15, scale = 3)
	public Double getTwoOrMoreDescendentsMin() {
		return twoOrMoreDescendentsMin;
	}
	public void setTwoOrMoreDescendentsMin(Double twoOrMoreDescendentsMin) {
		this.twoOrMoreDescendentsMin = twoOrMoreDescendentsMin;
	}
	
	@Column(name = "spousal_support", precision = 15, scale = 3)
	public Double getSpousalSupport() {
		return spousalSupport;
	}
	public void setSpousalSupport(Double spousalSupport) {
		this.spousalSupport = spousalSupport;
	}
	
	@Column(name = "food_annuity", precision = 15, scale = 3)
	public Double getFoodAnnuity() {
		return foodAnnuity;
	}
	public void setFoodAnnuity(Double foodAnnuity) {
		this.foodAnnuity = foodAnnuity;
	}
	
	@Column(name = "minimun_personal", precision = 15, scale = 3)
	public Double getMinimunPersonal() {
		return minimunPersonal;
	}
	public void setMinimunPersonal(Double minimunPersonal) {
		this.minimunPersonal = minimunPersonal;
	}
	
	@Column(name = "minimun_ascendents", precision = 15, scale = 3)
	public Double minimunAscendents() {
		return minimunAscendents;
	}
	public void setMinimunAscendents(Double minimunAscendents) {
		this.minimunAscendents = minimunAscendents;
	}
	
	@Column(name = "minimun_descendents", precision = 15, scale = 3)
	public Double getMinimunDescendents() {
		return minimunDescendents;
	}
	public void setMinimunDescendents(Double minimunDescendents) {
		this.minimunDescendents = minimunDescendents;
	}
	
	@Column(name = "minimun_disability", precision = 15, scale = 3)
	public Double getMinimunDisability() {
		return minimunDisability;
	}
	public void setMinimunDisability(Double minimunDisability) {
		this.minimunDisability = minimunDisability;
	}
	
	@Column(name = "descendents_minor_3_total", length = 2)
	public Integer getDescendentsMinor3Total() {
		return descendentsMinor3Total;
	}
	public void setDescendentsMinor3Total(Integer descendentsMinor3Total) {
		this.descendentsMinor3Total = descendentsMinor3Total;
	}
	
	@Column(name = "descendents_minor_3_entirely", length = 2)
	public Integer getDescendentsMinor3Entirely() {
		return descendentsMinor3Entirely;
	}
	public void setDescendentsMinor3Entirely(Integer descendentsMinor3Entirely) {
		this.descendentsMinor3Entirely = descendentsMinor3Entirely;
	}
	
	@Column(name = "descendents_remainder_total", length = 2)
	public Integer getDescendentsRemainderTotal() {
		return descendentsRemainderTotal;
	}
	public void setDescendentsRemainderTotal(Integer descendentsRemainderTotal) {
		this.descendentsRemainderTotal = descendentsRemainderTotal;
	}
	
	@Column(name = "descendents_remainder_entirely", length = 2)
	public Integer getDescendentsRemainderEntirely() {
		return descendentsRemainderEntirely;
	}
	public void setDescendentsRemainderEntirely(Integer descendentsRemainderEntirely) {
		this.descendentsRemainderEntirely = descendentsRemainderEntirely;
	}
	
	@Column(name = "descendents_33_65_total", length = 2)
	public Integer getDescendents33_65Total() {
		return descendents33_65Total;
	}
	public void setDescendents33_65Total(Integer descendents33_65Total) {
		this.descendents33_65Total = descendents33_65Total;
	}
	
	@Column(name = "descendents_33_65_entirely", length = 2)
	public Integer getDescendents33_65Entirely() {
		return descendents33_65Entirely;
	}
	public void setDescendents33_65Entirely(Integer descendents33_65Entirely) {
		this.descendents33_65Entirely = descendents33_65Entirely;
	}
	
	@Column(name = "descendents_moving_total", length = 2)
	public Integer getDescendentsMovingTotal() {
		return descendentsMovingTotal;
	}
	public void setDescendentsMovingTotal(Integer descendentsMovingTotal) {
		this.descendentsMovingTotal = descendentsMovingTotal;
	}
	
	@Column(name = "descendents_moving_entirely", length = 2)
	public Integer getDescendentsMovingEntirely() {
		return descendentsMovingEntirely;
	}
	public void setDescendentsMovingEntirely(Integer descendentsMovingEntirely) {
		this.descendentsMovingEntirely = descendentsMovingEntirely;
	}
	
	@Column(name = "descendents_65_Total", length = 2)
	public Integer getDescendents65Total() {
		return descendents65Total;
	}
	public void setDescendents65Total(Integer descendents65Total) {
		this.descendents65Total = descendents65Total;
	}
	
	@Column(name = "descendents_65_entirely", length = 2)
	public Integer getDescendents65Entirely() {
		return descendents65Entirely;
	}
	public void setDescendents65Entirely(Integer descendents65Entirely) {
		this.descendents65Entirely = descendents65Entirely;
	}
	
	@Column(name = "descendents_first", length = 2)
	public Integer getDescendentsFirst() {
		return descendentsFirst;
	}
	public void setDescendentsFirst(Integer descendentsFirst) {
		this.descendentsFirst = descendentsFirst;
	}
	
	@Column(name = "descendents_second", length = 2)
	public Integer getDescendentsSecond() {
		return descendentsSecond;
	}
	public void setDescendentsSecond(Integer descendentsSecond) {
		this.descendentsSecond = descendentsSecond;
	}
	
	@Column(name = "descendents_third", length = 2)
	public Integer getDescendentsThird() {
		return descendentsThird;
	}
	public void setDescendentsThird(Integer descendentsThird) {
		this.descendentsThird = descendentsThird;
	}
	
	@Column(name = "descendents_fourth_subsequent_total", length = 2)
	public Integer getDescendentsFourthSubsequentTotal() {
		return descendentsFourthSubsequentTotal;
	}
	public void setDescendentsFourthSubsequentTotal(
			Integer descendentsFourthSubsequentTotal) {
		this.descendentsFourthSubsequentTotal = descendentsFourthSubsequentTotal;
	}
	
	@Column(name = "descendents_fourth_subsequent_entirely", length = 2)
	public Integer getDescendentsFourthSubsequentEntirely() {
		return descendentsFourthSubsequentEntirely;
	}
	public void setDescendentsFourthSubsequentEntirely(
			Integer descendentsFourthSubsequentEntirely) {
		this.descendentsFourthSubsequentEntirely = descendentsFourthSubsequentEntirely;
	}
	
	@Column(name = "ascendents_minor_75_total", length = 2)
	public Integer getAscendentsMinor75Total() {
		return ascendentsMinor75Total;
	}
	public void setAscendentsMinor75Total(Integer ascendentsMinor75Total) {
		this.ascendentsMinor75Total = ascendentsMinor75Total;
	}
	
	@Column(name = "ascendents_Minor_75_entirely", length = 2)
	public Integer getAscendentsMinor75Entirely() {
		return ascendentsMinor75Entirely;
	}
	public void setAscendentsMinor75Entirely(Integer ascendentsMinor75Entirely) {
		this.ascendentsMinor75Entirely = ascendentsMinor75Entirely;
	}
	
	@Column(name = "ascendents_mayor_75_total", length = 2)
	public Integer getAscendentsMayor75Total() {
		return ascendentsMayor75Total;
	}
	public void setAscendentsMayor75Total(Integer ascendentsMayor75Total) {
		this.ascendentsMayor75Total = ascendentsMayor75Total;
	}
	
	@Column(name = "ascendents_mayor_75_entirely", length = 2)
	public Integer getAscendentsMayor75Entirely() {
		return ascendentsMayor75Entirely;
	}
	public void setAscendentsMayor75Entirely(Integer ascendentsMayor75Entirely) {
		this.ascendentsMayor75Entirely = ascendentsMayor75Entirely;
	}
	
	@Column(name = "ascendents_33_65_total", length = 2)
	public Integer getAscendents33_65Total() {
		return ascendents33_65Total;
	}
	public void setAscendents33_65Total(Integer ascendents33_65Total) {
		this.ascendents33_65Total = ascendents33_65Total;
	}
	
	@Column(name = "ascendents_33_65_entirely", length = 2)
	public Integer getAscendents33_65Entirely() {
		return ascendents33_65Entirely;
	}
	public void setAscendents33_65Entirely(Integer ascendents33_65Entirely) {
		this.ascendents33_65Entirely = ascendents33_65Entirely;
	}
	
	@Column(name = "ascendents_moving_total", length = 2)
	public Integer getAscendentsMovingTotal() {
		return ascendentsMovingTotal;
	}
	public void setAscendentsMovingTotal(Integer ascendentsMovingTotal) {
		this.ascendentsMovingTotal = ascendentsMovingTotal;
	}
	
	@Column(name = "ascendents_moving_entirely", length = 2)
	public Integer getAscendentsMovingEntirely() {
		return ascendentsMovingEntirely;
	}
	public void setAscendentsMovingEntirely(Integer ascendentsMovingEntirely) {
		this.ascendentsMovingEntirely = ascendentsMovingEntirely;
	}
	
	@Column(name = "ascendents_65_total", length = 2)
	public Integer getAscendents65Total() {
		return ascendents65Total;
	}
	public void setAscendents65Total(Integer ascendents65Total) {
		this.ascendents65Total = ascendents65Total;
	}
	
	@Column(name = "ascendents_65_entirely", length = 2)
	public Integer getAscendents65Entirely() {
		return ascendents65Entirely;
	}
	public void setAscendents65Entirely(Integer ascendents65Entirely) {
		this.ascendents65Entirely = ascendents65Entirely;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final IrpfResult o =  (IrpfResult) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)			
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.contract)		
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
