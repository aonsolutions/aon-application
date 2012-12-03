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
 * Datos de la comunicación de la copia básica del contrato.
 * 
 * <p>Clase Java para DATOS_COMUNICA_COPIA_BASICATYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_COMUNICA_COPIA_BASICATYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TIPO_FIRMA">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{1}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="TEXTO_COPIABASICA" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="750"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="DOMIC_CENTRO_TRABAJO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="150"/>
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
@XmlType(name = "DATOS_COMUNICA_COPIA_BASICATYPE", propOrder = {
    "tipofirma",
    "textocopiabasica",
    "domiccentrotrabajo"
})
public class DATOSCOMUNICACOPIABASICATYPE {

    @XmlElement(name = "TIPO_FIRMA", required = true)
    protected String tipofirma;
    @XmlElement(name = "TEXTO_COPIABASICA")
    protected String textocopiabasica;
    @XmlElement(name = "DOMIC_CENTRO_TRABAJO")
    protected String domiccentrotrabajo;

    /**
     * Obtiene el valor de la propiedad tipofirma.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTIPOFIRMA() {
        return tipofirma;
    }

    /**
     * Define el valor de la propiedad tipofirma.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTIPOFIRMA(String value) {
        this.tipofirma = value;
    }

    /**
     * Obtiene el valor de la propiedad textocopiabasica.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTEXTOCOPIABASICA() {
        return textocopiabasica;
    }

    /**
     * Define el valor de la propiedad textocopiabasica.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTEXTOCOPIABASICA(String value) {
        this.textocopiabasica = value;
    }

    /**
     * Obtiene el valor de la propiedad domiccentrotrabajo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDOMICCENTROTRABAJO() {
        return domiccentrotrabajo;
    }

    /**
     * Define el valor de la propiedad domiccentrotrabajo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDOMICCENTROTRABAJO(String value) {
        this.domiccentrotrabajo = value;
    }

}
