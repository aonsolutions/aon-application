package com.esferalia.aon.occam.test.accounting.account;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.nio.charset.Charset;

import org.junit.Test;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.impl.jooq.dao.AccountDAO;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.watson.util.AonStringUtils;

public class InsertTest extends AbstractOccamTest {

	@Test
	public void testInsert() throws IOException {
		InputStream inputStream = InsertTest.class.getResourceAsStream("/com/esferalia/aon/occam/test/accounting/account/accounts.txt");
		InputStreamReader inputStreamReader = new InputStreamReader(inputStream,Charset.forName("UTF-8"));
		LineNumberReader reader = new LineNumberReader(inputStreamReader);
		while ( reader.ready()) {
			String line = reader.readLine();
			String[] data = AonStringUtils.split(line, '|');
			String code = data[0];
			String description = data[1];
			Account account = AccountDAO.get(ctx, code);
			if (account == null) {
				account = new Account()
					.setDomain(ctx.getDomainId())
					.setCode( code )
					.setDescription( description );
				AccountDAO.save(ctx, account);
			}
		}
	}
	
}
