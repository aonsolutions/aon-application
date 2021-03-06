
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.SuministroInformacionCobrosPagos;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}SuministroInformacionCobrosPagos"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroLRCobros" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroLR.xsd}LRCobrosEmitidasType" maxOccurs="10000"/&gt;
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
    "registroLRCobros"
})
@XmlRootElement(name = "SuministroLRCobrosEmitidas")
public class SuministroLRCobrosEmitidas
    extends SuministroInformacionCobrosPagos
{

    @XmlElement(name = "RegistroLRCobros", required = true)
    protected List<LRCobrosEmitidasType> registroLRCobros;

    /**
     * Gets the value of the registroLRCobros property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroLRCobros property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroLRCobros().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LRCobrosEmitidasType }
     * 
     * 
     */
    public List<LRCobrosEmitidasType> getRegistroLRCobros() {
        if (registroLRCobros == null) {
            registroLRCobros = new ArrayList<LRCobrosEmitidasType>();
        }
        return this.registroLRCobros;
    }

}
