package com.code.aon.infoweb;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.WebInfoPageResourceDB;

@Entity
@Table(name="web_info_page_resource")
public class WebInfoPageResource extends WebInfoPageResourceDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}