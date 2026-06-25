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
 * <p>Clase Java para tipo_Seguros complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tipo_Seguros"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Balance" type="{}tipo_BalanceAseguradora" minOccurs="0"/&gt;
 *         &lt;element name="CuentaPyG" type="{}tipo_CuentaAseguradora" minOccurs="0"/&gt;
 *         &lt;element name="CambiosPP" type="{}tipo_CambiosPP_Aseguradora" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tipo_Seguros", propOrder = {
    "balance",
    "cuentaPyG",
    "cambiosPP"
})
public class TipoSeguros {

    @XmlElement(name = "Balance")
    protected TipoBalanceAseguradora balance;
    @XmlElement(name = "CuentaPyG")
    protected TipoCuentaAseguradora cuentaPyG;
    @XmlElement(name = "CambiosPP")
    protected TipoCambiosPPAseguradora cambiosPP;

    /**
     * Obtiene el valor de la propiedad balance.
     * 
     * @return
     *     possible object is
     *     {@link TipoBalanceAseguradora }
     *     
     */
    public TipoBalanceAseguradora getBalance() {
        return balance;
    }

    /**
     * Define el valor de la propiedad balance.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoBalanceAseguradora }
     *     
     */
    public void setBalance(TipoBalanceAseguradora value) {
        this.balance = value;
    }

    /**
     * Obtiene el valor de la propiedad cuentaPyG.
     * 
     * @return
     *     possible object is
     *     {@link TipoCuentaAseguradora }
     *     
     */
    public TipoCuentaAseguradora getCuentaPyG() {
        return cuentaPyG;
    }

    /**
     * Define el valor de la propiedad cuentaPyG.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoCuentaAseguradora }
     *     
     */
    public void setCuentaPyG(TipoCuentaAseguradora value) {
        this.cuentaPyG = value;
    }

    /**
     * Obtiene el valor de la propiedad cambiosPP.
     * 
     * @return
     *     possible object is
     *     {@link TipoCambiosPPAseguradora }
     *     
     */
    public TipoCambiosPPAseguradora getCambiosPP() {
        return cambiosPP;
    }

    /**
     * Define el valor de la propiedad cambiosPP.
     * 
     * @param value
     *     allowed object is
     *     {@link TipoCambiosPPAseguradora }
     *     
     */
    public void setCambiosPP(TipoCambiosPPAseguradora value) {
        this.cambiosPP = value;
    }

}
