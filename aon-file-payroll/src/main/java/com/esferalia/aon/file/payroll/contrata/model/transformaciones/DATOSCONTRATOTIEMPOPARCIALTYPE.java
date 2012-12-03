//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.5-2 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: PM.11.26 a las 06:55:00 PM CET 
//


package com.esferalia.aon.file.payroll.contrata.model.transformaciones;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Datos de las transformaciones a tiempo parcial. Distribución de la jornada de trabajo.
 * 
 * <p>Clase Java para DATOS_CONTRATOTIEMPOPARCIALTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_CONTRATOTIEMPOPARCIALTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="TIPO_JORNADA">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="HORAS_JORNADA" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{6}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="HORAS_CONVENIO" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{6}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="ACTIVIDAD_SIN_FECHACIERTA" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[S\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FIJODISCONTINUO_PERIODICO" minOccurs="0">
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
@XmlType(name = "DATOS_CONTRATOTIEMPOPARCIALTYPE", propOrder = {
    "tipojornada",
    "horasjornada",
    "horasconvenio",
    "actividadsinfechacierta",
    "fijodiscontinuoperiodico"
})
public class DATOSCONTRATOTIEMPOPARCIALTYPE {

    @XmlElement(name = "TIPO_JORNADA", required = true)
    protected String tipojornada;
    @XmlElement(name = "HORAS_JORNADA")
    protected String horasjornada;
    @XmlElement(name = "HORAS_CONVENIO")
    protected String horasconvenio;
    @XmlElement(name = "ACTIVIDAD_SIN_FECHACIERTA")
    protected String actividadsinfechacierta;
    @XmlElement(name = "FIJODISCONTINUO_PERIODICO")
    protected String fijodiscontinuoperiodico;

    /**
     * Obtiene el valor de la propiedad tipojornada.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTIPOJORNADA() {
        return tipojornada;
    }

    /**
     * Define el valor de la propiedad tipojornada.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTIPOJORNADA(String value) {
        this.tipojornada = value;
    }

    /**
     * Obtiene el valor de la propiedad horasjornada.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHORASJORNADA() {
        return horasjornada;
    }

    /**
     * Define el valor de la propiedad horasjornada.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHORASJORNADA(String value) {
        this.horasjornada = value;
    }

    /**
     * Obtiene el valor de la propiedad horasconvenio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHORASCONVENIO() {
        return horasconvenio;
    }

    /**
     * Define el valor de la propiedad horasconvenio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHORASCONVENIO(String value) {
        this.horasconvenio = value;
    }

    /**
     * Obtiene el valor de la propiedad actividadsinfechacierta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getACTIVIDADSINFECHACIERTA() {
        return actividadsinfechacierta;
    }

    /**
     * Define el valor de la propiedad actividadsinfechacierta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setACTIVIDADSINFECHACIERTA(String value) {
        this.actividadsinfechacierta = value;
    }

    /**
     * Obtiene el valor de la propiedad fijodiscontinuoperiodico.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFIJODISCONTINUOPERIODICO() {
        return fijodiscontinuoperiodico;
    }

    /**
     * Define el valor de la propiedad fijodiscontinuoperiodico.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFIJODISCONTINUOPERIODICO(String value) {
        this.fijodiscontinuoperiodico = value;
    }

}
