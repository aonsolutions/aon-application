package com.esferalia.aon.payroll.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelCategory;
import com.esferalia.aon.payroll.AgreementLevelPayment;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractBatch;
import com.esferalia.aon.payroll.ContractBatchDetail;
import com.esferalia.aon.payroll.ContractCalendarEvent;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.DeductionConcept;
import com.esferalia.aon.payroll.EnterpriseAgreement;
import com.esferalia.aon.payroll.EnterpriseCertificate;
import com.esferalia.aon.payroll.EnterpriseCertificateDetail;
import com.esferalia.aon.payroll.FunctionConstant;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryPayment;

/**
 * @author Consulting & Development. ecastellano - 22/01/2007
 *
 */
public class PayrollAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-payroll/src/main/java/com/esferalia/aon/payroll/dao/IPayrollAlias.java");
		String[] classes = new String[] { 
				Agreement.class.getName(),
				AgreementLevel.class.getName(),
				AgreementLevelCategory.class.getName(),
				AgreementLevelPayment.class.getName(),
				Contract.class.getName(),
				ContractBatch.class.getName(),
				ContractBatchDetail.class.getName(),
				ContractCalendarEvent.class.getName(),
				ContractData.class.getName(),
				ContractDeduction.class.getName(),
				ContractLeave.class.getName(),
				ContractLeaveDetail.class.getName(),
				ContractPayment.class.getName(),
				DeductionConcept.class.getName(),
				EnterpriseAgreement.class.getName(),
				EnterpriseCertificate.class.getName(),
				EnterpriseCertificateDetail.class.getName(),
				FunctionConstant.class.getName(),
				PaymentConcept.class.getName(),
				Salary.class.getName(),
				SalaryPayment.class.getName(),
				SalaryDeduction.class.getName()
				};
		HibernateUtil.getSessionFactory( HibernateUtil.getSessionFactoryName() );
		AliasWriter writer = new AliasWriter("com.esferalia.aon.payroll.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}