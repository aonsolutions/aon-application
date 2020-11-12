import {AonElement} from '../../components/AonElement.js';
import {serializeForm} from '../../services/utils.js'
import {getPersonas, getTipoContrato, getCuentaCotizacion, getOcupacion, getGrupoCotizacion, postAltaDirecta} from '../../services/service.js'
import '../../components/aon-card.js';
import '../../components/aon-input.js';
import '../../components/aon-date.js';
import '../../components/aon-suggestion.js';
import '../../components/aon-toast.js';

export class AonAltaDirecta extends AonElement {
    ID;
	constructor () {
        super();
        this.ID = 'aonAltaDirecta';
	}

	connectedCallback () {
        this.innerHTML = `
            <style>
                .aonCard{
                    overflow: auto;
                }
            </style>
            <aon-toast id="${this.ID}Toast"></aon-toast>
            <form id="${this.ID}Form" action="#" onsubmit="return false;">
                <div id="${this.ID}Div">
                    <div class="aonRow">
                        <div class="aonCol-sm-12">
                            <aon-card id="${this.ID}EmpresaCard" title="Datos de la empresa"></aon-card>
                        </div>
                        <div class="aonCol-sm-12 aonCol-md-6">
                            <aon-card id="${this.ID}TrabajadorCard" title="Datos del trabajador"></aon-card>
                        </div>
                        <div class="aonCol-sm-12 aonCol-md-6">
                            <aon-card id="${this.ID}ContratoCard" title="Datos del contrato"></aon-card>
                        </div>
                        <div class="aonCol-sm-12 offset-5">
                            <button type="button" id="${this.ID}Submit">Comunicar</button>
                        </div>
                    </div>
                </div>
            </form>
		`;

        this.build();
 	}

 	build() {
         
        let aonMovements = this.getElement('aonComunica');
        aonMovements.removeToolbarOptions();

        let aonEmpresaCard = this.getElement(`${this.ID}EmpresaCard`);
        aonEmpresaCard.setContentHTML(`
            <div class="aonCol-sm-12 aonCol-md-6">
                <aon-input name="cuenta_cotizacion" id="cuenta_cotizacion" description="Cuenta de cotización" type="list"></aon-input>
            </div> 
            <div class="aonCol-sm-12 aonCol-md-6">
                <aon-input name="convenio" id="convenio" description="Convenio" type="text"value="60888888888888"></aon-input>
            </div> 
            
        `);

        let aonTrabajadorCard = this.getElement(`${this.ID}TrabajadorCard`);
        aonTrabajadorCard.setContentHTML(`
            <div class="aonCol-sm-12">
                <div id="${this.ID}DivDni"></div>
            </div>
            <div class="aonCol-sm-12">
                <aon-input name="naf" id="naf" description="Número de afiliación" type="text"></aon-input>
            </div>
            <div class="aonCol-sm-12">
                <aon-input name="nombre" id="nombre" description="Nombre" type="text"></aon-input>
            </div>
            <div class="aonCol-sm-12">
                <aon-input name="apellido" id="apellido1" description="1er Apellido" type="text"></aon-input>
            </div>
            <div class="aonCol-sm-12">
                <aon-input name="apellido2" id="apellido2" description="2do Apellido" type="text"></aon-input>
            </div>
        `);

        let aonContratoCard = this.getElement(`${this.ID}ContratoCard`);
        aonContratoCard.setContentHTML(`
            <div class="aonCol-sm-12">
                <aon-date name="fecha" id="fecha" title="Fecha"></aon-date>
            </div> 
            <div class="aonCol-sm-12">
                <aon-input name="grupo_cotizacion" id="grupo_cotizacion" description="Grupo de cotización" type="list"></aon-input>
            </div> 
            <div class="aonCol-sm-12">
                <aon-input name="ocupacion" id="ocupacion" description="Ocupación" type="list"></aon-input>
            </div> 
            <div class="aonCol-sm-12">
                <aon-input name="tipo_contrato" id="tipo_contrato" description="Tipo de contrato" type="list"></aon-input>
            </div>
            <div class="aonCol-sm-12">
                <aon-input name="coeficiente_parcial" id="coeficiente_parcial" description="Coeficiente Parcial" type="text"></aon-input>
            </div>

        `);

        let aonAltaDirectaSubmit = this.getElement(`${this.ID}Submit`);
       // aonAltaDirectaSubmit.style.marginLeft = '100%';
        aonAltaDirectaSubmit.addEventListener('click',()=>this.formSubmit());

        let aonAltaDirectaDni = this.getElement(`${this.ID}DivDni`);
        aonAltaDirectaDni.innerHTML = `<aon-suggestion id="${this.ID}Dni" title="DNI/NIE" required="true"></aon-suggestion>`;

        let tipo_contrato = this.getElement('tipo_contrato');
        tipo_contrato.addEventListener('select', this.selectTipoContrato);

        this.startFunctions();

    }

    startFunctions(){
        this.suggestionDni();
        this.listCuentaCotizacion();
        this.listTipoContrato();
        this.listGrupoCotizacion();
        this.listOcupacion();
    }

    selectTipoContrato(e){
        //si el tipo de contrato es parcial se debe llenar el coeficiente parcial

    }

    suggestionDni(){
        let searchSuggestion = this.getElement(`${this.ID}Dni`);
		searchSuggestion.addEventListener('keyup', async({target:{value}}) => {
            let dni = value.toString().toUpperCase();
			if(dni.length > 2) {
                const resp = await getPersonas(dni);
                searchSuggestion.buildOptions(resp);
			} else {
				searchSuggestion.closeOptions();
			}
		});
		searchSuggestion.addEventListener('select', ({detail}) => {
            if(detail){
                this.getElement('naf').setAttribute('value', detail.naf);
                this.getElement('nombre').setAttribute('value', detail.nombre);
                this.getElement('apellido1').setAttribute('value', detail.last_name1);
                this.getElement('apellido2').setAttribute('value', detail.last_name2);
            }
		});
    }

    async listCuentaCotizacion(){
        let cuenta_cotizacion = this.getElement('cuenta_cotizacion');
        try {
            const resp = await getCuentaCotizacion();
            cuenta_cotizacion.options = JSON.stringify(resp);
        } catch (error) {}
    }

    async listTipoContrato(){
        let tipo_contrato = this.getElement('tipo_contrato');
        try {
            const resp = await getTipoContrato();
            tipo_contrato.options = JSON.stringify(
                resp.map(r=> {
                    return {
                        name: `${r.value} - ${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) {}
    }

    async listGrupoCotizacion(){
        let grupo_cotizacion = this.getElement('grupo_cotizacion');
        try {
            const resp = await getGrupoCotizacion()
            grupo_cotizacion.options = JSON.stringify(
                resp.map(r=> {
                    return {
                        name: `${r.value} - ${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) {}
    }

    async listOcupacion(){
        let ocupacion = this.getElement('ocupacion');
        try {
            const resp = await getOcupacion();
            ocupacion.options = JSON.stringify(
                resp.map(r=> {
                    return {
                        name: `${r.value} - ${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) {}
    }

    async formSubmit(){
        let aonAltaDirectaForm = this.getElement( `${this.ID}Form`);
        let formJson = serializeForm(aonAltaDirectaForm);
        let toast = this.getElement(`${this.ID}Toast`);
        try {
            const resp = await postAltaDirecta(formJson);
            toast.start({message:'Alta procesada!', type: 'success', delay:3000});
        } catch (error) {
            toast.start({message:'Alta NO procesada!', type: 'error'});
        }
      
    }
}
window.customElements.define('aon-alta-directa', AonAltaDirecta);
