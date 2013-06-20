package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.NewsletterDetailDB;

@Entity
@Table(name="newsletter_detail")
public class NewsletterDetail extends NewsletterDetailDB {

	private static final long serialVersionUID = 1L;
	
}