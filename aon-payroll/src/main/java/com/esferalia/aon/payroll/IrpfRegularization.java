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
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;

/**
 * @author eagirrezabal
 *
 */
@Entity
@Table(name = "irpf_regularization")
public class IrpfRegularization implements ITransferObject {
	
	private static final long serialVersionUID = -6709777545259193215L;

	/**
	 * the id
	 */
	private Integer id;
	/**
	 *  the contract
	 */
	private Contract contract;
	/**
	 * Causa de regularizacinn
	 */
	private IrpfRegularizationReason reason;
	/**
	 * Fecha de entrada en vigor
	 * 
	 */
	private Date effectiveDate;
	/**
	 * Retenciones practicadas con anterioridad a la regularizacin
	 * 
	 */
	private Double paidIrpf;
	/**
	 * Retribuciones ya satisfechas con anterioridad a la regularizacin
	 * 
	 */
	private Double paidRemuneration;
	/**
	 * Retenciones anuales anteriores a la regularizacin
	 * 
	 */
	private Double priorAnnualIrpf;
	/**
	 * Retribucines anulaes consideradas con anterioridad a la regularizacin
	 * 
	 */
	private Double priorAnnualRemuneration;
	/**
	 * Base para calcular el tipo de retencin determinado antes de la regularizacin
	 * 
	 */
	private Double priorBaseIrpf;
	/**
	 * Tipo de retencin aplicado antes de la regularizacin
	 * 
	 */
	private Double priorIrpf;
	/**
	 * Los rendimientos anteriores a la regularizacin fueron obtenidos en Ceuta o Melilla
	 * 
	 */
//	private boolean priorInCeutaMelilla;
	/**
	 * Mnimo personal y familiar para calcular el tipo de retencin determinado antes de la regularizacin
	 * 
	 */
	private Double priorMinimunPersonalFamily;
	/**
	 * En algn momento antes de la regularizacin de aplico la minoracin por pagos por la adquisin o rehabilitacin de la vivienda
	 * 
	 */
//	private boolean priorDeductHomeLoan;
	/**
	 * Importe de la minoracin por pagos por la adquisin o rehabilitacin de la vivienda antes de la regularizacin
	 * 
	 */
	private Double priorDeductHomeLoanAmount;
	
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
	@ForeignKey(name = "FK_IRPF_REGULARIZATION_CONTRACT")
	@Index(name = "IDX_IRPF_REGULARIZATION_CONTRACT")
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
	
	@Column(name = "paid_irpf", precision = 15, scale = 3)
	public Double getPaidIrpf() {
		return paidIrpf;
	}
	public void setPaidIrpf(Double paidIrpf) {
		this.paidIrpf = paidIrpf;
	}
	
	@Column( name = "reason")
	public IrpfRegularizationReason getReason() {
		return reason;
	}
	public void setReason(IrpfRegularizationReason reason) {
		this.reason = reason;
	}
	
	@Column(name = "paid_remuneration", precision = 15, scale = 3)
	public Double getPaidRemuneration() {
		return paidRemuneration;
	}
	public void setPaidRemuneration(Double paidRemuneration) {
		this.paidRemuneration = paidRemuneration;
	}
	
	@Column(name = "prior_annual_irpf", precision = 15, scale = 3)
	public Double getPriorAnnualIrpf() {
		return priorAnnualIrpf;
	}
	public void setPriorAnnualIrpf(Double priorAnnualIrpf) {
		this.priorAnnualIrpf = priorAnnualIrpf;
	}
	
	@Column(name = "prior_annual_remuneration", precision = 15, scale = 3)
	public Double getPriorAnnualRemuneration() {
		return priorAnnualRemuneration;
	}
	public void setPriorAnnualRemuneration(Double priorAnnualRemuneration) {
		this.priorAnnualRemuneration = priorAnnualRemuneration;
	}
	
	@Column(name = "prior_base_irpf", precision = 15, scale = 3)
	public Double getPriorBaseIrpf() {
		return priorBaseIrpf;
	}
	public void setPriorBaseIrpf(Double priorBaseIrpf) {
		this.priorBaseIrpf = priorBaseIrpf;
	}
	
	@Column(name = "prior_irpf", precision = 15, scale = 3)
	public Double getPriorIrpf() {
		return priorIrpf;
	}
	public void setPriorIrpf(Double priorIrpf) {
		this.priorIrpf = priorIrpf;
	}
	
//	@Column( name = "prior_in_ceuta_melilla")
//	public boolean isPriorInCeutaMelilla() {
//		return priorInCeutaMelilla;
//	}
//	public void setPriorInCeutaMelilla(boolean priorInCeutaMelilla) {
//		this.priorInCeutaMelilla = priorInCeutaMelilla;
//	}
	
	@Column(name = "prior_minimun_personal_family", precision = 15, scale = 3)
	public Double getPriorMinimunPersonalFamily() {
		return priorMinimunPersonalFamily;
	}
	public void setPriorMinimunPersonalFamily(Double priorMinimunPersonalFamily) {
		this.priorMinimunPersonalFamily = priorMinimunPersonalFamily;
	}
	
//	@Column( name = "prior_deduct_home_loan")
//	public boolean isPriorDeductHomeLoan() {
//		return priorDeductHomeLoan;
//	}
//	public void setPriorDeductHomeLoan(boolean priorDeductHomeLoan) {
//		this.priorDeductHomeLoan = priorDeductHomeLoan;
//	}
	
	@Column(name = "prior_deduct_home_loan_amount", precision = 15, scale = 3)
	public Double getPriorDeductHomeLoanAmount() {
		return priorDeductHomeLoanAmount;
	}
	public void setPriorDeductHomeLoanAmount(Double priorDeductHomeLoanAmount) {
		this.priorDeductHomeLoanAmount = priorDeductHomeLoanAmount;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		final IrpfRegularization o =  (IrpfRegularization) obj;
		if (o.getId() == null && getId() == null) {
			return new EqualsBuilder()
				.append(this.contract, o.contract)			
				.append(this.reason, o.reason)
				.append(this.effectiveDate, o.effectiveDate)
				.append(this.paidIrpf, o.paidIrpf)
				.append(this.paidRemuneration, o.paidRemuneration)
				.append(this.priorAnnualIrpf, o.priorAnnualIrpf)
				.append(this.priorAnnualRemuneration, o.priorAnnualRemuneration)
				.append(this.priorBaseIrpf, o.priorBaseIrpf)
				.append(this.priorIrpf, o.priorIrpf)
//				.append(this.priorInCeutaMelilla, o.priorInCeutaMelilla)
				.append(this.priorMinimunPersonalFamily, o.priorMinimunPersonalFamily)
//				.append(this.priorDeductHomeLoan, o.priorDeductHomeLoan)
				.append(this.priorDeductHomeLoanAmount, o.priorDeductHomeLoanAmount)
				.isEquals();
		}
		return ObjectUtils.equals(getId(), o.getId());		
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)		
			.append(this.contract)		
			.append(this.reason)
			.append(this.effectiveDate)
			.append(this.paidIrpf)
			.append(this.paidRemuneration)
			.append(this.priorAnnualIrpf)
			.append(this.priorAnnualRemuneration)
			.append(this.priorBaseIrpf)
			.append(this.priorIrpf)
//			.append(this.priorInCeutaMelilla)
			.append(this.priorMinimunPersonalFamily)
//			.append(this.priorDeductHomeLoan)
			.append(this.priorDeductHomeLoanAmount)
			.toHashCode();
	}	

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	

}
