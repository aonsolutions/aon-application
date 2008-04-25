package com.code.aon.ldap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class DistinguishedName {
	
	private String[] levels;

	public DistinguishedName(String ... levels) {
		if ( levels.length > 1 ) {
			this.levels = levels;			
		} else {
			String dn = levels[0];
			this.levels = StringUtils.split(dn, ",");			
		}
	}

	public DistinguishedName( String partList, DistinguishedName dn ) {
		String[] parts = StringUtils.split(partList, ",");
		this.levels = new String[dn.getDepth()+parts.length];
		System.arraycopy(parts, 0, this.levels, 0, parts.length);
		System.arraycopy(dn.levels, 0, this.levels, parts.length, dn.getDepth());
	}

	public DistinguishedName( DistinguishedName dn, String partList ) {
		String[] parts = StringUtils.split(partList, ",");	
		this.levels = new String[dn.getDepth()+parts.length];
		System.arraycopy(dn.levels, 0, this.levels, 0, dn.getDepth());
		System.arraycopy(parts, 0, this.levels, dn.getDepth(), parts.length);
	}
	
	public String getLevel( int index ) {
		return this.levels[index];
	}

	public String getLevelValue( int index ) {
		String level = getLevel(index);
		return StringUtils.split(level, "=")[1];
	}
	
	public int getDepth() {
		return this.levels.length;
	}
	
	public DistinguishedName getParent() {
		return new DistinguishedName( (String[]) ArrayUtils.subarray(levels, 1, getDepth()));
	}
	
	@Override
	public String toString() {
		return StringUtils.join(this.levels, ',');
	}

}
