package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum MediaType implements Serializable {

	 UNKNOWN( "-----" )
	 	{@Override public <T> T visit(MediaTypeVisitor<T> v) { return v.visitUnknown();}}
	,FIXED_PHONE("Tel\u00E9fono")
		{@Override public <T> T visit(MediaTypeVisitor<T> v) { return v.visitFixedPhone();}}
	,CELLULAR("M\u00F3vil")
		{@Override public <T> T visit(MediaTypeVisitor<T> v) { return v.visitCellular();}}
	,FAX("Fax")
		{@Override public <T> T visit(MediaTypeVisitor<T> v) { return v.visitFax();}}
	,EMAIL("eMail")
		{@Override public <T> T visit(MediaTypeVisitor<T> v) { return v.visitEmail();}}
	,WEB("Web")
		{@Override public <T> T visit(MediaTypeVisitor<T> v) { return v.visitWeb();}}
	;
	
	private String description;
	
	private MediaType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<MediaType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<MediaType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= MediaType.values().length) return Optional.empty();
		return Optional.of(MediaType.values()[i]);
	}
	
	public static Optional<MediaType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s)
				|| AonStringUtils.equalsIgnoreCase(t.getDescription(), s))
			.findFirst();
	}
	
	public abstract <T> T visit( MediaTypeVisitor<T> visitor );
	public static interface MediaTypeVisitor<T> {
		T visitUnknown();
		T visitFixedPhone();
		T visitCellular();
		T visitFax();
		T visitEmail();
		T visitWeb();
	}
	
}