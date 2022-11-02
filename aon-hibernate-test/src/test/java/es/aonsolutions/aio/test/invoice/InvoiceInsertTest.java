package es.aonsolutions.aio.test.invoice;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.List;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;

import es.aonsolutions.aio.test.AonHibernateTestBasic;
import es.aonsolutions.aio.test.AonHibernateTestFaker;

class InvoiceInsertTest extends AonHibernateTestBasic {

	@Test
	void testInsertInvoice() throws Exception {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.setBeginTransaction(false);
			HibernateUtil.setCloseSession(false);

			HibernateUtil.beginTransaction(sessionName);

			for (int i = 0; i < 50;i++) {
				Invoice invoice = AonHibernateTestFaker.getInvoice();
				IManagerBean bean = BeanManager.getManagerBean(Invoice.class);
				IManagerBean detailBean = BeanManager.getManagerBean(InvoiceDetail.class);
				Invoice inv = (Invoice) assertDoesNotThrow( () -> bean.insert(invoice),"Fallo al insertar Invoice" );
				List<InvoiceDetail> details = AonHibernateTestFaker.getInvoiceDetails( inv );
				for ( InvoiceDetail detail : details) {
					assertDoesNotThrow( () -> detailBean.insert(detail),"Fallo al insertar Invoice Detail" );	
				}
				System.out.println("Inserting invoice #" + i);
			}
			
			HibernateUtil.getSession(sessionName).flush();
			HibernateUtil.commitTransaction(sessionName);
			
		} catch (Exception e) {
			HibernateUtil.rollbackTransaction(sessionName);
			throw new ManagerBeanException(e.getMessage(),e);
		} finally {
			HibernateUtil.closeSession(sessionName);
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
			
		
	}

}
