package com.esferalia.aon.occam.test.fiscal.mod390hf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.util.Date;
import java.util.Optional;

import org.junit.Test;

import com.esferalia.aon.occam.api.fiscal.MODEL390HF;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFiscalStatusVisitor;
import com.esferalia.aon.occam.api.model.fiscal.FiscalStatus;
import com.esferalia.aon.occam.api.model.fiscal.Mod390HF;
import com.esferalia.aon.occam.api.model.type.Administration;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.Asserts;
import com.esferalia.aon.occam.test.faker.AonRandom;
import com.esferalia.aon.occam.test.faker.FiscalFaker;
import com.esferalia.aon.occam.test.faker.FiscalFaker.FiscalFakerParams;
import com.esferalia.aon.occam.test.fiscal.FiscalTestSuite;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;

public class Mod390HFValidationTest extends AbstractOccamTest {
	
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
			insertModel( params );
	    });
		String expected = AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage();
		assertEquals("Wrong Exception", expected, e.getMessage());
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
			insertModel( params );
	    });
		String expected = AonError.FISCAL_NO_REPLACED_DECLARATION.getMessage();
		assertEquals("Wrong Exception", expected, e.getMessage());
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
		Mod390HF mod = insertModel( params );
		Exception e = assertThrows(AonCoreException.class, () -> {
			insertModel( params );
	    });
		String expected = AonError.FISCAL_DECLARATION_ALREADY_EXISTS.format(mod.getModelFullName());
		assertEquals("Wrong Exception", expected, e.getMessage());
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
		Mod390HF original = insertModel( params );
		insertModel( params.setComplementary(true) );
		Exception e = assertThrows(AonCoreException.class, () -> {
			MODEL390HF.delete(getOccam(), original);
	    });
		String expected = AonError.FISCAL_WRONG_REPLACED_DELETION.getMessage();
		assertEquals("Wrong Exception", expected, e.getMessage());
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
				Mod390HF mod = insertModel( params );
				mod.setStatus( status );
				MODEL390HF.delete(getOccam(), mod);
				Mod390HF model = MODEL390HF.get(getOccam(), mod.getId());
				assertNull("Modelo no nulo!", model );
				return params;
			}
			
			private FiscalFakerParams visitNoRemoved(FiscalStatus status) {
				deleteAllModels();
				Mod390HF mod = insertModel( params );
				mod.setStatus(status);
				Exception e = assertThrows("Status: " + status.getName(), AonCoreException.class, () -> {
					MODEL390HF.delete(getOccam(), mod);
				});
				String expected = AonError.FISCAL_WRONG_STATUS_DELETION.format(mod.getStatus());
				assertEquals("Wrong Exception", expected, e.getMessage());
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
		Optional.of(MODEL390HF.getMod390HFs(getOccam()))
			.ifPresent( mods -> 
				mods.stream()
					.map( mod -> {
						return (Mod390HF) mod.setStatus(FiscalStatus.PENDING);
					})
					.forEach( mod -> MODEL390HF.delete(getOccam(), mod)));
	}

	private FiscalFakerParams getParams() {
		return new FiscalFakerParams(ctx,getOccam())
			.setAdministration(Administration.COMMON_TERRITORY)
			.setIssueDate(new Date())
			.setMonthly(true)
			.setProrratePercent( AonRandom.gt(10)? 0 : AonRandom.getPercent() );
	}
	
	private Mod390HF insertModel( FiscalFakerParams params) {
		Mod390HF mod = FiscalFaker.createMod390HF(params);
		MODEL390HF.save(getOccam(), mod);
		Mod390HF actual = MODEL390HF.get(getOccam(), mod.getId());  
		Asserts.assertMod390HF(mod, actual);
		FiscalTestSuite.printModel(actual);
		return actual;
	}
		
}
