
package eus.bizkaia.ogasuna.sii.documentos.suministrolr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.SuministroInformacion;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}SuministroInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroLRDetOperacionIntracomunitaria" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroLR.xsd}LROperacionIntracomunitariaType" maxOccurs="10000"/&gt;
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
    "registroLRDetOperacionIntracomunitaria"
})
@XmlRootElement(name = "SuministroLRDetOperacionIntracomunitaria")
public class SuministroLRDetOperacionIntracomunitaria
    extends SuministroInformacion
{

    @XmlElement(name = "RegistroLRDetOperacionIntracomunitaria", required = true)
    protected List<LROperacionIntracomunitariaType> registroLRDetOperacionIntracomunitaria;

    /**
     * Gets the value of the registroLRDetOperacionIntracomunitaria property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroLRDetOperacionIntracomunitaria property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroLRDetOperacionIntracomunitaria().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LROperacionIntracomunitariaType }
     * 
     * 
     */
    public List<LROperacionIntracomunitariaType> getRegistroLRDetOperacionIntracomunitaria() {
        if (registroLRDetOperacionIntracomunitaria == null) {
            registroLRDetOperacionIntracomunitaria = new ArrayList<LROperacionIntracomunitariaType>();
        }
        return this.registroLRDetOperacionIntracomunitaria;
    }

}
