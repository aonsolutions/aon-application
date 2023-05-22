package com.code.aon.marketing;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.config.IScopable;
import com.code.aon.marketing.enumeration.NewsType;
import com.esferalia.aon.entity.master.NewsDB;

@Entity
@Table(name="news")
public class News extends NewsDB implements IScopable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

    public News() {
    	setActive(true);
    	setType(NewsType.NEWS);
    }
	
}