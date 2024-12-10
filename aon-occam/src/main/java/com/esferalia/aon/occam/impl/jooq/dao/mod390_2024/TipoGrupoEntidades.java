//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2024.11.25 a las 11:44:35 AM CET 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod390_2024;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_GrupoEntidades complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_GrupoEntidades"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="GrupoEntidades"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="NumGrupo" type="{}tipo_NumGrupo"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="Dominante"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="Dependiente"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *         &lt;/choice&gt;
 *         &lt;choice&gt;
 *           &lt;element name="Art.6.5_SI"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="Art.6.5_NO"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *         &lt;/choice&gt;
 *         &lt;element name="NIFEntidadDominante" type="{}tipo_Nif" minOccurs="0"/&gt;
 *         &lt;choice&gt;
 *           &lt;element name="UltAutoliquid_SI"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *           &lt;element name="UltAutoliquid_NO"&gt;
 *             &lt;complexType&gt;
 *               &lt;complexContent&gt;
 *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;/restriction&gt;
 *               &lt;/complexContent&gt;
 *             &lt;/complexType&gt;
 *           &lt;/element&gt;
 *         &lt;/choice&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_GrupoEntidades", propOrder = {
    "grupoEntidades",
    "numGrupo",
    "dominante",
    "dependiente",
    "art65SI",
    "art65NO",
    "nifEntidadDominante",
    "ultAutoliquidSI",
    "ultAutoliquidNO"
})
public class TipoGrupoEntidades {

    @XmlElement(name = "GrupoEntidades", required = true)
    protected TipoGrupoEntidades.GrupoEntidades grupoEntidades;
    @XmlElement(name = "NumGrupo", required = true)
    protected String numGrupo;
    @XmlElement(name = "Dominante")
    protected TipoGrupoEntidades.Dominante dominante;
    @XmlElement(name = "Dependiente")
    protected TipoGrupoEntidades.Dependiente dependiente;
    @XmlElement(name = "Art.6.5_SI")
    protected TipoGrupoEntidades.Art65SI art65SI;
    @XmlElement(name = "Art.6.5_NO")
    protected TipoGrupoEntidades.Art65NO art65NO;
    @XmlElement(name = "NIFEntidadDominante")
    protected String nifEntidadDominante;
    @XmlElement(name = "UltAutoliquid_SI")
    protected TipoGrupoEntidades.UltAutoliquidSI ultAutoliquidSI;
    @XmlElement(name = "UltAutoliquid_NO")
    protected TipoGrupoEntidades.UltAutoliquidNO ultAutoliquidNO;

    /**
     * Obtiene el valor de la propiedad grupoEntidades.
     * 
     * @return
     *     possible object is
     *     {@link TipoGrupoEntidades.GrupoEntidades }
     *     
     */
    public TipoGrupoEntidades.GrupoEntidades getGrupoEntidades() {
        return grupoEntidades;
    }

    /**
     * Define el valor de la propiedad grupoEntidades.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoGrupoEntidades.GrupoEntidades }
     *     
     */
    public void setGrupoEntidades(TipoGrupoEntidades.GrupoEntidades value) {
        this.grupoEntidades = value;
    }

    /**
     * Obtiene el valor de la propiedad numGrupo.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumGrupo() {
        return numGrupo;
    }

    /**
     * Define el valor de la propiedad numGrupo.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumGrupo(String value) {
        this.numGrupo = value;
    }

    /**
     * Obtiene el valor de la propiedad dominante.
     * 
     * @return
     *     possible object is
     *     {@link TipoGrupoEntidades.Dominante }
     *     
     */
    public TipoGrupoEntidades.Dominante getDominante() {
        return dominante;
    }

    /**
     * Define el valor de la propiedad dominante.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoGrupoEntidades.Dominante }
     *     
     */
    public void setDominante(TipoGrupoEntidades.Dominante value) {
        this.dominante = value;
    }

    /**
     * Obtiene el valor de la propiedad dependiente.
     * 
     * @return
     *     possible object is
     *     {@link TipoGrupoEntidades.Dependiente }
     *     
     */
    public TipoGrupoEntidades.Dependiente getDependiente() {
        return dependiente;
    }

    /**
     * Define el valor de la propiedad dependiente.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoGrupoEntidades.Dependiente }
     *     
     */
    public void setDependiente(TipoGrupoEntidades.Dependiente value) {
        this.dependiente = value;
    }

    /**
     * Obtiene el valor de la propiedad art65SI.
     * 
     * @return
     *     possible object is
     *     {@link TipoGrupoEntidades.Art65SI }
     *     
     */
    public TipoGrupoEntidades.Art65SI getArt65SI() {
        return art65SI;
    }

    /**
     * Define el valor de la propiedad art65SI.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoGrupoEntidades.Art65SI }
     *     
     */
    public void setArt65SI(TipoGrupoEntidades.Art65SI value) {
        this.art65SI = value;
    }

    /**
     * Obtiene el valor de la propiedad art65NO.
     * 
     * @return
     *     possible object is
     *     {@link TipoGrupoEntidades.Art65NO }
     *     
     */
    public TipoGrupoEntidades.Art65NO getArt65NO() {
        return art65NO;
    }

    /**
     * Define el valor de la propiedad art65NO.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoGrupoEntidades.Art65NO }
     *     
     */
    public void setArt65NO(TipoGrupoEntidades.Art65NO value) {
        this.art65NO = value;
    }

    /**
     * Obtiene el valor de la propiedad nifEntidadDominante.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNIFEntidadDominante() {
        return nifEntidadDominante;
    }

    /**
     * Define el valor de la propiedad nifEntidadDominante.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNIFEntidadDominante(String value) {
        this.nifEntidadDominante = value;
    }

    /**
     * Obtiene el valor de la propiedad ultAutoliquidSI.
     * 
     * @return
     *     possible object is
     *     {@link TipoGrupoEntidades.UltAutoliquidSI }
     *     
     */
    public TipoGrupoEntidades.UltAutoliquidSI getUltAutoliquidSI() {
        return ultAutoliquidSI;
    }

    /**
     * Define el valor de la propiedad ultAutoliquidSI.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoGrupoEntidades.UltAutoliquidSI }
     *     
     */
    public void setUltAutoliquidSI(TipoGrupoEntidades.UltAutoliquidSI value) {
        this.ultAutoliquidSI = value;
    }

    /**
     * Obtiene el valor de la propiedad ultAutoliquidNO.
     * 
     * @return
     *     possible object is
     *     {@link TipoGrupoEntidades.UltAutoliquidNO }
     *     
     */
    public TipoGrupoEntidades.UltAutoliquidNO getUltAutoliquidNO() {
        return ultAutoliquidNO;
    }

    /**
     * Define el valor de la propiedad ultAutoliquidNO.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoGrupoEntidades.UltAutoliquidNO }
     *     
     */
    public void setUltAutoliquidNO(TipoGrupoEntidades.UltAutoliquidNO value) {
        this.ultAutoliquidNO = value;
    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Art65NO {


    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Art65SI {


    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Dependiente {


    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Dominante {


    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class GrupoEntidades {


    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class UltAutoliquidNO {


    }


    /**
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class UltAutoliquidSI {


    }

}
