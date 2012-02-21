package com.code.aon.ui.document.tree;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.code.aon.company.Enterprise;
import com.code.aon.document.EnterpriseDocument;
import com.code.aon.project.Project;

public class AonTreeKey implements Comparable<AonTreeKey> {

	private EnterpriseTreeType type;
	
	private String label;
	
	private String value;
	
	private static String formatId(EnterpriseDocument ed) {
		String id = ed.getId().getUuid();
		return StringUtils.remove(id, "-");
	}
	
	public AonTreeKey( Enterprise e ) {
		this(e.getId(), EnterpriseTreeType.ENTERPRISE, e.getRegistry().getFullName());
	}
	
	public AonTreeKey( Project project ) {
		this(project.getId(), EnterpriseTreeType.PROJECT, project.getName());
	}

	public AonTreeKey( EnterpriseDocument ed ) {
		this(formatId(ed), EnterpriseTreeType.DOCUMENT, ed.getName());
	}

	private AonTreeKey(Serializable id, EnterpriseTreeType type, String label ) {
		this.label = label;
		this.type = type;
		this.value = getKey(type, id);
	}
	
	public EnterpriseTreeType getType() {
		return type;
	}

	public String getLabel() {
		return label;
	}

	public String getValue() {
		return value;
	}

	private static String getKey( EnterpriseTreeType type, Serializable id ) {
		return type.ordinal() + "@" + id.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.getClass() != getClass()) return false;
		return StringUtils.equals(this.value, ((AonTreeKey) obj).value);
	}
	
	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public int compareTo(AonTreeKey o) {
		Integer type1 = type.ordinal();
		int result = type1.compareTo(o.type.ordinal());
		if ( result == 0 ) {
			result = label.compareTo(o.label);
		}
		return result;
	}

	@Override
	public String toString() {
		return value;
	}
	
}
