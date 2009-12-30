package com.code.aon.ui.cvitae.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.cvitae.enumeration.DriverLicense;
import com.code.aon.cvitae.enumeration.Europe;
import com.code.aon.cvitae.enumeration.EvaluationLevel;
import com.code.aon.cvitae.enumeration.KnowledgeLastUse;
import com.code.aon.cvitae.enumeration.KnowledgeLevel;
import com.code.aon.cvitae.enumeration.KnowledgeExperience;
import com.code.aon.cvitae.enumeration.Degree;
import com.code.aon.cvitae.enumeration.LanguageEnum;
import com.code.aon.cvitae.enumeration.LanguageLevel;
import com.code.aon.cvitae.enumeration.LatinAmerica;
import com.code.aon.cvitae.enumeration.RestOfWorld;
import com.code.aon.person.enumeration.Gender;

public class CVitaeCollectionsController {
	
	private List<SelectItem> genders;

	private List<SelectItem> countries;

	private List<SelectItem> degrees;
	
	private List<SelectItem> knowledgeLevels;
	
	private List<SelectItem> knowledgeExperiences;
	
	private List<SelectItem> knowledgeLastUses;
	
	private List<SelectItem> languageLevels;

	private List<SelectItem> evaluateLevels;
	
	private List<SelectItem> languages;
	
	private List<SelectItem> driverLicenses;

	public List<SelectItem> getGenders(){
		if (genders == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			genders = new LinkedList<SelectItem>();
			for (Gender gender : Gender.values()) {
				String name = gender.getName(locale);
				SelectItem item = new SelectItem(gender, name);
				genders.add(item);
			}
		}
		return genders;
	}

	public List<SelectItem> getCountries(){
		if (countries == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			countries = new LinkedList<SelectItem>();
			for (Europe europe : Europe.values()) {
				String name = europe.getName(locale);
				SelectItem item = new SelectItem(europe.name(), name);
				countries.add(item);
			}
			for (LatinAmerica la : LatinAmerica.values()) {
				String name = la.getName(locale);
				SelectItem item = new SelectItem(la.name(), name);
				countries.add(item);
			}
			for (RestOfWorld row : RestOfWorld.values()) {
				String name = row.getName(locale);
				SelectItem item = new SelectItem(row.name(), name);
				countries.add(item);
			}
		}
		return countries;
	}

	public List<SelectItem> getDegrees(){
		if (degrees == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			degrees = new LinkedList<SelectItem>();
			for (Degree degree : Degree.values()) {
				String name = degree.getName(locale);
				SelectItem item = new SelectItem(degree, name);
				degrees.add(item);
			}
		}
		return degrees;
	}

	public List<SelectItem> getKnowledgeLevels() {
		if (knowledgeLevels == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			knowledgeLevels = new LinkedList<SelectItem>();
			for (KnowledgeLevel knowledgeLevel : KnowledgeLevel.values()) {
				String name = knowledgeLevel.getName(locale);
				SelectItem item = new SelectItem(knowledgeLevel, name);
				knowledgeLevels.add(item);
			}
		}
		return knowledgeLevels;
	}

	public List<SelectItem> getKnowledgeExperiences() {
		if (knowledgeExperiences == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			knowledgeExperiences = new LinkedList<SelectItem>();
			for (KnowledgeExperience knwoledgeExperience : KnowledgeExperience.values()) {
				String name = knwoledgeExperience.getName(locale);
				SelectItem item = new SelectItem(knwoledgeExperience, name);
				knowledgeExperiences.add(item);
			}
		}
		return knowledgeExperiences;
	}

	public List<SelectItem> getKnowledgeLastUses() {
		if (knowledgeLastUses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			knowledgeLastUses = new LinkedList<SelectItem>();
			for (KnowledgeLastUse knowledgeLastUse : KnowledgeLastUse.values()) {
				String name = knowledgeLastUse.getName(locale);
				SelectItem item = new SelectItem(knowledgeLastUse, name);
				knowledgeLastUses.add(item);
			}
		}
		return knowledgeLastUses;
	}

	public List<SelectItem> getLanguageLevels() {
		if (languageLevels == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			languageLevels = new LinkedList<SelectItem>();
			for (LanguageLevel languageLevel : LanguageLevel.values()) {
				String name = languageLevel.getName(locale);
				SelectItem item = new SelectItem(languageLevel, name);
				languageLevels.add(item);
			}
		}
		return languageLevels;
	}

	public List<SelectItem> getEvaluateLevels() {
		if (evaluateLevels == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			evaluateLevels = new LinkedList<SelectItem>();
			for (EvaluationLevel evaluateLevel : EvaluationLevel.values()) {
				String name = evaluateLevel.getName(locale);
				SelectItem item = new SelectItem(evaluateLevel, name);
				evaluateLevels.add(item);
			}
		}
		return evaluateLevels;
	}

	public List<SelectItem> getLanguages() {
		if (languages == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			languages = new LinkedList<SelectItem>();
			for (LanguageEnum language : LanguageEnum.values()) {
				String name = language.getName(locale);
				SelectItem item = new SelectItem(language, name);
				languages.add(item);
			}
		}
		return languages;
	}
	
	public List<SelectItem> getDriverLicenses() {
		if (driverLicenses == null) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			driverLicenses = new LinkedList<SelectItem>();
			for (DriverLicense driverLicense : DriverLicense.values()) {
				String name = driverLicense.getName(locale);
				SelectItem item = new SelectItem(driverLicense, name);
				driverLicenses.add(item);
			}
		}
		return driverLicenses;
	}
}