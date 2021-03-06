
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.respuestaconsultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion.PersonaFisicaJuridicaUnicaESType;


/**
 * <p>Clase Java para RegistroRespuestaConsultaFactInformadasAgrupadasProveedorType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroRespuestaConsultaFactInformadasAgrupadasProveedorType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Proveedor" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}PersonaFisicaJuridicaUnicaESType"/&gt;
 *         &lt;element name="NumeroFacturas" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}Tipo10Type"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroRespuestaConsultaFactInformadasAgrupadasProveedorType", propOrder = {
    "proveedor",
    "numeroFacturas"
})
public class RegistroRespuestaConsultaFactInformadasAgrupadasProveedorType {

    @XmlElement(name = "Proveedor", required = true)
    protected PersonaFisicaJuridicaUnicaESType proveedor;
    @XmlElement(name = "NumeroFacturas", required = true)
    protected String numeroFacturas;

    /**
     * Obtiene el valor de la propiedad proveedor.
     * 
     * @return
     *     possible object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public PersonaFisicaJuridicaUnicaESType getProveedor() {
        return proveedor;
    }

    /**
     * Define el valor de la propiedad proveedor.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonaFisicaJuridicaUnicaESType }
     *     
     */
    public void setProveedor(PersonaFisicaJuridicaUnicaESType value) {
        this.proveedor = value;
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
