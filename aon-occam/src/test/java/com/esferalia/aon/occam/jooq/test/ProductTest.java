package com.esferalia.aon.occam.jooq.test;


import java.sql.SQLException;
import java.util.Date;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.office.Tag;
import com.esferalia.aon.occam.api.model.product.Item;
import com.esferalia.aon.occam.api.model.product.Product;
import com.esferalia.aon.occam.api.model.product.ProductTag;
import com.esferalia.aon.occam.impl.jooq.dao.ProductDAO;
import com.esferalia.aon.watson.error.AonCoreException;


public class ProductTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "garajeolabe.aibanez.net";
	private static int DOMAIN_ID = 596;
	private static String LOGIN = "contacto";

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( com.mysql.jdbc.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID, LOGIN);
	}
	
	// ------------------------------------ PRODUCT
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyDomain() {
		Product product = new Product();
		AON.insert(ctx, product);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyName() {
		Product product = new Product();
		product.setDomain(ctx.getDomainId());
		AON.insert(ctx, product);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyCode() {
		Product product = new Product();
		product.setDomain(ctx.getDomainId());
		product.setName("test");
		AON.insert(ctx, product);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testDuplicateProduct(){
		Product product = ProductDAO.getProduct(ctx, 1);
		if(product != null){
			product = new Product();
			product.setDomain(ctx.getDomainId());
			product.setName("test");
			product.setCode("C001234");
			product.setBrand(1);
			product.setCategory(1);
			product.setInventoriable((byte)1);
			product.setSerializable((byte)1);
			product.setLotable((byte)1);
			product.setStatus((byte)1);
			product.setVat(1);
			product.setRetention(1);
			product.setType((byte)1);
			product.setManufactured((byte)1);
			product.setComposition((byte)1);
			product.setCompositionPrice((byte)1);
			product.setSalesAccount(10);
			product.setPurchaseAccount(1);
			product.setCreationUser("junit");
			product.setCreationDate(new Date());
			product.setModificationUser("junit");
			product.setModificationDate(new Date());
			AON.insert(ctx, product);
		}
		AON.insert(ctx, product);
	}
	
	@Test
	@Ignore
	public void testDeleteProduct() {
		Product product = ProductDAO.getProduct(ctx, 1);
		if(product != null){
			AON.delete(ctx,product);
		}
	}
	
	
	// ------------------------------------ PRODUCT_TAG
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyDomainProductTag() {
		ProductTag pt = new ProductTag();
		AON.insertProductTag(ctx, pt);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyProductProductTag() {
		ProductTag pt = new ProductTag();
		pt.setDomain(ctx.getDomainId());
		AON.insertProductTag(ctx, pt);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyTagProductTag() {
		ProductTag pt = new ProductTag();
		pt.setDomain(ctx.getDomainId());
		pt.setProduct(1);
		AON.insertProductTag(ctx, pt);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testExistProductProductTag() {
		ProductTag pt = new ProductTag();
		pt.setDomain(ctx.getDomainId());
		pt.setProduct(1); // No tiene que existir
		pt.setTag(new Tag().setId(1));
		AON.insertProductTag(ctx, pt);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testExistTagProductTag() {
		ProductTag pt = new ProductTag();
		pt.setDomain(ctx.getDomainId());
		pt.setProduct(1); // Tiene que existir
		pt.setTag(new Tag().setId(1)); // No tiene que existir
		AON.insertProductTag(ctx, pt);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testDuplicateProductTag(){
		ProductTag productTag = ProductDAO.getProductTag(ctx, 1);
		if(productTag != null){
			productTag = new ProductTag();
			productTag.setDomain(ctx.getDomainId());
			productTag.setProduct(1);
			productTag.setTag(new Tag().setId(1));
			AON.insertProductTag(ctx, productTag);
		}
		AON.insertProductTag(ctx, productTag);
	}
	
	@Test
	@Ignore
	public void testDeleteProductTag() {
		ProductTag productTag = ProductDAO.getProductTag(ctx, 1);
		if(productTag != null){
			AON.deleteProductTag(ctx,productTag);
		}
	}
	
	// ------------------------------------ ITEM
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testEmptyDomainItem() {
		Product product = new Product();
		AON.insert(ctx, product);
	}
	
	@Test(expected=AonCoreException.class)
	@Ignore
	public void testDuplicateItem(){
		Item item = AON.getItem(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), 1);
		if(item != null){
			item = new Item();
			item.setDomain(ctx.getDomainId());
			item.setProductId(1);
			item.setDetail("detail");
			item.setDetail2("detail2");
			item.setDetail3("detail3");
			item.setDescription("test");
			item.setSerialNumber("12ws34");
			//item.setSerialDate("");
			item.setPrice(12);
			item.setStatus((byte)1);
			item.setExpensesPercent(100);
			item.setExpensesFixed(100);
			item.setProfitPercent(100);
			item.setPurchasePrice(100);
			item.setInternet(true);
			item.setBarcode("PAS23151235");
			//item.setCreationUser(record.value17());
			//item.setCreationDate(record.value18());
			//item.setModificationUser(record.value19());
			//item.setModificationDate(record.value20());
			AON.insertItem(ctx, item);
		}
		AON.insertItem(ctx, item);
	}
	
	@Test
	@Ignore
	public void testDeleteItem() {
		Item item = AON.getItem(ctx.getDomainName(), ctx.getDomainId(), ctx.getUser(), 1);
		if(item != null){
			AON.deleteItem(ctx,item);
		}
	}
	
	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
