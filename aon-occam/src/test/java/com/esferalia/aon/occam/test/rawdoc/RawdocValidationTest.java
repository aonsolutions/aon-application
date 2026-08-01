package com.esferalia.aon.occam.test.rawdoc;


import static com.esferalia.aon.occam.test.OccamAssertions.assertEquals;
import static com.esferalia.aon.occam.test.OccamAssertions.assertThrows;

import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Rawdoc;
import com.esferalia.aon.occam.api.model.type.RawdocNature;
import com.esferalia.aon.occam.api.model.type.RawdocType;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.AonError;
import com.esferalia.aon.watson.error.AonCoreException;


public class RawdocValidationTest extends AbstractOccamTest {

	@Test
	public void testEmptyDomain() {
		Rawdoc rawdoc = new Rawdoc();
		AonCoreException e = assertThrows(AonCoreException.class, () -> AON.rawdocSave( getOccam(), rawdoc) );
		assertEquals(AonError.EMPTY_DOMAIN.getMessage(),e.getMessage());
		
	}
	
	@Test
	public void testEmptyNature() {
		Rawdoc rawdoc = new Rawdoc();
		rawdoc.setDomain( DOMAIN_ID );
		AonCoreException e = assertThrows(AonCoreException.class, () -> AON.rawdocSave(getOccam(), rawdoc) );
		assertEquals(AonError.EMPTY_RAWDOC_NATURE.getMessage(),e.getMessage());
		
	}
	
	@Test
	public void testEmptyType() {
		Rawdoc rawdoc = new Rawdoc()
			.setDomain( DOMAIN_ID )
			.setNature( RawdocNature.INVOICE );
		AonCoreException e = assertThrows(AonCoreException.class, () -> AON.rawdocSave(getOccam(), rawdoc) );
		assertEquals(AonError.EMPTY_RAWDOC_TYPE.getMessage(),e.getMessage());
	}

	@Test
	public void testEmptyStatuc() {
		Rawdoc rawdoc = new Rawdoc()
			.setDomain( DOMAIN_ID )
			.setNature( RawdocNature.INVOICE )
			.setType( RawdocType.INPUT )
		;
		AonCoreException e = assertThrows(AonCoreException.class, () -> AON.rawdocSave(getOccam(), rawdoc) );
		assertEquals(AonError.EMPTY_RAWDOC_STATUS.getMessage(),e.getMessage());
	}
}
