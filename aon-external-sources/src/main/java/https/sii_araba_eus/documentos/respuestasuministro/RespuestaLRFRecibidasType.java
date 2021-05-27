
package https.sii_araba_eus.documentos.respuestasuministro;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  Respuesta a un envío Sii para suministro de Facturas emitidas
 * 
 * <p>Clase Java para RespuestaLRFRecibidasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaLRFRecibidasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://sii.araba.eus/documentos/RespuestaSuministro.xsd}RespuestaComunAltaType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RespuestaLinea" type="{https://sii.araba.eus/documentos/RespuestaSuministro.xsd}RespuestaRecibidaType" maxOccurs="10000" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaLRFRecibidasType", propOrder = {
    "respuestaLinea"
})
public class RespuestaLRFRecibidasType
    extends RespuestaComunAltaType
{

    @XmlElement(name = "RespuestaLinea")
    protected List<RespuestaRecibidaType> respuestaLinea;

    /**
     * Gets the value of the respuestaLinea property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the respuestaLinea property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRespuestaLinea().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RespuestaRecibidaType }
     * 
     * 
     */
    public List<RespuestaRecibidaType> getRespuestaLinea() {
        if (respuestaLinea == null) {
            respuestaLinea = new ArrayList<RespuestaRecibidaType>();
        }
        return this.respuestaLinea;
    }

}
