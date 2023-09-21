package com.esferalia.aon.occam.test.accounting;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;

import org.jooq.exception.DataAccessException;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.impl.jooq.dao.AmortizationTypeDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;

public class AmortizationTypeTest extends AbstractOccamTest {

	/**
	 * DELETE con @param List<AmortizationType> = Null
	 */

	@Test
	public void deleteNullTest() {

		assertThrows(NullPointerException.class, () -> AmortizationTypeDAO.delete(ctx, null));
	}

	/**
	 * Test para cuando getList tiene @param AmortizationTypeParams = Null
	 */

	@Test
	public void getListTest() {
		AmortizationTypeParams amp = null;
		assertThrows(NullPointerException.class, () -> AmortizationTypeDAO.getList(ctx, amp));
	}

	/**
	 * Test para cuando getList tiene @param AmortizationTypeParams = Empty
	 */

	@Test
	public void getListWithNoFiltersTest() {
		AmortizationTypeParams params = new AmortizationTypeParams();
		List<AmortizationType> result = AmortizationTypeDAO.getList(ctx, params);
		assertNotNull(result);
		assertFalse(result.isEmpty());
	}

	/*
	 * Test para comprobar que no acepte @param AmortizationType = Null
	 */

	@Test
	public void saveNullAmortizationType() {
		AmortizationType amortizationType = null;
		assertThrows(NullPointerException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

	/*
	 * Test para comprobar que no acepta un @param AmortizationType = Empty
	 */

	@Test
	public void saveEmptyAmortizationType() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDomain(new Domain());
		assertThrows(DataAccessException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

	/*
	 * Tests para comprobar que no acepta un dato con una longitud mayor establecida
	 */

	@Test
	public void saveFixedAssetAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setFixedAssetAccount("abcdefghi");
		assertThrows(DataAccessException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));

	}

	@Test
	public void saveAccumulatedAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAccumulatedAccount("abcdefghi");
		assertThrows(DataAccessException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

	@Test
	public void saveAllocationAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAllocationAccount("abcdefghi");
		assertThrows(DataAccessException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

	/*
	 * DAO permite la entrada de datos random
	 */

	@Test
	public void saveRandomTest() {

		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);

		AmortizationTypeDAO.save(ctx, amortizationType);

		AmortizationTypeParams amp = new AmortizationTypeParams().setDomain(ctx.getDomainId())
				.setFixedAssetAccount(amortizationType.getFixedAssetAccount())
				.setAccumulatedAccount(amortizationType.getAccumulatedAccount())
				.setAllocationAccount(amortizationType.getAllocationAccount())
				.setDescription(amortizationType.getDescription());

		List<AmortizationType> inserted = AmortizationTypeDAO.getList(ctx, amp);

		assertFalse(inserted.isEmpty());

		AmortizationType retrievedAmortizationType = inserted.get(0);

		assertEquals(amortizationType.getDomain().getId(), retrievedAmortizationType.getDomain().getId());
		assertEquals(amortizationType.getDescription(), retrievedAmortizationType.getDescription());
		assertEquals(amortizationType.getPercentage(), retrievedAmortizationType.getPercentage());
		assertEquals(amortizationType.getFixedAssetAccount(), retrievedAmortizationType.getFixedAssetAccount());
		assertEquals(amortizationType.getAccumulatedAccount(), retrievedAmortizationType.getAccumulatedAccount());
		assertEquals(amortizationType.getAllocationAccount(), retrievedAmortizationType.getAllocationAccount());
	}

	/*
	 * Tests para campos que no pueden ser nulos
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
		assertThrows(DataAccessException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

	@Test
	public void saveNullAccumulatedAccount() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAccumulatedAccount(null);
		assertThrows(DataAccessException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

	@Test
	public void saveNullAllocationAccount() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAllocationAccount(null);
		assertThrows(DataAccessException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

	@Test
	public void saveNullDescriptionAmortizationTypeTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDescription(null);
		assertThrows(DataAccessException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

}
