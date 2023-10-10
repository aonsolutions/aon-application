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
 * <p>Clase Java para AmountsWithheldType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="AmountsWithheldType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="WithholdingReason" type="{http://www.facturae.gob.es/formato/Versiones/Facturaev3_2_2.xml}TextMax2500Type"/&gt;
 *         &lt;element name="WithholdingRate" type="{http://www.facturae.gob.es/formato/Versiones/Facturaev3_2_2.xml}DoubleUpToEightDecimalType" minOccurs="0"/&gt;
 *         &lt;element name="WithholdingAmount" type="{http://www.facturae.gob.es/formato/Versiones/Facturaev3_2_2.xml}DoubleUpToEightDecimalType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AmountsWithheldType", propOrder = {
    "withholdingReason",
    "withholdingRate",
    "withholdingAmount"
})
public class AmountsWithheldType {

    @XmlElement(name = "WithholdingReason", required = true)
    protected String withholdingReason;
    @XmlElement(name = "WithholdingRate")
    protected Double withholdingRate;
    @XmlElement(name = "WithholdingAmount")
    protected double withholdingAmount;

    /**
     * Obtiene el valor de la propiedad withholdingReason.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWithholdingReason() {
        return withholdingReason;
    }

    /**
     * Define el valor de la propiedad withholdingReason.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWithholdingReason(String value) {
        this.withholdingReason = value;
    }

    /**
     * Obtiene el valor de la propiedad withholdingRate.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWithholdingRate() {
        return withholdingRate;
    }

    /**
     * Define el valor de la propiedad withholdingRate.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWithholdingRate(Double value) {
        this.withholdingRate = value;
    }

    /**
     * Obtiene el valor de la propiedad withholdingAmount.
     * 
     */
    public double getWithholdingAmount() {
        return withholdingAmount;
    }

    /**
     * Define el valor de la propiedad withholdingAmount.
     * 
     */
    public void setWithholdingAmount(double value) {
        this.withholdingAmount = value;
    }

}
