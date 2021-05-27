
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.ClaveOperacionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.PersonaFisicaJuridicaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion.RegistroSii;


/**
 *  Apunte correspondiente a operaciones de seguros. 
 * 
 * <p>Clase Java para LRBajaRegistroLROperacionesSegurosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRBajaRegistroLROperacionesSegurosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}RegistroSii"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Contraparte" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}PersonaFisicaJuridicaType"/&gt;
 *         &lt;element name="ClaveOperacion" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}ClaveOperacionType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRBajaRegistroLROperacionesSegurosType", propOrder = {
    "contraparte",
    "claveOperacion"
})
public class LRBajaRegistroLROperacionesSegurosType
    extends RegistroSii
{

    @XmlElement(name = "Contraparte", required = true)
    protected PersonaFisicaJuridicaType contraparte;
    @XmlElement(name = "ClaveOperacion", required = true)
    @XmlSchemaType(name = "string")
    protected ClaveOperacionType claveOperacion;

    /**
     * Obtiene el valor de la propiedad contraparte.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public PersonaFisicaJuridicaType getContraparte() {
        return contraparte;
    }

    /**
     * Define el valor de la propiedad contraparte.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public void setContraparte(PersonaFisicaJuridicaType value) {
        this.contraparte = value;
    }

    /**
     * Obtiene el valor de la propiedad claveOperacion.
     * 
     * @return
     *     possible object is
     *     {@link ClaveOperacionType }
     *     
     */
    public ClaveOperacionType getClaveOperacion() {
        return claveOperacion;
    }

    /**
     * Define el valor de la propiedad claveOperacion.
     * 
     * @param value
     *     allowed object is
     *     {@link ClaveOperacionType }
     *     
     */
    public void setClaveOperacion(ClaveOperacionType value) {
        this.claveOperacion = value;
    }

}
