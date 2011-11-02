package com.code.aon.ui.document;

import static org.alfresco.webservice.util.Constants.NAMESPACE_CONTENT_MODEL;

import java.util.Comparator;

import org.alfresco.webservice.types.NamedValue;
import org.alfresco.webservice.types.ResultSetRow;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

public class AlfrescoComparator implements Comparator<ResultSetRow> {
	
	private AlfrescoDAO dao;
	
	private String name;
	
	private boolean ascending;
	
	public AlfrescoComparator(AlfrescoDAO dao, String name, boolean ascending) {
		this.dao = dao;
		this.name = name;
		if ( name.startsWith("cm:") ) {
			this.name = StringUtils.replace(name, "cm:", "{" + NAMESPACE_CONTENT_MODEL + "}" );
		}
		this.ascending = ascending;
	}

	private Object getValue( ResultSetRow row ) {
		for( NamedValue nv : row.getColumns() ) {
			if ( name.equals(nv.getName()) ) {
				return dao.getValue(nv);
			}
		}
		return null;
	}
	
	@Override
	public int compare(ResultSetRow o1, ResultSetRow o2) {
		String value1 = ObjectUtils.toString( getValue(o1) );
		String value2 = ObjectUtils.toString( getValue(o2) );
		int result = 0;
		if ( ascending ) {
			result = value1.compareToIgnoreCase(value2);
		} else {
			result = value2.compareToIgnoreCase(value1);
		}
		return result;
	}

}
