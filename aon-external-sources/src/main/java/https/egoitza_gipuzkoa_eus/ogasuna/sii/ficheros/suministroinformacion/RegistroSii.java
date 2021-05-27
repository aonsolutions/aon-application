
package https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRFiltroAgenciasViajesType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRFiltroBienInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRFiltroCobrosMetalicoType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRFiltroDetOperIntracomunitariasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRFiltroEmitidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRFiltroOperacionesSegurosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.consultalr.LRFiltroRecibidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRAgenciasViajesType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRBajaAgenciasViajesType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRBajaBienesInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRBajaCobrosMetalicoType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRBajaExpedidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRBajaOperacionIntracomunitariaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRBajaRecibidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRBajaRegistroLROperacionesSegurosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRBienesInversionType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRCobrosMetalicoType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRFacturasRecibidasType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LROperacionIntracomunitariaType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LROperacionesSegurosType;
import https.egoitza_gipuzkoa_eus.ogasuna.sii.ficheros.suministrolr.LRfacturasEmitidasType;


/**
 *  Información básica que contienen los registros del suministro de información 
 * 
 * <p>Clase Java para RegistroSii complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="RegistroSii"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PeriodoLiquidacion"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *                 &lt;sequence&gt;
 *                   &lt;element name="Ejercicio" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}YearType"/&gt;
 *                   &lt;element name="Periodo" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/restriction&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistroSii", propOrder = {
    "periodoLiquidacion"
})
@XmlSeeAlso({
    LRfacturasEmitidasType.class,
    LRBajaExpedidasType.class,
    LRFacturasRecibidasType.class,
    LRBajaRecibidasType.class,
    LRBienesInversionType.class,
    LRBajaBienesInversionType.class,
    LRAgenciasViajesType.class,
    LRBajaAgenciasViajesType.class,
    LRCobrosMetalicoType.class,
    LRBajaCobrosMetalicoType.class,
    LROperacionesSegurosType.class,
    LRBajaRegistroLROperacionesSegurosType.class,
    LROperacionIntracomunitariaType.class,
    LRBajaOperacionIntracomunitariaType.class,
    LRFiltroEmitidasType.class,
    LRFiltroRecibidasType.class,
    LRFiltroBienInversionType.class,
    LRFiltroDetOperIntracomunitariasType.class,
    LRFiltroOperacionesSegurosType.class,
    LRFiltroCobrosMetalicoType.class,
    LRFiltroAgenciasViajesType.class
})
public class RegistroSii {

    @XmlElement(name = "PeriodoLiquidacion", required = true)
    protected RegistroSii.PeriodoLiquidacion periodoLiquidacion;

    /**
     * Obtiene el valor de la propiedad periodoLiquidacion.
     * 
     * @return
     *     possible object is
     *     {@link RegistroSii.PeriodoLiquidacion }
     *     
     */
    public RegistroSii.PeriodoLiquidacion getPeriodoLiquidacion() {
        return periodoLiquidacion;
    }

    /**
     * Define el valor de la propiedad periodoLiquidacion.
     * 
     * @param value
     *     allowed object is
     *     {@link RegistroSii.PeriodoLiquidacion }
     *     
     */
    public void setPeriodoLiquidacion(RegistroSii.PeriodoLiquidacion value) {
        this.periodoLiquidacion = value;
    }


    /**
     *  Período al que corresponden los apuntes. todos los apuntes deben corresponder al mismo período impositivo 
     * 
     * <p>Clase Java para anonymous complex type.
     * 
     * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
     *       &lt;sequence&gt;
     *         &lt;element name="Ejercicio" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}YearType"/&gt;
     *         &lt;element name="Periodo" type="{https://egoitza.gipuzkoa.eus/ogasuna/sii/ficheros/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
     *       &lt;/sequence&gt;
     *     &lt;/restriction&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "ejercicio",
        "periodo"
    })
    public static class PeriodoLiquidacion {

        @XmlElement(name = "Ejercicio", required = true)
        protected String ejercicio;
        @XmlElement(name = "Periodo", required = true)
        protected String periodo;

        /**
         * Obtiene el valor de la propiedad ejercicio.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getEjercicio() {
            return ejercicio;
        }

        /**
         * Define el valor de la propiedad ejercicio.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setEjercicio(String value) {
            this.ejercicio = value;
        }

        /**
         * Obtiene el valor de la propiedad periodo.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getPeriodo() {
            return periodo;
        }

        /**
         * Define el valor de la propiedad periodo.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setPeriodo(String value) {
            this.periodo = value;
        }

    }

}
