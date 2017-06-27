//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2017.05.30 a las 03:33:12 PM CEST 
//


package net.aonsolutions.aon.sii;

public enum ClaveRegimenEspecialOTrascendenciaRecibidasType {


    /**
     *	Operación de régimen general.
     */
    _01,

    /**
     *	Operaciones por las que los empresarios satisfacen compensaciones en las adquisiciones a personas acogidas al Régimen especial de la agricultura, ganaderia y pesca.
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
     *	Adquisiciones Intracomunitarias de bienes y prestaciones de servicios
     */
    _09,


    /**
     *	Operaciones de arrendamiento de local de negocio.
     */
    _12,

    /**
     *	Factura correspondiente a una importacion (informada sin asociar a un DUA)
     */
    _13,

    /**
     *	Primer sementre 2017.
     */
    _14,
    ;

	public String getName(){
		return this.toString().substring(1);
	}
    
	public byte value() {
        return (byte) this.ordinal();
    }

}
