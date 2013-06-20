package com.code.aon.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ClassUtils;
import org.dom4j.Element;
import org.hibernate.transform.BasicTransformerAdapter;

public class ExportResultTransformer extends BasicTransformerAdapter {

	private static final long serialVersionUID = -8420946186269808194L;
	
	private Class<?> entity;
	
	public ExportResultTransformer(Class<?> entity) {
		this.entity = entity;
	}
	
	/**
	 * Return just the root entity from the row tuple.
	 */
	public Object transformTuple(Object[] tuple, String[] aliases) {
		return tuple[ tuple.length-1 ];
	}	

	@Override
	@SuppressWarnings("unchecked")	
	public List transformList(List list) {
		if ( (! list.isEmpty()) && (list.get(0) instanceof Element) ) { 
			String name = ClassUtils.getShortClassName(this.entity);
			List result = new ArrayList( list.size() );
			for( Object o : list ) {
				Element element = (Element) o;
				if ( name.equals(element.getName()) ) {
					result.add(o);	
				}
			}
		}
		return list;
	}
	
}
