package com.code.aon.finance.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.BankConcept;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.BankStatementLink;
import com.code.aon.finance.CashFlowForecast;
import com.code.aon.finance.Creditor;
import com.code.aon.finance.CustomerFee;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceAddress;
import com.code.aon.finance.InvoiceAttachment;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.finance.InvoicingGroup;
import com.code.aon.finance.InvoicingGroupDetail;

public class FinanceAliasWriter {
	
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-finance/src/main/java/com/code/aon/finance/dao/IFinanceAlias.java");
		String[] classes = new String[]{
			BankConcept.class.getName(),
			BankStatement.class.getName(),
			BankStatementLink.class.getName(),
			CashFlowForecast.class.getName(),
			CustomerFee.class.getName(),
			Finance.class.getName(),
			FinanceBatch.class.getName(),
			FinanceBatchDetail.class.getName(),
			Invoice.class.getName(),
			InvoiceAddress.class.getName(),
			InvoiceAttachment.class.getName(),
			InvoiceDetail.class.getName(),
			InvoiceTax.class.getName(),
			Creditor.class.getName(),
			FinanceTracking.class.getName(),
			InvoicingGroup.class.getName(),
			InvoicingGroupDetail.class.getName()
		};
		HibernateUtil.getSessionFactory(HibernateUtil.getSessionFactoryName());
		AliasWriter writer = new AliasWriter("com.code.aon.finance.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}