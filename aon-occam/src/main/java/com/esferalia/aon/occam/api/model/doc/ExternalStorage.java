package com.esferalia.aon.occam.api.model.doc;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IExternalStorageVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum ExternalStorage {
	
	AON {
		@Override public <T> T visit(String contentDisposition, IExternalStorageVisitor<T> visitor) {return visitor.visitAon(contentDisposition);}
	},
	DRIVE {
	 	@Override public <T> T visit(String contentDisposition, IExternalStorageVisitor<T> visitor) {return visitor.visitDrive(contentDisposition);}
	},
	AWS() {
		@Override public <T> T visit(String contentDisposition, IExternalStorageVisitor<T> visitor) {return visitor.visitAws(contentDisposition);}
	},
	SCALEWAY() {
		@Override public <T> T visit(String contentDisposition, IExternalStorageVisitor<T> visitor) {return visitor.visitScaleway(contentDisposition);}
	};
	
	private ExternalStorage() {

	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public abstract <T> T visit(String contentDisposition, IExternalStorageVisitor<T> visitor);
	
	public static ExternalStorage safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ExternalStorage safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ExternalStorage.values().length) return null;
		return ExternalStorage.values()[i];
	}
	
	public static ExternalStorage safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ExternalStorage rs : values()) {
			if(rs.name().equalsIgnoreCase(i))
				return rs;
		}
		return null;
	}
	
	public boolean isAon() {
		return AON.equals(this);
	}
	
	public boolean isAws() {
		return AWS.equals(this);
	}
	
	public boolean isDrive() {
		return DRIVE.equals(this);
	}
	
	public boolean isScaleway() {
		return SCALEWAY.equals(this);
	}
}


