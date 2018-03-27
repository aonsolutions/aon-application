package net.aonsolutions.payroll.report;

import com.esferalia.aon.salary.enumeration.PaymentType;

public interface Payment extends HasId, HasName, HasType<PaymentType>, HasAmount, HasDescription, HasExpression{
	
	default Integer getCRA() {
		return getType().ordinal();
	};

	default boolean isExpense() {
		return 
		( getCRA() != null 
		&& getCRA() >= 27
		&& getCRA() <= 41 );
	}

	default boolean isSalaryInKind() {
		return 
		( getCRA() != null 
		&& getCRA() >= 13 
		&& getCRA() <= 26 );
	}
	
	default boolean isOtherNonWage() {
		return 
		( getCRA() != null 
		&& getCRA() >= 51
		&& getCRA() <= 54 );
	}

	default boolean isSpecialBonus() {
		return 
		( getCRA() != null 
		&& getCRA() >= 4
		&& getCRA() <= 5 );
	}

	default boolean isOvertimeHour() {
		return 
		( getCRA() != null 
		&& getCRA() >= 3 );
	}

	default boolean isNonOvertimeHour() {
		return 
		( getCRA() != null 
		&& getCRA() >= 2 );
	}

	default boolean isBaseSalary() {
		return 
		( getCRA() != null 
		&& getCRA() >= 1 
		&& "SALARIO_BASE".equals(getName())
		);
	}

	default boolean isSupplement() {
		return 
		!isExpense()
		&& !isOtherNonWage()
		&& !isSpecialBonus()
		&& !isSalaryInKind()
		&& !isOvertimeHour()
		&& !isNonOvertimeHour()
		;
	}
}