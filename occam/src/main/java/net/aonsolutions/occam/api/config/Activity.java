package net.aonsolutions.occam.api.config;

import java.io.Serializable;

import net.aonsolutions.occam.api.HasSelector;
import net.aonsolutions.watson.server.AonObjectUtils;

public class Activity implements Serializable, HasSelector<Activity> {

	private static final long serialVersionUID = 8605536086019198332L;
	
	private Integer id;
	private Integer domain;
	private String description;
	private String epigraph;
	
	private boolean dirty;
	private boolean selected;

	public Integer getId() {
		return id;
	}
	public Activity setId(Integer id) {
		this.dirtyMark( AonObjectUtils.notEquals(this.id,id) );
		this.id = id;
		return this;
	}
	
	public Integer getDomain() {
		return domain;
	}
	public Activity setDomain(Integer domain) {
		this.dirtyMark( AonObjectUtils.notEquals(this.domain,domain) );
		this.domain = domain;
		return this;
	}
	
	public String getDescription() {
		return description;
	}
	public Activity setDescription(String description) {
		this.dirtyMark( AonObjectUtils.notEquals(this.description,description) );
		this.description = description;
		return this;
	}
	
	public String getEpigraph() {
		return epigraph;
	}
	public Activity setEpigraph(String epigraph) {
		this.dirtyMark( AonObjectUtils.notEquals(this.epigraph,epigraph) );
		this.epigraph = epigraph;
		return this;
	}
	
	public boolean isDirty() {
		return dirty;
	}
	public Activity setDirty(boolean dirty) {
		this.dirty = dirty;
		return this;
	}
	public Activity dirtyMark(boolean dirty) {
		this.dirty = isDirty() || dirty;
		return this;
	}
	
	// ---------------------------------------------------------- HasSelector<Activity>
	@Override
	public boolean isSelected() {
		return selected;
	}
	@Override
	public Activity setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
}

