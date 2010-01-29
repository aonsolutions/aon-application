package com.code.aon.customer.dao;

import java.io.File;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.customer.Customer;
import com.code.aon.customer.CustomerSegment;

public class CustomerAliasWriter {

	public static void main(String[] args) throws Exception{
		File file = new File("/AON-PROJECT/aon-customer/src/main/java/com/code/aon/customer/dao/ICustomerAlias.java");
		String[] classes = new String[2];
		classes[0] = Customer.class.getName();
		classes[1] = CustomerSegment.class.getName();
		HibernateUtil.getSessionFactory( HibernateUtil.getSessionFactoryName() );
		AliasWriter writer = new AliasWriter("com.code.aon.customer.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}