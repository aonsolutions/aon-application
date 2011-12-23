package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.TemplateDB;

@Entity
@Table(name = "mk_template")
public class Template extends TemplateDB  {

	private static final long serialVersionUID = 1L;
	
    public Template() {
    	setActive( true );
    }
	
}