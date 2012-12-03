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
 * Datos propios de la prórroga
 * 
 * <p>Clase Java para DATOS_GENERALESPRORROGATYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_GENERALESPRORROGATYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="FECHA_INICIO" type="{}FECHATYPE"/>
 *         &lt;element name="FECHA_FIN" type="{}FECHATYPE"/>
 *         &lt;element name="INDICADOR_CONV_COL" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SN\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="INDICADOR_DISCONTINUIDAD" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[I\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IND_EMPRESA_AAPP_UNIVERSIDAD" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="[SN\s]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="IND_PERIODO_AUTORIZA_DURACION" minOccurs="0">
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
@XmlType(name = "DATOS_GENERALESPRORROGATYPE", propOrder = {
    "fechainicio",
    "fechafin",
    "indicadorconvcol",
    "indicadordiscontinuidad",
    "indempresaaappuniversidad",
    "indperiodoautorizaduracion"
})
public class DATOSGENERALESPRORROGATYPE {

    @XmlElement(name = "FECHA_INICIO", required = true)
    protected String fechainicio;
    @XmlElement(name = "FECHA_FIN", required = true)
    protected String fechafin;
    @XmlElement(name = "INDICADOR_CONV_COL")
    protected String indicadorconvcol;
    @XmlElement(name = "INDICADOR_DISCONTINUIDAD")
    protected String indicadordiscontinuidad;
    @XmlElement(name = "IND_EMPRESA_AAPP_UNIVERSIDAD")
    protected String indempresaaappuniversidad;
    @XmlElement(name = "IND_PERIODO_AUTORIZA_DURACION")
    protected String indperiodoautorizaduracion;

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
     * Obtiene el valor de la propiedad fechafin.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFECHAFIN() {
        return fechafin;
    }

    /**
     * Define el valor de la propiedad fechafin.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFECHAFIN(String value) {
        this.fechafin = value;
    }

    /**
     * Obtiene el valor de la propiedad indicadorconvcol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDICADORCONVCOL() {
        return indicadorconvcol;
    }

    /**
     * Define el valor de la propiedad indicadorconvcol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDICADORCONVCOL(String value) {
        this.indicadorconvcol = value;
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
     * Obtiene el valor de la propiedad indempresaaappuniversidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDEMPRESAAAPPUNIVERSIDAD() {
        return indempresaaappuniversidad;
    }

    /**
     * Define el valor de la propiedad indempresaaappuniversidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDEMPRESAAAPPUNIVERSIDAD(String value) {
        this.indempresaaappuniversidad = value;
    }

    /**
     * Obtiene el valor de la propiedad indperiodoautorizaduracion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getINDPERIODOAUTORIZADURACION() {
        return indperiodoautorizaduracion;
    }

    /**
     * Define el valor de la propiedad indperiodoautorizaduracion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setINDPERIODOAUTORIZADURACION(String value) {
        this.indperiodoautorizaduracion = value;
    }

}
