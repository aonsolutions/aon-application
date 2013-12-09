package com.code.aon.infoweb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringEscapeUtils;

import com.esferalia.aon.entity.master.WebInfoPageDetailDB;

@Entity
@Table(name="web_info_page_detail")
public class WebInfoPageDetail extends WebInfoPageDetailDB {
	
	private static final long serialVersionUID = 1L;
	
    @Transient
	public String getEscapedTtle() {
    	return StringEscapeUtils.escapeHtml(getTitle());
    }	

}