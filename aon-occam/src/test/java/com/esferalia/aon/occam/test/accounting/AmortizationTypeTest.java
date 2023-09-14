package com.esferalia.aon.occam.test.accounting;

import java.util.ArrayList;
import java.util.List;

import static com.esferalia.aon.jooq.tables.AmortizationType.AMORTIZATION_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.impl.jooq.dao.AmortizationTypeDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class AmortizationTypeTest extends AbstractOccamTest {

	// SAVE
	@Test
	public void saveTest() {

		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		AmortizationTypeParams amp = new AmortizationTypeParams();
		List<AmortizationType> saved = AmortizationTypeDAO.getList(ctx, amp);
		List<Integer> deletedIds = new ArrayList<>();

		AmortizationTypeDAO.save(ctx, amortizationType);
		int amortizationTypeId = amortizationType.getDomain().getId();
		for (int i = 0; i < saved.size(); i++) {
			int inserted = saved.get(i).getId();
			assertEquals(amortizationTypeId, inserted);

		}
	}
	
	// DELETE
	@Test
	public void deleteTest() {
		AmortizationTypeParams amp = new AmortizationTypeParams();
		List<AmortizationType> saved = AmortizationTypeDAO.getList(ctx, amp);
		List<Integer> deletedIds = new ArrayList<>();

		for (int i = 0; i < saved.size(); i++) {
			Integer deletedId = saved.get(i).getId();
			deletedIds.add(deletedId);
			AmortizationTypeDAO.delete(ctx, deletedIds);
			assertNull(deletedIds);
		}
	}

	// Comprueba que la lista no llegue como null
	@Test
	public void getListTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);

		AmortizationTypeParams amp = new AmortizationTypeParams();

		List<AmortizationType> saved = AmortizationTypeDAO.getList(ctx, amp);

		for (int i = 0; i < saved.size(); i++) {
			AmortizationType ampTest = saved.get(i);
			Asserts.assertEqualsAmortizationType(amortizationType, ampTest);
		}

	}

	// Test para comprobar que no acepte al objeto AmortizationType como nulo
	@Test
	public void saveNullAmortizationType() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, null));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL.getMessage(), e.getMessage());
	}

	// Test para comprobar que no acepta un nuevo tipo de objeto AmortizationType
	// vacio
	@Test
	public void saveEmptyAmortizationType() {
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, new AmortizationType()));
		assertEquals(AonError.AMORTIZATION_TYPE_EMPTY.getMessage(), e.getMessage());
	}

	// Tests para comprobar que no acepta un dato con una longitud mayor establecida
	@Test
	public void saveFixedAssetAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setFixedAssetAccount("abcdefghi");
		assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));

	}

	@Test
	public void saveAccumulatedAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAccumulatedAccount("abcdefghi");
		assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));

	}

	@Test
	public void saveAllocationAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAllocationAccount("abcdefghi");
		assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));

	}

	@Test
	public void saveIncorrectLenghtPercentage() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setPercentage(1234567891234567895456.123456789101521745846465454);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_INCORRECT_PERCENTAGE.getMessage(), e.getMessage());
	}

	// DAO permite la entrada de datos random
	@Test
	public void saveRandomTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

	// Tests para comprobar si un valor que no puede ser NULL lo es
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
	public void saveNullDomainAmortizationTypeTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDomain(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(), e.getMessage());
	}

	@Test
	public void saveNullDescriptionAmortizationTypeTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDescription(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

}
