
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.respuestaconsultalr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para RespuestaConsultaLRCobrosMetalicoType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RespuestaConsultaLRCobrosMetalicoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}RespuestaConsultaLRFacturasType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroRespuestaConsultaLRCobrosMetalico" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/RespuestaConsultaLR.xsd}RegistroRespuestaConsultaCobrosMetalicoType" maxOccurs="10000" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaConsultaLRCobrosMetalicoType", propOrder = {
    "registroRespuestaConsultaLRCobrosMetalico"
})
public class RespuestaConsultaLRCobrosMetalicoType
    extends RespuestaConsultaLRFacturasType
{

    @XmlElement(name = "RegistroRespuestaConsultaLRCobrosMetalico")
    protected List<RegistroRespuestaConsultaCobrosMetalicoType> registroRespuestaConsultaLRCobrosMetalico;

    /**
     * Gets the value of the registroRespuestaConsultaLRCobrosMetalico property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroRespuestaConsultaLRCobrosMetalico property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroRespuestaConsultaLRCobrosMetalico().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RegistroRespuestaConsultaCobrosMetalicoType }
     * 
     * 
     */
    public List<RegistroRespuestaConsultaCobrosMetalicoType> getRegistroRespuestaConsultaLRCobrosMetalico() {
        if (registroRespuestaConsultaLRCobrosMetalico == null) {
            registroRespuestaConsultaLRCobrosMetalico = new ArrayList<RegistroRespuestaConsultaCobrosMetalicoType>();
        }
        return this.registroRespuestaConsultaLRCobrosMetalico;
    }

}
