import { MSG, CSS, EVENT, TAG, APPPARAMS } from 'aonsolutions/environments/environments.js'; 
import { getApplicationParameters} from 'aonsolutions/services/applicationParameterService.js';
import { AonSuiteMenu } from '../aon-suite-menu.js';
import * as GWT from 'aonsolutions/gwt/gwt.js';

export class AonFiscalMenu extends AonSuiteMenu {

    AON_MENU;
    AON_HEADER;
    ROOT_PANEL;
    RIGHT_PANEL;
    APP_PARAMS;
    isAlava;
    isBizk;
    isGipu;
    isNavarra;
    isAEAT;

    constructor () {
        super();
    }

    async connectedCallback() {
        this.clear();
        await this.getAppParams(); // Esperar a que se resuelva getAppParams
        this.comercialInitialize(); // Llamar a comercialInitialize después de obtener los parámetros
        this.initialize();
        this.build();
        this.setTitle("Opciones fiscales");
    }
    
    async getAppParams() {
        let response = 9999;
        this.isAlava = false;
        this.isBizk = false;
        this.isGipu = false;
        this.isNavarra = false;
        this.isAEAT = false;
        try {
            const params = await getApplicationParameters({
                params: [APPPARAMS.FS_DEFAULT_ADMINISTRATION]
            });
    
            params
                .filter(p => p.value)
                .forEach(p => {
                    response = p.value;
                    alert(response);
                    if (response == 0) this.isAlava = true;
                    if (response == 1) this.isBizk = true;
                    if (response == 2) this.isGipu = true;
                    if (response == 3) this.isNavarra = true;
                    if (response == 4) this.isAEAT = true;
                });
        } catch (e) {
            console.log("error getAppParams", e);
        }
    }
       

