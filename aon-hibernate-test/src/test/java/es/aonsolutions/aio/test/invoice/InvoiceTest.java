package es.aonsolutions.aio.test.invoice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

import es.aonsolutions.aio.test.AonHibernateTestBasic;
import es.aonsolutions.aio.test.AonHibernateTestFaker;
import es.aonsolutions.aio.test.util.Asserts;

class InvoiceTest extends AonHibernateTestBasic {
	
	@Test
	void testListInvoice() throws Exception {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.INVOICE_TRANSACTION), InvoiceTransactionType.NATIONAL);
		List<ITransferObject> list = bean.getList(c,0,10);
		Asserts.assertNotEmptyCollection( "Empty invoice list", list);
		Invoice inv = (Invoice) list.get(0);
		
		InvoicePriceStrategy s = new InvoicePriceStrategy();
		List<TaxBreakDown> taxes = s.getTaxBreakDowns( inv, inv );
		Asserts.assertNotEmptyCollection( "Empty invoice breakdown list", taxes);
		
	}
	
	@RepeatedTest( 50 )
	//@Test
	void testInsertInvoice() throws Exception {
		Invoice invoice = AonHibernateTestFaker.getInvoice();
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Invoice inv = (Invoice) assertDoesNotThrow( () -> bean.insert(invoice),"Fallo al insertar Invoice" );
		List<InvoiceDetail> details = AonHibernateTestFaker.getInvoiceDetails( inv );
		for ( InvoiceDetail detail : details) {
			assertDoesNotThrow( () -> detailBean.insert(detail),"Fallo al insertar Invoice Detail" );	
		}
		System.out.println("Invoice Inserted!"); 
	}
	
	

}
