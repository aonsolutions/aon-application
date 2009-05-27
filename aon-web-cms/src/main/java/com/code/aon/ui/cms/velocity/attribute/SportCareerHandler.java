package com.code.aon.ui.cms.velocity.attribute;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.cms.SportCareerPath;
import com.code.aon.ui.cms.util.ControllerUtil;

public class SportCareerHandler {

	private static final Logger LOGGER = Logger.getLogger(SportCareerHandler.class.getName());
	
	private String club;

	private String initDate;
	
	private String endDate;
	
	public SportCareerHandler(SportCareerPath sportCareer){
		this.club = sportCareer.getClub();
		Locale locale = ControllerUtil.getCurrentLanguage().getLanguage().getLocale();
		SimpleDateFormat formatter;
		if ("eu".equals(locale.getLanguage()))
			formatter = new SimpleDateFormat ("dd/MM/yy");
		else
			formatter = (SimpleDateFormat)SimpleDateFormat.getDateInstance(DateFormat.SHORT,locale);
		try{
			this.initDate = formatter.format(sportCareer.getInitDate());
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		try{
			this.endDate = formatter.format(sportCareer.getEndDate());
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
	}

	public String getClub() {
		return club;
	}

	public String getInitDate() {
		return initDate;
	}

	public String getEndDate() {
		return endDate;
	}

	
}
