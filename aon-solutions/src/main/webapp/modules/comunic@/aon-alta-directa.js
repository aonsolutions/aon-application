import { AonElement } from '../../components/AonElement.js';
import { INPUTS_ALL } from '../../environments/constants.js';

import { getPersonas, getWorkplaceCCCs, getConvenios, getTipoContrato, getOcupacion, getGrupoCotizacion, postAltaDirecta, getTipoJornada, getIpfxnaf, getNafxipf } from '../../services/service.js'
import '../../components/aon-card.js';
import '../../components/aon-input.js';
import '../../components/aon-number.js';
import '../../components/aon-date.js';
import '../../components/aon-suggestion.js';
import '../../components/aon-select.js';
import '../../components/aon-switch.js';
import '../../components/aon-icon-button.js';
import { setValueName, serializeForm } from '../../services/utils.js';



export class AonAltaDirecta extends AonElement {
    ID;
    _contrato;

    static get observedAttributes() {
        return ['data'];
    }

    get id() {
        return this.getAttribute('id');
    }

    set id(id) {
        this.setAttribute('id', id);
    }

    get data() {
        return JSON.parse(this.getAttribute('data'));
    }

    set data(value) {
        this.setAttribute('data', JSON.stringify(value));
    }

    constructor() {
        super();
        this.ID = this.id || 'aonAltaDirecta';
        this.TOOLBAR = this.id + 'Toolbar';
        this._contrato = {};
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if("data" == name && newValue){
            this.parseData(this.data);
        }
    }

    setContrato(name, value) {
        this._contrato[name] = value;
    }

    getContrato() {
        const form = this.getElement(`${this.ID}Form`);
        return serializeForm(form);
    }

    parseData(data) {
        let obj = {
            ...data,
            regimen: data.regime,
            nombre: data.name,
            fecha: data.fra,
            grup_ctz: data.gc
        }
        if (data.ocup) obj['ocupacion'] = data.ocup.toString().toLowerCase();
        if (data.contract) obj['type_cto'] = data.contract;
        for (const property in obj) setValueName(property, obj[property]);

        let ctaCti = this.getElement('ctaCtiInput');
        if (ctaCti) ctaCti.value = obj.regimen + ' - ' + obj.ctaCti;

        let type_cto = document.querySelector('#type_cto > aon-input');
        if(obj.type_cto && type_cto && !type_cto.value) 
            type_cto.value = obj.type_cto;

        //hidden test 
        let toolb = document.querySelector(`#${this.TOOLBAR}`);
        if(toolb) toolb.style.display = "none";

    }

    connectedCallback() {
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
              #searchDniTitle{
                font-size: 12px;
              }
            </style>

