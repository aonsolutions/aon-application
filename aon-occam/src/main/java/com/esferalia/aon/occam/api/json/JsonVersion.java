package com.esferalia.aon.occam.api.json;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum JsonVersion implements Serializable  {

	 V1 {@Override public <T> T visit(JsonVersionVisitor<T> visitor) {return visitor.visitV1();}}
	,V2 {@Override public <T> T visit(JsonVersionVisitor<T> visitor) {return visitor.visitV2();}}
	;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public abstract <T> T visit(JsonVersionVisitor<T> visitor);
	
	public interface JsonVersionVisitor<T> {
		public T visitV1();		
		public T visitV2();
	}

	public static Optional<JsonVersion> safeValueOf(String v) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), v))
			.findFirst();
	}
	
	
}	
