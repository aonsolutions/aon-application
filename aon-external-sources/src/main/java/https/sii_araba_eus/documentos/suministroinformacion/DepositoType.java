
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DepositoType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DepositoType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Depositario" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaType" minOccurs="0"/&gt;
 *         &lt;element name="DireccionAlmacen" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}TextMax120Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DepositoType", propOrder = {
    "depositario",
    "direccionAlmacen"
})
public class DepositoType {

    @XmlElement(name = "Depositario")
    protected PersonaFisicaJuridicaType depositario;
    @XmlElement(name = "DireccionAlmacen", required = true)
    protected String direccionAlmacen;

    /**
     * Obtiene el valor de la propiedad depositario.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public PersonaFisicaJuridicaType getDepositario() {
        return depositario;
    }

    /**
     * Define el valor de la propiedad depositario.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaType }
     *     
     */
    public void setDepositario(PersonaFisicaJuridicaType value) {
        this.depositario = value;
    }

    /**
     * Obtiene el valor de la propiedad direccionAlmacen.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDireccionAlmacen() {
        return direccionAlmacen;
    }

    /**
     * Define el valor de la propiedad direccionAlmacen.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDireccionAlmacen(String value) {
        this.direccionAlmacen = value;
    }

}
