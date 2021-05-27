
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Tipo de Operacion sin calificar si se trata de una Prestacin de Servicios o una Entrega de Bienes
 * 
 * <p>Clase Java para TipoSinDesglosePrestacionType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TipoSinDesglosePrestacionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Sujeta" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}SujetaPrestacionType" minOccurs="0"/&gt;
 *         &lt;element name="NoSujeta" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}NoSujetaType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TipoSinDesglosePrestacionType", propOrder = {
    "sujeta",
    "noSujeta"
})
public class TipoSinDesglosePrestacionType {

    @XmlElement(name = "Sujeta")
    protected SujetaPrestacionType sujeta;
    @XmlElement(name = "NoSujeta")
    protected NoSujetaType noSujeta;

    /**
     * Obtiene el valor de la propiedad sujeta.
     * 
     * @return
     *     possible object is
     *     {@link SujetaPrestacionType }
     *     
     */
    public SujetaPrestacionType getSujeta() {
        return sujeta;
    }

    /**
     * Define el valor de la propiedad sujeta.
     * 
     * @param value
     *     allowed object is
     *     {@link SujetaPrestacionType }
     *     
     */
    public void setSujeta(SujetaPrestacionType value) {
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
