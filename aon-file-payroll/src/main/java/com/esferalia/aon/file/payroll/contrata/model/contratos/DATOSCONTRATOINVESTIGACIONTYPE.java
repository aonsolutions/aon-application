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
 * Datos de los contratos de investigación.
 * 
 * <p>Clase Java para DATOS_CONTRATOINVESTIGACIONTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_CONTRATOINVESTIGACIONTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IND_EMPLEADOR" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{1}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IND_TRABAJADOR" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{1}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IND_RD63_2006" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IND_CONTRATO_PREDOCTORAL" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
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
@XmlType(name = "DATOS_CONTRATOINVESTIGACIONTYPE", propOrder = {
    "indempleador",
    "indtrabajador",
    "indrd632006",
    "indcontratopredoctoral"
})
public class DATOSCONTRATOINVESTIGACIONTYPE {

    @XmlElement(name = "IND_EMPLEADOR")
    protected String indempleador;
    @XmlElement(name = "IND_TRABAJADOR")
    protected String indtrabajador;
    @XmlElement(name = "IND_RD63_2006")
    protected String indrd632006;
    @XmlElement(name = "IND_CONTRATO_PREDOCTORAL")
    protected String indcontratopredoctoral;

    /**
     * Obtiene el valor de la propiedad indempleador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDEMPLEADOR() {
        return indempleador;
    }

    /**
     * Define el valor de la propiedad indempleador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDEMPLEADOR(String value) {
        this.indempleador = value;
    }

    /**
     * Obtiene el valor de la propiedad indtrabajador.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDTRABAJADOR() {
        return indtrabajador;
    }

    /**
     * Define el valor de la propiedad indtrabajador.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDTRABAJADOR(String value) {
        this.indtrabajador = value;
    }

    /**
     * Obtiene el valor de la propiedad indrd632006.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDRD632006() {
        return indrd632006;
    }

    /**
     * Define el valor de la propiedad indrd632006.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDRD632006(String value) {
        this.indrd632006 = value;
    }

    /**
     * Obtiene el valor de la propiedad indcontratopredoctoral.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDCONTRATOPREDOCTORAL() {
        return indcontratopredoctoral;
    }

    /**
     * Define el valor de la propiedad indcontratopredoctoral.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDCONTRATOPREDOCTORAL(String value) {
        this.indcontratopredoctoral = value;
    }

}
