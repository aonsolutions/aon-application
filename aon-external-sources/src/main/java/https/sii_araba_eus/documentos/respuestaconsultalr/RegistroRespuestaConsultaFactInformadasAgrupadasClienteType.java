
package https.sii_araba_eus.documentos.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministroinformacion.PersonaFisicaJuridicaUnicaESType;


/**
 * <p>Clase Java para RegistroRespuestaConsultaFactInformadasAgrupadasClienteType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaFactInformadasAgrupadasClienteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cliente" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaUnicaESType"/&gt;
 *         &lt;element name="NumeroFacturas" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}Tipo10Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaFactInformadasAgrupadasClienteType", propOrder = {
    "cliente",
    "numeroFacturas"
})
public class RegistroRespuestaConsultaFactInformadasAgrupadasClienteType {

    @XmlElement(name = "Cliente", required = true)
    protected PersonaFisicaJuridicaUnicaESType cliente;
    @XmlElement(name = "NumeroFacturas", required = true)
    protected String numeroFacturas;

    /**
     * Obtiene el valor de la propiedad cliente.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public PersonaFisicaJuridicaUnicaESType getCliente() {
        return cliente;
    }

    /**
     * Define el valor de la propiedad cliente.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public void setCliente(PersonaFisicaJuridicaUnicaESType value) {
        this.cliente = value;
    }

    /**
     * Obtiene el valor de la propiedad numeroFacturas.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNumeroFacturas() {
        return numeroFacturas;
    }

    /**
     * Define el valor de la propiedad numeroFacturas.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNumeroFacturas(String value) {
        this.numeroFacturas = value;
    }

}
