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
import com.code.aon.common.domain.IDomain;

public class AonReverseEngineeringStrategy extends DelegatingReverseEngineeringStrategy {

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
		return true;
	}
	
	@Override
	public Properties getTableIdentifierProperties(TableIdentifier identifier) {
		Properties props = super.getTableIdentifierProperties(identifier);
		return props;
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
		if (!tableIdentifier.getName().equals("domain")) {
			if (map == null) {
				map = new HashMap<String, MetaAttribute>();
			}
			MetaAttribute meta = map.get(EXTRA_IMPORT_ATTR);
			if (meta == null) {
				meta = new MetaAttribute(EXTRA_IMPORT_ATTR);	
			}
			meta.addValue(IDomain.class.getName());
			map.put(meta.getName(), meta);
			meta.addValue("com.code.aon.config.Domain");
			map.put(meta.getName(), meta);
			
			meta = map.get(IMPLEMENTS_ATTR);
			if (meta == null) {
				meta = new MetaAttribute(IMPLEMENTS_ATTR);	
			}
			meta.addValue("IDomain<Domain>");
			map.put(meta.getName(), meta);
		}
		return map;
	}

}

