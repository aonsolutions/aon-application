package com.code.aon.marketing;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.NewsletterDetailDB;

@Entity
@Table(name="newsletter_detail")
public class NewsletterDetail extends NewsletterDetailDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
}