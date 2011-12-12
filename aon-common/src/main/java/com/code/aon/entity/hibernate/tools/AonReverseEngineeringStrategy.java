package com.code.aon.entity.hibernate.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.MetaAttribute;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;

public class AonReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {
	private static final String SECURITY_LEVEL_COLUMN = "security_level";
	private static final String EXTRA_IMPORT_ATTR = "extra-import";
	private static final String IMPLEMENTS_ATTR = "implements";
	private static final String FOREIGN = "foreign";

	public AonReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
		super(delegate);
	}
	
	@Override
	public String foreignKeyToEntityName(String keyname,
			TableIdentifier fromTable, List fromColumnNames,
			TableIdentifier referencedTable, List referencedColumnNames,
			boolean uniqueReference) {
		
		String entityName = super.foreignKeyToEntityName(keyname, fromTable, fromColumnNames,
				referencedTable, referencedColumnNames, uniqueReference); 
		return entityName; 
	}
	
	@Override
	public boolean excludeForeignKeyAsCollection(String keyname,
			TableIdentifier fromTable, List fromColumns,
			TableIdentifier referencedTable, List referencedColumns) {
		if ("account_entry_detail_ibfk_4".equals(keyname)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String getTableIdentifierStrategyName(TableIdentifier tableIdentifier) {
		Properties properties = getTableIdentifierProperties(tableIdentifier);
		if (properties != null && (properties.contains("registry") || properties.contains("project"))) {
			return FOREIGN;	
		}
		return super.getTableIdentifierStrategyName(tableIdentifier);
	}

	
	@Override
	public Map tableToMetaAttributes(TableIdentifier tableIdentifier) {
		Map<String,MetaAttribute> map = super.tableToMetaAttributes(tableIdentifier);
		if (map == null) {
			map = new HashMap<String, MetaAttribute>();
		}
		MetaAttribute meta = map.get(EXTRA_IMPORT_ATTR);
		if (meta == null) {
			meta = new MetaAttribute(EXTRA_IMPORT_ATTR);	
			map.put(meta.getName(), meta);
		}
		meta.addValue(ITransferObject.class.getName());
		
		meta = map.get(IMPLEMENTS_ATTR);
		if (meta == null) {
			meta = new MetaAttribute(IMPLEMENTS_ATTR);	
			map.put(meta.getName(), meta);
		}
		meta.addValue(ITransferObject.class.getSimpleName());
		return map;
	}

	@Override
	public String columnToHibernateTypeName(TableIdentifier table,
			String columnName, int sqlType, int length, int precision,
			int scale, boolean nullable, boolean generatedIdentifier) {
		if (SECURITY_LEVEL_COLUMN.equals(columnName)) {
			return SecurityLevel.class.getName();
		}
		return super.columnToHibernateTypeName(table, columnName, sqlType, length,
				precision, scale, nullable, generatedIdentifier);
	}
}
