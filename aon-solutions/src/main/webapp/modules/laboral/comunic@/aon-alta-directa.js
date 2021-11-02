import { AonElement } from '../../../components/AonElement.js';
import { setValueName, serializeForm, disabledForm, sortBy } from '../../../services/utils.js';
import { getRlce, getContractType, getOccupation, getQuoteGroup, sendAlta, sendBaja, getTipoJornada, getIpfxnaf, getNafxipf, getTipoCtz, updateContrato, getCccForActivity, getCodBaja } from '../../../services/service.js'
import { ToolbarType } from '../../../models/enums.js';
import { ACTION_COMUNICA, CONTRACT_OPTIONS, PAYROLL_VIEWS } from '../PayrollEnums.js';
import { CONSTANT, EVENT, MSG } from '../../../environments/environments.js';
import { createBajaDialogContent, createFormComunica, createEnterpriseData, createEmployeeData, createContractData } from '../createComponent.js';
import { createToolbar } from '../../notification/createComponent.js';
import { AonDateUtils } from '../../utils/AonDateUtils.js';

export class AonAltaDirecta extends AonElement {
    _contrato;
    ACTION;
    static get observedAttributes() {
        return [CONSTANT.DATA];
    }

    get id() {
        return this.getAttribute(CONSTANT.ID);
    }

    set id(id) {
        this.setAttribute(CONSTANT.ID, id);
    }

    get data() {
        return JSON.parse(this.getAttribute(CONSTANT.DATA));
    }

    set data(value) {
        this.setAttribute(CONSTANT.DATA, JSON.stringify(value));
    }

    attributeChangedCallback(name, oldValue, newValue) {}

    constructor() {
        super();
        this.initialize();
    }

    connectedCallback() {
        this.initialize();
        this.build();
    }

    initialize(){
        this.ACTION = 'CREATE';
        this.id = this.id || PAYROLL_VIEWS.AON_ALTA_DIRECTA;
        this.TOOLBAR = this.id + 'Toolbar';
        this.applicationEl = this.getApplication();
        this.applicationParentEl = this.getApplicationParent();
    }

    build() {
        this.paintView();
        this.buildToolbar();
        this.initGets().then(()=>{
            this.eventListener();
            if(this.data){
                const toolbarEl = this.getElement(this.TOOLBAR);
                if(toolbarEl) toolbarEl.title = 'Editar contrato';
                this.edit(this.data);
            } else {
                this.enterpriseDataDefault();
            } 
        });
    }


    paintView() {
        this.applicationEl.removeToolbarOptions();

        createToolbar({ id:this.TOOLBAR, type:ToolbarType.SECONDARY, title:"Alta Directa"}, this);

        createFormComunica(this.id, this);

        let aonEnterpriseCard = this.getElement(`${this.id}EmpresaCard`);
        createEnterpriseData(aonEnterpriseCard.getContent());

        let aonEmployeeCard = this.getElement(`${this.id}TrabajadorCard`);
        createEmployeeData(aonEmployeeCard.getContent(),  this.id);
 
        let aonContratoCard = this.getElement(`${this.id}ContratoCard`);
        createContractData(aonContratoCard.getContent(), this.applicationParentEl.getDur().isComunicaManager() && !this.data);

        if(!this.isMobile() && this.data && this.data.status) {
            const titleRight = aonContratoCard.getCardTitle2();
            titleRight.innerHTML = this.data.status;
        }
        if(this.data && !this.isAlta()){
            disabledForm(aonContratoCard.id);
        }

    }

    buildToolbar() {
        const toolbar = this.getElement(this.TOOLBAR);
        toolbar.removeButtons();
  
        if(!this.isMobile() && this.data && this.data.fra){
            toolbar.addButton2(ACTION_COMUNICA.INFORMES, (ev) => {
                ev.preventDefault();
                let rect = ev.target.getBoundingClientRect();
                let x = ev.clientX - rect.left + 180;
                let y = ev.clientY - rect.top;
        
                const top  = rect.top + y;
                const left = rect.left + x;
    
                let d = this.getApplication().getOptionDialog();
                d.getContent().style.width = "133px";
                let moreActions = [];
                //IDC
                let idc = CONTRACT_OPTIONS.IDC;
                idc.fn = () =>  this.applicationParentEl.getIdc(this.data);
                moreActions.push(idc);
                //TA
                let ta = CONTRACT_OPTIONS.TA;
                ta.fn = () =>  this.applicationParentEl.getTa(this.data);
                moreActions.push(ta);

                d.setMenuOptions(moreActions, top, left);
                d.open();
            });
            this.setStyleIconSegSocial(toolbar, ACTION_COMUNICA.INFORMES.id);
        }

        if(this.isAlta() && this.applicationParentEl.getDur().isComunicaManager()) 
            toolbar.addButton2(ACTION_COMUNICA.BAJA, (e) => this.openDialogBaja(e));

        if((this.isAlta() || !this.data) )  // ALTA
            toolbar.addButton2(ACTION_COMUNICA.COMUNICAR, () =>  this.formSubmit());

        if(this.data && this.applicationParentEl.anularCondition(this.data.situation, this.data.fra)){ 
            toolbar.addButton2(CONTRACT_OPTIONS.DELETE, (e) => this.applicationParentEl.deleteMov(this.data, e));
        }

        toolbar.addButton2(ACTION_COMUNICA.BACK, () => this.back());
    }


