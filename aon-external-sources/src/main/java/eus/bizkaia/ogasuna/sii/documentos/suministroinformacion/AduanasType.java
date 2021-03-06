
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Desglose cuando corresponda de la información asociada a los documentos de aduanas
 * 
 * <p>Clase Java para AduanasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="AduanasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="NumeroDUA" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}TextMax40Type" minOccurs="0"/&gt;
 *         &lt;element name="FechaRegContableDUA" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}fecha" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AduanasType", propOrder = {
    "numeroDUA",
    "fechaRegContableDUA"
})
public class AduanasType {

    @XmlElement(name = "NumeroDUA")
    protected String numeroDUA;
    @XmlElement(name = "FechaRegContableDUA")
    protected String fechaRegContableDUA;

    /**
     * Obtiene el valor de la propiedad numeroDUA.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroDUA() {
        return numeroDUA;
    }

    /**
     * Define el valor de la propiedad numeroDUA.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroDUA(String value) {
        this.numeroDUA = value;
    }

    /**
     * Obtiene el valor de la propiedad fechaRegContableDUA.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFechaRegContableDUA() {
        return fechaRegContableDUA;
    }

    /**
     * Define el valor de la propiedad fechaRegContableDUA.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFechaRegContableDUA(String value) {
        this.fechaRegContableDUA = value;
    }

}
