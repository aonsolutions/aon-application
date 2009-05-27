package com.code.aon.ui.cms.velocity.attribute;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.code.aon.cms.SportCareerPath;
import com.code.aon.cms.SportNationality;
import com.code.aon.cms.SportNationalityDetail;
import com.code.aon.cms.SportPlayer;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.velocity.SportGenerator;

public class SportPlayerHandler {
	
	private static final Logger LOGGER = Logger.getLogger(SportPlayerHandler.class.getName());

	private String alias;
	
	private String name;
	
	private String bornDate;
	
	private String bornPlace;
	
	private Double weight;
	
	private Double lenght;
	
	private String sportNationality;
	
	private boolean comunitary;
	
	private String photo;
	
	private String sportClub;

	private Integer number;

	private String url;

	private List<SportCareerHandler> careers;

	public SportPlayerHandler(SportPlayer sportPlayer){
		this.alias = ""+sportPlayer.getId();
		this.name = sportPlayer.getName();
		Locale locale = ControllerUtil.getCurrentLanguage().getLanguage().getLocale();
		SimpleDateFormat formatter;
		if ("eu".equals(locale.getLanguage()))
			formatter = new SimpleDateFormat ("dd/MM/yy");
		else
			formatter = (SimpleDateFormat)SimpleDateFormat.getDateInstance(DateFormat.SHORT,locale);
		try{
			this.bornDate = formatter.format(sportPlayer.getBornDate());
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		this.bornPlace = sportPlayer.getBornPlace();
		this.weight = sportPlayer.getWeight();
		this.lenght = sportPlayer.getLenght();
		try{
			this.sportNationality = getNationalityString(sportPlayer.getSportNationality());
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		this.comunitary = sportPlayer.isComunitary();
		this.photo = sportPlayer.getPhoto();
		try{
			this.sportClub = sportPlayer.getSportClub().getDescription();
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		this.number = sportPlayer.getNumber();
		try{
			this.careers = fillCareer(sportPlayer.getId());
		}catch (Throwable th) {
			LOGGER.log(Level.SEVERE, th.getMessage(), th);
		}
		this.url = Templates.SPORT.getHtmlName();
		this.url = this.url.replaceAll("%NAME%", SportGenerator.PLAYER + this.alias);
	}

	private String getNationalityString(SportNationality sn){
		try {
			IManagerBean bean = BeanManager.getManagerBean(SportNationalityDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_NATIONALITY_DETAIL_SPORT_NATIONALITY_ID), sn.getId());
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_NATIONALITY_DETAIL_LANGUAGE_ID), ControllerUtil.getCurrentLanguage().getId());
			List<ITransferObject> lst = bean.getList(criteria);
			if (!lst.isEmpty()){
				return ((SportNationalityDetail)lst.get(0)).getDescription();
			}
		} catch (ManagerBeanException e) {
		}
		return null;
	}
	
	private List<SportCareerHandler> fillCareer(Integer playerId){
		List<SportCareerHandler> listSportCareerHandler = new ArrayList<SportCareerHandler>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(SportCareerPath.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICMSAlias.SPORT_CAREER_PATH_SPORT_PLAYER_ID), playerId);
			List<ITransferObject> lst = bean.getList(criteria);
			for (Iterator iterator = lst.iterator(); iterator.hasNext();) {
				listSportCareerHandler.add(new SportCareerHandler((SportCareerPath) iterator.next()));
			}
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return listSportCareerHandler;
	}

	public String getAlias() {
		return alias;
	}

	public String getName() {
		return name;
	}

	public String getBornDate() {
		return bornDate;
	}

	public String getBornPlace() {
		return bornPlace;
	}

	public Double getWeight() {
		return weight;
	}

	public Double getLenght() {
		return lenght;
	}

	public String getSportNationality() {
		return sportNationality;
	}

	public boolean isComunitary() {
		return comunitary;
	}

	public String getPhoto() {
		return photo;
	}

	public String getSportClub() {
		return sportClub;
	}

	public Integer getNumber() {
		return number;
	}

	public String getUrl() {
		return url;
	}

	public List<SportCareerHandler> getCareers() {
		return careers;
	}
	
}
