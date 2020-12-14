import { AonElement } from '../../../components/AonElement.js';
import '../../../components/aon-number.js';
import { getAllTipoCtz, getWorkplaceCCCs } from '../../../services/service.js'

export class AonCta extends AonElement {
    ACTION;
    CUENTAS;
    _cta;
    set id(id) {
        this.setAttribute('id', id);
    }

    get id() {
        return this.getAttribute('id');
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
        this.id = this.id || 'aonComunicaCcc';
        this.aonComunicaEl = this.getElement('aonComunica');
        this.TOOLBAR = this.id + 'Toolbar';
        this.aonComunicaToolbar = this.getElement('aonComunicaToolbar');
        this._cta = [];
    }



    attributeChangedCallback(name, oldValue, newValue) {
        if ("data" == name && newValue) {
            this.edit(this.data);
        }
    }

    connectedCallback() {
        this.getCuentas();
    }

    async getCuentas() {
        try {
            this.CUENTAS = await getAllTipoCtz();
            this.paintView();
            this.build();
        } catch (error) { }
    }


    paintView() {
        const initHtml = `
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
            .regimenSpanDiv{
                position: absolute;
                top: 38%;
                z-index: 99;
            }
        </style>

        <aon-dialog-menu id="aonDialogAddOption" > </aon-dialog-menu>`;

        const form = `
        <form id="${this.id}Form" action="#" onsubmit="return false;">
            <div class="aonCol-sm-12">
                <aon-card id="${this.id}CtzCard" title="Cuenta de Cotización"></aon-card>
            </div>
        </form>`;
        this.innerHTML = initHtml + form;
    }

    build() {
        this.aonComunicaToolbar.setAttribute('option', 'Cuenta de Cotización');
        this.initLists();

        let cardTitle = this.getElement(`${this.id}CtzCardTitle`);
        let addDiv = document.createElement('div');
        addDiv.style.float = "right";
        cardTitle.appendChild(addDiv);
        addDiv.innerHTML = `<aon-icon-button id="${this.id}CardAddButton" icon="add"> </aon-icon-button>`;

        this.getElement(`${this.id}CardAddButton`).addEventListener('click', (e) => {
            this.addCtz();
        });
    }

    edit(data) {
        this.ACTION = "UPDATE";
        if (data.length > 0) {
            data.map(d => {
                this.addCtz({
                    ctaCti: d.ccc,
                    regimen: d.cccRegimeCode,
                    tipo: d.type,
                    activityId: d.activityId
                });
            });
        }
    }


    initLists() {
        this.listCtz();
    }

    async listCtz() {
        try {
            const resp = await getWorkplaceCCCs();
            const [ctas] = resp.map(({ ccc }) => ccc);
            if (ctas.length > 0)
                this.edit(ctas);
            else
                this.addCtz();
        } catch (error) { }
    }

    addCtz(cta) {
        const i = this._cta.length;
        if (!cta) {
            cta = {
                tipo: 1,
                regimen: '0111',
                ctaCti: ''
            }
        }
        this._cta.push(cta);
        this.printCtz(cta, i);
    }


    printCtz(cta, i) {
        let aonCtzCard = this.getElement(`${this.id}CtzCardContent`);
        const div = document.createElement('div');
        div.classList = 'aonRow';
        div.id = 'detail' + i;
        div.innerHTML = `
            <div class="aonCol-sm-6 aonCol-md-6 aonCol-xs-6">
                <aon-select name="tipo[${i}]" id="tipo${i}" title="Tipo"></aon-select>
            </div>
            <div class="aonCol-sm-6 aonCol-md-6 aonCol-xs-6">
                <span class="regimenSpanDiv" id="regimenSpanId${i}">${cta.regimen}</span>
                </aon-input><aon-number name="ctaCti[${i}]" id="ctaCti${i}" description="Cuenta" value="${cta.ctaCti}"></aon-number>
            </div>
            <aon-input name="regimen[${i}]" id="regimen${i}" description="regimen" value="${cta.regimen}" visible="false">
        `;
        aonCtzCard.appendChild(div);

        this.listTipo(i);
        this.eventListener(i);
        if (cta.tipo === 0) cta.tipo = 1;
        if (cta.tipo) {
            this.getElement(`tipo${i}`).value = cta.tipo;
        }
    }

    async listTipo(i) {
        let tipo = this.getElement(`tipo${i}`);
        try {
            tipo.options = JSON.stringify(this.CUENTAS);
        } catch (error) { }
    }

    eventListener(i) {
        this.getElement(`tipo${i}`).addEventListener('change', ({ detail }) => {
            const {regimen, value} = detail;
            this.getElement(`regimen${i}`).setAttribute('value', regimen);
            this.getElement(`regimenSpanId${i}`).innerText = regimen;

            this.setCta(i, 'regimen', regimen);
            this.setCta(i, 'tipo', value);
        });
        this.getElement(`ctaCti${i}`).addEventListener('change', ({ target }) => {
            this.setCta(i, 'ctaCti', target.value);
        });
    }

    setCta(i, name, value) {
        this._cta[i][name] = value;
        this.save();
    }

    async save() {
        console.log("save>>", this._cta);
    }

}
window.customElements.define('aon-cta', AonCta);
