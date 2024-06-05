package com.esferalia.aon.occam.test.product;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.product.Brand;
import com.esferalia.aon.occam.impl.jooq.dao.BrandDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class BrandTest extends AbstractOccamTest {

	@Test
	public void crudeTest() {
		Brand brand = AonFaker.getBrand(ctx); 
		brand = BrandDAO.insert(ctx, brand);

		Integer brandId = brand.getId();
		Brand inserted = BrandDAO.get(ctx, f -> f.getIdProperty().eq(brandId));
		Asserts.assertEqualsBrand(brand, inserted);
		
		brand = BrandDAO.update(ctx, brand);
		Brand updated = BrandDAO.get(ctx, f -> f.getIdProperty().eq(brandId));
		Asserts.assertEqualsBrand(brand, updated);
		
		BrandDAO.delete(ctx, brandId);
		Brand deleted = BrandDAO.get(ctx, f -> f.getIdProperty().eq(brandId));
		
		assertNull(deleted.getId());
	}
	
	@Test
	public void saveNullBrandTest() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> BrandDAO.save(ctx, null) );
		assertEquals(AonError.BRAND_NULL.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveEmptyBrandTest() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> BrandDAO.save(ctx, new Brand()));
		assertEquals(AonError.BRAND_EMPTY.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveEmptyDomainBrandTest() {
		Brand brand = AonFaker.getBrand(ctx); 
		brand.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> BrandDAO.save(ctx, brand));
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveEmptyNameBrandTest() {
		Brand brand = AonFaker.getBrand(ctx); 
		brand.setName(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> BrandDAO.save(ctx,  brand));
		assertEquals(AonError.EMPTY_NAME.getMessage(), e.getMessage());
		
		brand.setName("");
		e = assertThrows(AonCoreException.class, () -> BrandDAO.save(ctx, brand));
		assertEquals(AonError.EMPTY_NAME.getMessage(), e.getMessage());
		
		brand.setName(" ");
		e = assertThrows(AonCoreException.class, () -> BrandDAO.save(ctx, brand));
		assertEquals(AonError.EMPTY_NAME.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveRepeatNameBrandTest() {
		Brand brand = AonFaker.getBrand(ctx); 
		Brand inserted = BrandDAO.save(ctx, brand);
		Asserts.assertEqualsBrand(brand, inserted);

		AonCoreException e = assertThrows(AonCoreException.class, () -> BrandDAO.save(ctx,  brand));
		assertEquals(AonError.BRAND_REPEAT.getMessage(), e.getMessage());
	}
	
	

}
