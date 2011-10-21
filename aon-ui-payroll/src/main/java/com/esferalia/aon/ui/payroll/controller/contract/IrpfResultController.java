package com.esferalia.aon.ui.payroll.controller.contract;

import java.util.Date;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.IrpfRegularization;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.dao.IPayrollAlias;
import com.esferalia.aon.payroll.enumeration.IrpfRegularizationReason;
import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;

public class IrpfResultController extends BasicController implements
		IPayrollConstants, IIrpfController {
	
	
	IIrpfController controller = 
		new AbstractIrpfController() {
		
		@Override
		public IrpfResult getIrpfResult() {
			return ( IrpfResult ) getTo();
		}
	};

	public Integer getBirthYear() {
		return controller.getBirthYear();
	}

	public String getDocument() {
		return controller.getDocument();
	}

	public Date getEffectiveDate() {
		return controller.getEffectiveDate();
	}

	public Integer getEffectiveYear() {
		return controller.getEffectiveYear();
	}

	public Double getBaseIrpf() {
		return controller.getBaseIrpf();
	}

	public Double getMinimunPersonalFamily() {
		return controller.getMinimunPersonalFamily();
	}

	public Double getDeductHomeLoanAmount() {
		return controller.getDeductHomeLoanAmount();
	}

	public Double getDeduct80Bis() {
		return controller.getDeduct80Bis();
	}

	public Double getIrpf() {
		return controller.getIrpf();
	}

	public Double getAnnualIrpf() {
		return controller.getAnnualIrpf();
	}

	public Double getAnnualRemuneration() {
		return controller.getAnnualRemuneration();
	}

	public Double getIrregular18_2Reduction() {
		return controller.getIrregular18_2Reduction();
	}

	public Double getIrregular18_3Reduction() {
		return controller.getIrregular18_3Reduction();
	}

	public Double getDeducciblesExpenses() {
		return controller.getDeducciblesExpenses();
	}

	public Double getWorkRemunerationReduction() {
		return controller.getWorkRemunerationReduction();
	}

	public Double getWorkProlongationReduction() {
		return controller.getWorkProlongationReduction();
	}

	public Double getWorkMovingReduction() {
		return controller.getWorkMovingReduction();
	}

	public Double getWorkDisabilityReduction() {
		return controller.getWorkDisabilityReduction();
	}

	public Double getSocialSecurityPensioner() {
		return controller.getSocialSecurityPensioner();
	}

	public Double getTwoOrMoreDescendentsMin() {
		return controller.getTwoOrMoreDescendentsMin();
	}

	public Double getSpousalSupport() {
		return controller.getSpousalSupport();
	}

	public Double getFoodAnnuity() {
		return controller.getFoodAnnuity();
	}

	public Double getMinimunPersonal() {
		return controller.getMinimunPersonal();
	}

	public Double getMinimunAscendents() {
		return controller.getMinimunAscendents();
	}

	public Double getMinimunDescendents() {
		return controller.getMinimunDescendents();
	}

	public Double getMinimunDisability() {
		return controller.getMinimunDisability();
	}

	public Integer getDescendentsMinor3Total() {
		return controller.getDescendentsMinor3Total();
	}

	public Integer getDescendentsMinor3Entirely() {
		return controller.getDescendentsMinor3Entirely();
	}

	public Integer getDescendentsRemainderTotal() {
		return controller.getDescendentsRemainderTotal();
	}

	public Integer getDescendentsRemainderEntirely() {
		return controller.getDescendentsRemainderEntirely();
	}

	public Integer getDescendents33_65Total() {
		return controller.getDescendents33_65Total();
	}

	public Integer getDescendents33_65Entirely() {
		return controller.getDescendents33_65Entirely();
	}

	public Integer getDescendentsMovingTotal() {
		return controller.getDescendentsMovingTotal();
	}

	public Integer getDescendentsMovingEntirely() {
		return controller.getDescendentsMovingEntirely();
	}

	public Integer getDescendents65Total() {
		return controller.getDescendents65Total();
	}

	public Integer getDescendents65Entirely() {
		return controller.getDescendents65Entirely();
	}

	public Integer getDescendentsFirst() {
		return controller.getDescendentsFirst();
	}

	public Integer getDescendentsSecond() {
		return controller.getDescendentsSecond();
	}

	public Integer getDescendentsThird() {
		return controller.getDescendentsThird();
	}

	public Integer getDescendentsFourthSubsequentTotal() {
		return controller.getDescendentsFourthSubsequentTotal();
	}

	public Integer getDescendentsFourthSubsequentEntirely() {
		return controller.getDescendentsFourthSubsequentEntirely();
	}

	public Integer getAscendentsMinor75Total() {
		return controller.getAscendentsMinor75Total();
	}

	public Integer getAscendentsMinor75Entirely() {
		return controller.getAscendentsMinor75Entirely();
	}

	public Integer getAscendentsMayor75Total() {
		return controller.getAscendentsMayor75Total();
	}

	public Integer getAscendentsMayor75Entirely() {
		return controller.getAscendentsMayor75Entirely();
	}

	public Integer getAscendents33_65Total() {
		return controller.getAscendents33_65Total();
	}

	public Integer getAscendents33_65Entirely() {
		return controller.getAscendents33_65Entirely();
	}

	public Integer getAscendentsMovingTotal() {
		return controller.getAscendentsMovingTotal();
	}

	public Integer getAscendentsMovingEntirely() {
		return controller.getAscendentsMovingEntirely();
	}

	public Integer getAscendents65Total() {
		return controller.getAscendents65Total();
	}

	public Integer getAscendents65Entirely() {
		return controller.getAscendents65Entirely();
	}

	public Double getPaidIrpf() {
		return controller.getPaidIrpf();
	}

	public IrpfRegularizationReason getReason() {
		return controller.getReason();
	}

	public Double getPaidRemuneration() {
		return controller.getPaidRemuneration();
	}

	public Double getPriorAnnualIrpf() {
		return controller.getPriorAnnualIrpf();
	}

	public Double getPriorAnnualRemuneration() {
		return controller.getPriorAnnualRemuneration();
	}

	public Double getPriorBaseIrpf() {
		return controller.getPriorBaseIrpf();
	}

	public Double getPriorIrpf() {
		return controller.getPriorIrpf();
	}

	public Double getPriorMinimunPersonalFamily() {
		return controller.getPriorMinimunPersonalFamily();
	}

	public Double getPriorDeductHomeLoanAmount() {
		return controller.getPriorDeductHomeLoanAmount();
	}

	public Administration getAdministration() {
		return controller.getAdministration();
	}
	
	
	
	

}
