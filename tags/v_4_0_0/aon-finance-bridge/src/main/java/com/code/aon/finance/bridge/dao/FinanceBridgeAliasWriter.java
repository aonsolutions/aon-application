package com.code.aon.finance.bridge.dao;

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
import com.code.aon.finance.bridge.FinanceSales;

/**
 * @author Consulting & Development. jurkiri - 22/01/2007
 *
 */
public class FinanceBridgeAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-PROJECT/aon-finance-bridge/src/main/java/com/code/aon/finance/bridge/dao/IFinanceBridgeAlias.java");
		String[] classes = new String[1]; 
		classes[0] = FinanceSales.class.getName();
		HibernateUtil.getSessionFactory();
		AliasWriter writer = new AliasWriter("com.code.aon.finance.bridge.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}