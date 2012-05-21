package com.esferalia.aon.entity.hibernate.tools;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.reveng.DelegatingReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.TableIdentifier;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.MetaAttribute;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.enumeration.SecurityLevel;

public class AonReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {
	
	private static final String SECURITY_LEVEL_COLUMN = "security_level";
	private static final String EXTRA_IMPORT_ATTR = "extra-import";
	private static final String IMPLEMENTS_ATTR = "implements";
	private static final String FOREIGN = "foreign";
	private static String ENTITY_PACKAGE = "com.esferalia.aon.entity.master";
	private static String CLASS_SUFFIX= "DB";

	public AonReverseEngineeringStrategy(ReverseEngineeringStrategy delegate) {
		super(delegate);
	}
	@Override
	public String tableToClassName(TableIdentifier tableIdentifier) {
		String className = super.tableToClassName(tableIdentifier);
		if (className.startsWith(ENTITY_PACKAGE)) {
			String entitySimpleName = ClassUtils.getShortClassName(className);
			className = ENTITY_PACKAGE + "." + entitySimpleName + CLASS_SUFFIX;  
		}
		return className;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String foreignKeyToEntityName(String keyname,
			TableIdentifier fromTable, List fromColumnNames,
			TableIdentifier referencedTable, List referencedColumnNames,
			boolean uniqueReference) {
		
		String entityName = super.foreignKeyToEntityName(keyname, fromTable, fromColumnNames,
				referencedTable, referencedColumnNames, uniqueReference);
		if (StringUtils.endsWith(entityName, CLASS_SUFFIX)) {
			entityName = StringUtils.chomp(entityName,CLASS_SUFFIX);
		}
		return entityName; 
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean excludeForeignKeyAsManytoOne(String keyname,
			TableIdentifier fromTable, List fromColumns,
			TableIdentifier referencedTable, List referencedColumns) {
		if (!"profile".equals(fromTable.getName()) ) {
			Column column = (Column) fromColumns.get(0);
			if ("domain".equals(column.getName()) && "domain".equals(referencedTable.getName()) ) {
				return true;
			}			
		}
		return super.excludeForeignKeyAsManytoOne(keyname, fromTable, fromColumns,
				referencedTable, referencedColumns);
	}
		
	@SuppressWarnings("rawtypes")
	@Override
	public boolean excludeForeignKeyAsCollection(String keyname,
			TableIdentifier fromTable, List fromColumns,
			TableIdentifier referencedTable, List referencedColumns) {
		return true;
	}
	
	@Override
	public String getTableIdentifierStrategyName(TableIdentifier tableIdentifier) {
		Properties properties = getTableIdentifierProperties(tableIdentifier);
		if (properties != null && (properties.contains("registry") || properties.contains("project") || properties.contains("asset"))) {
			return FOREIGN;	
		}
		return super.getTableIdentifierStrategyName(tableIdentifier);
	}

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
	public String columnToHibernateTypeName(TableIdentifier table, String columnName, int sqlType, int length, int precision, int scale, boolean nullable, boolean generatedIdentifier) {
		if (SECURITY_LEVEL_COLUMN.equals(columnName)) {
			return SecurityLevel.class.getName();
		}
		String type = super.columnToHibernateTypeName(table, columnName, sqlType, length, precision, scale, nullable, generatedIdentifier); 
		return type;
	}

	/*
	 *  Se sobrecarga este metodo porque hay un campo en la tabla offer que se llama version, y de no ser asi lo controla el hibernate.
	 *
	 */
	@Override
	public boolean useColumnForOptimisticLock(TableIdentifier identifier, String column) {
		return false;
	}

}
