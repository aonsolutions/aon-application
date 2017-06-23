//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2017.05.30 a las 03:33:12 PM CEST 
//


package net.aonsolutions.aon.sii;

public enum ClaveRegimenEspecialOTrascendenciaType {


    /**
     *	Operación de régimen general.
     */
    _01,

    /**
     *	Exportacion.
     */
    _02,
    
    /**
     *	Operaciones a las que se aplique el régimen especial de bienes usados, objetos de
	 * 	arte, antigüedades y objetos de colección.
     */
    _03,

    /**
     *	Régimen especial del oro de inversión.
     */
    _04,

    /**
     *	Régimen especial de las agencias de viajes. 
     */
    _05,

    /**
     *	Régimen especial grupo de entidades en IVA (Nivel Avanzado).
     */
    _06,
    
    /**
     *	Régimen especial del criterio de caja
     */
    _07,

    /**
     *	Operaciones sujetas al IPSI / IGIC (Impuesto sobre la Producción, los Servicios y la
	 *	Importación / Impuesto General Indirecto Canario).
     */
    _08,

    /**
     *	Facturación de las prestaciones de servicios de agencias de viaje que actúan como
	 *	mediadoras en nombre y por cuenta ajena (D.A.4ª RD1619/2012).
     */
    _09,

    /**
     *	Cobros por cuenta de terceros de honorarios profesionales o de derechos derivados de
	 *	la propiedad industrial, de autor u otros por cuenta de sus socios, asociados o
	 *	colegiados efectuados por sociedades, asociaciones, colegios profesionales u otras
	 *	entidades que realicen estas funciones de cobro.
     */
    _10,

    /**
     *	Operaciones de arrendamiento de local de negocio sujetas a retención.
     */
    _11,

    /**
     *	Operaciones de arrendamiento de local de negocio no sujetas a retención.
     */
    _12,

    /**
     *	Operaciones de arrendamiento de local de negocio sujetas y no sujetas a retención.
     */
    _13,

    /**
     *	Factura con IVA pendiente de devengo en certificaciones de obra cuyo destinatario sea una Administracion Pública.
     */
    _14,

    /**
     *	Factura con IVA pendiente de devengo en operaciones de tracto sucesivo.
     */
    _15,

    /**
     *	Primer sementre 2017.
     */
    _16;

	public String getName(){
		return this.toString().substring(1);
	}
    
	public byte value() {
        return (byte) this.ordinal();
    }

}
