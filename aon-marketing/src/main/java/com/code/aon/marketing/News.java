package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.config.IScopable;
import com.code.aon.marketing.enumeration.NewsType;
import com.esferalia.aon.entity.master.NewsDB;

@Entity
@Table(name="news")
public class News extends NewsDB implements IScopable {

	private static final long serialVersionUID = 1L;

    public News() {
    	setActive(true);
    	setType(NewsType.NEWS);
    }
	
}