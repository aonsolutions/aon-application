package com.code.aon.infoweb;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.WebInfoStyleDB;

@Entity
@Table(name="web_info_style")
public class WebInfoStyle extends WebInfoStyleDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    @Transient
	public String getName() {
    	String text = getVariable();
    	text = StringUtils.substringAfter(text, "_");
    	text = StringUtils.substringAfter(text, "_");
    	text = StringUtils.replace(text, "_", " ");
		return WordUtils.capitalizeFully(text);
    }		
	
}