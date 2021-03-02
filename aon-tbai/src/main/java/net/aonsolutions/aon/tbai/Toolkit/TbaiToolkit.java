package net.aonsolutions.aon.tbai.Toolkit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.esferalia.aon.occam.api.model.type.Country;

import ticketbai.emision.CountryType2;
import ticketbai.emision.SiNoType;

public class TbaiToolkit {
	// BOOLEAN TO SINOTYPE
	public static SiNoType emision_boolean_to_siNoType(boolean b) {
		if (b)	return SiNoType.S;
		else	return SiNoType.N;
	}

	// COUNTRY TO COUNTRYTYPE
	public static CountryType2 emision_country_to_countryType2(Country c) {
		return CountryType2.valueOf(c.getIso2());
	}

	// FORMAT DATE
	public static Optional<String> format_date(Date date, String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);
		Optional<String> formattedDate;
		formattedDate = Optional.of(dateFormatter.format(date));
		return formattedDate;
	}
	
	//PARSE A DATE WITH AN SPECIFIC FORMAT
	public static Date parseDate(String dateStr,String format) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(format);	
		Date formattedDate;
		
		try {
			formattedDate = dateFormatter.parse(dateStr);
			return formattedDate;
		} catch (ParseException e){return null;}		
	}
}
