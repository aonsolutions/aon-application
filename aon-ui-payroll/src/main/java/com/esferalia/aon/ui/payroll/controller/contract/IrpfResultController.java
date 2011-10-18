package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Date;

import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class IrpfResultController extends BasicController implements
		IPayrollConstants {
	

	public Date getEffectiveDate() {
		return getIrpfResult().getEffectiveDate();
	}

	public Double getBaseIrpf() {
		return getDisplay( getIrpfResult().getBaseIrpf() );
	}

	public Double getMinimunPersonalFamily() {
		return getDisplay( getIrpfResult().getMinimunPersonalFamily() );
	}

	public Double getDeductHomeLoanAmount() {
		return getDisplay( getIrpfResult().getDeductHomeLoanAmount() );
	}

	public Double getDeduct80Bis() {
		return getDisplay( getIrpfResult().getDeduct80Bis() );
	}

	public Double getIrpf() {
		return getDisplay( getIrpfResult().getIrpf() );
	}

	public Double getAnnualIrpf() {
		return getDisplay( getIrpfResult().getAnnualIrpf() );
	}

	public Double getAnnualRemuneration() {
		return getDisplay( getIrpfResult().getAnnualRemuneration() );
	}

	public Double getIrregular18_2Reduction() {
		return getDisplay( getIrpfResult().getIrregular18_2Reduction() );
	}

	public Double getIrregular18_3Reduction() {
		return getDisplay( getIrpfResult().getIrregular18_3Reduction() );
	}

	public Double getDeducciblesExpenses() {
		return getDisplay( getIrpfResult().getDeducciblesExpenses() );
	}

	public Double getWorkRemunerationReduction() {
		return getDisplay( getIrpfResult().getWorkRemunerationReduction() );
	}

	public Double getWorkProlongationReduction() {
		return getDisplay( getIrpfResult().getWorkProlongationReduction() );
	}

	public Double getWorkMovingReduction() {
		return getDisplay( getIrpfResult().getWorkMovingReduction() );
	}

	public Double getWorkDisabilityReduction() {
		return getDisplay( getIrpfResult().getWorkDisabilityReduction() );
	}

	public Double getSocialSecurityPensioner() {
		return getDisplay( getIrpfResult().getSocialSecurityPensioner() );
	}

	public Double getTwoOrMoreDescendentsMin() {
		return getDisplay( getIrpfResult().getTwoOrMoreDescendentsMin() );
	}

	public Double getSpousalSupport() {
		return getDisplay( getIrpfResult().getSpousalSupport() );
	}

	public Double getFoodAnnuity() {
		return getDisplay( getIrpfResult().getFoodAnnuity() );
	}

	public Double getMinimunPersonal() {
		return getDisplay( getIrpfResult().getMinimunPersonal() );
	}

	public Double getMinimunAscendents() {
		return getDisplay( getIrpfResult().getMinimunAscendents() );
	}

	public Double getMinimunDescendents() {
		return getDisplay( getIrpfResult().getMinimunDescendents() );
	}

	public Double getMinimunDisability() {
		return getDisplay( getIrpfResult().getMinimunDisability() );
	}

	public Integer getDescendentsMinor3Total() {
		return getDisplay( getIrpfResult().getDescendentsMinor3Total() );
	}

	public Integer getDescendentsMinor3Entirely() {
		return getDisplay( getIrpfResult().getDescendentsMinor3Entirely() );
	}

	public Integer getDescendentsRemainderTotal() {
		return getDisplay( getIrpfResult().getDescendentsRemainderTotal() );
	}

	public Integer getDescendentsRemainderEntirely() {
		return getDisplay( getIrpfResult().getDescendentsRemainderEntirely() );
	}

	public Integer getDescendents33_65Total() {
		return getDisplay( getIrpfResult().getDescendents33_65Total() );
	}

	public Integer getDescendents33_65Entirely() {
		return getDisplay( getIrpfResult().getDescendents33_65Entirely() );
	}

	public Integer getDescendentsMovingTotal() {
		return getDisplay( getIrpfResult().getDescendentsMovingTotal() );
	}

	public Integer getDescendentsMovingEntirely() {
		return getDisplay( getIrpfResult().getDescendentsMovingEntirely() );
	}

	public Integer getDescendents65Total() {
		return getDisplay( getIrpfResult().getDescendents65Total() );
	}

	public Integer getDescendents65Entirely() {
		return getDisplay( getIrpfResult().getDescendents65Entirely() );
	}

	public Integer getDescendentsFirst() {
		return getDisplay( getIrpfResult().getDescendentsFirst() );
	}

	public Integer getDescendentsSecond() {
		return getDisplay( getIrpfResult().getDescendentsSecond() );
	}

	public Integer getDescendentsThird() {
		return getDisplay( getIrpfResult().getDescendentsThird() );
	}

	public Integer getDescendentsFourthSubsequentTotal() {
		return getDisplay( getIrpfResult().getDescendentsFourthSubsequentTotal() );
	}

	public Integer getDescendentsFourthSubsequentEntirely() {
		return getDisplay( getIrpfResult().getDescendentsFourthSubsequentEntirely() );
	}

	public Integer getAscendentsMinor75Total() {
		return getDisplay( getIrpfResult().getAscendentsMinor75Total() );
	}

	public Integer getAscendentsMinor75Entirely() {
		return getDisplay( getIrpfResult().getAscendentsMinor75Entirely() );
	}

	public Integer getAscendentsMayor75Total() {
		return getDisplay( getIrpfResult().getAscendentsMayor75Total() );
	}

	public Integer getAscendentsMayor75Entirely() {
		return getDisplay( getIrpfResult().getAscendentsMayor75Entirely() );
	}

	public Integer getAscendents33_65Total() {
		return getDisplay( getIrpfResult().getAscendents33_65Total() );
	}

	public Integer getAscendents33_65Entirely() {
		return getDisplay( getIrpfResult().getAscendents33_65Entirely() );
	}

	public Integer getAscendentsMovingTotal() {
		return getDisplay( getIrpfResult().getAscendentsMovingTotal() );
	}

	public Integer getAscendentsMovingEntirely() {
		return getDisplay( getIrpfResult().getAscendentsMovingEntirely() );
	}

	public Integer getAscendents65Total() {
		return getDisplay( getIrpfResult().getAscendents65Total() );
	}

	public Integer getAscendents65Entirely() {
		return getDisplay( getIrpfResult().getAscendents65Entirely() );
	}
	
	private IrpfResult getIrpfResult(){
		return ( IrpfResult ) getTo();
	}
	
	private Integer getDisplay(Integer value) {
		return value != null ? value : 0;
	}

	private Double getDisplay(Double value) {
		return value != null ? value : 0.00;
	}
	
}
