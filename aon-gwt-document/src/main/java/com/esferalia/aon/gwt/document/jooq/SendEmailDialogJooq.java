package com.esferalia.aon.gwt.document.jooq;

import java.util.LinkedList;

import com.esferalia.aon.gwt.document.shared.MailAccountList;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.security.User;


public class SendEmailDialogJooq {
	
	public static MailAccountList getMailAccounts(Domain domain,User user,Integer domainId) {
		LinkedList<MailAccount> list = AON.getMailAccountList(domain.getName(), domain.getId(), user.getLogin(), 
				f -> (f.getUserIdProperty().isNull().or(f.getUserIdProperty().eq(user.getId()))).and(f.getDomainProperty().eq(domainId)));
		list.stream().forEach(ma -> {
			ma.setSignatureStr(getSignature(domain, user, ma.getSignatureId()));
		});
		return new MailAccountList().setList(list);
	}
	
	public static String getSignature(Domain domain, User user, Integer signatureId){
		return AON.getSignature(domain.getName(), domain.getId(), user.getLogin(), signatureId)
				.getSignature();
	}
}
