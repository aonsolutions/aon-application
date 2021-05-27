package net.aonsolutions.payroll.report;

import static com.esferalia.aon.salary.enumeration.DeductionType.ADVANCE_PAYMENT;
import static com.esferalia.aon.salary.enumeration.DeductionType.COMMON_CONTINGENCY;
import static com.esferalia.aon.salary.enumeration.DeductionType.IRPF;
import static com.esferalia.aon.salary.enumeration.DeductionType.JOB_TRAINING;
import static com.esferalia.aon.salary.enumeration.DeductionType.NON_STRUCTURAL_OVERTIME;
import static com.esferalia.aon.salary.enumeration.DeductionType.OTHER;
import static com.esferalia.aon.salary.enumeration.DeductionType.STRUCTURAL_OVERTIME;
import static com.esferalia.aon.salary.enumeration.DeductionType.UNEMPLOYMENT;

import com.esferalia.aon.salary.deduction.IDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;

public interface Deduction extends IDeduction, HasType<DeductionType>, HasName, HasAmount, HasDescription, HasExpression{
	

	default boolean isIT() {
		return 
		( getName() != null 
		&& getName().equals("IT_E") );
	}


	default boolean isIMS() {
		return 
		( getName() != null 
		&& getName().equals("IMS_E") );
	}

	default boolean isCommonContingency() {
		return 
		( getType() != null 
		&& getType() == COMMON_CONTINGENCY );
	}

	default boolean isUnemployment() {
		return 
		( getType() != null 
		&& getType() == UNEMPLOYMENT );
	}

	default boolean isJobtraining() {
		return 
		( getType() != null 
		&& getType() == JOB_TRAINING );
	}
	
	default boolean isStructuralOvertime() {
		return 
		( getType() != null 
		&& getType() == STRUCTURAL_OVERTIME );
	}
	
	default boolean isNonStructuralOvertime() {
		return 
		( getType() != null 
		&& getType() == NON_STRUCTURAL_OVERTIME );
	}

	default boolean isIrpf() {
		return 
		( getType() != null 
		&& getType() == IRPF );
	}
	
	default boolean isAdvancePayment() {
		return 
		( getType() != null 
		&& getType() == ADVANCE_PAYMENT );
	}

	default boolean isOther() {
		return 
		( getType() != null 
		&& getType() == OTHER);
	}

	default boolean isFogasa() {
		return 
		( getType() != null 
		&& getType() == DeductionType.FOGASA );
	}

	
}
