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
 * Datos de medidas de fomento de la contratación indefinida. A partir del 12/02/2012 queda derogada esta D.A. por lo que no deberá enviarse este dato para los contratos iniciados a partir de esa fecha.
 * 
 * <p>Clase Java para DATOS_MEDIDASFOMENTOTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_MEDIDASFOMENTOTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IND_COSTE_DESPIDO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[12]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="CODIGO_COLECTIVO_DESPIDO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{2}"/>
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
@XmlType(name = "DATOS_MEDIDASFOMENTOTYPE", propOrder = {
    "indcostedespido",
    "codigocolectivodespido"
})
public class DATOSMEDIDASFOMENTOTYPE {

    @XmlElement(name = "IND_COSTE_DESPIDO", required = true)
    protected String indcostedespido;
    @XmlElement(name = "CODIGO_COLECTIVO_DESPIDO")
    protected String codigocolectivodespido;

    /**
     * Obtiene el valor de la propiedad indcostedespido.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDCOSTEDESPIDO() {
        return indcostedespido;
    }

    /**
     * Define el valor de la propiedad indcostedespido.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDCOSTEDESPIDO(String value) {
        this.indcostedespido = value;
    }

    /**
     * Obtiene el valor de la propiedad codigocolectivodespido.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODIGOCOLECTIVODESPIDO() {
        return codigocolectivodespido;
    }

    /**
     * Define el valor de la propiedad codigocolectivodespido.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODIGOCOLECTIVODESPIDO(String value) {
        this.codigocolectivodespido = value;
    }

}
