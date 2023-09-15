package com.code.aon.marketing;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.code.aon.config.IScopable;
import com.esferalia.aon.entity.master.NewsletterDB;

@Entity
@Table(name="newsletter")
public class Newsletter extends NewsletterDB implements IScopable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
    public Newsletter() {
    	setActive(true);
    }
	
}