            <aon-toolbar id="${this.TOOLBAR}" type="secondary" title="Alta directa"> </aon-toolbar>
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
                        <div class="aonCol-sm-12 offset-5"  id="${this.ID}DivSubmit"></div>
                    </div>
                </div>
            </form>
		`;

        this.build();
    }

    build() {

        let aonComunica = this.getElement('aonComunica');
        aonComunica.removeToolbarOptions();

        let aonEmpresaCard = this.getElement(`${this.ID}EmpresaCard`);
        aonEmpresaCard.setContentHTML(`
            <div class="aonCol-sm-12 aonCol-md-4">
                <aon-select name="centro_trabajo" id="centro_trabajo" title="Centro de trabajo"></aon-select>
            </div>
            <div class="aonCol-sm-12 aonCol-md-4">
                <aon-select name="ctaCti" id="ctaCti" title="Cuenta de cotización"></aon-select>
            </div>
            <div class="aonCol-sm-12 aonCol-md-4">
                <aon-select name="convenio" id="convenio" title="Convenio" type="list"></aon-select>
            </div>
            <aon-input name="regimen" id="regimen" description="regimen" visible="false"></aon-input>
        `);

        let aonTrabajadorCard = this.getElement(`${this.ID}TrabajadorCard`);
        aonTrabajadorCard.setContentHTML(`
            <div class="aonCol-sm-12 aonCol-md-4">
                <aon-switch id="switchDni" title="Buscar por DNI"></aon-switch>
                <div id="${this.ID}Reiniciar" hidden>
                    Reiniciar <aon-icon-button id="${this.ID}IConSearch" icon="cached"> </aon-icon-button>
                </div>
            </div>
            <div class="aonCol-sm-12 aonCol-md-4">
                <div id="${this.ID}DivNss"></div>
            </div>
            <div class="aonCol-sm-12 aonCol-md-4">
                <div id="${this.ID}DivDni"></div>
            </div>
            <div id="div_apellidos" hidden>
                <div class="aonCol-sm-12 aonCol-md-6">
                    <aon-input name="apellido1" id="apellido1" description="1er Apellido" type="text"></aon-input>
                </div>
                <div class="aonCol-sm-11 aonCol-md-5">
                    <aon-input name="apellido2" id="apellido2" description="2do Apellido" type="text"></aon-input>
                </div>
                <div class="aonCol-sm-1 aonCol-md-1">
                    <aon-icon-button id="iconSegSocial" icon="search"> </aon-icon-button>
                </div>
            </div>  
            <div class="aonCol-sm-12">
                <aon-input name="nombre" id="nombre" description="Nombre" type="text" disabled="true"></aon-input>
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
            <aon-input name="situation" id="situation" description="situation" value="AL" visible="false"></aon-input>
        `);


        let aonAltaDirectaSubmit = this.getElement(`${this.ID}Submit`);
        if (aonAltaDirectaSubmit) aonAltaDirectaSubmit.addEventListener('click', () => this.formSubmit());

        let centro_trabajo = this.getElement('centro_trabajo');
        centro_trabajo.addEventListener('change', (e) => this.listCuentaCotizacion(e));

        let ctaCti = this.getElement('ctaCti');
        ctaCti.addEventListener('change', ({ detail }) => {
            this.getElement('regimen').setAttribute('value', detail.cccRegimeCode);
        });


        let aonAltaDirectaDni = this.getElement(`${this.ID}DivDni`);
        aonAltaDirectaDni.innerHTML = `<aon-suggestion id="${this.ID}Dni" title="DNI/NE" name="ipf"></aon-suggestion>`;
        aonAltaDirectaDni.setAttribute('disabled', true);

        let aonAltaDirectaNss = this.getElement(`${this.ID}DivNss`);
        aonAltaDirectaNss.innerHTML = `<aon-suggestion id="${this.ID}Nss" title="NSS/NAF" name="nss"></aon-suggestion>`;

        this.getElement(`${this.ID}Nss`).addEventListener('change', ({target}) => {
            this.comprobarNss(target.value);
        });

        this.getElement('type_cto').addEventListener('change', (e) => this.selectTipoContrato(e));

        this.getElement('tipo_jornada').addEventListener('change', (e) => this.selectTipojornada(e));

        this.getElement('horas_convenio').addEventListener('change', () => this.calculoCoef());

        let horas = this.getElement('horas');
        horas.addEventListener('change', () => {
            this.calculoCoef();
        });

        let switchDni = this.getElement('switchDni');
        switchDni.addEventListener('change', ({ target }) => {
            let div_apellidos = this.getElement('div_apellidos');
            let nss = this.getElement(`${this.ID}Nss`);
            let dni = this.getElement(`${this.ID}Dni`);
            nss.disabled = target.checked;
            dni.disabled = !target.checked;
            dni.value = nss.value = "";
            div_apellidos.hidden = target.checked;
            div_apellidos.hidden = !target.checked;
            nss.removeIcon();
        });

        this.getElement('coefparcial').addEventListener('change', (e) => this.calculoHoras());

        this.getElement('iconSegSocial').addEventListener('click', (e) => this.getNaf());


        this.getElement(`${this.ID}IConSearch`).addEventListener('click', (e) => this.disabledCardTrabajor(false));

        this.getElement(`${this.ID}Dni`).disabled = true;

        let formChangeField = [...this.getElement(`${this.ID}Form`).querySelectorAll(INPUTS_ALL)];
        formChangeField.map(el => {
            el.addEventListener('change', ({ target }) => this.setContrato(target.name, target.value));
            el.addEventListener('keyup', ({ target }) => this.setContrato(target.name, target.value));
        })

        // start list functions
        this.startListFunctions();

        if (!this.isMobile()) this.buildToolbar();
        else {
            this.getElement(`${this.ID}DivSubmit`)
                .innerHTML = `<button class="aonButton" type="button" id="${this.ID}Submit">Comunicar</button>`;
        }

    }
    

    buildToolbar() {
        let toolbar = this.getElement(this.TOOLBAR);
        toolbar.removeButtons();

        toolbar.addButton2({
            id: 'Save',
            name: 'Save',
            icon: 'save'
        }, () => this.formSubmit());

        toolbar.addButton2({
            id: 'Previous',
            name: 'Back',
            icon: 'keyboard_arrow_left'
        }, () => this.back());

        toolbar.addSeparator();
    }

    startListFunctions() {
        this.listCentroTrabajo();
        this.suggestionDni();
        this.listTipoContrato();
        this.listTipoJornada();
        this.listGrupoCotizacion();
        this.listOcupacion();
        this.listConvenios();
        // this.testFieldValues();
    }

    selectTipojornada({ detail }) {
        if (detail) {
            let { value } = detail;
            let hr = 0;
            if ("semanal" == value) hr = 40;
            else if ("diaria" == value) hr = 8;
            this.getElement("horas_convenio").value = hr;
            this.calculoCoef();
        }
    }

    selectTipoContrato({ detail }) {
        if (detail) {
            let { tipo_jornada } = detail;
            tipo_jornada = parseInt(tipo_jornada);
            let coefparcial = this.getElement('coefparcial');
            coefparcial.value = '';
            let div_parcial = this.getElement('div_parcial');
            if (tipo_jornada) {
                //si es parcial
                div_parcial.hidden = false;
                coefparcial.required = true;
            } else {
                div_parcial.hidden = true;
                coefparcial.required = false;
            }
        }
    }

    suggestionDni() {
        let searchSuggestion = this.getElement(`${this.ID}Dni`);
        searchSuggestion.addEventListener('keyup', async ({ target: { value } }) => {
            let dni = value.toString().toUpperCase();
            if (dni.length > 2) {
                const resp = await getPersonas(dni);
                searchSuggestion.buildOptions(resp);
            } else {
                searchSuggestion.closeOptions();
            }
        });
        searchSuggestion.addEventListener('change', ({ detail }) => {
            if (detail) {
                this.getElement('nss').setAttribute('value', detail.nss);
                this.getElement('nombre').setAttribute('value', detail.nombre);
                this.getElement('apellido1').setAttribute('value', detail.last_name1);
                this.getElement('apellido2').setAttribute('value', detail.last_name2);
            }
        });
    }

    async listCentroTrabajo() {
        let centro_trabajo = this.getElement('centro_trabajo');
        try {
            const resp = await getWorkplaceCCCs();
            let centros = resp.filter(r => r.ccc && r.ccc.length > 0);
            centro_trabajo.options = JSON.stringify(
                centros.map((r, index) => {
                    return {
                        ...r,
                        name: `${r.workplace.description}`,
                        value: index
                    }
                })
            );
        } catch (error) { }
    }

    listCuentaCotizacion({ detail }) {
        if (detail) {
            try {
                let { ccc: cccs } = detail;
                let ctaCti = this.getElement('ctaCti');
                ctaCti.options = JSON.stringify(
                    cccs.map(r => {
                        return {
                            ...r,
                            name: `${r.cccRegimeCode} - ${r.ccc}`,
                            value: r.ccc
                        }
                    })
                );
            } catch (error) { }
        }
    }

    async listTipoContrato() {
        let type_cto = this.getElement('type_cto');
        try {
            const resp = await getTipoContrato();
            type_cto.options = JSON.stringify(
                resp.map(r => {
                    return {
                        ...r,
                        name: `${r.value} - ${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) { }
    }

    async listTipoJornada() {
        let tipo_jornada = this.getElement('tipo_jornada');
        try {
            const resp = await getTipoJornada();
            tipo_jornada.options = JSON.stringify(
                resp.map(r => {
                    return {
                        ...r,
                        name: `${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) { }
    }

    async listConvenios() {
        let convenio = this.getElement('convenio');
        try {
            const resp = await getConvenios();
            convenio.options = JSON.stringify(
                resp.map(r => {
                    return {
                        ...r,
                        name: `${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) { }
    }

    async listGrupoCotizacion() {
        let grup_ctz = this.getElement('grup_ctz');
        try {
            const resp = await getGrupoCotizacion();
            grup_ctz.options = JSON.stringify(
                resp.map(r => {
                    return {
                        ...r,
                        name: `${r.value} - ${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) { }
    }

    async listOcupacion() {
        let ocupacion = this.getElement('ocupacion');
        try {
            const resp = await getOcupacion();
            ocupacion.options = JSON.stringify(
                resp.map(r => {
                    return {
                        name: `${r.value} - ${r.name}`,
                        value: r.value
                    }
                })
            );
        } catch (error) { }
    }

    calculoCoef() {
        let horas_convenio = this.getElement('horas_convenio').value;
        let horas = this.getElement('horas').value;
        let coef = '';
        if (horas_convenio > 0 && horas > 0) {
            let calc = Math.round(parseFloat((parseFloat(horas) / parseFloat(horas_convenio)) * 100));
            if (calc > 0 && calc <= 99.99) coef = calc.toString().padStart(3, "0");
        }
        this.getElement('coefparcial').value = coef;
    }

    calculoHoras() {
        let horas_convenio = this.getElement('horas_convenio').value;
        let coef = this.getElement('coefparcial').value;
        let horas = this.getElement('horas');

        if (horas_convenio > 0 && coef > 0 && coef <= 99.99) {
            horas.value = (parseInt(coef) / 100) * horas_convenio;
        }
    }

    async comprobarNss(value) {
        const nss_sugges = this.getElement(`${this.ID}Nss`); //SUGGESTION
        if (value) {
            const last_nss = value.toString().slice(-2);
            let mod = (parseInt(last_nss) % 97).toString();
            let nombre = this.getElement('nombre');
            let dni = this.getElement(`${this.ID}Dni`);
            dni.value = nombre.value = "";
            if (mod < 10) mod = '0' + mod;
            if (value.length > 11 && mod == last_nss) {
                nss_sugges.removeIcon();
                nss_sugges.loading(true);
                await this.getIpf(value);
                nss_sugges.loading(false);
            } else {
                nss_sugges.addIcon("error", "#B42000"); //incorrect
            }
        }
    }

    async getIpf(nss) {
        const resp = await getIpfxnaf({ nss }).then(r => r.length ? r[0] : null).catch(e => null);
        if (resp) {
            setValueName('nombre', resp.name);
            setValueName('ipf', resp.ipf.toString().substring(1));
            setValueName('nss', resp.nss);
            this.disabledCardTrabajor(true);
        }
    }

    async formSubmit() {
        const toast = this.getElement(`aonComunicaToast`);
        try {
            await postAltaDirecta(this.getContrato());
            toast.start({ message: 'Alta procesada!', type: 'success', delay: 3000 });
            this.back();
        } catch (error) {
            toast.start({ message: error, type: 'error' });
        }
    }

    back() {
        this.getElement('aonComunica').setContentHTML(`<aon-movements></aon-movements>`);
    }

    async getNaf() {
        const contrato = this.getContrato();
        const ipf = contrato.ipf;
        const apellido1 = contrato.apellido2;
        const nss_sugges = document.querySelector(`#${this.ID}Nss`); //SUGGESTION
        if (ipf && apellido1) {
            nss_sugges.loading(true);
            try {
                const resp = await getNafxipf(contrato);
                if (resp) {
                    nss_sugges.value = resp.nss;
                    setValueName('nombre', resp.name);
                    this.disabledCardTrabajor(true);
                }
            } catch (error) { }
            nss_sugges.loading(false);
        }
    }

    disabledCardTrabajor(vl) {
        let fields = document.querySelectorAll(`#${this.ID}TrabajadorCard aon-input`);
        let switchDni = this.getElement('switchDni');
        fields.forEach(el => {
            el.disabled = vl;
            if (!vl) el.value = '';
        });
        switchDni.hidden = vl;
        switchDni.checked = false;
        this.getElement(`${this.ID}Reiniciar`).hidden = !vl;
        this.getElement('div_apellidos').hidden = true;
        if (!vl) {
            this.getElement(`${this.ID}Dni`).disabled = true;
            this.getElement(`nombre`).disabled = true;
        }
    }

    disabledForm(form, elems) {
        let elems_disabled = INPUTS_ALL;
        if (elems) elems_disabled = elems + ', ' + INPUTS_ALL;
        [...this.getElement(form).querySelectorAll(elems_disabled)].map(el => {
            el.disabled = true;
        })
    }

    testFieldValues() {
        let regimen = "0111";
        this.getElement('regimen').setAttribute('value', regimen);

        let ctaCti = "01105360062";
        this.getElement('ctaCti').setAttribute('value', ctaCti);

        let nss = "010022757387";
        this.getElement('aonAltaDirectaNss').setAttribute('value', nss);

        let ipf = "016262835H";
        this.getElement('aonAltaDirectaDni').setAttribute('value', ipf);

        //second screen
        let fecha = "2020-12-28";
        this.getElement('fecha').setAttribute('value', fecha);

        let convenio = "60888888888888";
        this.getElement('convenio').setAttribute('value', convenio);

        let grup_ctz = "03";
        this.getElement('grup_ctz').setAttribute('value', grup_ctz);

        let type_cto = "402";
        this.getElement('type_cto').setAttribute('value', type_cto);
    }

}
window.customElements.define('aon-alta-directa', AonAltaDirecta);
