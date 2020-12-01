import { AonElement } from '../../components/AonElement.js';
import { serializeForm } from '../../services/utils.js'
import { getPersonas, getWorkplaceCCCs, getConvenios, getTipoContrato, getOcupacion, getGrupoCotizacion, postAltaDirecta, getTipoJornada, getIpfxnaf, getNafxipf } from '../../services/service.js'
import '../../components/aon-card.js';
import '../../components/aon-input.js';
import '../../components/aon-number.js';
import '../../components/aon-date.js';
import '../../components/aon-suggestion.js';
import '../../components/aon-toast.js';
import '../../components/aon-select.js';
import '../../components/aon-switch.js';
import '../../components/aon-icon-button.js';

export class AonAltaDirecta extends AonElement {
    ID;
    constructor() {
        super();
        this.ID = 'aonAltaDirecta';
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
            </div>
            <div class="aonCol-sm-12 aonCol-md-4">
                <aon-select name="convenio" id="convenio" title="Convenio" type="list"></aon-select>
            </div>
            <aon-input name="regimen" id="regimen" description="Regimen" visible="false"></aon-input>
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
        aonAltaDirectaSubmit.addEventListener('click', () => this.formSubmit());

        let centro_trabajo = this.getElement('centro_trabajo');
        centro_trabajo.addEventListener('select', (e) => this.listCuentaCotizacion(e));

        let ctaCti = this.getElement('ctaCti');
        ctaCti.addEventListener('select', ({ detail }) => {
            this.getElement('regimen').setAttribute('value', detail.cccRegimeCode);
        });


        let aonAltaDirectaDni = this.getElement(`${this.ID}DivDni`);
        aonAltaDirectaDni.innerHTML = `<aon-suggestion id="${this.ID}Dni" title="DNI/NE" name="ipf"></aon-suggestion>`;
        aonAltaDirectaDni.setAttribute('disabled', true);

        let aonAltaDirectaNss = this.getElement(`${this.ID}DivNss`);
        aonAltaDirectaNss.innerHTML = `<aon-suggestion id="${this.ID}Nss" title="NSS/NAF" name="nss"></aon-suggestion>`;

        this.getElement(`${this.ID}NssInput`).addEventListener('blur', (e) => {
            this.comprobarNss(e);
        });

        this.getElement('type_cto').addEventListener('select', (e) => this.selectTipoContrato(e));

        this.getElement('tipo_jornada').addEventListener('select', (e) => this.selectTipojornada(e));

        this.getElement('horas_convenio').addEventListener('select', () => this.calculoCoef());

        let horas = this.getElement('horas');
        horas.addEventListener('blur', () => {
            this.calculoCoef();
        });

        let switchDni = this.getElement('switchDni');
        switchDni.addEventListener('change', ({ target }) => {
            let div_apellidos = this.getElement('div_apellidos');
            let nssInput = this.getElement(`${this.ID}NssInput`);
            let dniInput = this.getElement(`${this.ID}DniInput`);
            nssInput.disabled = target.checked;
            dniInput.disabled = !target.checked;
            dniInput.value = nssInput.value = "";
            div_apellidos.hidden = target.checked;
            div_apellidos.hidden = !target.checked;
            nssInput.removeIcon();
        });

        this.getElement('coefparcial').addEventListener('blur', (e) => this.calculoHoras());

        this.getElement('iconSegSocial').addEventListener('click', (e) => this.getNaf());


        this.getElement(`${this.ID}IConSearch`).addEventListener('click', (e) => this.disabledCardTrabajor(false));

        this.getElement(`${this.ID}DniInput`).disabled = true;

        this.startFunctions();
    }

    startFunctions() {
        this.listCentroTrabajo();
        this.suggestionDni();
        this.listTipoContrato();
        this.listTipoJornada();
        this.listGrupoCotizacion();
        this.listOcupacion();
        this.listConvenios();


        // this.testFieldValues();
    }


    selectTipojornada(e) {
        let { detail: { value } } = e;
        let hr = 0;
        if ("semanal" == value) hr = 40;
        else if ("diaria" == value) hr = 8;
        this.getElement("horas_convenio").value = hr;
        this.calculoCoef();
    }

    selectTipoContrato(e) {
        let { detail: { tipo_jornada } } = e;
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
        searchSuggestion.addEventListener('select', ({ detail }) => {
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

    listCuentaCotizacion(e) {
        try {
            let { detail: { ccc: cccs } } = e;
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

    async comprobarNss(ev) {
        const nss_sugges = this.getElement(`${this.ID}Nss`); //SUGGESTION
        let { target: { value } } = ev; //VALUE
        if (value) {
            const last_nss = value.toString().slice(-2);
            let mod = (parseInt(last_nss) % 97).toString();
            let nombre = this.getElement('nombre');
            let dni = this.getElement('aonAltaDirectaDni');
            dni.value = nombre.value = "";
            if (mod < 10) mod = '0' + mod;
            if (value.length > 11 && mod == last_nss) {
                nss_sugges.removeIcon();
                nss_sugges.loading(true);
                await this.getIpf(ev);
                nss_sugges.loading(false);
            } else {
                nss_sugges.addIcon("error", "#B42000"); //incorrect
            }
        }
    }

    async getIpf({ target }) {

        let nombre = this.getElement('nombre');
        let dni = this.getElement('aonAltaDirectaDni');
        const resp = await getIpfxnaf({ nss: target.value }).then(r => r.length ? r[0] : null).catch(e => null);
        if (resp) {
            nombre.value = resp.name;
            dni.value = resp.ipf.toString().substring(1);
            this.getElement('aonAltaDirectaNss').value = resp.nss;
            this.disabledCardTrabajor(true);
        }
    }


    async formSubmit() {
        const aonAltaDirectaForm = this.getElement(`${this.ID}Form`);
        const formJson = serializeForm(aonAltaDirectaForm);
        const toast = this.getElement(`${this.ID}Toast`);
        try {
            await postAltaDirecta(formJson);
            toast.start({ message: 'Alta procesada!', type: 'success', delay: 3000 });
        } catch (error) {
            toast.start({ message: error, type: 'error' });
        }
    }

    async getNaf() {
        const aonAltaDirectaForm = this.getElement(`${this.ID}Form`);
        const ipf = aonAltaDirectaForm.querySelector(`#${this.ID}Dni`);
        const apellido1 = aonAltaDirectaForm.querySelector('#apellido1');
        const nss_sugges = aonAltaDirectaForm.querySelector(`#${this.ID}Nss`); //SUGGESTION
        if (ipf && ipf.value && apellido1) {
            nss_sugges.loading(true);
            try {
                const formJson = serializeForm(aonAltaDirectaForm);
                const resp = await getNafxipf(formJson);
                if (resp) {
                    nss_sugges.value = resp.nss;
                    this.getElement('nombre').value = resp.name;
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
            this.getElement(`${this.ID}DniInput`).disabled = true;
            this.getElement(`nombre`).disabled = true;
        }
    }

    testFieldValues() {
        let regimen = "0111";
        this.getElement('regimen').setAttribute('value', regimen);

        let ctaCti = "01105360062";
        this.getElement('ctaCti').setAttribute('value', ctaCti);

        let nss = "010022757387";
        this.getElement('nss').setAttribute('value', nss);

        let ipf = "16262835H";
        this.getElement('aonAltaDirectaDni').setAttribute('value', ipf);

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
