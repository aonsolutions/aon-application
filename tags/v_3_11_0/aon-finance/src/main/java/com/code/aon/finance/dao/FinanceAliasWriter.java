package com.code.aon.finance.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.finance.InvoicingGroupDetail;

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
		File file = new File("/AON-PROJECT/aon-finance/src/main/java/com/code/aon/finance/dao/IFinanceAlias.java");
		String[] classes = new String[12]; 
		classes[0] = CustomerFee.class.getName();
		classes[1] = Finance.class.getName();
		classes[2] = FinanceBatch.class.getName();
		classes[3] = FinanceBatchDetail.class.getName();
		classes[4] = Invoice.class.getName();
		classes[5] = InvoiceAddress.class.getName();
		classes[6] = InvoiceDetail.class.getName();
		classes[7] = InvoiceTax.class.getName();
		classes[8] = Creditor.class.getName();
		classes[9] = FinanceTracking.class.getName();
		classes[10] = InvoicingGroup.class.getName();
		classes[11] = InvoicingGroupDetail.class.getName();
		HibernateUtil.getSessionFactory(null);
		AliasWriter writer = new AliasWriter("com.code.aon.finance.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}