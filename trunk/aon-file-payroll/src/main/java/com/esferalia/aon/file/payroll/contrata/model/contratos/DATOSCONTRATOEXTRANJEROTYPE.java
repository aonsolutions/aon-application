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
 * Datos del contingente de extranjeros no comunitarios (iniciados a partir del 01-01-2008).
 * 
 * <p>Clase Java para DATOS_CONTRATO_EXTRANJEROTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_CONTRATO_EXTRANJEROTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IND_CARACTER_OFERTA">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[ET\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="AÑO_CONTINGENTE">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{4}"/>
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
@XmlType(name = "DATOS_CONTRATO_EXTRANJEROTYPE", propOrder = {
    "indcaracteroferta",
    "a\u00f1ocontingente"
})
public class DATOSCONTRATOEXTRANJEROTYPE {

    @XmlElement(name = "IND_CARACTER_OFERTA", required = true)
    protected String indcaracteroferta;
    @XmlElement(name = "A\u00d1O_CONTINGENTE", required = true)
    protected String añocontingente;

    /**
     * Obtiene el valor de la propiedad indcaracteroferta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDCARACTEROFERTA() {
        return indcaracteroferta;
    }

    /**
     * Define el valor de la propiedad indcaracteroferta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDCARACTEROFERTA(String value) {
        this.indcaracteroferta = value;
    }

    /**
     * Obtiene el valor de la propiedad añocontingente.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAÑOCONTINGENTE() {
        return añocontingente;
    }

    /**
     * Define el valor de la propiedad añocontingente.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAÑOCONTINGENTE(String value) {
        this.añocontingente = value;
    }

}
