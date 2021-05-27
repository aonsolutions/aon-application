
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
 *         &lt;element name="RegistroLRBajaVentaBienesConsigna" type="{https://sii.araba.eus/documentos/SuministroLR.xsd}LRBajaVentaBienesConsignaType" maxOccurs="10000"/&gt;
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
    "registroLRBajaVentaBienesConsigna"
})
@XmlRootElement(name = "BajaLRVentaBienesConsigna")
public class BajaLRVentaBienesConsigna
    extends SuministroInformacionBaja
{

    @XmlElement(name = "RegistroLRBajaVentaBienesConsigna", required = true)
    protected List<LRBajaVentaBienesConsignaType> registroLRBajaVentaBienesConsigna;

    /**
     * Gets the value of the registroLRBajaVentaBienesConsigna property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroLRBajaVentaBienesConsigna property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroLRBajaVentaBienesConsigna().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LRBajaVentaBienesConsignaType }
     * 
     * 
     */
    public List<LRBajaVentaBienesConsignaType> getRegistroLRBajaVentaBienesConsigna() {
        if (registroLRBajaVentaBienesConsigna == null) {
            registroLRBajaVentaBienesConsigna = new ArrayList<LRBajaVentaBienesConsignaType>();
        }
        return this.registroLRBajaVentaBienesConsigna;
    }

}
