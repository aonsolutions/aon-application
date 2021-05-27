
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestasuministro;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 *  Respuesta a un envío Sii 
 * 
 * <p>Clase Java para RespuestaLRBajaFRecibidasType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaLRBajaFRecibidasType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/RespuestaSuministro.xsd}RespuestaComunBajaType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RespuestaLinea" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/RespuestaSuministro.xsd}RespuestaRecibidaBajaType" maxOccurs="10000" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaLRBajaFRecibidasType", propOrder = {
    "respuestaLinea"
})
public class RespuestaLRBajaFRecibidasType
    extends RespuestaComunBajaType
{

    @XmlElement(name = "RespuestaLinea")
    protected List<RespuestaRecibidaBajaType> respuestaLinea;

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
     * {@link RespuestaRecibidaBajaType }
     * 
     * 
     */
    public List<RespuestaRecibidaBajaType> getRespuestaLinea() {
        if (respuestaLinea == null) {
            respuestaLinea = new ArrayList<RespuestaRecibidaBajaType>();
        }
        return this.respuestaLinea;
    }

}
