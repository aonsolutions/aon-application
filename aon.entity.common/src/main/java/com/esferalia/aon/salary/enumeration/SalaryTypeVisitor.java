package com.esferalia.aon.salary.enumeration;

public interface SalaryTypeVisitor<E> {

	E visitSalary(SalaryType salaryType);

	E visitExtra(SalaryType salaryType);

	E visitSettle(SalaryType salaryType);

	E visitDelay(SalaryType salaryType);

	E visitProcedural(SalaryType salaryType);

	default E visitL00(SalaryType salaryType) {return visitSalary(salaryType);};

	default E visitL02(SalaryType salaryType) {return visitProcedural(salaryType);};

	default E visitL03(SalaryType salaryType) {return visitDelay(salaryType); };

	default E visitL13(SalaryType salaryType) {return visitSettle(salaryType);};

	default E visitM190(SalaryType salaryType) {return visitSalary(salaryType);};
}
