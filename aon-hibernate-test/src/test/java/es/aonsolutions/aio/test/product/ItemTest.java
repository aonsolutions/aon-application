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
	
	@RepeatedTest( 5 )
	void testInsertCommercialItem() throws Exception {
		testInsertItem( AonHibernateTestFaker.getNewCommercialItem() );
	}
	
	@RepeatedTest( 5 )
	void testInsertServiceItem() throws Exception {
		testInsertItem( AonHibernateTestFaker.getNewServiceItem() );
	}
	
	@RepeatedTest( 5 )
	void testInsertExpenseItem() throws Exception {
		Item item = AonHibernateTestFaker.getNewExpenseItem();
		Product product = item.getProduct();
		if ( product != null && product.getId() == null ) {
			product.setPurchaseAccount( AonHibernateTestFaker.getAccount("629") );
			IManagerBean bean = BeanManager.getManagerBean(Product.class);
			Product prod = (Product) assertDoesNotThrow( () -> bean.insert(product),"Fallo al insertar Product" );
			item.setProduct(prod);
		}
		IManagerBean bean = BeanManager.getManagerBean(Item.class);
		assertDoesNotThrow( () -> bean.insert(item),"Fallo al insertar item" );
	}

	private void testInsertItem( Item item ) throws Exception {
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
