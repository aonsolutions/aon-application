package es.aonsolutions.aio.test.product;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.product.Product;

import es.aonsolutions.aio.test.AonHibernateTestFaker;
import es.aonsolutions.aio.test.AonHibernateTestBasic;


class ProductTest extends AonHibernateTestBasic {
	
	@Test
	void testInsertAccount() throws Exception {
		Product product = AonHibernateTestFaker.getNewProduct();
		IManagerBean bean = BeanManager.getManagerBean(Product.class);
		assertDoesNotThrow( () -> bean.insert(product),"Fallo al insertar producto" );
	}

}
