package com.code.aon.config.dao;

import java.io.File;
import java.io.IOException;

import com.code.aon.common.dao.AliasWriter;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.Bank;
import com.code.aon.config.PayMethod;
import com.code.aon.config.Scope;
import com.code.aon.config.Series;
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
		File file = new File("/AON-PROJECT/aon-config/src/main/java/com/code/aon/config/dao/IConfigAlias.java");
		String[] classes = new String[11]; 
		classes[0] = ApplicationParameter.class.getName();
		classes[1] = Bank.class.getName();
		classes[2] = PayMethod.class.getName();
		classes[3] = Scope.class.getName();
		classes[4] = Series.class.getName();
		classes[5] = Tax.class.getName();
		classes[6] = TaxDetail.class.getName();
		classes[7] = User.class.getName();
		classes[8] = UserScope.class.getName();
		classes[9] = UserWorkGroup.class.getName();
		classes[10] = WorkGroup.class.getName();
		HibernateUtil.getSessionFactory();
		AliasWriter writer = new AliasWriter("com.code.aon.config.dao");
		writer.write(classes, file);
		System.out.println( file.getAbsolutePath() );
		System.out.println("Alias generados");
	}
}