    comercialInitialize() {
        alert("Álava "+ this.isAlava);
        alert("Bizkaia "+ this.isBizk);
        alert("Gipu "+ this.isGipu);
        alert("Navarra "+this.isNavarra);
        alert("AEAT "+this.isAEAT);
        if(this.isAlava == false && this.isBizk == false && this.isGipu == false && this.isNavarra == false && this.isAEAT == false)
            this.allFalse = true;
        else
            this.allFalse = false;
        this.AON_HEADER = 'aonHeader';
        this.ROOT_PANEL = 'rootPanel';
        this.RIGHT_PANEL = 'rightPanel';
        this.last = "Matriz fiscal";
        this.new = "Nueva Acción";
        this.cardData={
            title: "Facturas",
            info:["con IRPF profesional", "con IRPF alquiler", "Intracomunitarias", "Extracomunitarias"]
        };
        this.selectOptions= [{
            title: "Modelo 303",
            action : () => GWT.load(GWT.MODEL_303)
        },{
            title: "Modelo 111",
            action : () => GWT.load(GWT.MODEL_111)
        },{
            title: "Modelo 115",
            action : () => GWT.load(GWT.MODEL_115)
        }];
        this.options = [{
            title: 'IVA AEAT',
            visible: this.isAEAT,
            options: [ {
                description: "Modelo 303 ",
                title: "IVA Autoliquidación",
                action: () => GWT.load(GWT.MODEL_303)
            },{
                description: "Modelo 349 ",
                title: "Declaración recapitulativas de operaciones intracomunitarias",
                action: () => GWT.load(GWT.MODEL_349)
            },{
                description: "Modelo 390 ",
                title: "Declaración resumen anual IVA",
                action: () => GWT.load(GWT.MODEL_390)
            },{
                description: "Modelo 347 ",
                title: "Declaración anual operaciones con terceras personas",
                action: () => GWT.load(GWT.MODEL_347)
            },{
                description: "Declaración SII ",
                title: "Suministro Inmediato de Información",
                action: () => GWT.load(GWT.MODEL_SII)
            }]
        },{
            title: 'IRPF AEAT',
            visible: this.isAEAT,
            options: [{
                description: "Modelo 111 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, permios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => GWT.load(GWT.MODEL_111)
            },{
                description: "Modelo 115 ",
                title: "Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos",
                action: () => GWT.load(GWT.MODEL_115)
            },{
                description: "Modelo 123 ",
                title: "Retención e ingreso a cuenta sobre determinadas rendimientos del capital mobiliario o determinadas rentas",
                action: () => GWT.load(GWT.MODEL_123)
            },{
                description: "Modelo 180 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos",
                action: () => GWT.load(GWT.MODEL_180)
            },{
                description: "Modelo 184 ",
                title: "Declaración anual. Entidades en régimen de atribución de rentas",
                action: () => GWT.load(GWT.MODEL_184)
            },{
                description: "Modelo 190 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta",
                action: () => GWT.load(GWT.MODEL_190)
            },{
                description: "Modelo 193 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientosdel capital mobiliario, IS e IRNR sobre determinadas rentas",
                action: () =>  GWT.load(GWT.MODEL_193)
            }]
        },{
            title: 'AEAT',
            visible: this.isAEAT,
            options: [{
                description: "Modelo 130 ",
                description2: " |Profes./Empresar.",
                title: "IRPF. Pago fraccionado. Empresarios y profesionales en estimación directa",
                action: () => GWT.load(GWT.MODEL_130)
            },{
                description: "Modelo 131 ",
                description2: " |Profes./Empresar.",
                title: "Pago fraccionado. Empresarios y profesionales en estimación objetiva",
                action: () => GWT.load(GWT.MODEL_131)
            },{
                description: "Modelo 200 ",
                description2: " |Sociedades",
                title: "Impuesto sobre Sociedades",
                action: () => GWT.load(GWT.MODEL_200)
            },{
                description: "Modelo 202 ",
                description2: " |Sociedades",
                title: "Impuesto Sociedades. Pago fraccionado",
                action: () => GWT.load(GWT.MODEL_202)
            }]
        },{
            title: 'IVA Forales',
            visible: this.isAlava || this.isGipu || this.isBizk,
            options: [{
                description: "Modelo 303 ",
                description2: " |320 Gipuzkoa",
                title: "IVA. Autoliquidación",
                action: () => GWT.load(GWT.MODEL_303)
            },{
                description: "Modelo 349 ",
                tite: "Declaración recapitulativa de operaciones intracomunitarias",
                action: () => GWT.load(GWT.MODEL_349)
            },{
                description: "Modelo 390 ",
                title: "Declaración resume anual IVA",
                action: () => GWT.load(GWT.MODEL_390)
            },{
                description: "Modelo 347 ",
                title: "Declaración anual operaciones con terceras personas",
                action: () => GWT.load(GWT.MODEL_347)
            },{
                description: "Declaración SII ",
                title: "Suministro Inmediato de Información",
                action: () => GWT.load(GWT.MODEL_SII)
            }]
        },{
            title: 'IRPF Forales',
            visible: this.isAlava || this.isGipu || this.isBizk,
            options: [{
                description: "Modelo 110/111 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y de actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => GWT.load(GWT.MODEL_111)
            },{
                description: "Modelo 115 ",
                tite: "Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanes",
                action: () => GWT.load(GWT.MODEL_115)
            },{
                description: "Modelo 123 ",
                title: "Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas",
                action: () => GWT.load(GWT.MODEL_123)
            },{
                description: "Modelo 180 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos procedentes del arrendamiento de inmuebles Urbanos",
                action: () => GWT.load(GWT.MODEL_180)
            },{
                description: "Modelo 184 ",
                title: "Declaración anual. Entidades en régimen de atribución de rentas",
                action: () => GWT.load(GWT.MODEL_184)
            },{
                description: "Modelo 190 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta",
                action: () => GWT.load(GWT.MODEL_190)
            },{
                description: "Modelo 193 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas",
                action: () => GWT.load(GWT.MODEL_193)
            }]
        },{
            title: 'LROE Bizkaia',
            visible: this.isBizk,
            options: [{
                description: "Modelo 140 ",
                title: "Libro-registro de operaciones económicas de personas físicas",
                action: () => GWT.load(GWT.MODEL_140)
            },{
                description: "Modelo 240 ",
                tite: "Libro-registro de operaciones económicas de sociedades",
                action: () => GWT.load(GWT.MODEL_240)
            }]
        },{
            title: 'IVA Navarra',
            visible: this.isNavarra,
            options: [{
                description: "Modelo F69 ",
                title: "Autoliquidación",
                action: () => GWT.load(GWT.MODEL_303)
            },{
                description: "Modelo 349 ",
                tite: "Declaración recapitulativa de operaciones intracomunitarias",
                action: () => GWT.load(GWT.MODEL_349)
            },{
                description: "Modelo 111 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => GWT.load(GWT.MODEL_111)
            },{
                description: "Modelo 347 ",
                title: "Declaración anual operaciones con terceras personas",
                action: () => GWT.load(GWT.MODEL_347)
            },{
                description: "Declaración SII ",
                title: "Suministro Inmediato de Información",
                action: () => GWT.load(GWT.MODEL_SII)
            }]
        },{
            title: 'IRPF Navarra',
            visible: this.isNavarra,
            options: [{
                description: "Modelo 745/715 ",
                title: "Retenciones e ingresos a cuenta sobre rendimientos del trabajo y actividades económicas, premios y determinadas ganancias patrimoniales e imputaciones de renta",
                action: () => GWT.load(GWT.MODEL_110)
            },{
                description: "Modelo 759/760 ",
                tite: "Retenciones e ingresos a cuenta sobre determinadas rentas o rendimientos procedentes del arrendamiento de inmuebles urbanos",
                action: () => GWT.load(GWT.MODEL_115)
            },{
                description: "Modelo 716 ",
                title: "Retención e ingreso a cuenta sobre determinados rendimientos del capital mobiliario o determinadas rentas",
                action: () => GWT.load(GWT.MODEL_123)
            },{
                description: "Modelo 180 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimiento procedentes de arrendamiento de inmuebles Urbanos",
                action: () => GWT.load(GWT.MODEL_180)
            },{
                description: "Modelo 184 ",
                title: "Declaración anual. Entidades en régmien de atribución de rentas",
                action: () => GWT.load(GWT.MODEL_184)
            },{
                description: "Modelo 190 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del trabajo de determinadas actividades económicas, premios y determinadas imputaciones de renta",
                action: () => GWT.load(GWT.MODEL_190)
            },{
                description: "Modelo 193 ",
                title: "Resumen anual de retenciones e ingresos a cuenta. Rendimientos del capital mobiliario, IS e IRNR sobre determinadas rentas",
                action: () => GWT.load(GWT.MODEL_193)
            }]
        }];
    }
    /*
    build() {
        
    }
    */
}
if(!window.customElements.get(TAG.AON_FISCAL_MENU)){
    window.customElements.define(TAG.AON_FISCAL_MENU, AonFiscalMenu);
}