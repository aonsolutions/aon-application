package com.esferalia.aon.payroll.enumeration;

public abstract class AbstractSSRegimeTypeVisitor<E> implements
		SSRegimeTypeVisitor<E> {

	@Override
	public E visitGeneralRegime(SSRegimeType ssRegimeType) {
		return null;
	}

	@Override
	public E visitAgriculturalRegime(SSRegimeType ssRegimeType) {
		return null;
	}

	@Override
	public E visitDomesticEmployeesRegime(SSRegimeType ssRegimeType) {
		return null;
	}

	@Override
	public E visitSelfEmployedRegime(SSRegimeType ssRegimeType) {
		return null;
	}

	@Override
	public E visitCoalMiningRegime(SSRegimeType ssRegimeType) {
		return null;
	}

	@Override
	public E visitSeaWorkersRegime(SSRegimeType ssRegimeType) {
		return null;
	}

	@Override
	public E visitStudentInsuranceRegime(SSRegimeType ssRegimeType) {
		return null;
	}

	@Override
	public E visitArtistRegime(SSRegimeType ssRegimeType) {
		return null;
	}
	
	@Override
	public E visitIsfasRegime(SSRegimeType ssRegimeType) {
		return null;
	}
	
	@Override
	public E visitMufaceRegime(SSRegimeType ssRegimeType) {
		return null;
	}

}
