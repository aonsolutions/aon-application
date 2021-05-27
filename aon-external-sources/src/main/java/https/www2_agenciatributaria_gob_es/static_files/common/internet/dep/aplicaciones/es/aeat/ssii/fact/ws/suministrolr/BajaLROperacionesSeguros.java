
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.SuministroInformacionBaja;


/**
 * <p>Clase Java para anonymous complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}SuministroInformacionBaja"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RegistroLRBajaOperacionesSeguros" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroLR.xsd}LRBajaRegistroLROperacionesSegurosType" maxOccurs="10000"/&gt;
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
    "registroLRBajaOperacionesSeguros"
})
@XmlRootElement(name = "BajaLROperacionesSeguros")
public class BajaLROperacionesSeguros
    extends SuministroInformacionBaja
{

    @XmlElement(name = "RegistroLRBajaOperacionesSeguros", required = true)
    protected List<LRBajaRegistroLROperacionesSegurosType> registroLRBajaOperacionesSeguros;

    /**
     * Gets the value of the registroLRBajaOperacionesSeguros property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the registroLRBajaOperacionesSeguros property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRegistroLRBajaOperacionesSeguros().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LRBajaRegistroLROperacionesSegurosType }
     * 
     * 
     */
    public List<LRBajaRegistroLROperacionesSegurosType> getRegistroLRBajaOperacionesSeguros() {
        if (registroLRBajaOperacionesSeguros == null) {
            registroLRBajaOperacionesSeguros = new ArrayList<LRBajaRegistroLROperacionesSegurosType>();
        }
        return this.registroLRBajaOperacionesSeguros;
    }

}
