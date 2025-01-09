//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.12.05 a las 04:40:27 PM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod390_2024;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_Doc complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_Doc"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CodModelo" type="{}tipo_CodModelo"/&gt;
 *         &lt;element name="Ejercicio" type="{http://www.w3.org/2001/XMLSchema}int"/&gt;
 *         &lt;element name="VER" type="{}tipo_IdDoc20" minOccurs="0"/&gt;
 *         &lt;element name="SO" type="{}tipo_IdDoc40" minOccurs="0"/&gt;
 *         &lt;element name="Justif" type="{}tipo_Justificante" minOccurs="0"/&gt;
 *         &lt;element name="RefAEAT" type="{}tipo_IdDoc20Ref" minOccurs="0"/&gt;
 *         &lt;element name="IdentClienteEEDD" type="{}tipo_IdDoc20" minOccurs="0"/&gt;
 *         &lt;element name="FechaHora" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *         &lt;element name="NumExp" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *         &lt;element name="CodElec" type="{http://www.w3.org/2001/XMLSchema}anyType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_Doc", propOrder = {
    "codModelo",
    "ejercicio",
    "ver",
    "so",
    "justif",
    "refAEAT",
    "identClienteEEDD",
    "fechaHora",
    "numExp",
    "codElec"
})
public class TipoDoc {

    @XmlElement(name = "CodModelo", required = true)
    protected String codModelo;
    @XmlElement(name = "Ejercicio")
    protected int ejercicio;
    @XmlElement(name = "VER")
    protected String ver;
    @XmlElement(name = "SO")
    protected String so;
    @XmlElement(name = "Justif")
    protected String justif;
    @XmlElement(name = "RefAEAT")
    protected String refAEAT;
    @XmlElement(name = "IdentClienteEEDD")
    protected String identClienteEEDD;
    @XmlElement(name = "FechaHora")
    protected Object fechaHora;
    @XmlElement(name = "NumExp")
    protected Object numExp;
    @XmlElement(name = "CodElec")
    protected Object codElec;

    /**
     * Obtiene el valor de la propiedad codModelo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodModelo() {
        return codModelo;
    }

    /**
     * Define el valor de la propiedad codModelo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodModelo(String value) {
        this.codModelo = value;
    }

    /**
     * Obtiene el valor de la propiedad ejercicio.
     * 
     */
    public int getEjercicio() {
        return ejercicio;
    }

    /**
     * Define el valor de la propiedad ejercicio.
     * 
     */
    public void setEjercicio(int value) {
        this.ejercicio = value;
    }

    /**
     * Obtiene el valor de la propiedad ver.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVER() {
        return ver;
    }

    /**
     * Define el valor de la propiedad ver.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVER(String value) {
        this.ver = value;
    }

    /**
     * Obtiene el valor de la propiedad so.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSO() {
        return so;
    }

    /**
     * Define el valor de la propiedad so.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSO(String value) {
        this.so = value;
    }

    /**
     * Obtiene el valor de la propiedad justif.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getJustif() {
        return justif;
    }

    /**
     * Define el valor de la propiedad justif.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setJustif(String value) {
        this.justif = value;
    }

    /**
     * Obtiene el valor de la propiedad refAEAT.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefAEAT() {
        return refAEAT;
    }

    /**
     * Define el valor de la propiedad refAEAT.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefAEAT(String value) {
        this.refAEAT = value;
    }

    /**
     * Obtiene el valor de la propiedad identClienteEEDD.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIdentClienteEEDD() {
        return identClienteEEDD;
    }

    /**
     * Define el valor de la propiedad identClienteEEDD.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIdentClienteEEDD(String value) {
        this.identClienteEEDD = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaHora.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getFechaHora() {
        return fechaHora;
    }

    /**
     * Define el valor de la propiedad fechaHora.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setFechaHora(Object value) {
        this.fechaHora = value;
    }

    /**
     * Obtiene el valor de la propiedad numExp.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getNumExp() {
        return numExp;
    }

    /**
     * Define el valor de la propiedad numExp.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setNumExp(Object value) {
        this.numExp = value;
    }

    /**
     * Obtiene el valor de la propiedad codElec.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getCodElec() {
        return codElec;
    }

    /**
     * Define el valor de la propiedad codElec.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setCodElec(Object value) {
        this.codElec = value;
    }

}
