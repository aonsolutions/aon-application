
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Tipo de Operacion sin calificar si se trata de una Prestacin de Servicios o una Entrega de Bienes
 * 
 * <p>Clase Java para TipoSinDesgloseType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TipoSinDesgloseType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Sujeta" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}SujetaType" minOccurs="0"/&gt;
 *         &lt;element name="NoSujeta" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}NoSujetaType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TipoSinDesgloseType", propOrder = {
    "sujeta",
    "noSujeta"
})
public class TipoSinDesgloseType {

    @XmlElement(name = "Sujeta")
    protected SujetaType sujeta;
    @XmlElement(name = "NoSujeta")
    protected NoSujetaType noSujeta;

    /**
     * Obtiene el valor de la propiedad sujeta.
     * 
     * @return
     *     possible object is
     *     {@link SujetaType }
     *     
     */
    public SujetaType getSujeta() {
        return sujeta;
    }

    /**
     * Define el valor de la propiedad sujeta.
     * 
     * @param value
     *     allowed object is
     *     {@link SujetaType }
     *     
     */
    public void setSujeta(SujetaType value) {
        this.sujeta = value;
    }

    /**
     * Obtiene el valor de la propiedad noSujeta.
     * 
     * @return
     *     possible object is
     *     {@link NoSujetaType }
     *     
     */
    public NoSujetaType getNoSujeta() {
        return noSujeta;
    }

    /**
     * Define el valor de la propiedad noSujeta.
     * 
     * @param value
     *     allowed object is
     *     {@link NoSujetaType }
     *     
     */
    public void setNoSujeta(NoSujetaType value) {
        this.noSujeta = value;
    }

}
