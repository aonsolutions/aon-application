//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 06:54:59 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.contratos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Datos de los contratos de interinidad.
 * 
 * <p>Clase Java para DATOS_CONTRATOINTERINIDADTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_CONTRATOINTERINIDADTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CAUSA_INTERINIDAD">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DATOS_CONTRATOINTERINIDADTYPE", propOrder = {
    "causainterinidad"
})
public class DATOSCONTRATOINTERINIDADTYPE {

    @XmlElement(name = "CAUSA_INTERINIDAD", required = true)
    protected String causainterinidad;

    /**
     * Obtiene el valor de la propiedad causainterinidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCAUSAINTERINIDAD() {
        return causainterinidad;
    }

    /**
     * Define el valor de la propiedad causainterinidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCAUSAINTERINIDAD(String value) {
        this.causainterinidad = value;
    }

}
