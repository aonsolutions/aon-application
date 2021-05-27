package com.esferalia.aon.occam.test.accounting.entry;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Diario {

	
	public static void main(String[] args) throws IOException {
		FileInputStream input = new FileInputStream("/home/ecastellano/Documents/DIARIO.csv");
		InputStreamReader in = new InputStreamReader(input);
		try (LineNumberReader reader = new LineNumberReader(in)) {
			int oldId = -1;
			while (reader.ready()) {
				String line = reader.readLine();
				String[] tokens = AonStringUtils.splitPreserveAllTokens(line, '|');
				int journal = AonNumberUtils.toint(tokens[1]);
				if (journal != oldId) {
					if (oldId != -1) {
						System.out.println("\t);\n");
					}
					oldId = journal;
					String date = tokens[2];
					System.out.println(
						"ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, AccountEntry.clone(ori).setEntryDate( FORMATTER.parse(\""+date+"\"))" );
				}
				String account 	= tokens[6];
				String accountDescription 	= tokens[7];
				String concept 	= tokens[8];
				String debit 	= AonStringUtils.replace(tokens[9],",",".");
				String credit 	= AonStringUtils.replace(tokens[10],",",".");
				String balancingAccount = tokens[11];
				String balancingAccountDescription = tokens[12];
				String documentNumber = tokens[13];
				System.out.println(
				"\t.addDetail( getAccountEntryDetail(\""+account+"\",\""+accountDescription+"\",\""+concept+"\"," + debit+","+credit+",\""+balancingAccount+"\",\""+balancingAccountDescription+"\",\""+documentNumber+"\"))"
				);
			}
			System.out.println("\t);\n");
		}
		
	}
/*
AccountEntry ae = AccountEntry.clone(ori).setEntryDate( FORMATTER.parse("01/01/2020"))
	.addDetail( getAccountEntryDetail("410000022","S/Fra: 2001C01065609",0.00,98.99	,"623000003","R-2020/000002"))
	.addDetail( getAccountEntryDetail("472000000","S/Fra: 2001C01065609",17.18,0.00	,"410000022","R-2020/000002"))
	.addDetail( getAccountEntryDetail("623000003","S/Fra: 2001C01065609",81.81,0.00	,"410000022","R-2020/000002"));
ae = ACCOUNTING.save(DOMAIN_NAME, DOMAIN_ID, USER, ae);
 	
 */
	
}
