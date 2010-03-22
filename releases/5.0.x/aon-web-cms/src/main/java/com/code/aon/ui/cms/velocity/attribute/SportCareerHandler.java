package com.code.aon.ui.cms.velocity.attribute;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.SportCareerPath;
import com.code.aon.ui.cms.util.ControllerUtil;

public class SportCareerHandler {

	private final static Logger LOGGER = LoggerFactory.getLogger(SportCareerHandler.class);
	
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
			LOGGER.error(th.getMessage(), th);
		}
		try{
			this.endDate = formatter.format(sportCareer.getEndDate());
		}catch (Throwable th) {
			LOGGER.error(th.getMessage(), th);
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
