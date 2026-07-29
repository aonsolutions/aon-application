package com.esferalia.aon.occam.test.fiscal.mod303;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import java.util.Optional;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL303;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalStatusVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod303;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod303ValidationTest extends Mod303AbstractTest {
	
	/**
	 * No se puede grabar una complementaria sin algo a lo que complementar.
	 */
	@Test
	public void testComplementary() {
		FiscalFakerParams params = getParams()
			.setComplementary(true)
			.setReplacement(false);
		deleteAllModels();
		Exception e = assertThrows(AonCoreException.class, () -> {
			insertMod303( params );
	    });
		String expected = AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage();
		assertEquals(expected, e.getMessage(), "Wrong Exception");
	}
	
	/**
	 * No se puede grabar una sustitutiva algo a lo que sustituir.
	 */
	@Test
	public void testReplacement() {
		FiscalFakerParams params = getParams()
			.setComplementary(false)
			.setReplacement(true);
		deleteAllModels();
		Exception e = assertThrows(AonCoreException.class, () -> {
			insertMod303( params );
	    });
		String expected = AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage();
		assertEquals(expected, e.getMessage(), "Wrong Exception");
	}
	
	/**
	 * No se puede grabar una delcaración que ya exista.
	 */
	@Test
	public void testDuplicated() {
		FiscalFakerParams params = getParams()
			.setComplementary(false)
			.setReplacement(false);
		deleteAllModels();
		Mod303 mod303 = insertMod303( params );
		Exception e = assertThrows(AonCoreException.class, () -> {
			insertMod303( params );
	    });
		String expected = AonError.FISCAL_DECLARATION_ALREADY_EXISTS.format(mod303.getModelFullName());
		assertEquals(expected, e.getMessage(), "Wrong Exception");
	}

	/**
	 * No se puede borrar una declaración si no está pendiente.
	 */
	@Test
	public void testDeleteWithComplementary() {
		FiscalFakerParams params = getParams()
			.setComplementary(false)
			.setReplacement(false);
		deleteAllModels();
		Mod303 original = insertMod303( params );
		insertMod303( params.setComplementary(true) );
		Exception e = assertThrows(AonCoreException.class, () -> {
			MODEL303.delete(getOccam(), original);
	    });
		String expected = AonError.FISCAL_WRONG_REPLACED_DELETION.getMessage();
		assertEquals(expected, e.getMessage(), "Wrong Exception");
	}

	/**
	 * No se puede borrar una declaración si algo la complemeta
	 */
	@Test
	public void testDeleteFinishedModel() {
		final FiscalFakerParams params = getParams()
				.setComplementary(false)
				.setReplacement(false);
		IFiscalStatusVisitor<FiscalFakerParams> visitor = new IFiscalStatusVisitor<FiscalFakerParams>() {

			private FiscalFakerParams visitRemoved(FiscalStatus status) {
				deleteAllModels();
				Mod303 mod = insertMod303( params );
				mod.setStatus( status );
				MODEL303.delete(getOccam(), mod);
				Mod303 model = MODEL303.get(getOccam(), mod.getId());
				assertNull(model, "Modelo no nulo!");
				return params;
			}
			
			private FiscalFakerParams visitNoRemoved(FiscalStatus status) {
				deleteAllModels();
				Mod303 mod = insertMod303( params );
				mod.setStatus(status);
				Exception e = assertThrows(AonCoreException.class, () -> {
					MODEL303.delete(getOccam(), mod);
				}, "Status: " + status.getName());
				String expected = AonError.FISCAL_WRONG_STATUS_DELETION.format(mod.getStatus());
				assertEquals(expected, e.getMessage(), "Wrong Exception");
				return params;
			}
			
			@Override public FiscalFakerParams visitPending()  {return visitRemoved(FiscalStatus.PENDING); }
			@Override public FiscalFakerParams visitFinished() {return visitNoRemoved(FiscalStatus.FINISHED); }
			@Override public FiscalFakerParams visitBatched()  {return visitRemoved(FiscalStatus.PENDING); }
			@Override public FiscalFakerParams visitBlocked()  {return visitRemoved(FiscalStatus.PENDING); }
			@Override public FiscalFakerParams visitSent()     {return visitNoRemoved(FiscalStatus.FINISHED); }
			@Override public FiscalFakerParams visitMissing()  {return visitRemoved(FiscalStatus.PENDING); }
			@Override public FiscalFakerParams visitCustomerCheck() {return visitNoRemoved(FiscalStatus.FINISHED); }
			@Override public FiscalFakerParams visitCustomerAccepted() {return visitNoRemoved(FiscalStatus.FINISHED); }
			@Override public FiscalFakerParams visitCustomerRejected() {return visitRemoved(FiscalStatus.PENDING); }
		}; 
		for (FiscalStatus status : FiscalStatus.values()) {
			status.visit(visitor);
		}
	}

	private void  deleteAllModels() {
		Optional.of(MODEL303.getMod303s(getOccam()))
		.ifPresent( mods -> 
		mods.stream()
			.map( mod -> {
				return (Mod303) mod.setStatus(FiscalStatus.PENDING);
			})
			.forEach( mod -> MODEL303.delete(getOccam(), mod)));
	}

	private FiscalFakerParams getParams() {
		double prorratePercent = AonRandom.gt(10)? 0 : AonRandom.getPercent();
		return new FiscalFakerParams(ctx,getOccam())
			.setAdministration(Administration.COMMON_TERRITORY)
			.setIssueDate(getTestDate())
			.setMonthly(true)
			.setProrratePercent( prorratePercent )
			.setSpecialProrrate( AonMathUtils.isNotZero(prorratePercent) && AonRandom.gt(60) )
			;
	}
	
	private Mod303 insertMod303( FiscalFakerParams params) {
		Mod303 mod303 = FiscalFaker.createMod303(params);
		MODEL303.save(getOccam(), mod303);
		Mod303 actual = MODEL303.get(getOccam(), mod303.getId());  
		Asserts.assertMod303(mod303, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
		
}
