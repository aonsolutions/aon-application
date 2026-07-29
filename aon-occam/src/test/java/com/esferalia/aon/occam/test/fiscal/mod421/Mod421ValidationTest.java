package com.esferalia.aon.occam.test.fiscal.mod421;

import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertNull;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import java.util.Optional;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL421;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalStatusVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod421;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod421ValidationTest extends Mod421AbstractTest {
	
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
			insertMod421( params );
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
		Mod421 mod421 = insertMod421( params );
		Exception e = assertThrows(AonCoreException.class, () -> {
			insertMod421( params );
	    });
		String expected = AonError.FISCAL_DECLARATION_ALREADY_EXISTS.format(mod421.getModelFullName());
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
		Mod421 original = insertMod421( params );
		insertMod421( params.setComplementary(true) );
		Exception e = assertThrows(AonCoreException.class, () -> {
			MODEL421.delete(getOccam(), original);
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
				Mod421 mod = insertMod421( params );
				mod.setStatus( status );
				MODEL421.delete(getOccam(), mod);
				Mod421 model = MODEL421.get(getOccam(), mod.getId());
				assertNull(model, "Modelo no nulo!");
				return params;
			}
			
			private FiscalFakerParams visitNoRemoved(FiscalStatus status) {
				deleteAllModels();
				Mod421 mod = insertMod421( params );
				mod.setStatus(status);
				Exception e = assertThrows(AonCoreException.class, () -> {
					MODEL421.delete(getOccam(), mod);
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
		Optional.of(MODEL421.getMod421s(getOccam()))
		.ifPresent( mods -> 
		mods.stream()
			.map( mod -> {
				return (Mod421) mod.setStatus(FiscalStatus.PENDING);
			})
			.forEach( mod -> MODEL421.delete(getOccam(), mod)));
	}

	private FiscalFakerParams getParams() {
		double prorratePercent = AonRandom.gt(10)? 0 : AonRandom.getPercent();
		return new FiscalFakerParams(ctx,getOccam())
			.setAdministration(Administration.CANARIAS)
			.setIssueDate(getTestDate())
			.setMonthly(false)
			.setProrratePercent( prorratePercent )
			.setSpecialProrrate( AonMathUtils.isNotZero(prorratePercent) && AonRandom.gt(60) )
			;
	}
	
	private Mod421 insertMod421( FiscalFakerParams params) {
		Mod421 mod421 = FiscalFaker.createMod421(params);
		MODEL421.save(getOccam(), mod421);
		Mod421 actual = MODEL421.get(getOccam(), mod421.getId());  
		Asserts.assertMod421(mod421, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
		
}
