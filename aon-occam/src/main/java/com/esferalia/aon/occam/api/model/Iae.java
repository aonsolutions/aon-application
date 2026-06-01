package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class Iae implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String section;
	private String epigraph;
	private String title;
	
	public Iae() {
	}
	public Iae(String section,String epigraph,String title) {
		setSection(section);
		setEpigraph(epigraph);
		setTitle(title);
	}
	
	public Integer getId() {
		return id;
	}
	
	public Iae setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getSection() {
		return section;
	}
	
	public Iae setSection(String section) {
		this.section = section;
		return this;
	}
	
	public String getEpigraph() {
		return epigraph;
	}
	
	public Iae setEpigraph(String epigraph) {
		this.epigraph = epigraph;
		return this;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Iae setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public String getFullEpigraph() {
		if(getSection() == null && getEpigraph() == null) {
			return "";
		}
		String str = getSection() + getEpigraph().replace(".", "");
		while (str.length() < 6) {
			str = str + "0";
		}
		
		if(str.equals("183320")) str = "183321";
		if(str.equals("183310")) str = "183311";
		return str;		
	}
	
	public boolean isEmpty() {
		return getId() == null && getSection() == null && getEpigraph() == null;
	}
	
	@Override
	public String toString() {
		if(getSection() == null && getEpigraph() == null)
			return "";
		
		String str = getSection() + " - " + getEpigraph().replace(".", "") + " " + getTitle();
		return str;		
	}

}
