package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum TagType implements Serializable{

	RATTACH,
    PRODUCT,    
    PRIORITY,
    MARKETPLACE,
    OFFICE_NOTICE,
    OFFICE_PRIORITY,
    OFFICE_STATUS,
    OFFICE_TYPE,
	PACKING,
	TASK_TYPE,
	TASK_PRIORITY,
	TASK_LABEL,
	TASK_DOCUMENT,
	CERTIFICATE,
	NOTE,
	MARKETING
	;

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<TagType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<TagType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= TagType.values().length) return Optional.empty();
		return Optional.of(TagType.values()[i]);
	}
	
	public static Optional<TagType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}

}