package es.aonsolutions.aio.test.invoice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.TestInfo;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.util.SeriesNumberUtil;
import com.code.aon.customer.Customer;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.invoicing.finance.FinanceGenerator;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

import es.aonsolutions.aio.test.AonHibernateTestBasic;
import es.aonsolutions.aio.test.AonHibernateTestFaker;
import es.aonsolutions.aio.test.AonHibernateTestRandom;

class InvoiceInsertTest extends AonHibernateTestBasic {

	@RepeatedTest(value = 10)
	void testInsertInvoice(TestInfo testInfo) throws Exception {
		Invoice invoice = AonHibernateTestFaker.getInvoice();
		IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
		IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
		Invoice inv = (Invoice) assertDoesNotThrow( () -> bean.insert(invoice),"Fallo al insertar Invoice" );
		List<InvoiceDetail> details = AonHibernateTestFaker.getInvoiceDetails( inv );
		for ( InvoiceDetail detail : details) {
			assertDoesNotThrow( () -> detailBean.insert(detail),"Fallo al insertar Invoice Detail" );	
		}
		inv = (Invoice) bean.get(inv.getId());
		new FinanceGenerator().generateFinances( inv, inv.getTotal());
	}

	public static Invoice getSalesInvoice( ) throws ManagerBeanException  {
		Invoice inv = new Invoice();
		inv.setType(InvoiceType.SALES);
		inv.setSeries("2022");
		Criteria criteria = new Criteria();
		criteria.addEqualExpression("invoice.type", InvoiceType.SALES.ordinal());
		inv.setNumber(SeriesNumberUtil.obtainNumber(inv.getSeries(), "Invoice", criteria));
		Customer customer = getCustomer();
		inv.setRegistry( customer==null?null:customer.getRegistry() );
		return inv;
	}

	public static Customer getCustomer() throws ManagerBeanException  {
		IManagerBean bean = BeanManager.getManagerBean(Customer.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.CUSTOMER_REGISTRY_ID), 31);
		int count = bean.getCount(c);
		if (count > 0) {
			List<ITransferObject> customers = bean.getList(c
				,AonHibernateTestRandom.number( 0, count-1 ),1);
			if ( customers != null && !customers.isEmpty()) {
				return (Customer) customers.get(0);
			}
		}
		return null;
	}
}
