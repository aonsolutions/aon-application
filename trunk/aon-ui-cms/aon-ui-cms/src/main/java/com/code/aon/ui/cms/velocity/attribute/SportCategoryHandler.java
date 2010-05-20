package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.List;

import com.code.aon.cms.SportCategoryDetail;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.velocity.SportGenerator;

public class SportCategoryHandler {

	private String alias;
	
	private String description;
	
	private String url;
	
	private List<SportClubHandler> clubs;

	public SportCategoryHandler(SportCategoryDetail sportCategory){
		this.alias = sportCategory.getSportCategory().getAlias();
		this.description = sportCategory.getDescription();
		this.clubs = new ArrayList<SportClubHandler>();
		this.url = Templates.SPORT.getHtmlName();
		this.url = this.url.replaceAll("%NAME%", SportGenerator.CATEGORY + this.alias);
	}

	public void addSportClubHandler(SportClubHandler sportClubHandler){
		this.clubs.add(sportClubHandler);
	}

	public String getAlias() {
		return alias;
	}

	public String getDescription() {
		return description;
	}
	
	public String getUrl() {
		return url;
	}

	public List<SportClubHandler> getClubs() {
		return clubs;
	}
	
}
