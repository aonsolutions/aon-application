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
 * <p>Clase Java para PartiesType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PartiesType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="SellerParty" type="{http://www.facturae.gob.es/formato/Versiones/Facturaev3_2_2.xml}BusinessType"/&gt;
 *         &lt;element name="BuyerParty" type="{http://www.facturae.gob.es/formato/Versiones/Facturaev3_2_2.xml}BusinessType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PartiesType", propOrder = {
    "sellerParty",
    "buyerParty"
})
public class PartiesType {

    @XmlElement(name = "SellerParty", required = true)
    protected BusinessType sellerParty;
    @XmlElement(name = "BuyerParty", required = true)
    protected BusinessType buyerParty;

    /**
     * Obtiene el valor de la propiedad sellerParty.
     * 
     * @return
     *     possible object is
     *     {@link BusinessType }
     *     
     */
    public BusinessType getSellerParty() {
        return sellerParty;
    }

    /**
     * Define el valor de la propiedad sellerParty.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessType }
     *     
     */
    public void setSellerParty(BusinessType value) {
        this.sellerParty = value;
    }

    /**
     * Obtiene el valor de la propiedad buyerParty.
     * 
     * @return
     *     possible object is
     *     {@link BusinessType }
     *     
     */
    public BusinessType getBuyerParty() {
        return buyerParty;
    }

    /**
     * Define el valor de la propiedad buyerParty.
     * 
     * @param value
     *     allowed object is
     *     {@link BusinessType }
     *     
     */
    public void setBuyerParty(BusinessType value) {
        this.buyerParty = value;
    }

}
