package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Date;

import com.code.aon.config.enumeration.Administration;
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;

public interface IIrpfController {

	public abstract Integer getBirthYear();

	public abstract String getDocument();

	public abstract Date getEffectiveDate();

	public abstract Integer getEffectiveYear();

	public abstract Double getBaseIrpf();

	public abstract Double getMinimunPersonalFamily();

	public abstract Double getDeductHomeLoanAmount();

	public abstract Double getDeduct80Bis();

	public abstract Double getIrpf();

	public abstract Double getAnnualIrpf();

	public abstract Double getAnnualRemuneration();

	public abstract Double getIrregular18_2Reduction();

	public abstract Double getIrregular18_3Reduction();

	public abstract Double getDeducciblesExpenses();

	public abstract Double getWorkRemunerationReduction();

	public abstract Double getWorkProlongationReduction();

	public abstract Double getWorkMovingReduction();

	public abstract Double getWorkDisabilityReduction();

	public abstract Double getSocialSecurityPensioner();

	public abstract Double getTwoOrMoreDescendentsMin();

	public abstract Double getSpousalSupport();

	public abstract Double getFoodAnnuity();

	public abstract Double getMinimunPersonal();

	public abstract Double getMinimunAscendents();

	public abstract Double getMinimunDescendents();

	public abstract Double getMinimunDisability();

	public abstract Integer getDescendentsMinor3Total();

	public abstract Integer getDescendentsMinor3Entirely();

	public abstract Integer getDescendentsRemainderTotal();

	public abstract Integer getDescendentsRemainderEntirely();

	public abstract Integer getDescendents33_65Total();

	public abstract Integer getDescendents33_65Entirely();

	public abstract Integer getDescendentsMovingTotal();

	public abstract Integer getDescendentsMovingEntirely();

	public abstract Integer getDescendents65Total();

	public abstract Integer getDescendents65Entirely();

	public abstract Integer getDescendentsFirst();

	public abstract Integer getDescendentsSecond();

	public abstract Integer getDescendentsThird();

	public abstract Integer getDescendentsFourthSubsequentTotal();

	public abstract Integer getDescendentsFourthSubsequentEntirely();

	public abstract Integer getAscendentsMinor75Total();

	public abstract Integer getAscendentsMinor75Entirely();

	public abstract Integer getAscendentsMayor75Total();

	public abstract Integer getAscendentsMayor75Entirely();

	public abstract Integer getAscendents33_65Total();

	public abstract Integer getAscendents33_65Entirely();

	public abstract Integer getAscendentsMovingTotal();

	public abstract Integer getAscendentsMovingEntirely();

	public abstract Integer getAscendents65Total();

	public abstract Integer getAscendents65Entirely();

	public abstract Double getPaidIrpf();

	public abstract IrpfRegularizationReason getReason();

	public abstract Double getPaidRemuneration();

	public abstract Double getPriorAnnualIrpf();

	public abstract Double getPriorAnnualRemuneration();

	public abstract Double getPriorBaseIrpf();

	public abstract Double getPriorIrpf();

	public abstract Double getPriorMinimunPersonalFamily();

	public abstract Double getPriorDeductHomeLoanAmount();

	public abstract Administration getAdministration();

}