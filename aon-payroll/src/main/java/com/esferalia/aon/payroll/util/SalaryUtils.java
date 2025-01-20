package com.esferalia.aon.payroll.util;

import com.esferalia.aon.salary.ISalary;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class SalaryUtils {
	
	
	
	public static boolean equalsAmounts( ISalary salary1, ISalary salary2) {
		return
		AonNumberUtils.equals(salary1.getCommonBase(), salary2.getCommonBase())
		&& AonNumberUtils.equals(salary1.getProfessionalBase(), salary2.getProfessionalBase())
		&& AonNumberUtils.equals(salary1.getExtraPayProration(), salary2.getExtraPayProration())
		&& AonNumberUtils.equals(salary1.getOvertimeBase(), salary2.getOvertimeBase())
		&& AonNumberUtils.equals(salary1.getNonEstructuralOvertimeBase(), salary2.getNonEstructuralOvertimeBase())

		&& AonNumberUtils.equals(salary1.getTotalIrpf(), salary2.getTotalIrpf())
		&& AonNumberUtils.equals(salary1.getIrpfBase(), salary2.getIrpfBase())
		&& AonNumberUtils.equals(salary1.getInKindIrpfBase(), salary2.getInKindIrpfBase())
		&& AonNumberUtils.equals(salary1.getInMoneyIrpfBase(), salary2.getInMoneyIrpfBase())

		&& AonNumberUtils.equals(salary1.getTotalLiquid(), salary2.getTotalLiquid())
		&& AonNumberUtils.equals(salary1.getTotalPayment(), salary2.getTotalPayment())
		&& AonNumberUtils.equals(salary1.getTotalDeduction(), salary2.getTotalDeduction())
		&& AonNumberUtils.equals(salary1.getTotalEnterprise(), salary2.getTotalEnterprise())
		;
		
	}

	public static boolean equalsIrpfAmounts( ISalary salary1, ISalary salary2) {
		return equalsAmounts(salary1, salary2);
	}
}
