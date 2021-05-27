package net.aonsolutions.dump;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

public class ModifyDataCallBack extends AbstractChaimCallbackDump {

	private CallbackDump  cb;
	String pattern;
	ArrayList<String> fieldNames;
	String table;
	String field;
	
	public ModifyDataCallBack(CallbackDump cb, String pattern, String table, String field) {
		super(cb);
		this.cb = cb;
		this.pattern = pattern;
		this.table = table;
		this.field = field;
		this.fieldNames = compile(this.pattern);
	}
	
	@Override
	public void onNewRow(Table<?> t, Map<Field<?>, Object> insertMap, Record r) {

		if (t.getName().equals(this.table)){	
			
			String value = pattern;
			
			for (String fieldName : fieldNames){
				Field<?> field = r.field(fieldName);
				Object fieldValue = r.getValue(field);
				if (fieldValue != null)
					value = value.replaceAll("\\{"+fieldName+"\\}", fieldValue.toString());
			};	
			
			insertMap.put(t.field(this.field), value);
		}
		
		cb.onNewRow(t, insertMap, r);
	}
	
	private ArrayList<String> compile(String rename) {
		
		ArrayList<String> result = new ArrayList<String>();
		Pattern pattern = Pattern.compile("\\{([^\\}]*)\\}");
		Matcher matcher = pattern.matcher(rename);
		
		while ( matcher.find() )
			result.add(matcher.group(1));
		
		return result;
		
	}

}
