package es.aonsolutions.aio.test.product;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.RepeatedTest;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.product.Item;
import com.code.aon.product.Product;

import es.aonsolutions.aio.test.AonHibernateTestBasic;
import es.aonsolutions.aio.test.AonHibernateTestFaker;


class ItemTest extends AonHibernateTestBasic {
	
	@RepeatedTest( 20 )
	void testInsertAccount() throws Exception {
		Item item = AonHibernateTestFaker.getnewItem();
		Product product = item.getProduct();
		if ( product != null &&  product.getId() == null ) {
			IManagerBean bean = BeanManager.getManagerBean(Product.class);
			Product prod = (Product) assertDoesNotThrow( () -> bean.insert(product),"Fallo al insertar Product" );
			item.setProduct(prod);
		}
		IManagerBean bean = BeanManager.getManagerBean(Item.class);
		assertDoesNotThrow( () -> bean.insert(item),"Fallo al insertar item" );
	}

}
