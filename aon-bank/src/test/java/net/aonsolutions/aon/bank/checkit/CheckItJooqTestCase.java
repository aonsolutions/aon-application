package net.aonsolutions.aon.bank.checkit;

import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;

import net.aonsolutions.aon.bank.checkit.CheckItAPI;

public class CheckItJooqTestCase {

	private static final Integer AON_ID = 11413;
//	private static final Integer RAYSON_ID = 11414;
	
	@Ignore("Ignored because the iban's been modified and it'd crash")
	@Test
	public void InsertTransactionsTest() {
//		ES6830581804332720027870 -> de muestra en la BD
//		ES0614650100936000306237 -> El de Rayson
		try {
			int insertedRows = CheckItAPI.insertTransactions(
					"b72384936-ayudat.aonsolutions.net"
					, 7138
					, ""
					, AON_ID
					, "ES2601826809110101503779"
				);
			
			System.out.println("InsertTransactionsTest - INSERTED ROWS: "+insertedRows);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}
