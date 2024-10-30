package net.aonsolutions.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Iae implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private String section;
	private String epigraph;
	private String title;
	
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
	
	public String formatEpigraph() {
		return AonStringUtils.rightPad(
			AonStringUtils.replace(
					AonStringUtils.defaultIfBlank(getSection())+AonStringUtils.defaultIfBlank(getEpigraph())
			,".", "")
		, 6, '0');
	}
}
