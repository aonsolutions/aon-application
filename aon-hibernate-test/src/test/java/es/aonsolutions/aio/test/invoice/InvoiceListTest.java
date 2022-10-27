package es.aonsolutions.aio.test.invoice;

import java.util.List;

import org.junit.Test;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;

import es.aonsolutions.aio.test.AonHibernateTestBasic;
import es.aonsolutions.aio.test.util.Asserts;

public class InvoiceListTest extends AonHibernateTestBasic {
	
	@Test
	public void testListAccount() throws Exception {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria c = new Criteria();
		c.addEqualExpression( "Invoice.domain" , 400);
		List<ITransferObject> list = bean.getList(c,100,1);
		Asserts.assertNotEmptyCollection( "Empty invoice list", list);
		Invoice inv = (Invoice) list.get(0);
		
		InvoicePriceStrategy s = new InvoicePriceStrategy();
		List<TaxBreakDown> taxes = s.getTaxBreakDowns( inv, inv );
		Asserts.assertNotEmptyCollection( "Empty invoice breakdown list", taxes);
		
	}


}
