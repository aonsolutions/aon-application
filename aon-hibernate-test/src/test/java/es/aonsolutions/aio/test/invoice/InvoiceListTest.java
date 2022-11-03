package es.aonsolutions.aio.test.invoice;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.finance.Invoice;
import com.code.aon.ql.Criteria;

import es.aonsolutions.aio.test.AonHibernateTestBasic;
import es.aonsolutions.aio.test.util.Asserts;

class InvoiceListTest extends AonHibernateTestBasic {
	
	@Test
	void testListInvoice() throws Exception {
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		Criteria c = new Criteria();
		List<ITransferObject> list = bean.getList(c,0,10);
		Asserts.assertNotEmptyCollection( "Empty invoice list", list);
	}
	
}
