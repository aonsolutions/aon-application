package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public enum RegistryStatus implements Serializable {

	ACTIVE("Activo")	{ @Override public <T> T visit(RegistryStatusVisitor<T> visitor) { return visitor.visitActive();} },
	INACTIVE("Inactivo"){ @Override public <T> T visit(RegistryStatusVisitor<T> visitor) { return visitor.visitInactive();} },
	BLOCKED("Bloqueado"){ @Override public <T> T visit(RegistryStatusVisitor<T> visitor) { return visitor.visitBlocked();} };
    
	private String description;

	private RegistryStatus(String description) {
		this.description = description;
	}

	public String getDescription(){
		return this.description;
	}
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
	public static Optional<RegistryStatus> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<RegistryStatus> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= RegistryStatus.values().length) return Optional.empty();
		return Optional.of(RegistryStatus.values()[i]);
	}
	
	public static Optional<RegistryStatus> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s)
				|| AonStringUtils.equalsIgnoreCase(t.getDescription(), s))
			.findFirst();
	}
	
	public static Byte value(RegistryStatus t) {
		return t == null ? null : t.value();
	}
	public static String name(RegistryStatus t) {
		return t == null ? null : t.name();
	}
	
	public abstract <T> T visit(RegistryStatusVisitor<T> visitor);
	public static interface RegistryStatusVisitor<T> {
		T visitActive();
		T visitInactive();
		T visitBlocked();
	}
	
	
}