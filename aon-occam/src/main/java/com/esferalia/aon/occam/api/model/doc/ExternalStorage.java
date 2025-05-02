package com.esferalia.aon.occam.api.model.doc;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum ExternalStorage {
	
	AON 		{@Override public <T> T visit(ExternalStorageVisitor<T> visitor) {return visitor.visitAon();}},
	DRIVE 		{@Override public <T> T visit(ExternalStorageVisitor<T> visitor) {return visitor.visitDrive();}},
	AWS() 		{@Override public <T> T visit(ExternalStorageVisitor<T> visitor) {return visitor.visitAws();}},
	SCALEWAY() 	{@Override public <T> T visit(ExternalStorageVisitor<T> visitor) {return visitor.visitScaleway();}};
	
	private ExternalStorage() {
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static ExternalStorage safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ExternalStorage safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ExternalStorage.values().length) return null;
		return ExternalStorage.values()[i];
	}
	
	public static ExternalStorage safeValueOf( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst()
			.orElse(null);
	}
	
	public static String name(ExternalStorage e) {
		return e==null?null:e.name();
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
	
	public abstract <T> T visit(ExternalStorageVisitor<T> visitor);

	public interface ExternalStorageVisitor<T> {
		T visitAon();
		T visitDrive();
		T visitAws();
		T visitScaleway();
	}

}


