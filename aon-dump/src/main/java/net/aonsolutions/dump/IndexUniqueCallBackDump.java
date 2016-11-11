package net.aonsolutions.dump;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

public class IndexUniqueCallBackDump extends ModifyDataCallBack{

	private Set<String> logins;
	
	public IndexUniqueCallBackDump(CallbackDump cb) {
		super(cb, "{login}-1", "user", "login");
		this.logins = new HashSet<String>();
	}

	@Override
	public void onNewRow(Table<?> t, Map<Field<?>, Object> insertMap, Record r) {
		
		if (t.getName().equals("user")){
			String login = "";
			login = (String) insertMap.get(t.field("login"));
			if (!logins.add(login)){
				super.onNewRow(t, insertMap, r);
			}
		}
		next.onNewRow(t, insertMap, r);
	}

}
