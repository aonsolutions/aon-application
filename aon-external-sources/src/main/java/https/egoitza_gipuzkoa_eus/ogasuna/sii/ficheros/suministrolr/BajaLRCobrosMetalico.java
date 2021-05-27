
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.SuministroInformacionBaja;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}SuministroInformacionBaja"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroLRBajaCobrosMetalico" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroLR.xsd}LRBajaCobrosMetalicoType" maxOccurs="10000"/&gt;
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
    "registroLRBajaCobrosMetalico"
})
@XmlRootElement(name = "BajaLRCobrosMetalico")
public class BajaLRCobrosMetalico
    extends SuministroInformacionBaja
{

    @XmlElement(name = "RegistroLRBajaCobrosMetalico", required = true)
    protected List<LRBajaCobrosMetalicoType> registroLRBajaCobrosMetalico;

    /**
     * Gets the value of the registroLRBajaCobrosMetalico property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroLRBajaCobrosMetalico property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroLRBajaCobrosMetalico().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LRBajaCobrosMetalicoType }
     * 
     * 
     */
    public List<LRBajaCobrosMetalicoType> getRegistroLRBajaCobrosMetalico() {
        if (registroLRBajaCobrosMetalico == null) {
            registroLRBajaCobrosMetalico = new ArrayList<LRBajaCobrosMetalicoType>();
        }
        return this.registroLRBajaCobrosMetalico;
    }

}
