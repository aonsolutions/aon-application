package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.SportClub;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.velocity.SportGenerator;

public class SportClubHandler {

	private String alias;
	private String description;
	private String stadium;
	private String image;
	private String logo;
	private String url;
	private List<SportPositionHandler> positions;
	private List<SportCoachHandler> coachs;
	
	public SportClubHandler(SportClub sportClub){
		this.alias = ""+sportClub.getId();
		this.description = sportClub.getDescription();
		this.stadium = sportClub.getStadium();
		this.image = sportClub.getImage();
		this.logo = sportClub.getLogo();
		this.positions = new ArrayList<SportPositionHandler>();
		this.coachs = new ArrayList<SportCoachHandler>();
		this.url = Templates.SPORT.getHtmlName();
		this.url = this.url.replaceAll("%NAME%", SportGenerator.CLUB + this.alias);
	}

	public void addSportPositionHandler(SportPositionHandler sportPositionHandler){
		this.positions.add(sportPositionHandler);
	}

	public void addSportCoachHandler(SportCoachHandler sportCoachHandler){
		this.coachs.add(sportCoachHandler);
	}

	public String getAlias() {
		return alias;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getStadium() {
		return stadium;
	}

	public String getImage() {
		return image;
	}

	public String getLogo() {
		return logo;
	}

	public String getUrl() {
		return url;
	}

	public List<SportPositionHandler> getPositions() {
		return this.positions;
	}

	public List<SportCoachHandler> getCoachs() {
		return coachs;
	}
	
}
