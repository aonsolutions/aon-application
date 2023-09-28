//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0.1 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.09.28 a las 10:07:44 AM UTC 
//


package es.gob.facturae.formato.versiones.facturaev3_2_2;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para InvoiceClassType.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * <p>
 * <pre>
 * &lt;simpleType name="InvoiceClassType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OO"/&gt;
 *     &lt;enumeration value="OR"/&gt;
 *     &lt;enumeration value="OC"/&gt;
 *     &lt;enumeration value="CO"/&gt;
 *     &lt;enumeration value="CR"/&gt;
 *     &lt;enumeration value="CC"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "InvoiceClassType")
@XmlEnum
public enum InvoiceClassType {


    /**
     * Original.
     * 
     */
    OO,

    /**
     * Original rectificativa
     * 
     */
    OR,

    /**
     * Original recapitulativa.
     * 
     */
    OC,

    /**
     * Copia original.
     * 
     */
    CO,

    /**
     * Copia rectificativa.
     * 
     */
    CR,

    /**
     * Copia recapitulativa.
     * 
     */
    CC;

    public String value() {
        return name();
    }

    public static InvoiceClassType fromValue(String v) {
        return valueOf(v);
    }

}
