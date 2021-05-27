
package eus.bizkaia.ogasuna.sii.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRAgenciasViajes;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRBienesInversion;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRCobrosMetalico;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRDetOperacionIntracomunitaria;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRFacturasEmitidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLRFacturasRecibidas;
import eus.bizkaia.ogasuna.sii.documentos.suministrolr.BajaLROperacionesSeguros;


/**
 *  Sii - Suministro Inmediato de Información, compuesto por datos 
 * 			                              de contexto y una secuencia de 1 o más registros. 
 * 
 * <p>Clase Java para SuministroInformacionBaja complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="SuministroInformacionBaja"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="Cabecera" type="{http://www.bizkaia.eus/ogasuna/sii/documentos/SuministroInformacion.xsd}CabeceraSiiBaja"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SuministroInformacionBaja", propOrder = {
    "cabecera"
})
@XmlSeeAlso({
    BajaLRDetOperacionIntracomunitaria.class,
    BajaLROperacionesSeguros.class,
    BajaLRCobrosMetalico.class,
    BajaLRAgenciasViajes.class,
    BajaLRBienesInversion.class,
    BajaLRFacturasRecibidas.class,
    BajaLRFacturasEmitidas.class
})
public class SuministroInformacionBaja {

    @XmlElement(name = "Cabecera", required = true)
    protected CabeceraSiiBaja cabecera;

    /**
     * Obtiene el valor de la propiedad cabecera.
     * 
     * @return
     *     possible object is
     *     {@link CabeceraSiiBaja }
     *     
     */
    public CabeceraSiiBaja getCabecera() {
        return cabecera;
    }

    /**
     * Define el valor de la propiedad cabecera.
     * 
     * @param value
     *     allowed object is
     *     {@link CabeceraSiiBaja }
     *     
     */
    public void setCabecera(CabeceraSiiBaja value) {
        this.cabecera = value;
    }

}
