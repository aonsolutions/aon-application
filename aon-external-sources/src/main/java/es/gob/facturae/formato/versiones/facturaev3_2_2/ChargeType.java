//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.3.0.1 
// Visite <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2023.09.28 a las 10:07:44 AM UTC 
//


package es.gob.facturae.formato.versiones.facturaev3_2_2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ChargeType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ChargeType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ChargeReason" type="{http://www.facturae.gob.es/formato/Versiones/Facturaev3_2_2.xml}TextMax2500Type"/&gt;
 *         &lt;element name="ChargeRate" type="{http://www.facturae.gob.es/formato/Versiones/Facturaev3_2_2.xml}DoubleUpToEightDecimalType" minOccurs="0"/&gt;
 *         &lt;element name="ChargeAmount" type="{http://www.facturae.gob.es/formato/Versiones/Facturaev3_2_2.xml}DoubleUpToEightDecimalType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ChargeType", propOrder = {
    "chargeReason",
    "chargeRate",
    "chargeAmount"
})
public class ChargeType {

    @XmlElement(name = "ChargeReason", required = true)
    protected String chargeReason;
    @XmlElement(name = "ChargeRate")
    protected Double chargeRate;
    @XmlElement(name = "ChargeAmount")
    protected double chargeAmount;

    /**
     * Obtiene el valor de la propiedad chargeReason.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChargeReason() {
        return chargeReason;
    }

    /**
     * Define el valor de la propiedad chargeReason.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChargeReason(String value) {
        this.chargeReason = value;
    }

    /**
     * Obtiene el valor de la propiedad chargeRate.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getChargeRate() {
        return chargeRate;
    }

    /**
     * Define el valor de la propiedad chargeRate.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setChargeRate(Double value) {
        this.chargeRate = value;
    }

    /**
     * Obtiene el valor de la propiedad chargeAmount.
     * 
     */
    public double getChargeAmount() {
        return chargeAmount;
    }

    /**
     * Define el valor de la propiedad chargeAmount.
     * 
     */
    public void setChargeAmount(double value) {
        this.chargeAmount = value;
    }

}
