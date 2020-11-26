import {AonElement} from '../../components/AonElement.js';
import {serializeForm} from '../../services/utils.js'
import {getPersonas, getWorkplaceCCCs, getConvenios, getTipoContrato, getOcupacion, getGrupoCotizacion, postAltaDirecta, getTipoJornada, getIpfxnaf, getNafxipf} from '../../services/service.js'
import '../../components/aon-card.js';
import '../../components/aon-input.js';
import '../../components/aon-number.js';
import '../../components/aon-date.js';
import '../../components/aon-suggestion.js';
import '../../components/aon-toast.js';
import '../../components/aon-select.js';

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
                position: relative;
                display: -ms-flexbox;
                display: flex;
                -ms-flex-direction: column;
                flex-direction: column;
                min-width: 0;
                word-wrap: break-word;
                background-color: #fff;
                background-clip: border-box;
                border: 1px solid rgba(0,0,0,.125);
                border-radius: .25rem;
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
                            <button class="aonButton" type="button" id="${this.ID}Submit">Comunicar</button>
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
            <div class="aonCol-sm-12 aonCol-md-4">
                <aon-select name="centro_trabajo" id="centro_trabajo" title="Centro de trabajo"></aon-select>
            </div>
            <div class="aonCol-sm-12 aonCol-md-4">
                <aon-select name="ctaCti" id="ctaCti" title="Cuenta de cotización"></aon-select>
                <aon-input name="regimen" id="regimen" description="Regimen" visible="false"></aon-input>
            </div>
            <div class="aonCol-sm-12 aonCol-md-4">
                <aon-select name="convenio" id="convenio" title="Convenio" type="list"></aon-select>
            </div>
        `);

        let aonTrabajadorCard = this.getElement(`${this.ID}TrabajadorCard`);
        aonTrabajadorCard.setContentHTML(`
            <div class="aonCol-sm-12 aonCol-md-6">
                <aon-input name="nss" id="nss" description="Número de afiliación" type="number" pattern="^[0-9]{1,12}$"></aon-input>
            </div>
            <div class="aonCol-sm-12 aonCol-md-6">
                <div id="${this.ID}DivDni"></div>
            </div>
            <div class="aonCol-sm-12">
                <aon-input name="nombre" id="nombre" description="Nombre" type="text"></aon-input>
            </div>
            <div id="div_apellidos" hidden>
                <div class="aonCol-sm-12 aonCol-md-6">
                    <aon-input name="apellido1" id="apellido1" description="1er Apellido" type="text"></aon-input>
                </div>
                <div class="aonCol-sm-12 aonCol-md-6">
                    <aon-input name="apellido2" id="apellido2" description="2do Apellido" type="text"></aon-input>
                </div>
            </div>
        `);

        let aonContratoCard = this.getElement(`${this.ID}ContratoCard`);
        aonContratoCard.setContentHTML(`
            <div class="aonCol-sm-12 aonCol-md-6">
                <aon-select name="type_cto" id="type_cto" title="Tipo de contrato"></aon-select>
            </div>
            <div class="aonCol-sm-12 aonCol-md-6">
                <aon-date name="fecha" id="fecha" title="Fecha inicio"></aon-date>
            </div>
            <div class="aonCol-sm-12 aonCol-md-6">
                <aon-select name="grup_ctz" id="grup_ctz" title="Grupo de cotización"></aon-select>
            </div>
            <div class="aonCol-sm-12 aonCol-md-6">
                <aon-select name="ocupacion" id="ocupacion" title="Ocupación" ></aon-select>
            </div>
            <div class="aonCol-sm-12 aonCol-md-12" id="div_parcial" hidden>
                <div class="aonCol-sm-3">
                    <aon-select id="tipo_jornada" title="Tipo de jornada"></aon-select>
                </div>
                <div class="aonCol-sm-3">
                    <aon-number id="horas_convenio" description="Horas convenio" format="true" decimals="2"></aon-number>
                </div>
                <div class="aonCol-sm-3">
                    <aon-number id="horas" description="Horas" format="true" decimals="2"></aon-number>
                </div>
                <div class="aonCol-sm-3">
                    <aon-number name="coefparcial" id="coefparcial" description="Coeficiente Parcial"></aon-number>
                </div>
            </div>
            <aon-input name="situation" id="situation" description="Situacion" value="AL" visible="false"></aon-input>
        `);

        let aonAltaDirectaSubmit = this.getElement(`${this.ID}Submit`);
        aonAltaDirectaSubmit.addEventListener('click',()=>this.formSubmit());

        let centro_trabajo = this.getElement('centro_trabajo');
        centro_trabajo.addEventListener('select', (e)=> this.listCuentaCotizacion(e));

        let ctaCti = this.getElement('ctaCti');
        ctaCti.addEventListener('select', ({detail})=> {
            this.getElement('regimen').setAttribute('value', detail.cccRegimeCode);
        });   

        let nss = this.getElement('nssInput');
        nss.addEventListener('blur', (e)=> this.getIpf(e));

        let aonAltaDirectaDni = this.getElement(`${this.ID}DivDni`);
        aonAltaDirectaDni.innerHTML = `<aon-suggestion id="${this.ID}Dni" title="DNI/NIE" name="ipf"></aon-suggestion>`;

        let type_cto = this.getElement('type_cto');
        type_cto.addEventListener('select', (e)=> this.selectTipoContrato(e));

        let tipo_jornada = this.getElement('tipo_jornada');
        tipo_jornada.addEventListener('select', (e)=> this.selectTipojornada(e));

        let horas_convenio = this.getElement('horas_convenio');
        horas_convenio.addEventListener('select', () => this.calculoCoef());

        let horas = this.getElement('horas');
        horas.addEventListener('blur', ()=> {
            this.calculoCoef();
        });

        let coefparcial = this.getElement('coefparcial');
        coefparcial.addEventListener('blur', (e) => this.calculoHoras());

        let apellido1 = this.getElement('apellido1');
        apellido1.addEventListener('blur', (e) => this.getNaf());

        let apellido2 = this.getElement('apellido2');
        apellido2.addEventListener('blur', (e) => this.getNaf());

        this.startFunctions();
    }

    startFunctions(){
        this.listCentroTrabajo();
        this.suggestionDni();
        this.listTipoContrato();
        this.listTipoJornada();
        this.listGrupoCotizacion();
        this.listOcupacion();
        this.listConvenios();
        // this.testFieldValues();
    }

    selectTipojornada(e){
        let {detail:{value}} = e;
        let hr = 0;
        if("semanal" == value) hr = 40;
        else if("diaria" == value) hr = 8;
        this.getElement("horas_convenio").value = hr;
        this.calculoCoef();
    }

    selectTipoContrato(e){
        let {detail:{tipo_jornada}} = e;
        tipo_jornada = parseInt(tipo_jornada);
        let coefparcial= this.getElement('coefparcial');
        coefparcial.value = '';
        let div_parcial = this.getElement('div_parcial');
        if(tipo_jornada){
            //si es parcial
            div_parcial.hidden =  false;
            coefparcial.required = true;
        }  else {
            div_parcial.hidden = true;
            coefparcial.required = false;
        }

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
                this.getElement('nss').setAttribute('value', detail.nss);
                this.getElement('nombre').setAttribute('value', detail.nombre);
                this.getElement('apellido1').setAttribute('value', detail.last_name1);
                this.getElement('apellido2').setAttribute('value', detail.last_name2);
            }
		});
    }

    async listCentroTrabajo(){
        let centro_trabajo = this.getElement('centro_trabajo');
        try {
            const resp = await getWorkplaceCCCs();
            let centros = resp.filter(r=>r.ccc && r.ccc.length > 0);
            centro_trabajo.options = JSON.stringify(
                centros.map((r,index)=> {
                    return {
                        ...r,
                        name: `${r.workplace.description}`,
                        value: index
                    }
                })
            );
        } catch (error) {}
    }

    async listCuentaCotizacion(e){
        let {detail:{ccc:cccs}} = e;
        let ctaCti = this.getElement('ctaCti');
        try {
            ctaCti.options = JSON.stringify(
                cccs.map(r=> {
                    return {
                        ...r,
                        name: `${r.cccRegimeCode} - ${r.ccc}`,
                        value: r.ccc
                    }
                })
            );
        } catch (error) {}
    }

    async listTipoContrato(){
        let type_cto = this.getElement('type_cto');
        try {
            const resp = await getTipoContrato();
            type_cto.options = JSON.stringify(
                resp.map(r=> {
                    return {
                        ...r,
                        name: `${r.value} - ${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) {}
    }

    async listTipoJornada(){
        let tipo_jornada = this.getElement('tipo_jornada');
        try {
            const resp = await getTipoJornada();
            tipo_jornada.options = JSON.stringify(
                resp.map(r=> {
                    return {
                        ...r,
                        name: `${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) {}
    }
  
    async listConvenios(){
        let convenio = this.getElement('convenio');
        try {
            const resp = await getConvenios();
            convenio.options = JSON.stringify(
                resp.map(r=> {
                    return {
                        ...r,
                        name: `${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) {}
    }

    async listGrupoCotizacion(){
        let grup_ctz = this.getElement('grup_ctz');
        try {
            const resp = await getGrupoCotizacion();
            grup_ctz.options = JSON.stringify(
                resp.map(r=> {
                    return {
                        ...r,
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

    calculoCoef(){
        let horas_convenio = this.getElement('horas_convenio').value;
        let horas = this.getElement('horas').value ;
        let coef = '';
        if(horas_convenio > 0 && horas > 0){
            let calc =  Math.round( parseFloat( (parseFloat(horas) / parseFloat(horas_convenio) ) *  100) );
            if(calc > 0 && calc <=99.99) coef = calc.toString().padStart(3, "0"); 
        }
        this.getElement('coefparcial').value = coef;
    }

    calculoHoras(){
        let horas_convenio = this.getElement('horas_convenio').value;
        let coef =  this.getElement('coefparcial').value;
        let horas = this.getElement('horas');

        if(horas_convenio > 0 && coef > 0  && coef <=99.99){
            horas.value = (parseInt(coef) / 100) * horas_convenio;
        }
    }

    async getIpf({target}){
        let nss = target.value;
        let div_apellidos = this.getElement('div_apellidos');
        let nombre = this.getElement('nombre');
        let dni = this.getElement('aonAltaDirectaDni');
        dni.setAttribute('value', "");
        nombre.setAttribute('value', "");
        
        if( nss.length > 9 ){
            try {
                const resp = await getIpfxnaf({nss});
                if(resp.length){
                    let [datos] = resp;
                    this.getElement('nss').setAttribute('value', datos.nss);
                    dni.setAttribute('value', datos.ipf.toString().substring(1));
                    nombre.setAttribute('value', datos.name);
                    div_apellidos.hidden = true;
                } else {
                    div_apellidos.hidden = false;
                }
            } catch (error) {
                div_apellidos.hidden = false;
            }
        }
    }


    async formSubmit(){
        let aonAltaDirectaForm = this.getElement( `${this.ID}Form`);
        let formJson = serializeForm(aonAltaDirectaForm);
        let toast = this.getElement(`${this.ID}Toast`);
        try {
            const resp = await postAltaDirecta(formJson);
            toast.start({message:'Alta procesada!', type: 'success', delay:3000});
        } catch (error) {
            toast.start({message:error, type: 'error'});
        }
    }

    async getNaf(){
        let ipf = this.getElement('aonAltaDirectaDni');
        let apellido1 = this.getElement('apellido1');
        let apellido2 = this.getElement('apellido2');
        if( ipf && ipf.value && apellido1 ){
            try {
                const resp = await getNafxipf({ipf:ipf.value, apellido1: apellido1.value, apellido2:apellido2.value});
                if(resp.length){
                    this.getElement('nss').setAttribute('value', resp);
                }
            } catch (error) {}
        }
    }

    testFieldValues(){
        let regimen =  "0111";
        this.getElement('regimen').setAttribute('value', regimen);
        
        let ctaCti = "01105360062";
        this.getElement('ctaCti').setAttribute('value',ctaCti);
       
        let nss = "010022757387";
        this.getElement('nss').setAttribute('value',nss);

        let ipf = "16262835H";
        this.getElement('aonAltaDirectaDni').setAttribute('value',ipf);
        
        //second screen
        let fecha = "28-12-2020";
        this.getElement('fecha').setAttribute('value', fecha);

        let convenio = "99001355011983";
        this.getElement('convenio').setAttribute('value', convenio);

        let grup_ctz = "03";
        this.getElement('grup_ctz').setAttribute('value', grup_ctz);
       
        let type_cto = "402";   
        this.getElement('type_cto').setAttribute('value', type_cto);
    }

    
}
window.customElements.define('aon-alta-directa', AonAltaDirecta);
