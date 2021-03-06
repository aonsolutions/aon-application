
package https.sii_araba_eus.documentos.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.sii_araba_eus.documentos.suministrolr.BajaLRAgenciasViajes;
import https.sii_araba_eus.documentos.suministrolr.BajaLRBienesInversion;
import https.sii_araba_eus.documentos.suministrolr.BajaLRCobrosMetalico;
import https.sii_araba_eus.documentos.suministrolr.BajaLRDetOperacionIntracomunitaria;
import https.sii_araba_eus.documentos.suministrolr.BajaLRFacturasEmitidas;
import https.sii_araba_eus.documentos.suministrolr.BajaLRFacturasRecibidas;
import https.sii_araba_eus.documentos.suministrolr.BajaLROperacionesSeguros;
import https.sii_araba_eus.documentos.suministrolr.BajaLRVentaBienesConsigna;


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
 *         &lt;element name="Cabecera" type="{https://sii.araba.eus/documentos/SuministroInformacion.xsd}CabeceraSiiBaja"/&gt;
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
    BajaLRVentaBienesConsigna.class,
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
