
package eus.bizkaia.ogasuna.sii.documentos.suministrolr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.ClaveOperacionType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.PersonaFisicaJuridicaType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.PersonaFisicaJuridicaUnicaESType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.RegistroSii;


/**
 *  Apunte de operaciones de seguros. 
 * 
 * <p>Clase Java para LROperacionesSegurosType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LROperacionesSegurosType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RegistroSii"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Contraparte" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaType"/&gt;
 *         &lt;element name="ClaveOperacion" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ClaveOperacionType"/&gt;
 *         &lt;element name="ImporteTotal" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}ImporteSgn12.2Type"/&gt;
 *         &lt;element name="EntidadSucedida" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaUnicaESType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LROperacionesSegurosType", propOrder = {
    "contraparte",
    "claveOperacion",
    "importeTotal",
    "entidadSucedida"
})
public class LROperacionesSegurosType
    extends RegistroSii
{

    @XmlElement(name = "Contraparte", required = true)
    protected PersonaFisicaJuridicaType contraparte;
    @XmlElement(name = "ClaveOperacion", required = true)
    @XmlSchemaType(name = "string")
    protected ClaveOperacionType claveOperacion;
    @XmlElement(name = "ImporteTotal", required = true)
    protected String importeTotal;
    @XmlElement(name = "EntidadSucedida")
    protected PersonaFisicaJuridicaUnicaESType entidadSucedida;

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

    /**
     * Obtiene el valor de la propiedad importeTotal.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getImporteTotal() {
        return importeTotal;
    }

    /**
     * Define el valor de la propiedad importeTotal.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setImporteTotal(String value) {
        this.importeTotal = value;
    }

    /**
     * Obtiene el valor de la propiedad entidadSucedida.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public PersonaFisicaJuridicaUnicaESType getEntidadSucedida() {
        return entidadSucedida;
    }

    /**
     * Define el valor de la propiedad entidadSucedida.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public void setEntidadSucedida(PersonaFisicaJuridicaUnicaESType value) {
        this.entidadSucedida = value;
    }

}
