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
 * Datos generales de la transformación
 * 
 * <p>Clase Java para DATOS_GENERALES_TRANSFORMACIONTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_GENERALES_TRANSFORMACIONTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FECHA_INICIO" type="{}FECHATYPE"/>
 *         &lt;element name="INDICADOR_DISCONTINUIDAD" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[IP\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FECHA_TERMINO_REAL" type="{}FECHATYPE" minOccurs="0"/>
 *         &lt;element name="CODIGO_OCUPACION">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;length value="8"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NACIONALIDAD_CT" type="{}PAISTYPE"/>
 *         &lt;element name="MUNICIPIO_CT" type="{}MUNICIPIOTYPE" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DATOS_GENERALES_TRANSFORMACIONTYPE", propOrder = {
    "fechainicio",
    "indicadordiscontinuidad",
    "fechaterminoreal",
    "codigoocupacion",
    "nacionalidadct",
    "municipioct"
})
public class DATOSGENERALESTRANSFORMACIONTYPE {

    @XmlElement(name = "FECHA_INICIO", required = true)
    protected String fechainicio;
    @XmlElement(name = "INDICADOR_DISCONTINUIDAD")
    protected String indicadordiscontinuidad;
    @XmlElement(name = "FECHA_TERMINO_REAL")
    protected String fechaterminoreal;
    @XmlElement(name = "CODIGO_OCUPACION", required = true)
    protected String codigoocupacion;
    @XmlElement(name = "NACIONALIDAD_CT", required = true)
    protected String nacionalidadct;
    @XmlElement(name = "MUNICIPIO_CT")
    protected String municipioct;

    /**
     * Obtiene el valor de la propiedad fechainicio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFECHAINICIO() {
        return fechainicio;
    }

    /**
     * Define el valor de la propiedad fechainicio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFECHAINICIO(String value) {
        this.fechainicio = value;
    }

    /**
     * Obtiene el valor de la propiedad indicadordiscontinuidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDICADORDISCONTINUIDAD() {
        return indicadordiscontinuidad;
    }

    /**
     * Define el valor de la propiedad indicadordiscontinuidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDICADORDISCONTINUIDAD(String value) {
        this.indicadordiscontinuidad = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaterminoreal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFECHATERMINOREAL() {
        return fechaterminoreal;
    }

    /**
     * Define el valor de la propiedad fechaterminoreal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFECHATERMINOREAL(String value) {
        this.fechaterminoreal = value;
    }

    /**
     * Obtiene el valor de la propiedad codigoocupacion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCODIGOOCUPACION() {
        return codigoocupacion;
    }

    /**
     * Define el valor de la propiedad codigoocupacion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCODIGOOCUPACION(String value) {
        this.codigoocupacion = value;
    }

    /**
     * Obtiene el valor de la propiedad nacionalidadct.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNACIONALIDADCT() {
        return nacionalidadct;
    }

    /**
     * Define el valor de la propiedad nacionalidadct.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNACIONALIDADCT(String value) {
        this.nacionalidadct = value;
    }

    /**
     * Obtiene el valor de la propiedad municipioct.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMUNICIPIOCT() {
        return municipioct;
    }

    /**
     * Define el valor de la propiedad municipioct.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMUNICIPIOCT(String value) {
        this.municipioct = value;
    }

}
