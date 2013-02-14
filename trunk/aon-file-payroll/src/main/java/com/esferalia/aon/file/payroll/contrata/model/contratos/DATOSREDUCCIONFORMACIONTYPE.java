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
 * Datos de reducción de cuotas para los contratos de formación. Opcional para los contratos de formación iniciados a partir del 31/08/2011.
 * 
 * <p>Clase Java para DATOS_REDUCCION_FORMACIONTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_REDUCCION_FORMACIONTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CODIGO_COLECTIVO_REDUCCION_FORMACION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{2}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="PORCENTAJE_REDUCCION_FORMACION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="2"/>
 *               &lt;maxLength value="3"/>
 *               &lt;pattern value="([0-9])+"/>
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
@XmlType(name = "DATOS_REDUCCION_FORMACIONTYPE", propOrder = {
    "codigocolectivoreduccionformacion",
    "porcentajereduccionformacion"
})
public class DATOSREDUCCIONFORMACIONTYPE {

    @XmlElement(name = "CODIGO_COLECTIVO_REDUCCION_FORMACION", required = true)
    protected String codigocolectivoreduccionformacion;
    @XmlElement(name = "PORCENTAJE_REDUCCION_FORMACION", required = true)
    protected String porcentajereduccionformacion;

    /**
     * Obtiene el valor de la propiedad codigocolectivoreduccionformacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODIGOCOLECTIVOREDUCCIONFORMACION() {
        return codigocolectivoreduccionformacion;
    }

    /**
     * Define el valor de la propiedad codigocolectivoreduccionformacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODIGOCOLECTIVOREDUCCIONFORMACION(String value) {
        this.codigocolectivoreduccionformacion = value;
    }

    /**
     * Obtiene el valor de la propiedad porcentajereduccionformacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPORCENTAJEREDUCCIONFORMACION() {
        return porcentajereduccionformacion;
    }

    /**
     * Define el valor de la propiedad porcentajereduccionformacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPORCENTAJEREDUCCIONFORMACION(String value) {
        this.porcentajereduccionformacion = value;
    }

}
