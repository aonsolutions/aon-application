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
 * <p>Clase Java para tipo_Domicilio complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_Domicilio"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SG" type="{}tipo_SG" minOccurs="0"/&gt;
 *         &lt;element name="ViaPublica" type="{}tipo_NombreViaPublica" minOccurs="0"/&gt;
 *         &lt;element name="Num" type="{}tipo_Numero" minOccurs="0"/&gt;
 *         &lt;element name="Esc" type="{}tipo_Escalera" minOccurs="0"/&gt;
 *         &lt;element name="Piso" type="{}tipo_Piso" minOccurs="0"/&gt;
 *         &lt;element name="Puerta" type="{}tipo_Puerta" minOccurs="0"/&gt;
 *         &lt;element name="Telefono" type="{}tipo_Telefono" minOccurs="0"/&gt;
 *         &lt;element name="CPostal" type="{}tipo_CodigoPostal" minOccurs="0"/&gt;
 *         &lt;element name="Municipio" type="{}tipo_Municipio" minOccurs="0"/&gt;
 *         &lt;element name="CodProv" type="{}tipo_CodigoProvincia" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_Domicilio", propOrder = {
    "sg",
    "viaPublica",
    "num",
    "esc",
    "piso",
    "puerta",
    "telefono",
    "cPostal",
    "municipio",
    "codProv"
})
public class TipoDomicilio {

    @XmlElement(name = "SG")
    protected String sg;
    @XmlElement(name = "ViaPublica")
    protected String viaPublica;
    @XmlElement(name = "Num")
    protected String num;
    @XmlElement(name = "Esc")
    protected String esc;
    @XmlElement(name = "Piso")
    protected String piso;
    @XmlElement(name = "Puerta")
    protected String puerta;
    @XmlElement(name = "Telefono")
    protected String telefono;
    @XmlElement(name = "CPostal")
    protected String cPostal;
    @XmlElement(name = "Municipio")
    protected String municipio;
    @XmlElement(name = "CodProv")
    protected String codProv;

    /**
     * Obtiene el valor de la propiedad sg.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSG() {
        return sg;
    }

    /**
     * Define el valor de la propiedad sg.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSG(String value) {
        this.sg = value;
    }

    /**
     * Obtiene el valor de la propiedad viaPublica.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getViaPublica() {
        return viaPublica;
    }

    /**
     * Define el valor de la propiedad viaPublica.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setViaPublica(String value) {
        this.viaPublica = value;
    }

    /**
     * Obtiene el valor de la propiedad num.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNum() {
        return num;
    }

    /**
     * Define el valor de la propiedad num.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNum(String value) {
        this.num = value;
    }

    /**
     * Obtiene el valor de la propiedad esc.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsc() {
        return esc;
    }

    /**
     * Define el valor de la propiedad esc.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsc(String value) {
        this.esc = value;
    }

    /**
     * Obtiene el valor de la propiedad piso.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPiso() {
        return piso;
    }

    /**
     * Define el valor de la propiedad piso.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPiso(String value) {
        this.piso = value;
    }

    /**
     * Obtiene el valor de la propiedad puerta.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPuerta() {
        return puerta;
    }

    /**
     * Define el valor de la propiedad puerta.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPuerta(String value) {
        this.puerta = value;
    }

    /**
     * Obtiene el valor de la propiedad telefono.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Define el valor de la propiedad telefono.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTelefono(String value) {
        this.telefono = value;
    }

    /**
     * Obtiene el valor de la propiedad cPostal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCPostal() {
        return cPostal;
    }

    /**
     * Define el valor de la propiedad cPostal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCPostal(String value) {
        this.cPostal = value;
    }

    /**
     * Obtiene el valor de la propiedad municipio.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMunicipio() {
        return municipio;
    }

    /**
     * Define el valor de la propiedad municipio.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMunicipio(String value) {
        this.municipio = value;
    }

    /**
     * Obtiene el valor de la propiedad codProv.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCodProv() {
        return codProv;
    }

    /**
     * Define el valor de la propiedad codProv.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCodProv(String value) {
        this.codProv = value;
    }

}
