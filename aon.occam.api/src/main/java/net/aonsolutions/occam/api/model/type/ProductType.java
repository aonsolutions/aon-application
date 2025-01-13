package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum ProductType implements Serializable {
	
	LABOUR("Mano de Obra"),
    SERVICE("Servicio"),
	COMMERCIAL_PRODUCT("Producto Comercial"),
	EXTERNAL_WORK("Trabajo Externo"),
	EXPENSE("Gasto"),
	PREPAYMENT("Suplidos"),
	INCREASE("Recargo"),
	AUXILIARY("Auxiliar");

	private String name;
	private ProductType(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
	public static Optional<ProductType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<ProductType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= ProductType.values().length) return Optional.empty();
		return Optional.of(ProductType.values()[i]);
	}
	
	public static Optional<ProductType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s)
					|| AonStringUtils.equalsIgnoreCase(t.getName(), s))
			.findFirst();
	}
	
	public boolean isAuxiliary() {
		return this == AUXILIARY;
	}
}
