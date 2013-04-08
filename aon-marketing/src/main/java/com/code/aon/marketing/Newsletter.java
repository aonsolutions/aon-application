package com.code.aon.marketing;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.NewsletterDB;

@Entity
@Table(name="newsletter")
public class Newsletter extends NewsletterDB {

	private static final long serialVersionUID = 1L;
	
}