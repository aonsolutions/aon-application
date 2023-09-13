package com.esferalia.aon.occam.test.accounting;

import java.util.ArrayList;
import java.util.List;

import static com.esferalia.aon.jooq.tables.AmortizationType.AMORTIZATION_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

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

	@Test
	public void crudeTest() {

		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		AmortizationTypeParams amp = (AmortizationTypeParams) ctx.getDslContext();
		List<AmortizationType> saved = AmortizationTypeDAO.getList(ctx, amp);
		List<Integer> deletedIds = new ArrayList<>();

		// SAVE
		AmortizationTypeDAO.save(ctx, amortizationType);
		int amortizationTypeId = amortizationType.getId();

		for (int i = 0; i < saved.size(); i++) {
			int inserted = saved.get(i).getId();
			assertEquals(amortizationTypeId, inserted);

		}
		// DELETE
		for (int i = 0; i < saved.size(); i++) {
			Integer deletedId = saved.get(i).getId();
			deletedIds.add(deletedId);
			AmortizationTypeDAO.delete(ctx, deletedIds);
			assertNull(deletedIds);
		}

	}

	@Test
	public void getListTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);

		AmortizationTypeParams amp = (AmortizationTypeParams) ctx.getDslContext();

		List<AmortizationType> saved = AmortizationTypeDAO.getList(ctx, amp);

		for (int i = 0; i < saved.size(); i++) {
			AmortizationType ampTest = saved.get(i);
			Asserts.assertEqualsAmortizationType(amortizationType, ampTest);
		}

	}

	@Test
	public void saveNullAmortizationType() {
		AonCoreException e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, null));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL.getMessage(), e.getMessage());
	}

	@Test
	public void saveEmptyAmortizationType() {
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, new AmortizationType()));
		assertEquals(AonError.AMORTIZATION_TYPE_EMPTY.getMessage(), e.getMessage());
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
	public void saveNullAndEmptyDescriptionAmortizationTypeTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDescription(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL_DESCRIPTION.getMessage(), e.getMessage());

		amortizationType.setDescription("");
		e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_EMPTY_DESCRIPTION.getMessage(), e.getMessage());

		amortizationType.setDescription("  ");
		e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_EMPTY_DESCRIPTION.getMessage(), e.getMessage());

	}

	@Test
	public void saveNullAndEmptyFixedAssetAccount() {

		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setFixedAssetAccount(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL_FIXED_ASSET_ACCOUNT.getMessage(), e.getMessage());

		amortizationType.setFixedAssetAccount("");
		e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_EMPTY_FIXED_ASSET_ACCOUNT.getMessage(), e.getMessage());

		amortizationType.setFixedAssetAccount("  ");
		e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_EMPTY_FIXED_ASSET_ACCOUNT.getMessage(), e.getMessage());

	}

	@Test
	public void saveNullAndEmptyAccumulatedAccount() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAccumulatedAccount(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL_ACCUMULATED_ACCOUNT.getMessage(), e.getMessage());

		amortizationType.setAccumulatedAccount("");
		e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_EMPTY_ACCUMULATED_ACCOUNT.getMessage(), e.getMessage());

		amortizationType.setAccumulatedAccount("  ");
		e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_EMPTY_ACCUMULATED_ACCOUNT.getMessage(), e.getMessage());

	}

	@Test
	public void saveNullAndEmptyAllocationAccount() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAllocationAccount(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT.getMessage(), e.getMessage());

		amortizationType.setAllocationAccount("");
		e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_EMPTY_ALLOCATION_ACCOUNT.getMessage(), e.getMessage());

		amortizationType.setAllocationAccount("  ");
		e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_EMPTY_ALLOCATION_ACCOUNT.getMessage(), e.getMessage());

	}

	@Test
	public void saveIncorrectPercentage() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setPercentage(1.123456789101521745846465454);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_INCORRECT_PERCENTAGE.getMessage(), e.getMessage());

	}

}
