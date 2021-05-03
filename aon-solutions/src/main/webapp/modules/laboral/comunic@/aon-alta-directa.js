import { AonElement } from '../../../components/AonElement.js';
import { setValueName, serializeForm, formatDateOrigin, disabledForm } from '../../../services/utils.js';
import { getPersonas, getWorkplaceCCCs, getConvenios, getTipoContrato, getOcupacion, getGrupoCotizacion, postAltaDirecta, getTipoJornada, getIpfxnaf, getNafxipf, getTipoCtz, postUpdateCto } from '../../../services/service.js'
import { ToolbarType } from '../../../models/enums.js';
import { PAYROLL_VIEWS } from '../PayrollEnums.js';
import { CONSTANT, MSG } from '../../../environments/environments.js';
import '../../../components/aon-card.js';
import '../../../components/aon-input.js';
import '../../../components/aon-number.js';
import '../../../components/aon-date.js';
import '../../../components/aon-suggestion.js';
import '../../../components/aon-select.js';
import '../../../components/aon-switch.js';
import '../../../components/aon-icon-button.js';


export class AonAltaDirecta extends AonElement {
    _contrato;
    ACTION;
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
        this.ACTION = 'CREATE';
        this.id = this.id || PAYROLL_VIEWS.AON_ALTA_DIRECTA;
        this.TOOLBAR = this.id + 'Toolbar';
        this.applicationEl = this.getApplication();
        this.applicationParentEl = this.getApplicationParent();
        this.applicationElToolbar = this.getElement(this.applicationEl.TOOLBAR);
        this.applicationElToolbar.setAttribute('option', 'Comunicar contrato');
    }

    attributeChangedCallback(name, oldValue, newValue) {
        if ("data" == name && newValue) {
            const textEdit =  'Modificar contrato';
            this.applicationElToolbar.setAttribute('option', textEdit);
            const toolbarEl = this.getElement(this.TOOLBAR);
            if(toolbarEl){
                toolbarEl.title = textEdit;
            }
            this.edit(this.data);
        }
    }

    connectedCallback() {
        this.build();
    }

    build() {
        this.paintView();
        this.buildToolbar();
        this.initLists();
        this.eventListener();
    }


    paintView() {
        const toolbar = /*html*/`<aon-toolbar id="${this.TOOLBAR}" type="${ToolbarType.SECONDARY}" title="Alta directa"> </aon-toolbar>`;
        const form = 
        /*html*/`
        <div>
            <form id="${this.id}Form" action="#" onsubmit="return false;">
                <div id="${this.id}Div">
                    <div class="aonCol-sm-12">
                        <aon-card id="${this.id}EmpresaCard" title="Datos de la empresa" flex="true"></aon-card>
                    </div>
                    <div class="aonCol-sm-12 aonCol-md-6">
                        <aon-card id="${this.id}TrabajadorCard" title="Datos del trabajador" flex="true"></aon-card>
                    </div>
                    <div class="aonCol-sm-12 aonCol-md-6">
                        <aon-card id="${this.id}ContratoCard" title="Datos del contrato" flex="true"></aon-card>
                    </div>
                </div>
            </form>
        `;

        this.innerHTML = toolbar + form;

        this.applicationEl.removeToolbarOptions();

        let aonEmpresaCard = this.getElement(`${this.id}EmpresaCard`);
        aonEmpresaCard.setContentHTML(
            /*html*/`
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
            `
        );

        let aonTrabajadorCard = this.getElement(`${this.id}TrabajadorCard`);
        aonTrabajadorCard.setContentHTML(
            /*html*/`
            <div class="aonCol-sm-12 aonCol-md-4" style="padding-top: 18px;">
                <aon-switch id="switchDni" title="Buscar por DNI"></aon-switch>
                <div id="${this.id}Reiniciar" hidden>
                    Reiniciar <aon-icon-button id="${this.id}IconReset" icon="cached"> </aon-icon-button>
                </div>
            </div>
            <div class="aonCol-sm-12 aonCol-md-4">
                <div id="${this.id}NssDiv"></div>
            </div>
            <div class="aonCol-sm-12 aonCol-md-4">
                <div id="${this.id}DniDiv"></div>
            </div>
            <div id="div_apellidos" hidden>
                <div class="aonCol-sm-12 aonCol-md-6">
                    <aon-input name="apellido1" id="apellido1" description="1er Apellido" type="text"></aon-input>
                </div>
                <div class="aonCol-sm-10 aonCol-md-5 aonCol-xs-11">
                    <aon-input name="apellido2" id="apellido2" description="2do Apellido" type="text"></aon-input>
                </div>
                <div class="aonCol-sm-2 aonCol-md-1 aonCol-xs-1">
                    <aon-icon-button id="iconSegSocial" aonIcon="aon_seg_social"> </aon-icon-button>
                </div>
            </div>
            <div class="aonCol-sm-12 aonCol-md-12 aonCol-xs-12">
                <aon-input name="nombre" id="nombre" description="Nombre" type="text" disabled="true"></aon-input>
            </div>
        `
        );
        const buttonSubmit = this.isMobile() ? /*html*/`<div class="aonCol-sm-12 aonCol-md-12"><br/> <div class="offset-4" id="${this.id}DivSubmit"></div></div>` : '';
        let aonContratoCard = this.getElement(`${this.id}ContratoCard`);
        aonContratoCard.setContentHTML(
           /*html*/`
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
                        <aon-select id="tipo_jornada" name="tipo_jornada" title="Tipo de jornada"></aon-select>
                    </div>
                    <div class="aonCol-sm-3">
                        <aon-number id="horas_convenio" name="horas_convenio"  description="Horas convenio" format="true" decimals="2"></aon-number>
                    </div>
                    <div class="aonCol-sm-3">
                        <aon-number id="horas" description="Horas" format="true" decimals="2"></aon-number>
                    </div>
                    <div class="aonCol-sm-3">
                        <aon-number name="coefparcial" id="coefparcial" description="Coef. Parcial"></aon-number>
                    </div>
                </div>
                <aon-input name="situation" id="situation" description="situation" value="AL" visible="false"></aon-input>
                ${buttonSubmit}
            `
        );

        let aonAltaDirectaDni = this.getElement(`${this.id}DniDiv`);
        aonAltaDirectaDni.innerHTML = /*html*/`<aon-suggestion id="${this.id}Dni" title="DNI/NIE" name="ipf"></aon-suggestion>`;
        aonAltaDirectaDni.setAttribute('disabled', true);

        let aonAltaDirectaNss = this.getElement(`${this.id}NssDiv`);
        aonAltaDirectaNss.innerHTML = /*html*/`<aon-suggestion id="${this.id}Nss" title="NSS/NAF" name="nss"></aon-suggestion>`;

        if (this.isMobile()){
            this.getElement(`${this.id}DivSubmit`)
                .innerHTML = /*html*/`<button class="aonButton" type="button" id="${this.id}Submit">Comunicar</button>`;
        }
        this.addSpanDecimal();
        this.getElement(`${this.id}Dni`).disabled = true;
        this.getElement('fecha').value = formatDateOrigin(new Date());
    }

    buildToolbar() {
        const toolbar = this.getElement(this.TOOLBAR);
        toolbar.removeButtons();

        toolbar.addButton2({
            id: 'Idc',
            name: 'Obtener IDC',
            aonIcon: 'aon_idc',
        }, () => this.applicationParentEl.getIdc(this.data));

        toolbar.addButton2({
            id: 'Ta',
            name: 'Obtener TA',
            aonIcon: 'aon_ta',
        }, () => this.applicationParentEl.getTa(this.data));

        toolbar.addButton2({
            id: 'Delete',
            name: 'Anular',
            icon: 'delete_forever',
        }, async (el) => {
            try {
                await this.applicationParentEl.deleteMov(this.data, el);
            } catch (error) {
                console.log(error);
            }
        });

        if(!this.isMobile()){
            toolbar.addButton2({
                id: 'Save',
                name: 'Comunicar',
                icon: 'send'
            }, () => this.formSubmit());
        }

        toolbar.addButton2({
            id: 'Previous',
            name: 'Volver',
            icon: 'arrow_back'
        }, () => this.back());

        this.hiddenButtonToolbar({ 'Delete': true, 'Idc': true, 'Ta': true });
    }


    initLists() {
        this.suggestionDni();
        this.listCentroTrabajo();
        this.listTipoContrato();
        this.listTipoJornada();
        this.listGrupoCotizacion();
        this.listOcupacion();
        this.listConvenios();
    }

    eventListener() {
        let aonAltaDirectaSubmit = this.getElement(`${this.id}Submit`);
        if (aonAltaDirectaSubmit) aonAltaDirectaSubmit.addEventListener('click', () => this.formSubmit());

        this.getElement('centro_trabajo').addEventListener('change', (e) => this.listCuentaCotizacion(e));

        this.getElement('ctaCti').addEventListener('change', ({ detail }) => {
            this.getElement('regimen').setAttribute('value', detail.cccRegimeCode);
        });

        this.getElement(`${this.id}Nss`).addEventListener('change', ({ target }) => {
            this.comprobarNss(target.value);
        });

        this.getElement('type_cto').addEventListener('change', (e) => this.selectTipoContrato(e));

        this.getElement('tipo_jornada').addEventListener('change', (e) => this.selectTipojornada(e));

        this.getElement('horas_convenio').addEventListener('change', () => this.calculoCoef());

        this.getElement('horas').addEventListener('change', () => this.calculoCoef());

        this.getElement('switchDni').addEventListener('change', ({ target }) => {
            let div_apellidos = this.getElement('div_apellidos');
            let nss = this.getElement(`${this.id}Nss`);
            let dni = this.getElement(`${this.id}Dni`);
            nss.disabled = target.checked;
            dni.disabled = !target.checked;
            dni.value = nss.value = "";
            div_apellidos.hidden = target.checked;
            div_apellidos.hidden = !target.checked;
            nss.removeIcon();
        });

        this.getElement('coefparcial').addEventListener('change', (e) => this.calculoHoras());

        this.getElement('iconSegSocial').addEventListener('click', (e) => this.getNaf());

        this.getElement(`${this.id}IconReset`).addEventListener('click', (e) => this.disabledCardTrabajor(false));
    }



    hiddenButtonToolbar(buttonToolbar) {
        let toolbarSection = this.getElement(this.TOOLBAR);
        if (toolbarSection) {
            toolbarSection = toolbarSection.TOOL_SECTION
            for (const button in buttonToolbar) {
                const deleteToolbar = this.getElement(toolbarSection + button + 'Button');
                if (deleteToolbar) deleteToolbar.hidden = buttonToolbar[button];
            }
        }
    }

    getContrato() {
        const form = this.getElement(`${this.id}Form`);
        return serializeForm(form);
    }

    edit(data) {
        this.ACTION = "UPDATE";
        let obj = {
            ...data,
            regimen: data.regime,
            nombre: data.name,
            fecha: data.fra,
            grup_ctz: data.gc,
        }
        if (data.ocup) obj['ocupacion'] = data.ocup.toString().toLowerCase();
        if (data.contract) obj['type_cto'] = data.contract;
        if (data.coef) {
            obj['tipo_jornada'] = "semanal";
            obj['coefparcial'] = parseInt(data.coef.toString().replace(',', ''));
        }
        for (const property in obj) setValueName(property, obj[property]);
        this._contrato = obj; //contrato

        //seleccionar workplace;
        const centro_trabajo = this.getElement('centro_trabajo');
        if (centro_trabajo && centro_trabajo.options) {
            const options = JSON.parse(centro_trabajo.options);
            const { workplace } = options.find((r, index) => r.ccc.some(rs => rs.cccRegimeCode === obj.regimen && rs.ccc === obj.ctaCti) === true);
            if (workplace) {
                const workplaceInput = this.getElement('centro_trabajoInput');
                if (workplaceInput && workplace.description) workplaceInput.value = workplace.description;
            }
        }

        //seleccionar ccc;
        const ctaCtiInput = this.getElement('ctaCtiInput');
        if (ctaCtiInput) ctaCtiInput.value = obj.regimen + ' - ' + obj.ctaCti;

        //tipear el tipo de contrato
        const type_cto = document.querySelector('#type_cto > aon-input');
        if (obj.type_cto && type_cto && !type_cto.value) {
            type_cto.value = obj.type_cto;
        } else {
            //seleccionamos por tipo de cuenta
            if (centro_trabajo && centro_trabajo.options) {
                const options = JSON.parse(centro_trabajo.options);
                for (const property in options) {
                    if (property && options[property]) {
                        let { type } = options[property].ccc.find(rs => rs.cccRegimeCode === obj.regimen && rs.ccc === obj.ctaCti);
                        if (type) {
                            this.selectTypeCto(type);
                            break;
                        }
                    }
                }
            }
        }

        //calculo horas
        this.calculoHoras();

        //hidden toolbar button
        let buttonToolbar = { "Idc": false, "Ta": false };
        if (this.applicationParentEl.anularCondition(obj.situation, obj.fecha)) buttonToolbar["Delete"] = false;
        this.hiddenButtonToolbar(buttonToolbar);
        //end hidden toolbar

        //ocultar switch
        let switchDni = this.getElement('switchDni');
        if (switchDni) switchDni.parentNode.hidden = true;
        //extender dni y nss
        let nss = this.getElement(`${this.id}NssDiv`);
        let dni = this.getElement(`${this.id}DniDiv`);
        if (nss && dni)
            nss.parentNode.classList = dni.parentNode.classList = 'aonCol-sm-12 aonCol-md-6';

        //disabled tipo de contrato
        this.getElement('type_cto').disabled = true;
        disabledForm('type_cto');
    }

    addSpanDecimal() {
        let coefparcialInput = this.getElement('coefparcialInput')
        let span = document.createElement('span');
        span.innerHTML = '0,';
        span.style.position = "absolute";
        span.style.top = "50%";
        span.style.zIndex = "9";
        coefparcialInput.parentNode.insertBefore(span, coefparcialInput);
    }

    selectTipojornada({ detail }) {
        if (detail) {
            const { value } = detail;
            let hr = 0;
            if ("semanal" == value) hr = 40;
            else if ("diaria" == value) hr = 8;
            this.getElement("horas_convenio").value = hr;
            this.calculoCoef();
        }
    }

    async selectTypeCto(type) {
        const type_cto = document.querySelector('#type_cto > aon-input');
        const { name } = await getTipoCtz(type);
        if (type_cto && !type_cto.value && name)
            type_cto.value = name;
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
            } else {
                div_parcial.hidden = true;
            }
        }
    }

    suggestionDni() {
        const searchSuggestion = this.getElement(`${this.id}Dni`);
        searchSuggestion.addEventListener('keyup', async ({ target: { value } }) => {
            const dni = value.toString().toUpperCase();
            if (dni.length > 2) {
                const resp = await getPersonas(dni);
                searchSuggestion.buildOptions(resp);
            } else {
                searchSuggestion.closeOptions();
            }
        });
        searchSuggestion.addEventListener('select', ({ detail }) => {
            if (detail) {
                setValueName('nss', detail.nss);
                setValueName('nombre', detail.nombre);
                setValueName('apellido1', detail.last_name1);
                setValueName('apellido2', detail.last_name2);
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
            let calc = Math.round(parseFloat((parseFloat(horas) / parseFloat(horas_convenio)) * 1000));
            if (calc > 0 && calc <= 999) coef = calc;
        }
        this.getElement('coefparcial').value = coef;
    }

    calculoHoras() {
        let horas_convenio = this.getElement('horas_convenio').value;
        let coef = this.getElement('coefparcial').value;
        let horas = this.getElement('horas');
        if (horas_convenio > 0 && coef > 0 && coef <= 999) {
            horas.value = parseFloat(coef / 1000) * horas_convenio;
        }
    }

    async comprobarNss(value) {
        const nss_sugges = this.getElement(`${this.id}Nss`); //SUGGESTION
        if (value) {
            const last_nss = value.toString().slice(-2);
            let mod = (parseInt(last_nss) % 97).toString();
            let nombre = this.getElement('nombre');
            let dni = this.getElement(`${this.id}Dni`);
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

    formSubmit() {
        this.applicationEl.confirmDialog("Comunicar", "Desea comunicar a la seguridad social?", () => {
            switch (this.ACTION) {
                case "CREATE":
                    this.save();
                    break;
                case "UPDATE":
                    this.update();
                    break;
                default:
                    break;
            }
        });
    }

    async save() {
        this.applicationEl.startLoading();
        try {
            await postAltaDirecta(this.getContrato());
            this.showToast({ message: MSG.PROCESSED_MOVEMENT, type: CONSTANT.SUCCESS, delay: 3000 });
            this.applicationParentEl._movements = undefined;
            this.back();
        } catch (error) {
            this.showToast(error);
        }
        this.applicationEl.stopLoading();
    }

    async update() {
        this.applicationEl.startLoading();
        let cto_new = this.getContrato();
        const cto_old = this._contrato;
        for (const property in cto_new) {
            if (cto_new[property] && (cto_old[property] != cto_new[property])) {
                cto_new[`${property}_edit`] = true;
            }
        }
        try {
            await postUpdateCto(cto_new);
            this.showToast({ message: MSG.UPDATED_CONTRACT, type: CONSTANT.PRIMARY, delay: 3000 });
            this.applicationParentEl._movements = undefined;
            this.back();
        } catch (error) {
            this.showToast(error);
        }
        this.applicationEl.stopLoading();
    }

    back() {
        this.applicationParentEl.showView(PAYROLL_VIEWS.AON_MOVEMENTS);
    }

    async getNaf() {
        const contrato = this.getContrato();
        const ipf = contrato.ipf;
        const apellido1 = contrato.apellido1;
        const nss_sugges = this.getElement(`${this.id}Nss`); //SUGGESTION
        if (ipf && apellido1) {
            nss_sugges.loading(true);
            try {
                const resp = await getNafxipf(contrato);
                if (resp) {
                    nss_sugges.value = resp.nss;
                    setValueName('nombre', resp.name);
                    this.disabledCardTrabajor(true);
                }
            } catch (error) {
                this.showToast(error);
            }
            nss_sugges.loading(false);
        }
    }

    disabledCardTrabajor(vl) {
        let fields = document.querySelectorAll(`#${this.id}TrabajadorCard aon-input`);
        let switchDni = this.getElement('switchDni');
        fields.forEach(el => {
            el.disabled = vl;
            if (!vl) el.value = '';
        });
        switchDni.hidden = vl;
        switchDni.checked = false;
        this.getElement(`${this.id}Reiniciar`).hidden = !vl;
        this.getElement('div_apellidos').hidden = true;
        if (!vl) {
            this.getElement(`${this.id}Dni`).disabled = true;
            this.getElement(`nombre`).disabled = true;
        }
    }
}

window.customElements.define('aon-alta-directa', AonAltaDirecta);
