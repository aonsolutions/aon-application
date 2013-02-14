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
 * Datos del trabajador contratado
 * 
 * <p>Clase Java para DATOS_TRABAJADORTYPE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DATOS_TRABAJADORTYPE">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="IDENTIFICADORPFISICA">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="12"/>
 *               &lt;pattern value="[DEUW][0-9XYZ ]+\d{7}[A-Z]"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="NOMBRE_APELLIDOS" type="{}NOMBREAPELLIDOSTYPE"/>
 *         &lt;element name="SEXO">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{1}"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="FECHA_NACIMIENTO" type="{}FECHATYPE"/>
 *         &lt;element name="NACIONALIDAD" type="{}PAISTYPE"/>
 *         &lt;element name="MUNICIPIO_RESIDENCIA" type="{}MUNICIPIOTYPE" minOccurs="0"/>
 *         &lt;element name="PAIS_RESIDENCIA" type="{}PAISTYPE"/>
 *         &lt;element name="NUMERO_SEGURIDAD_SOCIAL" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;pattern value="\d{12}"/>
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
@XmlType(name = "DATOS_TRABAJADORTYPE", propOrder = {
    "identificadorpfisica",
    "nombreapellidos",
    "sexo",
    "fechanacimiento",
    "nacionalidad",
    "municipioresidencia",
    "paisresidencia",
    "numeroseguridadsocial"
})
public class DATOSTRABAJADORTYPE {

    @XmlElement(name = "IDENTIFICADORPFISICA", required = true)
    protected String identificadorpfisica;
    @XmlElement(name = "NOMBRE_APELLIDOS", required = true)
    protected NOMBREAPELLIDOSTYPE nombreapellidos;
    @XmlElement(name = "SEXO", required = true)
    protected String sexo;
    @XmlElement(name = "FECHA_NACIMIENTO", required = true)
    protected String fechanacimiento;
    @XmlElement(name = "NACIONALIDAD", required = true)
    protected String nacionalidad;
    @XmlElement(name = "MUNICIPIO_RESIDENCIA")
    protected String municipioresidencia;
    @XmlElement(name = "PAIS_RESIDENCIA", required = true)
    protected String paisresidencia;
    @XmlElement(name = "NUMERO_SEGURIDAD_SOCIAL")
    protected String numeroseguridadsocial;

    /**
     * Obtiene el valor de la propiedad identificadorpfisica.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDENTIFICADORPFISICA() {
        return identificadorpfisica;
    }

    /**
     * Define el valor de la propiedad identificadorpfisica.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDENTIFICADORPFISICA(String value) {
        this.identificadorpfisica = value;
    }

    /**
     * Obtiene el valor de la propiedad nombreapellidos.
     * 
     * @return
     *     possible object is
     *     {@link NOMBREAPELLIDOSTYPE }
     *     
     */
    public NOMBREAPELLIDOSTYPE getNOMBREAPELLIDOS() {
        return nombreapellidos;
    }

    /**
     * Define el valor de la propiedad nombreapellidos.
     * 
     * @param value
     *     allowed object is
     *     {@link NOMBREAPELLIDOSTYPE }
     *     
     */
    public void setNOMBREAPELLIDOS(NOMBREAPELLIDOSTYPE value) {
        this.nombreapellidos = value;
    }

    /**
     * Obtiene el valor de la propiedad sexo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSEXO() {
        return sexo;
    }

    /**
     * Define el valor de la propiedad sexo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSEXO(String value) {
        this.sexo = value;
    }

    /**
     * Obtiene el valor de la propiedad fechanacimiento.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFECHANACIMIENTO() {
        return fechanacimiento;
    }

    /**
     * Define el valor de la propiedad fechanacimiento.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFECHANACIMIENTO(String value) {
        this.fechanacimiento = value;
    }

    /**
     * Obtiene el valor de la propiedad nacionalidad.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNACIONALIDAD() {
        return nacionalidad;
    }

    /**
     * Define el valor de la propiedad nacionalidad.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNACIONALIDAD(String value) {
        this.nacionalidad = value;
    }

    /**
     * Obtiene el valor de la propiedad municipioresidencia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMUNICIPIORESIDENCIA() {
        return municipioresidencia;
    }

    /**
     * Define el valor de la propiedad municipioresidencia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMUNICIPIORESIDENCIA(String value) {
        this.municipioresidencia = value;
    }

    /**
     * Obtiene el valor de la propiedad paisresidencia.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPAISRESIDENCIA() {
        return paisresidencia;
    }

    /**
     * Define el valor de la propiedad paisresidencia.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPAISRESIDENCIA(String value) {
        this.paisresidencia = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroseguridadsocial.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNUMEROSEGURIDADSOCIAL() {
        return numeroseguridadsocial;
    }

    /**
     * Define el valor de la propiedad numeroseguridadsocial.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNUMEROSEGURIDADSOCIAL(String value) {
        this.numeroseguridadsocial = value;
    }

}
