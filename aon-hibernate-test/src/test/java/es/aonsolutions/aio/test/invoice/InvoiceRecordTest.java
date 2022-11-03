package es.aonsolutions.aio.test.invoice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.code.aon.account.bridge.writer.AccountEntryInvoiceWriter;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

import es.aonsolutions.aio.test.AonHibernateTestBasic;
import es.aonsolutions.aio.test.util.Asserts;

class InvoiceRecordTest extends AonHibernateTestBasic {
	
	private AccountEntryInvoiceWriter accountEntryInvoiceWriter;
	
	public AccountEntryInvoiceWriter getAccountEntryInvoiceWriter() {
		if (accountEntryInvoiceWriter == null) {
			accountEntryInvoiceWriter = new AccountEntryInvoiceWriter();
		}
		return accountEntryInvoiceWriter;
	}
	
	@Test
	void testInvoiceRecord() throws Exception {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_STATUS), InvoiceStatus.PENDING);
		bean.getList( c )
			.stream()
			.map( to -> (Invoice) to)
			.forEach( inv->  assertDoesNotThrow( () ->  getAccountEntryInvoiceWriter().recordAndUpdateInvoice( inv )) );
		List<ITransferObject> list =  bean.getList( c );
		Asserts.assertEmptyCollection("Quedan facturas sin contabilizar", list);
	}
	
}