    async initGets() {
        await Promise.all([
            this.getWorkplace(),
            this.getContractTye(),
            this.getTipoJornada(),
            this.getQuoteGroup(),
            this.getOccupation(),
            this.getRlce(),
            // this.suggestionConvenio()
        ]).catch(e=> console.log(e));
    }

    eventListener() {
        let workplace = this.getElement('centro_trabajo');
        workplace.addEventListener(EVENT.CHANGE, (ev) => this.listCtaCti(ev));

        let ctaCti = this.getElement('ctaCti');
        ctaCti.addEventListener(EVENT.CHANGE, ({ detail }) =>  this.getElement('regimen').setAttribute('value', detail.cccRegimeCode)  );

        let nss = this.getElement(`${this.id}Nss`);
        nss.addEventListener(EVENT.CHANGE, ({ target }) =>  this.comprobarNss(target.value));

        let typeCtoSelect = this.getElement('type_cto');
        if(typeCtoSelect){
            typeCtoSelect.addEventListener(EVENT.CHANGE, (ev) =>this.selectTypeContract(ev));
            let inputSelect = typeCtoSelect.querySelector(`input`);
            if(inputSelect)
                inputSelect.addEventListener(EVENT.FOCUS, () => inputSelect.select());    
        }

        let typeJornada = this.getElement('tipo_jornada');
        typeJornada.addEventListener(EVENT.CHANGE, (ev) => this.selectTipojornada(ev));

        this.getElement('horas_convenio').addEventListener(EVENT.CHANGE, () => this.calculoCoef());

        this.getElement('horas').addEventListener(EVENT.CHANGE, () => this.calculoCoef());

        this.getElement('switchDni').addEventListener(EVENT.CHANGE, ({ target }) => {
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

        this.getElement('coefparcial').addEventListener(EVENT.CHANGE, () => this.calculoHoras());

        this.getElement('apellido2IconLabel').addEventListener(EVENT.CLICK, () => this.getNaf());

        this.getElement(`${this.id}IconReset`).addEventListener(EVENT.CLICK, () => this.disabledCardTrabajor(false));
    }

    getContrato() {
        return serializeForm(this.getElement(`${this.id}Form`));
    }

    enterpriseDataDefault(){
        let workplace = this.getElement('centro_trabajo');
        let ctaCti    = this.getElement('ctaCti')
        let options = workplace.getOptions();
        let workplaceOne = 1 === options.length;
        if(workplaceOne){
            workplace.setIndexOf(0);
    
            let ctaCtiOne  = options[0].cccs && 1 === options[0].cccs.length;
            if(ctaCtiOne){
                ctaCti.setIndexOf(0);
                this.getElement(`${this.id}Nss`).focus();
            } else 
                ctaCti.focus();
        } else {
            workplace.focus();
        }
    }

    edit(data) {
        this.ACTION = "UPDATE";
        let obj = {
            ...data,
            regimen: data.regime,
            name: data.name,
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
        const workplaceInput = centro_trabajo.querySelector('aon-input');
        const options = centro_trabajo.getOptions();
        if (centro_trabajo && workplaceInput && options) {
            const {name:nameWp} = options.find((r) => r.cccs.some(rs => rs.cccRegimeCode === obj.regimen && rs.ccc === obj.ctaCti) === true);
            if (nameWp) workplaceInput.value = nameWp;
        }

        //seleccionar ccc;
        const ctaCtiInput = this.getElement('ctaCtiInput');
        if (ctaCtiInput) ctaCtiInput.value = obj.regimen + ' - ' + obj.ctaCti;

        //tipear el tipo de contrato
        const type_cto = document.querySelector('#type_cto > aon-input');
        if (obj.type_cto && type_cto && !type_cto.value) {
            type_cto.value = obj.type_cto;
        } else { //seleccionamos por tipo de cuenta si no tiene tipo de contrato
            this.selectWorkPlace(centro_trabajo, obj);
        }

        //calculo horas
        this.calculoHoras();

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

    selectWorkPlace(centro_trabajo, {regimen, ctaCti}){
        if (centro_trabajo && centro_trabajo.options) {
            const options = JSON.parse(centro_trabajo.options);
            for (const property in options) 
                if (property && options[property] && options[property].cccs) {
                    const res  = options[property].cccs.find(({cccRegimeCode, ccc}) => cccRegimeCode === regimen && ccc === ctaCti);
                    if (res && res.type) {
                        this.selectTypeCto(res.type);
                        break;
                    }
                }
        }
    }

    selectTypeCto(type) {
        const type_cto = document.querySelector('#type_cto > aon-input');
        getTipoCtz(type).then(({name})=>{
            if (type_cto && !type_cto.value && name)
                type_cto.value = name;
        });
    }

    selectTypeContract({ detail }) {
        if (detail) {
            let tipo_jornada = parseInt(detail.tipo_jornada);
            let divParcial = this.getElement('div_parcial');
            let hourEl = this.getElement('horas_convenio');
            this.getElement('coefparcial').value = "";
            // this.btnBajaShow(tipo_jornada);//baja display none
            if (tipo_jornada){ //si es parcial
                divParcial.hidden = false;
                hourEl.value = 40;
            }  else {
                divParcial.hidden = true;
                hourEl.value = "";
            }
        }
    }

    async getWorkplace() {
        try {
            const resp = await getCccForActivity();
            if(resp && resp.cccs){
                const groupedGeozone = this.groupBy(resp.cccs, ccc => ccc.geozone);
                let geozones = [];
                groupedGeozone.forEach((cccs, name)=>{
                    if(cccs && cccs.length)
                        cccs = cccs.filter( (value,index, self)=>self.findIndex((m) => m.ccc === value.ccc) === index );
                        
                    geozones.push({
                        cccs,
                        name,
                        value:name
                    });
                })
                let centro_trabajo = this.getElement('centro_trabajo');
                centro_trabajo.setOptions(geozones);
            }
        } catch (error) { }
    }

    listCtaCti({ detail }) {
        if (detail) {
            try {
                const { cccs } = detail;
                let ctaCti = this.getElement('ctaCti');
                let options = cccs
                .map(r => ({ ...r, name: `${r.cccRegimeCode} - ${r.ccc}`, value: r.ccc }));
                ctaCti.setOptions(options);
            } catch (error) { }
        }
    }

    groupBy(list, keyGetter) {
        const map = new Map();
        for(let it in list){
            const item = list[it];
            const key = keyGetter(item);
            const collection = map.get(key);
            if (!collection) {
                map.set(key, [item]);
            } else {
                collection.push(item);
            }
        }
        return map;
    }

    async getContractTye() {
        try {
            let manager = this.applicationParentEl.getDur().isComunicaManager();
            let resp = await getContractType();
            let contract = this.data && this.data.contract ? this.data.contract : "";
            if(!manager)
                resp = resp.filter(({enable, value})=> enable || value === contract);
            
            let type_cto = this.getElement('type_cto');
            type_cto.setOptions(resp.map(r => ({ ...r, name: `${r.value} - ${r.name}`, value: r.value})));
        } catch (error) { }
    }

    async getTipoJornada() {
        let tipo_jornada = this.getElement('tipo_jornada');
        try {
            const resp =  getTipoJornada();
            const options = resp.map(r => ({ ...r, name: `${r.name}`, value: r.value }) );
            tipo_jornada.setOptions(options);
            tipo_jornada.value = options[0].value;
        } catch (error) { }
    }

    // async suggestionConvenio() {
    //     const convenios = await getConvenios();

    //     const suggestion = this.getElement(`convenio`);
    //     suggestion.querySelector("input").autocomplete = "on";
        
    //     suggestion.addEventListener(EVENT.AON_KEYUP, ({ target: { value } }) => {
    //         let newValue = value.toString().toUpperCase();
    //         if (newValue.length > 2) {
    //             const resp = convenios.filter(c=> (c.name && c.name.indexOf(newValue)>=0) );
    //             suggestion.buildOptions(resp);
    //         } else {
    //             suggestion.closeOptions();
    //         }
    //     });
    // }

    async getQuoteGroup() {
        try {
            let resp = await getQuoteGroup();
            resp = sortBy(resp, 'name', 'asc');
            let grup_ctz = this.getElement('grup_ctz');
            grup_ctz.setOptions(resp.map(r => ({ ...r, name: `${r.name}`, value: r.value})));
        } catch (error){}
    }

    async getOccupation() {
        try {
            let resp = await getOccupation();
            resp = sortBy(resp, 'value', 'asc');
            let ocupacion = this.getElement('ocupacion');
            ocupacion.setOptions(resp.map(r => ({ name: `${r.name}`, value: r.value})));
        } catch (error){}
    }

    async getRlce() {
        try {
            let rlce = this.getElement('rlce');
            if(rlce){
                let resp = await getRlce();
                let options = sortBy( resp.map(r =>  ({ ...r, name: `${r.value} - ${r.name}`, value: r.value})), 'value', 'asc');
              
                rlce.setOptions( options );
            }

        } catch (error){}
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
            let name = this.getElement('name');
            let dni = this.getElement(`${this.id}Dni`);
            dni.value = name.value = "";
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
            setValueName('name', resp.name);
            setValueName('ipf', resp.ipf.toString().substring(1));
            setValueName('nss', resp.nss);
            this.disabledCardTrabajor(true);
        }
    }

    formSubmit() {
        this.applicationEl.confirmDialog(MSG.COMMUNICATE, MSG.COMMUNICATE_CONFIRM, () => {
            switch (this.ACTION) {
                case "CREATE":
                    this.alta();
                    break;
                case "BAJA":
                    this.baja();
                    break;
                case "UPDATE":
                    this.update();
                    break;
                default:
                    break;
            }
        }, MSG.COMMUNICATE);
    }

    async alta() {
        this.applicationEl.startLoading();
        try {
            await sendAlta(this.getContrato());
            this.showToast({ message: MSG.PROCESSED_MOVEMENT, type: CONSTANT.SUCCESS, delay: 3000 });
            this.applicationParentEl._movements = [];
            this.back();
        } catch (error) {
            this.showToast(error);
        }
        this.applicationEl.stopLoading();
    }

    async baja(){
        this.applicationEl.startLoading();
        try {
            const fechaBajaEl = this.getElement("fechaBaja");
            const codBajaEl = this.getElement("codBaja");
            await sendBaja({...this.data, fechaBaja: fechaBajaEl.value, situation: codBajaEl.value});
            this.applicationEl.getOptionDialog().close();
            this.showToast({ message: MSG.PROCESSED_MOVEMENT_BJ, type: CONSTANT.SUCCESS, delay: 3000 });
            this.applicationParentEl._movements = [];
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
            await updateContrato(cto_new);
            this.showToast({ message: MSG.UPDATED_CONTRACT, type: CONSTANT.PRIMARY, delay: 3000 });
            this.applicationParentEl._movements = [];
            this.back();
        } catch (error) {
            this.showToast(error);
        }
        this.applicationEl.stopLoading();
    }

    back() {
        this.applicationParentEl.showView(PAYROLL_VIEWS.AON_MOVEMENTS_LIST);
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
                    setValueName('name', resp.name);
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
            this.getElement(`name`).disabled = true;
        }
    }

    openDialogBaja(ev){
		const rect = ev.target.getBoundingClientRect();
		const x = ev.clientX - rect.left + 180;
		const y = ev.clientY - rect.top;
		const top  = rect.top + y;
		let left = rect.left + x;
        const dialog = this.applicationEl.getOptionDialog();
        const content = dialog.getContent();
        dialog.clear();
        content.style.textAlign = "center";
        content.style.width = "250px";
        dialog.setContentTitle("Dar Baja");
        const div = createBajaDialogContent();
        dialog.setContent(div);
        dialog.openPosition({top, left});
        
        if(this.isMobile()) {
            content.style.left = 0;
            content.style.right = 0;
        }

        const button = this.getElement("btnSubmitBaja");

        const fechaEl = this.getElement("fechaBaja");
        fechaEl.value = AonDateUtils.formatDateOrigin(new Date());
        fechaEl.addEventListener(EVENT.CHANGE, ()=>{
            if(new Date(fechaEl.value).isValid()) button.disabled = false;
            else button.disabled = true;
        })

        //list cod de baja
        const codBajaEl = this.getElement("codBaja");
        getCodBaja().then(cods => {
            codBajaEl.setOptions(
                cods.map(r=>({value:r.value, name:r.value+" - "+r.name }))
            );
            codBajaEl.value = "93";
        })
                
        button.addEventListener(EVENT.CLICK, ()=>{
            this.ACTION = "BAJA";
            this.formSubmit();
        })
    }

    setStyleIconSegSocial(toolbar, id){
        const aonIconButton = this.getElement(toolbar.TOOL_SECTION + id + "Button")
        if(aonIconButton){
            aonIconButton.style.position = "relative";
            aonIconButton.classList.add("iconArrow");
            const icon = aonIconButton.querySelector(`aon-icon`);
            if(icon) 
                icon.size = "20px";
        }
    }

    isAlta(){
        return this.data && this.data.situation && this.data.situation.indexOf("AL")>=0;
    }
}

window.customElements.define('aon-alta-directa', AonAltaDirecta);
