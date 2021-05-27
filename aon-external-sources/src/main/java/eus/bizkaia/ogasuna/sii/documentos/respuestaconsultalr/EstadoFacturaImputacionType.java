
package eus.bizkaia.ogasuna.sii.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para EstadoFacturaImputacionType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="EstadoFacturaImputacionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="EstadoCuadre" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}EstadoCuadreType" minOccurs="0"/&gt;
 *         &lt;element name="TimestampEstadoCuadre" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}Timestamp" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "EstadoFacturaImputacionType", propOrder = {
    "estadoCuadre",
    "timestampEstadoCuadre"
})
public class EstadoFacturaImputacionType {

    @XmlElement(name = "EstadoCuadre")
    protected String estadoCuadre;
    @XmlElement(name = "TimestampEstadoCuadre")
    protected String timestampEstadoCuadre;

    /**
     * Obtiene el valor de la propiedad estadoCuadre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoCuadre() {
        return estadoCuadre;
    }

    /**
     * Define el valor de la propiedad estadoCuadre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoCuadre(String value) {
        this.estadoCuadre = value;
    }

    /**
     * Obtiene el valor de la propiedad timestampEstadoCuadre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimestampEstadoCuadre() {
        return timestampEstadoCuadre;
    }

    /**
     * Define el valor de la propiedad timestampEstadoCuadre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestampEstadoCuadre(String value) {
        this.timestampEstadoCuadre = value;
    }

}
