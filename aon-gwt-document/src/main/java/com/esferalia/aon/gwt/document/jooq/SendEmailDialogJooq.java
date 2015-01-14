package com.esferalia.aon.gwt.document.jooq;

import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Signature.SIGNATURE;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Record4;
import org.jooq.Result;
import org.jooq.impl.DSL;

import com.code.aon.google.apis.DatabaseSync;
import com.code.aon.google.apis.jooq.JooqSettings;
import com.esferalia.aon.gwt.document.shared.MailAccount;
import com.esferalia.aon.gwt.document.shared.MailAccountList;

import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;


public class SendEmailDialogJooq {
	
	public static MailAccountList getMailAccounts(String domain,Integer user_id,Integer domainId) throws SQLException{
		Connection connection = null;
		try {
			
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Result<Record4<Integer, String, String,Integer>> data = dslContext
					.select(MAIL_ACCOUNT.ID,MAIL_ACCOUNT.NAME,MAIL_ACCOUNT.EMAIL,MAIL_ACCOUNT.SIGNATURE)
					.from(MAIL_ACCOUNT)
					.where((MAIL_ACCOUNT.USER_ID.isNull().or(MAIL_ACCOUNT.USER_ID.eq(user_id))).and(MAIL_ACCOUNT.DOMAIN.eq(domainId))).fetch();
			
			Vector<MailAccount> list = new Vector<MailAccount>();
			for (Record4<Integer, String, String,Integer> record : data) {
				MailAccount ma = new MailAccount();
				ma.setId(record.value1());
				ma.setName(record.value2());
				ma.setEmail(record.value3());
				if(record.value4()!=null)
					ma.setSignature(getSignature(domain,record.value4()));
				else ma.setSignature("");
				list.add(ma);
			}
			MailAccountList mal = new MailAccountList();
			mal.setList(list);
			return mal;
			
		} finally {
			if (connection != null)
				connection.close();
		}
		
	}
	
	public static String getSignature(String domain, Integer id) throws SQLException {
		Connection connection = null;
		try {
			
			connection = DatabaseSync.getConnection(domain);
			DSLContext dslContext = DSL.using(connection,
					JooqSettings.getDefaultSettings());

			Record1<String> data = dslContext
					.select(SIGNATURE.SIGNATURE_)
					.from(SIGNATURE)
					.where(SIGNATURE.ID.eq(id)).fetchOne();
			
			return data.value1();
			
		} finally {
			if (connection != null)
				connection.close();
		}
	}
}
