package com.esferalia.aon.payroll.enumeration;

public interface  SSRegimeTypeVisitor<E> {

	E visitGeneralRegime(SSRegimeType ssRegimeType);

	E visitAgriculturalRegime(SSRegimeType ssRegimeType);

	E visitDomesticEmployeesRegime(SSRegimeType ssRegimeType);

	E visitSelfEmployedRegime(SSRegimeType ssRegimeType);

	E visitCoalMiningRegime(SSRegimeType ssRegimeType);

	E visitSeaWorkersRegime(SSRegimeType ssRegimeType);

	E visitStudentInsuranceRegime(SSRegimeType ssRegimeType);

	E visitArtistRegime(SSRegimeType ssRegimeType);

	E visitIsfasRegime(SSRegimeType ssRegimeType);

	E visitMufaceRegime(SSRegimeType ssRegimeType);
}
