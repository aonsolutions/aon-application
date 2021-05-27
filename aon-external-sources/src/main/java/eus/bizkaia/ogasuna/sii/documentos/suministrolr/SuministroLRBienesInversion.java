
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
 *         &lt;element name="RegistroLRBienesInversion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroLR.xsd}LRBienesInversionType" maxOccurs="10000"/&gt;
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
    "registroLRBienesInversion"
})
@XmlRootElement(name = "SuministroLRBienesInversion")
public class SuministroLRBienesInversion
    extends SuministroInformacion
{

    @XmlElement(name = "RegistroLRBienesInversion", required = true)
    protected List<LRBienesInversionType> registroLRBienesInversion;

    /**
     * Gets the value of the registroLRBienesInversion property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroLRBienesInversion property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroLRBienesInversion().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LRBienesInversionType }
     * 
     * 
     */
    public List<LRBienesInversionType> getRegistroLRBienesInversion() {
        if (registroLRBienesInversion == null) {
            registroLRBienesInversion = new ArrayList<LRBienesInversionType>();
        }
        return this.registroLRBienesInversion;
    }

}
