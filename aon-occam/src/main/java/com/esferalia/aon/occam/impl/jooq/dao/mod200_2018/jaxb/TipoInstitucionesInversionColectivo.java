//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2019.05.15 a las 12:12:26 PM CEST 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod200_2018.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_InstitucionesInversionColectivo complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_InstitucionesInversionColectivo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Balance" type="{}tipo_Balance" minOccurs="0"/>
 *         &lt;element name="PatrimonioCuentas" type="{}tipo_PatrimonioCuentas" minOccurs="0"/>
 *         &lt;element name="CuentaPyG" type="{}tipo_CuentaPyG" minOccurs="0"/>
 *         &lt;element name="VariacionPatrimonial" type="{}tipo_VariacionPatrimonial" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_InstitucionesInversionColectivo", propOrder = {
    "balance",
    "patrimonioCuentas",
    "cuentaPyG",
    "variacionPatrimonial"
})
public class TipoInstitucionesInversionColectivo {

    @XmlElement(name = "Balance")
    protected TipoBalance balance;
    @XmlElement(name = "PatrimonioCuentas")
    protected TipoPatrimonioCuentas patrimonioCuentas;
    @XmlElement(name = "CuentaPyG")
    protected TipoCuentaPyG cuentaPyG;
    @XmlElement(name = "VariacionPatrimonial")
    protected TipoVariacionPatrimonial variacionPatrimonial;

    /**
     * Obtiene el valor de la propiedad balance.
     * 
     * @return
     *     possible object is
     *     {@link TipoBalance }
     *     
     */
    public TipoBalance getBalance() {
        return balance;
    }

    /**
     * Define el valor de la propiedad balance.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoBalance }
     *     
     */
    public void setBalance(TipoBalance value) {
        this.balance = value;
    }

    /**
     * Obtiene el valor de la propiedad patrimonioCuentas.
     * 
     * @return
     *     possible object is
     *     {@link TipoPatrimonioCuentas }
     *     
     */
    public TipoPatrimonioCuentas getPatrimonioCuentas() {
        return patrimonioCuentas;
    }

    /**
     * Define el valor de la propiedad patrimonioCuentas.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoPatrimonioCuentas }
     *     
     */
    public void setPatrimonioCuentas(TipoPatrimonioCuentas value) {
        this.patrimonioCuentas = value;
    }

    /**
     * Obtiene el valor de la propiedad cuentaPyG.
     * 
     * @return
     *     possible object is
     *     {@link TipoCuentaPyG }
     *     
     */
    public TipoCuentaPyG getCuentaPyG() {
        return cuentaPyG;
    }

    /**
     * Define el valor de la propiedad cuentaPyG.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoCuentaPyG }
     *     
     */
    public void setCuentaPyG(TipoCuentaPyG value) {
        this.cuentaPyG = value;
    }

    /**
     * Obtiene el valor de la propiedad variacionPatrimonial.
     * 
     * @return
     *     possible object is
     *     {@link TipoVariacionPatrimonial }
     *     
     */
    public TipoVariacionPatrimonial getVariacionPatrimonial() {
        return variacionPatrimonial;
    }

    /**
     * Define el valor de la propiedad variacionPatrimonial.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoVariacionPatrimonial }
     *     
     */
    public void setVariacionPatrimonial(TipoVariacionPatrimonial value) {
        this.variacionPatrimonial = value;
    }

}
