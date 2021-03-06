
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DetalleExentaType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DetalleExentaType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="CausaExencion" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}CausaExencionType" minOccurs="0"/&gt;
 *         &lt;element name="BaseImponible" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DetalleExentaType", propOrder = {
    "causaExencion",
    "baseImponible"
})
public class DetalleExentaType {

    @XmlElement(name = "CausaExencion")
    @XmlSchemaType(name = "string")
    protected CausaExencionType causaExencion;
    @XmlElement(name = "BaseImponible", required = true)
    protected String baseImponible;

    /**
     * Obtiene el valor de la propiedad causaExencion.
     * 
     * @return
     *     possible object is
     *     {@link CausaExencionType }
     *     
     */
    public CausaExencionType getCausaExencion() {
        return causaExencion;
    }

    /**
     * Define el valor de la propiedad causaExencion.
     * 
     * @param value
     *     allowed object is
     *     {@link CausaExencionType }
     *     
     */
    public void setCausaExencion(CausaExencionType value) {
        this.causaExencion = value;
    }

    /**
     * Obtiene el valor de la propiedad baseImponible.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBaseImponible() {
        return baseImponible;
    }

    /**
     * Define el valor de la propiedad baseImponible.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBaseImponible(String value) {
        this.baseImponible = value;
    }

}
