
package https.sii_araba_eus.documentos.suministrolr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.SuministroInformacionBaja;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}SuministroInformacionBaja"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroLRBajaExpedidas" type="{https://sii.araba.eus/documentos/SuministroLR.xsd}LRBajaExpedidasType" maxOccurs="10000"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "registroLRBajaExpedidas"
})
@XmlRootElement(name = "BajaLRFacturasEmitidas")
public class BajaLRFacturasEmitidas
    extends SuministroInformacionBaja
{

    @XmlElement(name = "RegistroLRBajaExpedidas", required = true)
    protected List<LRBajaExpedidasType> registroLRBajaExpedidas;

    /**
     * Gets the value of the registroLRBajaExpedidas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroLRBajaExpedidas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroLRBajaExpedidas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LRBajaExpedidasType }
     * 
     * 
     */
    public List<LRBajaExpedidasType> getRegistroLRBajaExpedidas() {
        if (registroLRBajaExpedidas == null) {
            registroLRBajaExpedidas = new ArrayList<LRBajaExpedidasType>();
        }
        return this.registroLRBajaExpedidas;
    }

}
