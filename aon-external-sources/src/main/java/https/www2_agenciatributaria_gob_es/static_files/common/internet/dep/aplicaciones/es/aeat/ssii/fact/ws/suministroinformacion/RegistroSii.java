
package https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministroinformacion;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroAgenciasViajesType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroBienInversionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroCobrosMetalicoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroDetOperIntracomunitariasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroEmitidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroOperacionesSegurosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.consultalr.LRFiltroRecibidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRAgenciasViajesType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaAgenciasViajesType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaBienesInversionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaCobrosMetalicoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaExpedidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaOperacionIntracomunitariaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaRecibidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBajaRegistroLROperacionesSegurosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRBienesInversionType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRCobrosMetalicoType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRFacturasRecibidasType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LROperacionIntracomunitariaType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LROperacionesSegurosType;
import https.www2_agenciatributaria_gob_es.static_files.common.internet.dep.aplicaciones.es.aeat.ssii.fact.ws.suministrolr.LRfacturasEmitidasType;


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
 *                   &lt;element name="Ejercicio" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}YearType"/&gt;
 *                   &lt;element name="Periodo" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
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
     *         &lt;element name="Ejercicio" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}YearType"/&gt;
     *         &lt;element name="Periodo" type="{https://www2.agenciatributaria.gob.es/static_files/common/internet/dep/aplicaciones/es/aeat/ssii/fact/ws/SuministroInformacion.xsd}TipoPeriodoType"/&gt;
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
