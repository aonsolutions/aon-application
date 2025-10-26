package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

public enum VATExemptionCause implements Serializable {
	
	/**
	 * Art. 20 - Exenciones interiores (en España)
	 * Ciertas actividades no llevan IVA, como:
	 * 	- Educación
	 * 	- Sanidad
	 * 	- Alquiler de vivienda
	 * 	- Servicios financieros y seguros
	 * 	- Actividades sin ánimo de lucro
	 */
	E1("Exenta por el articulo 20"),
	
    /**
     * 	Art. 21 - Exportaciones
     * 		Vender fuera de la UE está exento de IVA.
     */
    E2("Exenta por el articulo 21"),
    
    /**
     * Art. 22 - Servicios relacionados con exportaciones
     * 	- Transportes internacionales
     * 	- Servicios aduaneros
     * 	- Ayudas a la exportación (embalaje, carga, etc.)
     */
    E3("Exenta por el articulo 22"),
    
    /**
     * 	Art. 23 - Exenciones en zonas francas y depósitos aduaneros
     * 	Operaciones dentro de zonas especiales o depósitos aduaneros están exentas de IVA.
     * 
     * 	Art. 24 - Exenciones en regímenes aduaneros suspensivos
     *	No se aplica IVA en operaciones bajo ciertos regímenes aduaneros 
     *	(tránsito, depósito temporal, etc.).
     */
    E4("Exenta por el articulo 23 y 24"),
    
    /**
     * Art. 25 - Entregas intracomunitarias
     * 	Vender a empresas en otros países de la UE también está exento de IVA, 
     * 	si el comprador tiene NIF-IVA intracomunitario y se prueba el envío.	
     */
    E5("Exenta por el articulo 25"),
    
    /**
     *	- Aplicación de normas internacionales o convenios bilaterales
     *		Ej: ventas a organismos internacionales, embajadas, fuerzas armadas extranjeras.
     *	- Exenciones por normativa comunitaria
     *		Ej: oro de inversión (Art. 140), o ventas dentro del régimen 
     *		especial de agencia de viajes.
     *	- Régimen especial de bienes usados, objetos de arte, antigüedades
     *		Exento el total o parte según el método aplicado.
     *	- Ventas no sujetas que por simplificación se declaran como exentas
     *		Algunas empresas las incluyen como exentas por facilidad contable, aunque 
     *		técnicamente no lo son.
     *	- Casos por resoluciones administrativas o jurisprudencia
     *		Ej. operaciones específicas reconocidas como exentas por Hacienda en 
     *		consultas vinculantes.
     * 
     */
    E6("Exenta por otros")
    ;

    private final String description;

	private VATExemptionCause(String description){
		this.description = description;
	}

	public Byte value(){
		return (byte) ordinal();
	}
	
	public String getDescription() {
		return description;
	}
	
	public static VATExemptionCause safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static VATExemptionCause safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= VATExemptionCause.values().length) return null;
		return VATExemptionCause.values()[i];
	}
	
	public static VATExemptionCause safeValueOf( String i ) {
		if(i == null || "".equals(i)) return null;
		for (VATExemptionCause rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}