
package eus.bizkaia.ogasuna.sii.documentos.consultalr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.PersonaFisicaJuridicaUnicaESType;
import eus.bizkaia.ogasuna.sii.documentos.suministroinformacion.RegistroSiiImputacion;


/**
 * <p>Clase Java para LRFiltroFactInformadasAgrupadasClienteType complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="LRFiltroFactInformadasAgrupadasClienteType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}RegistroSiiImputacion"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cliente" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}PersonaFisicaJuridicaUnicaESType" minOccurs="0"/&gt;
 *         &lt;element name="EstadoCuadre" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}EstadoCuadreImputacionType" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LRFiltroFactInformadasAgrupadasClienteType", propOrder = {
    "cliente",
    "estadoCuadre"
})
public class LRFiltroFactInformadasAgrupadasClienteType
    extends RegistroSiiImputacion
{

    @XmlElement(name = "Cliente")
    protected PersonaFisicaJuridicaUnicaESType cliente;
    @XmlElement(name = "EstadoCuadre")
    protected String estadoCuadre;

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
     * Obtiene el valor de la propiedad estadoCuadre.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEstadoCuadre() {
        return estadoCuadre;
    }

    /**
     * Define el valor de la propiedad estadoCuadre.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEstadoCuadre(String value) {
        this.estadoCuadre = value;
    }

}
