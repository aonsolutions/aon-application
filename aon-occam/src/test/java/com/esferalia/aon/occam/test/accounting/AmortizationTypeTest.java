package com.esferalia.aon.occam.test.accounting;
import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.impl.jooq.dao.AmortizationTypeDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonStringUtils;

import static com.esferalia.aon.jooq.tables.AmortizationType.AMORTIZATION_TYPE;

public class AmortizationTypeTest extends AbstractOccamTest {
	
	/*
	 * Tests for AmortizationTypeDAO class
	 * */

	/**
	 * DELETE @param List<AmortizationType> = Null
	 */

	@Test
	public void deleteNullTest() {
		AonCoreException e =
		assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.delete(ctx, null));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL.getMessage(), e.getMessage());
	}

	/**
	 * Test getList @param AmortizationTypeParams = Null
	 */

	@Test
	public void getListTest() {
		AmortizationTypeParams amp = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.getList(ctx, amp));
		assertEquals(AonError.AMORTIZATION_TYPE_PARAMS_NULL.getMessage(), e.getMessage());
	}

	/*
	 * Test save @param AmortizationType = Null
	 */

	@Test
	public void saveNullAmortizationType() {
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, null)); 
		assertEquals(AonError.AMORTIZATION_TYPE_NULL.getMessage(), e.getMessage());
	}

	/*
	 * Test save @param AmortizationType = null
	 */

	@Test
	public void saveEmptyAmortizationType() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(), e.getMessage());
	}

	/*
	 * Tests for control introduced fields length
	 */

	@Test
	public void saveFixedAssetAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		String text = AonStringUtils.repeat("h", AMORTIZATION_TYPE.FIXED_ASSET_ACCOUNT.getDataType().length()+1);
		amortizationType.setFixedAssetAccount(text);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.INVALID_LENGTH.getMessage(), e.getMessage());	
	}
	
	@Test
	public void saveAccumulatedAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		String text = AonStringUtils.repeat("h", AMORTIZATION_TYPE.ACCUMULATED_ACCOUNT.getDataType().length()+1);
		amortizationType.setAccumulatedAccount(text);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.INVALID_LENGTH.getMessage(), e.getMessage());	
		}

	@Test
	public void saveAllocationAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		String text = AonStringUtils.repeat("h", AMORTIZATION_TYPE.ALLOCATION_ACCOUNT.getDataType().length()+1);
		amortizationType.setAllocationAccount(text);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.INVALID_LENGTH.getMessage(), e.getMessage());	
		}

	/*
	 * Tests for fields that cant be null 
	 * 
	 * @param parametro1 FixedAssetAccount = null
	 * 
	 * @param parametro1 AccumulatedAccount = null
	 * 
	 * @param parametro1 AllocationAccount = null
	 * 
	 * @param parametro1 Description = null
	 */

	@Test
	public void saveNullFixedAssetAccount() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setFixedAssetAccount(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL_FIXED_ASSET_ACCOUNT.getMessage(), e.getMessage());
	}

	@Test
	public void saveNullAccumulatedAccount() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAccumulatedAccount(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL_ACCUMULATED_ACCOUNT.getMessage(), e.getMessage());
	}

	@Test
	public void saveNullAllocationAccount() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAllocationAccount(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT.getMessage(), e.getMessage());
	}
	
	@Test
	public void saveNullDescriptionAmortizationTypeTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDescription(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL_DESCRIPTION.getMessage(), e.getMessage());
	}	
}
