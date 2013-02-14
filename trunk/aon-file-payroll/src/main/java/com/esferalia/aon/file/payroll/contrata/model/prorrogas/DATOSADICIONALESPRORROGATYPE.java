//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 07:11:31 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.prorrogas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Datos adicionales de la prórroga
 * 
 * <p>Clase Java para DATOS_ADICIONALESPRORROGATYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_ADICIONALESPRORROGATYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="HORAS_FORMACION" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{6}"/>
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
@XmlType(name = "DATOS_ADICIONALESPRORROGATYPE", propOrder = {
    "horasformacion",
    "indduracinferior"
})
public class DATOSADICIONALESPRORROGATYPE {

    @XmlElement(name = "HORAS_FORMACION")
    protected String horasformacion;
    @XmlElement(name = "IND_DURAC_INFERIOR")
    protected String indduracinferior;

    /**
     * Obtiene el valor de la propiedad horasformacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHORASFORMACION() {
        return horasformacion;
    }

    /**
     * Define el valor de la propiedad horasformacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHORASFORMACION(String value) {
        this.horasformacion = value;
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
