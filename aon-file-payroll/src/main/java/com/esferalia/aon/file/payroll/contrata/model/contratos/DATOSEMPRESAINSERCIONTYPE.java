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
 * Datos de contratos de empresas de inserción. 
 * 
 * <p>Clase Java para DATOS_EMPRESA_INSERCIONTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_EMPRESA_INSERCIONTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IND_EMPRESA_INSERCION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SN\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IND_DURAC_INFERIOR" minOccurs="0">
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
@XmlType(name = "DATOS_EMPRESA_INSERCIONTYPE", propOrder = {
    "indempresainsercion",
    "indduracinferior"
})
public class DATOSEMPRESAINSERCIONTYPE {

    @XmlElement(name = "IND_EMPRESA_INSERCION", required = true)
    protected String indempresainsercion;
    @XmlElement(name = "IND_DURAC_INFERIOR")
    protected String indduracinferior;

    /**
     * Obtiene el valor de la propiedad indempresainsercion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDEMPRESAINSERCION() {
        return indempresainsercion;
    }

    /**
     * Define el valor de la propiedad indempresainsercion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDEMPRESAINSERCION(String value) {
        this.indempresainsercion = value;
    }

    /**
     * Obtiene el valor de la propiedad indduracinferior.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDDURACINFERIOR() {
        return indduracinferior;
    }

    /**
     * Define el valor de la propiedad indduracinferior.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDDURACINFERIOR(String value) {
        this.indduracinferior = value;
    }

}
