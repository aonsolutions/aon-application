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
 * Datos de evaluación para la obligatoriedad de la copia básica
 * 
 * <p>Clase Java para DATOS_COPIABASICATYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_COPIABASICATYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IND_CONTRATO_ALTA_DIRECCION" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SN\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IND_CONTRATO_ESCRITO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SN\s]"/>
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
@XmlType(name = "DATOS_COPIABASICATYPE", propOrder = {
    "indcontratoaltadireccion",
    "indcontratoescrito"
})
public class DATOSCOPIABASICATYPE {

    @XmlElement(name = "IND_CONTRATO_ALTA_DIRECCION")
    protected String indcontratoaltadireccion;
    @XmlElement(name = "IND_CONTRATO_ESCRITO", required = true)
    protected String indcontratoescrito;

    /**
     * Obtiene el valor de la propiedad indcontratoaltadireccion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDCONTRATOALTADIRECCION() {
        return indcontratoaltadireccion;
    }

    /**
     * Define el valor de la propiedad indcontratoaltadireccion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDCONTRATOALTADIRECCION(String value) {
        this.indcontratoaltadireccion = value;
    }

    /**
     * Obtiene el valor de la propiedad indcontratoescrito.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDCONTRATOESCRITO() {
        return indcontratoescrito;
    }

    /**
     * Define el valor de la propiedad indcontratoescrito.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDCONTRATOESCRITO(String value) {
        this.indcontratoescrito = value;
    }

}
