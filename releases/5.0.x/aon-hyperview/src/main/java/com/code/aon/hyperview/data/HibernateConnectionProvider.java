package com.code.aon.hyperview.data;

import java.sql.Connection;

import org.hibernate.Session;

import com.code.aon.common.dao.hibernate.HibernateUtil;

public class HibernateConnectionProvider implements IConnectionProvider {

	public Connection getConnection() {
		Session session = HibernateUtil.getSession();
		return session.connection();
	}

}
