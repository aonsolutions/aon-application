package net.aonsolutions.aon.tbai.toolkit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.Country;

import ticketbai.emision.CountryType2;
import ticketbai.emision.SiNoType;

public class EmisionToolkit {
	
	// BOOLEAN TO SINOTYPE
	public static SiNoType booleanToSiNoType(boolean b) {
		if (b)	return SiNoType.S;
		else	return SiNoType.N;
	}

	// COUNTRY TO COUNTRYTYPE
	public static CountryType2 countryToCountryType2(Country c) {
		return CountryType2.valueOf(c.getIso2());
	}
}
