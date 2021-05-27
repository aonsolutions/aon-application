
package https.sii_araba_eus.documentos.suministrolr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.SuministroInformacion;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}SuministroInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroLRDetOperacionIntracomunitariaVentasEnConsigna" type="{https://sii.araba.eus/documentos/SuministroLR.xsd}LRVentaBienesConsignaType" maxOccurs="10000"/&gt;
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
    "registroLRDetOperacionIntracomunitariaVentasEnConsigna"
})
@XmlRootElement(name = "SuministroLRVentaBienesConsigna")
public class SuministroLRVentaBienesConsigna
    extends SuministroInformacion
{

    @XmlElement(name = "RegistroLRDetOperacionIntracomunitariaVentasEnConsigna", required = true)
    protected List<LRVentaBienesConsignaType> registroLRDetOperacionIntracomunitariaVentasEnConsigna;

    /**
     * Gets the value of the registroLRDetOperacionIntracomunitariaVentasEnConsigna property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroLRDetOperacionIntracomunitariaVentasEnConsigna property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroLRDetOperacionIntracomunitariaVentasEnConsigna().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LRVentaBienesConsignaType }
     * 
     * 
     */
    public List<LRVentaBienesConsignaType> getRegistroLRDetOperacionIntracomunitariaVentasEnConsigna() {
        if (registroLRDetOperacionIntracomunitariaVentasEnConsigna == null) {
            registroLRDetOperacionIntracomunitariaVentasEnConsigna = new ArrayList<LRVentaBienesConsignaType>();
        }
        return this.registroLRDetOperacionIntracomunitariaVentasEnConsigna;
    }

}
