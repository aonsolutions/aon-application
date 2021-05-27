
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SuministroInformacion;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}SuministroInformacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroLRAgenciasViajes" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroLR.xsd}LRAgenciasViajesType" maxOccurs="10000"/&gt;
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
    "registroLRAgenciasViajes"
})
@XmlRootElement(name = "SuministroLRAgenciasViajes")
public class SuministroLRAgenciasViajes
    extends SuministroInformacion
{

    @XmlElement(name = "RegistroLRAgenciasViajes", required = true)
    protected List<LRAgenciasViajesType> registroLRAgenciasViajes;

    /**
     * Gets the value of the registroLRAgenciasViajes property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroLRAgenciasViajes property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroLRAgenciasViajes().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LRAgenciasViajesType }
     * 
     * 
     */
    public List<LRAgenciasViajesType> getRegistroLRAgenciasViajes() {
        if (registroLRAgenciasViajes == null) {
            registroLRAgenciasViajes = new ArrayList<LRAgenciasViajesType>();
        }
        return this.registroLRAgenciasViajes;
    }

}
