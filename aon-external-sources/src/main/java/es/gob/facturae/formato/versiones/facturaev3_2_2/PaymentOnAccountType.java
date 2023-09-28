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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Clase Java para PaymentOnAccountType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="PaymentOnAccountType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PaymentOnAccountDate" type="{http://www.w3.org/2001/XMLSchema}date"/&gt;
 *         &lt;element name="PaymentOnAccountAmount" type="{http://www.facturae.gob.es/formato/Versiones/Facturaev3_2_2.xml}DoubleUpToEightDecimalType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PaymentOnAccountType", propOrder = {
    "paymentOnAccountDate",
    "paymentOnAccountAmount"
})
public class PaymentOnAccountType {

    @XmlElement(name = "PaymentOnAccountDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar paymentOnAccountDate;
    @XmlElement(name = "PaymentOnAccountAmount")
    protected double paymentOnAccountAmount;

    /**
     * Obtiene el valor de la propiedad paymentOnAccountDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaymentOnAccountDate() {
        return paymentOnAccountDate;
    }

    /**
     * Define el valor de la propiedad paymentOnAccountDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaymentOnAccountDate(XMLGregorianCalendar value) {
        this.paymentOnAccountDate = value;
    }

    /**
     * Obtiene el valor de la propiedad paymentOnAccountAmount.
     * 
     */
    public double getPaymentOnAccountAmount() {
        return paymentOnAccountAmount;
    }

    /**
     * Define el valor de la propiedad paymentOnAccountAmount.
     * 
     */
    public void setPaymentOnAccountAmount(double value) {
        this.paymentOnAccountAmount = value;
    }

}
