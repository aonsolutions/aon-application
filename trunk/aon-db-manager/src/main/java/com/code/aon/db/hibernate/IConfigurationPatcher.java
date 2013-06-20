package com.code.aon.db.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public interface IConfigurationPatcher {
	
	void setSessionFactory( SessionFactory sessionFactory );

	void completeConfiguration( Configuration configuration);
	
}
