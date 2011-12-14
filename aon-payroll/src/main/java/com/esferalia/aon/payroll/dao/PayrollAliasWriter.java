package com.esferalia.aon.payroll.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.esferalia.aon.payroll.Agreement;
import com.esferalia.aon.payroll.AgreementData;
import com.esferalia.aon.payroll.AgreementExtra;
import com.esferalia.aon.payroll.AgreementLevel;
import com.esferalia.aon.payroll.AgreementLevelCategory;
import com.esferalia.aon.payroll.AgreementLevelData;
import com.esferalia.aon.payroll.AgreementPayment;
import com.esferalia.aon.payroll.BonusConcept;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.Certifica2Batch;
import com.esferalia.aon.payroll.Certifica2BatchAttachment;
import com.esferalia.aon.payroll.Certifica2BatchData;
import com.esferalia.aon.payroll.Certifica2BatchDetail;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractAttachment;
import com.esferalia.aon.payroll.ContractBatch;
import com.esferalia.aon.payroll.ContractBatchAttachment;
import com.esferalia.aon.payroll.ContractBatchDetail;
import com.esferalia.aon.payroll.ContractBonus;
import com.esferalia.aon.payroll.ContractCalendarEvent;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.ContractDeduction;
import com.esferalia.aon.payroll.ContractEmbargo;
import com.esferalia.aon.payroll.ContractLeave;
import com.esferalia.aon.payroll.ContractLeaveDetail;
import com.esferalia.aon.payroll.ContractPayment;
import com.esferalia.aon.payroll.DeductionConcept;
import com.esferalia.aon.payroll.EnterpriseActivity;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.FanBatch;
import com.esferalia.aon.payroll.FanBatchAttachment;
import com.esferalia.aon.payroll.FanBatchDetail;
import com.esferalia.aon.payroll.GeozoneIrpf;
import com.esferalia.aon.payroll.GeozoneIrpfDescendant;
import com.esferalia.aon.payroll.GeozoneIrpfHandicap;
import com.esferalia.aon.payroll.IrpfRegularization;
import com.esferalia.aon.payroll.IrpfResult;
import com.esferalia.aon.payroll.LeaveBatch;
import com.esferalia.aon.payroll.LeaveBatchAttachment;
import com.esferalia.aon.payroll.LeaveBatchDetail;
import com.esferalia.aon.payroll.IrpfData;
import com.esferalia.aon.payroll.IrpfDataAscendants;
import com.esferalia.aon.payroll.IrpfDataDescendients;
import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.PayrollWorkPlace;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.SalaryBonus;
import com.esferalia.aon.payroll.SalaryCost;
import com.esferalia.aon.payroll.SalaryDeduction;
import com.esferalia.aon.payroll.SalaryEmbargo;
import com.esferalia.aon.payroll.SalaryPayment;
import com.esferalia.aon.payroll.SystemCost;
import com.esferalia.aon.payroll.SystemData;
import com.esferalia.aon.payroll.SystemDeduction;
import com.esferalia.aon.payroll.SystemPayment;

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
//		File file = new File("/home/rtrepiana/workspace/aon-payroll/src/main/java/com/esferalia/aon/payroll/dao/IPayrollAlias.java");
		String[] classes = new String[] { 
				Agreement.class.getName(),
				AgreementData.class.getName(),
				AgreementExtra.class.getName(),
				AgreementLevel.class.getName(),
				AgreementLevelCategory.class.getName(),
				AgreementLevelData.class.getName(),
				AgreementPayment.class.getName(),
				BonusConcept.class.getName(),
				Certifica2Batch.class.getName(),
				Certifica2BatchAttachment.class.getName(),
				Certifica2BatchData.class.getName(),
				Certifica2BatchDetail.class.getName(),
				CNO.class.getName(),
				Contract.class.getName(),
				ContractAttachment.class.getName(),
				ContractBatch.class.getName(),
				ContractBatchAttachment.class.getName(),
				ContractBatchDetail.class.getName(),
				ContractBonus.class.getName(),
				ContractCalendarEvent.class.getName(),
				ContractData.class.getName(),
				ContractDeduction.class.getName(),
				ContractEmbargo.class.getName(),
				ContractLeave.class.getName(),
				ContractLeaveDetail.class.getName(),
				ContractPayment.class.getName(),
				DeductionConcept.class.getName(),
				EnterpriseCCC.class.getName(),
				EnterpriseActivity.class.getName(),
				FanBatch.class.getName(),
				FanBatchAttachment.class.getName(),
				FanBatchDetail.class.getName(),
				GeozoneIrpf.class.getName(),
				GeozoneIrpfDescendant.class.getName(),
				GeozoneIrpfHandicap.class.getName(),
				LeaveBatch.class.getName(),
				LeaveBatchAttachment.class.getName(),
				LeaveBatchDetail.class.getName(),
				IrpfData.class.getName(),
				IrpfDataAscendants.class.getName(),
				IrpfDataDescendients.class.getName(),
				IrpfRegularization.class.getName(),
				IrpfResult.class.getName(),
				PaymentConcept.class.getName(),
				PayrollWorkPlace.class.getName(),
				Salary.class.getName(),
				SalaryBonus.class.getName(),
				SalaryCost.class.getName(),
				SalaryDeduction.class.getName(),
				SalaryEmbargo.class.getName(),
				SalaryPayment.class.getName(),
				SystemCost.class.getName(),
				SystemData.class.getName(),
				SystemDeduction.class.getName(),
				SystemPayment.class.getName()
				};
		HibernateUtil.getSessionFactory( HibernateUtil.getSessionFactoryName() );
		AliasWriter writer = new AliasWriter("com.esferalia.aon.payroll.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}