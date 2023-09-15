package com.code.aon.marketing;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.config.IScopable;
import com.esferalia.aon.entity.master.TemplateDB;

@Entity
@Table(name = "mk_template")
public class Template extends TemplateDB implements IScopable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    public Template() {
    	setActive( true );
    }
	
}