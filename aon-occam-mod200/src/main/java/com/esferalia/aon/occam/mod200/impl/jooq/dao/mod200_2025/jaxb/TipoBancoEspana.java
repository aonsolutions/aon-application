//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.1-b171012.0423 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2025.05.05 a las 11:04:44 AM CEST 
//


package com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2025.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para tipo_BancoEspana complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_BancoEspana"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Balance" type="{}tipo_BalanceBancos" minOccurs="0"/&gt;
 *         &lt;element name="CuentaPyG" type="{}tipo_CuentaBancos" minOccurs="0"/&gt;
 *         &lt;element name="CambiosPN" type="{}tipo_CambiosPN_Bancos" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_BancoEspana", propOrder = {
    "balance",
    "cuentaPyG",
    "cambiosPN"
})
public class TipoBancoEspana {

    @XmlElement(name = "Balance")
    protected TipoBalanceBancos balance;
    @XmlElement(name = "CuentaPyG")
    protected TipoCuentaBancos cuentaPyG;
    @XmlElement(name = "CambiosPN")
    protected TipoCambiosPNBancos cambiosPN;

    /**
     * Obtiene el valor de la propiedad balance.
     * 
     * @return
     *     possible object is
     *     {@link TipoBalanceBancos }
     *     
     */
    public TipoBalanceBancos getBalance() {
        return balance;
    }

    /**
     * Define el valor de la propiedad balance.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoBalanceBancos }
     *     
     */
    public void setBalance(TipoBalanceBancos value) {
        this.balance = value;
    }

    /**
     * Obtiene el valor de la propiedad cuentaPyG.
     * 
     * @return
     *     possible object is
     *     {@link TipoCuentaBancos }
     *     
     */
    public TipoCuentaBancos getCuentaPyG() {
        return cuentaPyG;
    }

    /**
     * Define el valor de la propiedad cuentaPyG.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoCuentaBancos }
     *     
     */
    public void setCuentaPyG(TipoCuentaBancos value) {
        this.cuentaPyG = value;
    }

    /**
     * Obtiene el valor de la propiedad cambiosPN.
     * 
     * @return
     *     possible object is
     *     {@link TipoCambiosPNBancos }
     *     
     */
    public TipoCambiosPNBancos getCambiosPN() {
        return cambiosPN;
    }

    /**
     * Define el valor de la propiedad cambiosPN.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoCambiosPNBancos }
     *     
     */
    public void setCambiosPN(TipoCambiosPNBancos value) {
        this.cambiosPN = value;
    }

}
