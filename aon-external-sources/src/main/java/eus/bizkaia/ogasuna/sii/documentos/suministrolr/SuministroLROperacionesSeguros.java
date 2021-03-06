
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
 *         &lt;element name="RegistroLROperacionesSeguros" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroLR.xsd}LROperacionesSegurosType" maxOccurs="10000"/&gt;
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
    "registroLROperacionesSeguros"
})
@XmlRootElement(name = "SuministroLROperacionesSeguros")
public class SuministroLROperacionesSeguros
    extends SuministroInformacion
{

    @XmlElement(name = "RegistroLROperacionesSeguros", required = true)
    protected List<LROperacionesSegurosType> registroLROperacionesSeguros;

    /**
     * Gets the value of the registroLROperacionesSeguros property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroLROperacionesSeguros property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroLROperacionesSeguros().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LROperacionesSegurosType }
     * 
     * 
     */
    public List<LROperacionesSegurosType> getRegistroLROperacionesSeguros() {
        if (registroLROperacionesSeguros == null) {
            registroLROperacionesSeguros = new ArrayList<LROperacionesSegurosType>();
        }
        return this.registroLROperacionesSeguros;
    }

}
