package com.code.aon.infoweb;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringEscapeUtils;

import com.code.aon.infoweb.enumeration.WebInfoPageType;
import com.esferalia.aon.entity.master.WebInfoPageDB;

@Entity
@Table(name="web_info_page")
public class WebInfoPage extends WebInfoPageDB {
	
	private static final long serialVersionUID = 1L;

	private Set<WebInfoPageDetail> details;
	private Set<WebInfoPageResource> resources;

	public WebInfoPage() {
		setActive(true);
		setPosition(0);
	}

	@OneToMany(mappedBy = "webInfoPage", cascade={CascadeType.REMOVE})
	public Set<WebInfoPageDetail> getDetails() {
		return details;
	}

	public void setDetails(Set<WebInfoPageDetail> details) {
		this.details = details;
	}

	@OneToMany(mappedBy = "webInfoPage", cascade={CascadeType.REMOVE})
	public Set<WebInfoPageResource> getResources() {
		return resources;
	}

	public void setResources(Set<WebInfoPageResource> resources) {
		this.resources = resources;
	}

    @Transient
	public boolean isDetailed() {
		if (getType()== WebInfoPageType.GALLERY) return true;
		if (getType()== WebInfoPageType.GENERIC) return true;
		if (getType()== WebInfoPageType.LOCATION) return true;
		return false;
    }

    @Transient
	public String getEscapedName() {
    	return StringEscapeUtils.escapeHtml(getName());
    }
    
}