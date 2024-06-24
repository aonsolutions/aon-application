//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
//  
//


package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2023.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="Normal" type="{}tipo_Normal"/>
 *         &lt;element name="BancoEspana" type="{}tipo_BancoEspana"/>
 *         &lt;element name="Seguros" type="{}tipo_Seguros"/>
 *         &lt;element name="SociedadGarantia" type="{}tipo_SociedadGarantia"/>
 *         &lt;element name="InstitucionesInversionColectiva" type="{}tipo_InstitucionesInversionColectivo"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "normal",
    "bancoEspana",
    "seguros",
    "sociedadGarantia",
    "institucionesInversionColectiva"
})
@XmlRootElement(name = "MOD2002023")
public class MOD2002023 {

    @XmlElement(name = "Normal")
    protected TipoNormal normal;
    @XmlElement(name = "BancoEspana")
    protected TipoBancoEspana bancoEspana;
    @XmlElement(name = "Seguros")
    protected TipoSeguros seguros;
    @XmlElement(name = "SociedadGarantia")
    protected TipoSociedadGarantia sociedadGarantia;
    @XmlElement(name = "InstitucionesInversionColectiva")
    protected TipoInstitucionesInversionColectivo institucionesInversionColectiva;

    /**
     * Obtiene el valor de la propiedad normal.
     * 
     * @return
     *     possible object is
     *     {@link TipoNormal }
     *     
     */
    public TipoNormal getNormal() {
        return normal;
    }

    /**
     * Define el valor de la propiedad normal.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoNormal }
     *     
     */
    public void setNormal(TipoNormal value) {
        this.normal = value;
    }

    /**
     * Obtiene el valor de la propiedad bancoEspana.
     * 
     * @return
     *     possible object is
     *     {@link TipoBancoEspana }
     *     
     */
    public TipoBancoEspana getBancoEspana() {
        return bancoEspana;
    }

    /**
     * Define el valor de la propiedad bancoEspana.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoBancoEspana }
     *     
     */
    public void setBancoEspana(TipoBancoEspana value) {
        this.bancoEspana = value;
    }

    /**
     * Obtiene el valor de la propiedad seguros.
     * 
     * @return
     *     possible object is
     *     {@link TipoSeguros }
     *     
     */
    public TipoSeguros getSeguros() {
        return seguros;
    }

    /**
     * Define el valor de la propiedad seguros.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoSeguros }
     *     
     */
    public void setSeguros(TipoSeguros value) {
        this.seguros = value;
    }

    /**
     * Obtiene el valor de la propiedad sociedadGarantia.
     * 
     * @return
     *     possible object is
     *     {@link TipoSociedadGarantia }
     *     
     */
    public TipoSociedadGarantia getSociedadGarantia() {
        return sociedadGarantia;
    }

    /**
     * Define el valor de la propiedad sociedadGarantia.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoSociedadGarantia }
     *     
     */
    public void setSociedadGarantia(TipoSociedadGarantia value) {
        this.sociedadGarantia = value;
    }

    /**
     * Obtiene el valor de la propiedad institucionesInversionColectiva.
     * 
     * @return
     *     possible object is
     *     {@link TipoInstitucionesInversionColectivo }
     *     
     */
    public TipoInstitucionesInversionColectivo getInstitucionesInversionColectiva() {
        return institucionesInversionColectiva;
    }

    /**
     * Define el valor de la propiedad institucionesInversionColectiva.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoInstitucionesInversionColectivo }
     *     
     */
    public void setInstitucionesInversionColectiva(TipoInstitucionesInversionColectivo value) {
        this.institucionesInversionColectiva = value;
    }

}
