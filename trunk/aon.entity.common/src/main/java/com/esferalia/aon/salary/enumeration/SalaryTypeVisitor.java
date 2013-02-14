package com.esferalia.aon.salary.enumeration;

public interface SalaryTypeVisitor<E> {

	E visitSalary(SalaryType salaryType);

	E visitExtra(SalaryType salaryType);

	E visitSettle(SalaryType salaryType);

	E visitDelay(SalaryType salaryType);

	E visitNotEnjoyedVacations(SalaryType salaryType);

}
