package com.esferalia.aon.gwt.document.jooq;

import static com.esferalia.aon.jooq.tables.MailAccount.MAIL_ACCOUNT;
import static com.esferalia.aon.jooq.tables.Signature.SIGNATURE;

import java.util.Vector;

import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Result;

import com.esferalia.aon.gwt.document.shared.MailAccount;
import com.esferalia.aon.gwt.document.shared.MailAccountList;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.User;


public class SendEmailDialogJooq {
	
	public static MailAccountList getMailAccounts(Domain domain,User user,Integer domainId) {
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Result<Record4<Integer, String, String,Integer>> data = ctx.getDslContext()
					.select(MAIL_ACCOUNT.ID,MAIL_ACCOUNT.NAME,MAIL_ACCOUNT.EMAIL,MAIL_ACCOUNT.SIGNATURE)
					.from(MAIL_ACCOUNT)
					.where((MAIL_ACCOUNT.USER_ID.isNull().or(MAIL_ACCOUNT.USER_ID.eq(user.getId()))).and(MAIL_ACCOUNT.DOMAIN.eq(domainId))).fetch();
			
			Vector<MailAccount> list = new Vector<MailAccount>();
			for (Record4<Integer, String, String,Integer> record : data) {
				MailAccount ma = new MailAccount();
				ma.setId(record.value1());
				ma.setName(record.value2());
				ma.setEmail(record.value3());
				if(record.value4()!=null)
					ma.setSignature(getSignature(domain, user, record.getValue(MAIL_ACCOUNT.SIGNATURE)));
				else ma.setSignature("");
				list.add(ma);
			}
			MailAccountList mal = new MailAccountList();
			mal.setList(list);
			return mal;
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
		
	}
	
	public static String getSignature(Domain domain, User user, Integer id){
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());
			Record1<String> data = ctx.getDslContext()
					.select(SIGNATURE.SIGNATURE_)
					.from(SIGNATURE)
					.where(SIGNATURE.ID.eq(id)).fetchOne();
			
			return data.value1();
			
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
}
