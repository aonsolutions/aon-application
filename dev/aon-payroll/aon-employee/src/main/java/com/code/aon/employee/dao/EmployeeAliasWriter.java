package com.code.aon.employee.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.employee.Agreement;
import com.code.aon.employee.AgreementLevel;
import com.code.aon.employee.AgreementLevelCategory;
import com.code.aon.employee.AgreementLevelPayment;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractBatch;
import com.code.aon.employee.ContractBatchDetail;
import com.code.aon.employee.ContractData;
import com.code.aon.employee.ContractDeduction;
import com.code.aon.employee.ContractPayment;
import com.code.aon.employee.ContractTracking;
import com.code.aon.employee.ContractType;
import com.code.aon.employee.DeductionConcept;
import com.code.aon.employee.FunctionConstant;
import com.code.aon.employee.PaymentConcept;
import com.code.aon.employee.Salary;
import com.code.aon.employee.SalaryDeduction;
import com.code.aon.employee.SalaryPayment;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class EmployeeAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/home/ecastellano/AON-PAYROLL/aon-employee/src/main/java/com/code/aon/employee/dao/IEmployeeAlias.java");
		String[] classes = new String[] { 
				Agreement.class.getName(),
				AgreementLevel.class.getName(),
				AgreementLevelCategory.class.getName(),
				AgreementLevelPayment.class.getName(),
				Contract.class.getName(),
				ContractBatch.class.getName(),
				ContractBatchDetail.class.getName(),
				ContractData.class.getName(),
				ContractDeduction.class.getName(),
				ContractPayment.class.getName(),
				ContractTracking.class.getName(),
				ContractType.class.getName(),
				DeductionConcept.class.getName(),
				FunctionConstant.class.getName(),
				PaymentConcept.class.getName(),
				Salary.class.getName(),
				SalaryPayment.class.getName(),
				SalaryDeduction.class.getName()
				};
		HibernateUtil.getSessionFactory( HibernateUtil.getSessionFactoryName() );
		AliasWriter writer = new AliasWriter("com.code.aon.employee.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}