package com.esferalia.aon.occam.test.accounting;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;

import org.jooq.exception.DataAccessException;
import org.junit.Test;

import com.esferalia.aon.occam.api.model.accounting.AmortizationType;
import com.esferalia.aon.occam.api.model.accounting.AmortizationTypeParams;
import com.esferalia.aon.occam.impl.jooq.dao.AmortizationTypeDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonFaker;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class AmortizationTypeTest extends AbstractOccamTest {

	// SAVE
	//ID == NULL -> INSERT
	//SIN TERMINAR
	@Test
	public void saveTest() {

		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		AmortizationTypeParams amp = new AmortizationTypeParams();
		List<AmortizationType> saved = AmortizationTypeDAO.getList(ctx, amp);
		for (int i = 0; i < saved.size(); i++) {
			int inserted = saved.get(i).getId();
			amortizationType.setId(inserted);
		}
		
		AmortizationTypeDAO.save(ctx, amortizationType);
		System.out.println(amortizationType.getId());
		if (amortizationType.getId() != null) {
			System.out.println(AonError.AMORTIZATION_TYPE_ID_EXISTS.getMessage());	
		}

	}
	
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
			System.out.println(e.getMessage());  
		} 
	}
	
	// Comprueba que la lista no llegue como null
	@Test
	public void getListTest() {

		AmortizationTypeParams amp = null;
		Exception e = assertThrows(Exception.class, () -> AmortizationTypeDAO.getList(ctx, amp));
		System.out.println(e.getMessage());
	}

	// Test para comprobar que no acepte al objeto AmortizationType como nulo
	@Test
	public void saveNullAmortizationType() {
		Exception e = assertThrows(Exception.class, () -> AmortizationTypeDAO.save(ctx, null));
		System.out.println(e.getMessage());
	}

	// Test para comprobar que no acepta un nuevo tipo de objeto AmortizationType
	// vacio
	@Test
	public void saveEmptyAmortizationType() {
		Exception e = assertThrows(Exception.class,
				() -> AmortizationTypeDAO.save(ctx, new AmortizationType()));
		if (e.getMessage().equals("Cannot invoke \"com.esferalia.aon.occam.api.model.Domain.getId()\" because the return value of \"com.esferalia.aon.occam.api.model.accounting.AmortizationType.getDomain()\" is null")) {
			System.out.println(AonError.AMORTIZATION_TYPE_EMPTY.getMessage()); 
		}
	}

	// Tests para comprobar que no acepta un dato con una longitud mayor establecida
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

	@Test
	public void saveIncorrectLenghtPercentage() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setPercentage(1234567891234567895456.123456789101521745846465454);
		assertThrows(DataAccessException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));

	}

	// DAO permite la entrada de datos random
	@Test
	public void saveRandomTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		System.out.println(amortizationType.getId());
		System.out.println(amortizationType.getDomain());
		System.out.println(amortizationType.getFixedAssetAccount());
		System.out.println(amortizationType.getAccumulatedAccount());
		System.out.println(amortizationType.getAllocationAccount());
		System.out.println(amortizationType.getPercentage());
		System.out.println(amortizationType.getDescription());
		assertThrows(AonCoreException.class, () -> AmortizationTypeDAO.save(ctx, amortizationType));
	}   
 
	// Tests para comprobar si un valor que no puede ser NULL lo es
	@Test
	public void saveNullFixedAssetAccount() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setFixedAssetAccount(null);
		DataAccessException e = assertThrows(DataAccessException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		if (e.getMessage().equals("SQL [insert into `amortization_type` (`domain`, `description`, `percentage`, `fixed_asset_account`, `accumulated_account`, `allocation_account`) values (?, ?, ?, ?, ?, ?)]; Column 'fixed_asset_account' cannot be null")) {
			System.out.println(AonError.AMORTIZATION_TYPE_NULL_FIXED_ASSET_ACCOUNT.getMessage()); 
		}
	}

	@Test
	public void saveNullAccumulatedAccount() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAccumulatedAccount(null);
		DataAccessException e = assertThrows(DataAccessException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

	@Test 
	public void saveNullAllocationAccount() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setAllocationAccount(null);
		DataAccessException e = assertThrows(DataAccessException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		if (e.getMessage().equals("SQL [insert into `amortization_type` (`domain`, `description`, `percentage`, `fixed_asset_account`, `accumulated_account`, `allocation_account`) values (?, ?, ?, ?, ?, ?)]; Column 'allocation_account' cannot be null")) {
			System.out.println(AonError.AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT.getMessage()); 
		}
	}

	@Test
	public void saveNullDomainAmortizationTypeTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDomain(null);
		Exception e = assertThrows(Exception.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
		System.out.println(e.getMessage());
	}

	@Test
	public void saveNullDescriptionAmortizationTypeTest() {
		AmortizationType amortizationType = AonFaker.getAmortizationType(ctx);
		amortizationType.setDescription(null);
		DataAccessException e = assertThrows(DataAccessException.class,
				() -> AmortizationTypeDAO.save(ctx, amortizationType));
	}

}
