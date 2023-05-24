package net.aonsolutions.occam.json;

import org.json.JSONObject;

import net.aonsolutions.occam.api.AonNames;
import net.aonsolutions.occam.api.config.AccountingConfiguration;
import net.aonsolutions.occam.api.constants.AppParam;

public class AccountingConfigurationJSON {
	
	private AccountingConfigurationJSON() {
	}
	
	public static AccountingConfiguration from(JSONObject json) {
		if (json == null) return null;
		AccountingConfiguration ac = new AccountingConfiguration();
		JSONObject accounts = json.optJSONObject(AonNames.ACCOUNTS);
		if (accounts != null) {
			accounts
				.keySet()
				.stream()
				.filter(k -> AppParam.safeValueOf(k).isPresent())
				.map(k -> AppParam.safeValueOf(k).get())
				.forEach( appParam -> ac.setAccount(appParam, AccountJSON.from(accounts.optJSONObject(appParam.toString()))));
		}
		return ac;
	}
	
	public static JSONObject to(AccountingConfiguration ac) {
		if (ac == null) return null;
		JSONObject json = new JSONObject();
		JSONObject accounts = new JSONObject();
		ac.getAccounts()
			.entrySet()
			.stream()
			.forEach(e -> accounts.put( e.getKey().toString(), AccountJSON.to(e.getValue())));
		return json.put(AonNames.ACCOUNTS, accounts);
	}
}
