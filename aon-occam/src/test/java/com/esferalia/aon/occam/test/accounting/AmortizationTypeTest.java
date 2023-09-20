package com.esferalia.aon.occam.test.accounting;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.impl.jooq.dao.AmortizationTypeDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class AmortizationTypeTest extends AbstractOccamTest {
	// DELETE
	@Test
	public void deleteTest() {
		AmortizationTypeParams amp = new AmortizationTypeParams();
		List<AmortizationType> saved = AmortizationTypeDAO.getList(ctx, amp);
		List<Integer> deletedIds = new ArrayList<>();

		for (int i = 0; i < saved.size(); i++) {
			deletedIds.add(saved.get(i).getId());
			System.out.println(deletedIds.get(i)); 
			Exception e = 
					assertThrows(Exception.class, () -> deletedIds.forEach(deletedId -> AmortizationTypeDAO.delete(ctx, null)));
			assertEquals(AonError.AMORTIZATION_TYPE_NULL.getMessage(), e.getMessage());
			
		} 
	}
	// Comprueba que la lista no llegue como null
	@Test
	public void getListTest() {
		AmortizationTypeParams amp = null;
		AonCoreException e = assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.getList(ctx, amp));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL.getMessage(), e.getMessage());
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
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDomain(new Domain());
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_EMPTY_DOMAIN.getMessage(), e.getMessage());
	}

	// Tests para comprobar que no acepta un dato con una longitud mayor establecida
	@Test
	public void saveFixedAssetAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setFixedAssetAccount("abcdefghi");
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_LENGHT_EXCEED, e.getMessage());
		
	}

	@Test
	public void saveAccumulatedAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAccumulatedAccount("abcdefghi");
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_LENGHT_EXCEED, e.getMessage());

	}

	@Test
	public void saveAllocationAccountLengthTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAllocationAccount("abcdefghi");
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_LENGHT_EXCEED, e.getMessage());
	}

	// DAO permite la entrada de datos random
	//SEGUIR MAÑANA , COMPARAR TODOS LOS PARAMETROS 
	@Test
	public void saveRandomTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		
		AmortizationTypeDAO.save(ctx, amortizationType);
		AmortizationTypeParams amp = new AmortizationTypeParams()
				.setDomain(ctx.getDomainId())
				.setFixedAssetAccount(amortizationType.getFixedAssetAccount())
				.setAccumulatedAccount(amortizationType.getAccumulatedAccount())
				.setAllocationAccount(amortizationType.getAllocationAccount())
				.setDescription(amortizationType.getDescription());
		
		List inserted = AmortizationTypeDAO.getList(ctx, amp);
		System.out.println(inserted.size());
//		AmortizationType amorType = (AmortizationType) inserted.get(amortizationId);
		assertEquals(amortizationType, inserted);
		
	}   
 
	// No controlado en el DAO, acepta los valores como nulos y salta una SQL Exception
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
		assertEquals(AonError.AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT, e.getMessage());
	}

	@Test
	public void saveNullDescriptionAmortizationTypeTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDescription(null);
		AonCoreException e = assertThrows(AonCoreException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		assertEquals(AonError.AMORTIZATION_TYPE_NULL_DESCRIPTION, e.getMessage());

	}

}
