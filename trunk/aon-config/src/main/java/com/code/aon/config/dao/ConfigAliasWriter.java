package com.code.aon.config.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Bank;
import com.code.aon.config.CommissionType;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
import com.code.aon.config.Tariff;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.User;
import com.code.aon.config.UserScope;
import com.code.aon.config.UserWorkGroup;
import com.code.aon.config.WorkGroup;

public class ConfigAliasWriter {
	
	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("/AON-TRUNK/aon-config/src/main/java/com/code/aon/config/dao/IConfigAlias.java");
		String[] classes = new String[] { 
			ApplicationParameter.class.getName(),
			Bank.class.getName(),
			CommissionType.class.getName(),
			PayMethod.class.getName(),
			Scope.class.getName(),
			Series.class.getName(),
			Tariff.class.getName(),
			Tax.class.getName(),
			TaxDetail.class.getName(),
			User.class.getName(),
			UserScope.class.getName(),
			UserWorkGroup.class.getName(),
			WorkGroup.class.getName(), };
		HibernateUtil.getSessionFactory(null);
		AliasWriter writer = new AliasWriter("com.code.aon.config.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}