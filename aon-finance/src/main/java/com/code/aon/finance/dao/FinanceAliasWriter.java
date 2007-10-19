package com.code.aon.finance.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.finance.Bank;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.finance.InvoicingGroupDetail;
import com.code.aon.finance.PayMethod;
import com.code.aon.finance.RegistryBank;
import com.code.aon.finance.RegistryPayMethod;

/**
 * @author Consulting & Development. jurkiri - 22/01/2007
 *
 */
public class FinanceAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/PROYECTOS/aon-finance/src/com/code/aon/finance/dao/IFinanceAlias.java");
//		File file = new File("c:/IFinanceAlias.java");
		String[] classes = new String[14]; 
		classes[0] = Bank.class.getName();
		classes[1] = Finance.class.getName();
		classes[2] = FinanceBatch.class.getName();
		classes[3] = FinanceBatchDetail.class.getName();
		classes[4] = Invoice.class.getName();
		classes[5] = InvoiceDetail.class.getName();
		classes[6] = InvoiceTax.class.getName();
		classes[7] = PayMethod.class.getName();
		classes[8] = RegistryBank.class.getName();
		classes[9] = RegistryPayMethod.class.getName();
		classes[10] = Creditor.class.getName();
		classes[11] = FinanceTracking.class.getName();
		classes[12] = InvoicingGroup.class.getName();
		classes[13] = InvoicingGroupDetail.class.getName();
		AliasWriter writer = new AliasWriter("com.code.aon.finance.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}