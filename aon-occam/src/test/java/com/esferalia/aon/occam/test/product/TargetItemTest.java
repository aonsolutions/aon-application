package com.esferalia.aon.occam.test.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.RepeatedTest;

import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryItem;
import com.esferalia.aon.occam.api.model.registry.RegistryItemStatus;
import com.esferalia.aon.occam.api.model.registry.RegistryMode;
import com.esferalia.aon.occam.api.model.type.Priority;
import com.esferalia.aon.occam.impl.jooq.dao.CustomerDAO;
import com.esferalia.aon.occam.impl.jooq.dao.InvoiceOLDDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ItemDAO;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.occam.impl.jooq.dao.RegistryOldDAO;
import com.esferalia.aon.occam.impl.jooq.dao.TargetItemDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.occam.test.faker.InvoiceFaker.InvoiceFakerParams;
import com.esferalia.aon.watson.error.AonCoreException;

public class TargetItemTest extends AbstractOccamTest {

	@RepeatedTest(10)
	public void testRegistryItemStatus() {
		boolean repeat;
		Product product = null;
		do {			
			repeat = false;
			product = AonFaker.getProduct( ctx );
			try {
				product = ProductDAO.insert(ctx, product);			
			} catch (AonCoreException e) {
				repeat = true;
			}
		} while (repeat);
			
		Item newItem = ItemDAO.save(ctx, 
			new Item()
				.setDomain(new Domain().setId(DOMAIN_ID))
				.setProduct(product)
				.setStatus(ProductStatus.ACTIVE)
		);
			
		InvoiceFakerParams params = new InvoiceFakerParams(ctx, getConfiguration()).setIssueDate(AonRandom.today());
		Invoice inserted = InvoiceOLDDAO.insert(ctx, InvoiceFaker.getRandom(params));
			
		List<InvoiceDetail> dets = inserted.getDetails();
		for (InvoiceDetail det : dets) {
			det.setItem(newItem);
		}
			
		Invoice insertedUpdated = InvoiceOLDDAO.update(ctx, inserted);
		
		TargetItemDAO.updateAllTargetItem(ctx, f -> f.getIdProperty().eq(insertedUpdated.getId()), false);
		List<InvoiceDetail> details = insertedUpdated.getDetails();
		for (InvoiceDetail detail : details) {
			Item item = detail.getItem();
			if (item != null) {
				RegistryItem ritem = RegistryOldDAO.getRItemStream(ctx, f -> f.getItemProperty().eq(item.getId()))
					.findFirst()
					.orElse(null);
				if (ritem != null 
					&& ritem.getType() != null 
					&& ritem.getType().equals(RegistryMode.TARGET)) {							
						assertEquals(RegistryItemStatus.ACTIVE, ritem.getStatus());
				}
			}
		}			
//			Domain domain = new Domain().setId(DOMAIN_ID).setName(DOMAIN_NAME);
//			AON.deleteInvoice(DOMAIN_NAME, DOMAIN_ID, USER, insertedUpdated.getId());
//			AON.deleteRItem(domain, new User().setLogin(USER), f -> f.getItemProperty().eq(newItem.getId()));
//			AON.deleteItem(domain, USER, newItem.getId());
//			AON.deleteProduct(domain, USER, product.getId());
	}
	
	@RepeatedTest(10)
	public void testRegistryItemStatus2() {
		Customer customer = AonFaker.getCustomer( ctx ); 
		customer = CustomerDAO.save(ctx, customer);
		
		final Integer registry = customer.getId();
		
		boolean repeat;
		Product product = null;
		do {			
			repeat = false;
			product = AonFaker.getProduct( ctx );
			try {
				product = ProductDAO.insert(ctx, product);			
			} catch (AonCoreException e) {
				repeat = true;
			}
		} while (repeat);
		
		
		Item item = new Item()
			.setDomain(new Domain().setId(DOMAIN_ID))
			.setProduct(product)
			.setStatus(ProductStatus.ACTIVE);
		
		item = ItemDAO.save(ctx, item);

		RegistryItem ritem = new RegistryItem()
			.setDomain(DOMAIN_ID)
			.setRegistry(customer.getId())
			.setType(RegistryMode.TARGET)
			.setStatus(RegistryItemStatus.ACTIVE)
			.setPriority(Priority.NORMAL)
			.setItem(item);
		
		
		RegistryItem[] ritemArr = ItemDAO.saveRItem(ctx, ritem);
		assertNotNull(ritemArr);
		assertNotEquals(0, ritemArr.length);
		
		final Integer itemId = item.getId();
		
		ItemDAO.updateRItemStatus(ctx, RegistryItemStatus.INTERESTED,
				f -> f.getDomainProperty().eq(DOMAIN_ID)
				.and(f.getRegistryProperty().eq(registry))
				.and(f.getItemProperty().eq(itemId))
				.and(f.getTypeProperty().eq(RegistryMode.TARGET.value())));
		
		RegistryItem obtainedRitem = RegistryOldDAO.getRItemStream(ctx, f -> f.getRegistryProperty().eq(registry) )
			.findFirst()
			.orElse(null);		
		assertNotNull(obtainedRitem);
		assertEquals(RegistryItemStatus.INTERESTED, obtainedRitem.getStatus());
		
//		Domain domain = new Domain().setId(DOMAIN_ID).setName(DOMAIN_NAME);
//		AON.deleteRItem(domain, new User().setLogin(USER), f -> f.getItemProperty().eq(itemId));
//		AON.deleteItem(domain, USER, item.getId());
//		AON.deleteProduct(domain, USER, product.getId());
		
//		InvoiceFakerParams params = new InvoiceFakerParams(ctx, config).setIssueDate(AonRandom.today());
//		Invoice inserted = AON.insertInvoice(DOMAIN_NAME, DOMAIN_ID, USER, InvoiceFaker.getRandom(params));
		
		
		
	}
	

}
