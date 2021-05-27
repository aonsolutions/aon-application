
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.DatosInmuebleType;


/**
 * <p>Clase Java para InmueblesAdicionalType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="InmueblesAdicionalType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="DetalleInmueble" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}DatosInmuebleType" maxOccurs="10000" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InmueblesAdicionalType", propOrder = {
    "detalleInmueble"
})
public class InmueblesAdicionalType {

    @XmlElement(name = "DetalleInmueble")
    protected List<DatosInmuebleType> detalleInmueble;

    /**
     * Gets the value of the detalleInmueble property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the detalleInmueble property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDetalleInmueble().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DatosInmuebleType }
     * 
     * 
     */
    public List<DatosInmuebleType> getDetalleInmueble() {
        if (detalleInmueble == null) {
            detalleInmueble = new ArrayList<DatosInmuebleType>();
        }
        return this.detalleInmueble;
    }

}
