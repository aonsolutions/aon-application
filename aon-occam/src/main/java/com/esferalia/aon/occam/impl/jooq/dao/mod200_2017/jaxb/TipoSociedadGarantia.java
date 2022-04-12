//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.8-b130911.1802 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2018.05.25 a las 09:12:21 AM CEST 
//


package com.esferalia.aon.occam.impl.jooq.dao.mod200_2017.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_SociedadGarantia complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_SociedadGarantia">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Balance" type="{}tipo_BalanceGarantia" minOccurs="0"/>
 *         &lt;element name="CuentaPyG" type="{}tipo_CuentaGarantia" minOccurs="0"/>
 *         &lt;element name="IngresosPerdidas" type="{}tipo_IngresosPerdidas" minOccurs="0"/>
 *         &lt;element name="CambiosPN" type="{}tipo_CambiosPN_Garantia" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_SociedadGarantia", propOrder = {
    "balance",
    "cuentaPyG",
    "ingresosPerdidas",
    "cambiosPN"
})
public class TipoSociedadGarantia {

    @XmlElement(name = "Balance")
    protected TipoBalanceGarantia balance;
    @XmlElement(name = "CuentaPyG")
    protected TipoCuentaGarantia cuentaPyG;
    @XmlElement(name = "IngresosPerdidas")
    protected TipoIngresosPerdidas ingresosPerdidas;
    @XmlElement(name = "CambiosPN")
    protected TipoCambiosPNGarantia cambiosPN;

    /**
     * Obtiene el valor de la propiedad balance.
     * 
     * @return
     *     possible object is
     *     {@link TipoBalanceGarantia }
     *     
     */
    public TipoBalanceGarantia getBalance() {
        return balance;
    }

    /**
     * Define el valor de la propiedad balance.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoBalanceGarantia }
     *     
     */
    public void setBalance(TipoBalanceGarantia value) {
        this.balance = value;
    }

    /**
     * Obtiene el valor de la propiedad cuentaPyG.
     * 
     * @return
     *     possible object is
     *     {@link TipoCuentaGarantia }
     *     
     */
    public TipoCuentaGarantia getCuentaPyG() {
        return cuentaPyG;
    }

    /**
     * Define el valor de la propiedad cuentaPyG.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoCuentaGarantia }
     *     
     */
    public void setCuentaPyG(TipoCuentaGarantia value) {
        this.cuentaPyG = value;
    }

    /**
     * Obtiene el valor de la propiedad ingresosPerdidas.
     * 
     * @return
     *     possible object is
     *     {@link TipoIngresosPerdidas }
     *     
     */
    public TipoIngresosPerdidas getIngresosPerdidas() {
        return ingresosPerdidas;
    }

    /**
     * Define el valor de la propiedad ingresosPerdidas.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoIngresosPerdidas }
     *     
     */
    public void setIngresosPerdidas(TipoIngresosPerdidas value) {
        this.ingresosPerdidas = value;
    }

    /**
     * Obtiene el valor de la propiedad cambiosPN.
     * 
     * @return
     *     possible object is
     *     {@link TipoCambiosPNGarantia }
     *     
     */
    public TipoCambiosPNGarantia getCambiosPN() {
        return cambiosPN;
    }

    /**
     * Define el valor de la propiedad cambiosPN.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoCambiosPNGarantia }
     *     
     */
    public void setCambiosPN(TipoCambiosPNGarantia value) {
        this.cambiosPN = value;
    }

}
