import { AonElement } from '../../../components/AonElement.js';
import { setValueName, serializeForm, disabledForm, sortBy } from '../../../services/utils.js';
import { getRlce, getContractType, getOccupation, getQuoteGroup, sendAlta, sendBaja, getJourneyType, getIpfxnaf, getNafxipf, getQuoteType, updateContract, getCccForActivity, getCodBaja, getWorkersCollective, getApplicationParameters, openFileBase64 } from '../../../services/service.js'
import { ToolbarType } from '../../../models/enums.js';
import { ACTION_COMUNICA, APP_PARAMS_PAYROLL, CONTRACT_OPTIONS, PAYROLL_VIEWS } from '../PayrollEnums.js';
import { CONSTANT, CSS, EVENT, MSG } from '../../../environments/environments.js';
import { createBajaDialogContent, createFormComunica, createEnterpriseData, createEmployeeData, createContractData, createContractDataMdCtz, createQuoteMonthly, createAsociativeSA } from '../createComponent.js';
import { AonDateUtils } from '../../utils/AonDateUtils.js';
import { CreateComponent } from '../../../components/CreateComponent.js';
// import * as LS from '../../../services/localStorageService.js';

export class AonAltaDirecta extends AonElement {
    _contract;
    ACTION;
    APP_PARAMS;
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
        this.APP_PARAMS = [];
    }

    build() {
        this.paintView();
        this.buildToolbar();
        this.initGets().then(()=>{
            this.eventListeners();
            if(this.data){
                const toolbarEl = this.getElement(this.TOOLBAR);

                if(toolbarEl && this.isEdit()) {
                    toolbarEl.title = 'Editar contrato';
                }

                this.edit(this.data);
            } else {
                this.enterpriseDataDefault();
            } 
        });
    }


    paintView() {
        this.applicationEl.removeToolbarOptions();

        const toolbar = CreateComponent.createAonToolbar({ id:this.TOOLBAR, type:ToolbarType.SECONDARY, title:"Alta Directa1"});
        this.appendChild(toolbar);

        createFormComunica(this.id, this);

        let aonEnterpriseCard = this.getElement(`${this.id}EmpresaCard`);
        createEnterpriseData(aonEnterpriseCard.getContent());

        let aonEmployeeCard = this.getElement(`${this.id}TrabajadorCard`);
        createEmployeeData(aonEmployeeCard.getContent(),  this.id);
 
        let aonContratoCard = this.getElement(`${this.id}ContratoCard`);
        createContractData(aonContratoCard.getContent(), this.isManager() && !(this.data && this.data.fra) );

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

        if(this.data && this.data.fra && this.data.contract) {
            let aib = toolbar.addButton2(ACTION_COMUNICA.DUPLICATE, () => this.duplicateMov());
            // let btn = aib.getButton();
            // if(btn)
            //     btn.classList.add(CSS.PULSE);
        }
            
        if(!this.isMobile() && this.data && this.data.fra){
            toolbar.addButton2(ACTION_COMUNICA.INFORMES, (ev) => this.openDialogReports(ev));
            this.setStyleIconSegSocial(toolbar, ACTION_COMUNICA.INFORMES.id);
        }

        if(this.isEdit() && this.isAlta() && this.isManager()) { 
            toolbar.addButton2(ACTION_COMUNICA.BAJA, (e) => this.openDialogBaja(e));
        }
    
        if( this.isAlta() || !this.data ){ // ALTA
            toolbar.addButton2(ACTION_COMUNICA.COMUNICAR, () =>  this.formSubmit());
        }

        if(this.data && this.data.fra && this.applicationParentEl.anularCondition(this.data.situation, this.data.fra)){
            toolbar.addButton2(CONTRACT_OPTIONS.DELETE, (e) => this.applicationParentEl.deleteMov(this.data, e));
        }
    
        toolbar.addButton2(ACTION_COMUNICA.BACK, () => this.back());
    }


    async initGets() {
        await Promise.all([
            this.getAppParams(),
            this.getWorkplace(),
            this.getJourneyType(),
            this.getOccupation(),
            this.getRlce(),
            this.getWorkersCollective()
        ]).catch(e=> console.log(e));

        await Promise.all([
            this.getContractType(),
            this.getQuoteGroup()
        ]).catch(e=> console.log(e));
    }

    eventListeners() {
        let workplace = this.getElement('workplace');
        workplace.addEventListener(EVENT.CHANGE, (ev) => this.listCtaCti(ev));

        let ctaCti = this.getElement('ctaCti');
        ctaCti.addEventListener(EVENT.CHANGE, (ev) => this.onChangeCtaCti(ev) );

        let nss = this.getElement(`${this.id}Nss`);
        nss.addEventListener(EVENT.CHANGE, ({ target }) =>  this.comprobarNss(target.value));

        let contractEl = this.querySelector('#contract');
        if(contractEl){
            contractEl.addEventListener(EVENT.CHANGE, (ev) =>this.selectTypeContract(ev));
            let inputSelect = contractEl.querySelector(`input`);
            if(inputSelect)
                inputSelect.addEventListener(EVENT.FOCUS, () => inputSelect.select());    
        }

        let typeJornada = this.getElement('tipo_jornada');
        typeJornada.addEventListener(EVENT.CHANGE, (ev) => this.selectTipojornada(ev));

        this.getElement('horas_convenio').addEventListener(EVENT.CHANGE, () => this.calculoCoef());

        this.getElement('horas').addEventListener(EVENT.CHANGE, () => this.calculoCoef());

        this.getElement('switchDni').addEventListener(EVENT.CHANGE, ({ target }) => {
            let div_apellidos = this.getElement('div_apellidos');
            let dni = this.getElement(`${this.id}Dni`);
            nss.disabled = target.checked;
            dni.disabled = !target.checked;
            dni.value = nss.value = "";
            div_apellidos.hidden = target.checked;
            div_apellidos.hidden = !target.checked;
            nss.removeIcon();
        });

        this.getElement('coef').addEventListener(EVENT.CHANGE, () => this.calculoHoras());

        this.getElement('apellido2IconLabel').addEventListener(EVENT.CLICK, () => this.getNaf());

        this.getElement(`${this.id}IconReset`).addEventListener(EVENT.CLICK, () => this.disabledCardTrabajor(false));

        if(!this.isEdit()){
            this.getElement(`gc`).addEventListener(EVENT.CHANGE, ({target}) =>{
                createQuoteMonthly(target.getDetail(), this.isManager());
            });
        }
    }

    getContract() {
        return serializeForm(this.getElement(`${this.id}Form`));
    }

    enterpriseDataDefault(){
        let workplace = this.getElement('workplace');
        let ctaCti    = this.getElement('ctaCti');

        let options = workplace.getOptions();
        
        if(options.length === 1){
            workplace.setIndexOf(0);
    
            let ctaCtiOne  = options[0].cccs && 1 === options[0].cccs.length;
            if(ctaCtiOne){
                ctaCti.setIndexOf(0);
                this.getElement(`${this.id}Nss`).focus();
            } else {
                ctaCti.focus();
            }
        } else {
            workplace.focus();
        }
    }

    edit(data) {

        if(this.isEdit()){
            this.ACTION = "UPDATE";
        }
       
        let obj = { 
            ...data, 
            name: data.name, 
            fecha: data.fra
        };

        if (data.ocup){
            obj.ocup = data.ocup.toString().toLowerCase();
        }
      
        if (data.coef) {
            obj.tipo_jornada = "semanal";
            let coefStr = data.coef;
            if(coefStr.indexOf(".")>=0)
                coefStr = parseFloat(coefStr) * 1000;
            obj.coef = parseInt(coefStr.toString().replace(',', ''));
        }

        for (const property in obj) {
            setValueName(property, obj[property]);
        }

        this._contract = obj; //contrato

        //COEF
        if(obj.coef){
            this.getElement("coef").value = obj.coef;
        }
       
        //seleccionar workplace;
        const workplace = this.getElement('workplace');
        const workplaceInput = workplace.querySelector('aon-input');
        const options = workplace.getOptions();
        if (workplace && workplaceInput && options) {
            const {name:nameWp} = options.find((r) => r.cccs.some(rs => rs.cccRegimeCode === obj.regime && rs.ccc === obj.ctaCti) === true);
            if (nameWp) workplaceInput.value = nameWp;
        }

        //seleccionar ccc;
        const ctaCtiInput = this.getElement('ctaCtiInput');
        if (ctaCtiInput) ctaCtiInput.value = obj.regime + ' - ' + obj.ctaCti;

        //tipear el tipo de contrato
        const contract = this.querySelector('#contract > aon-input');
        if (obj.contract && contract && !contract.value) {
            contract.value = obj.contract;
        } else { //seleccionamos por tipo de cuenta si no tiene tipo de contrato
            this.selectWorkPlace(workplace, obj);
        }

        //calculo horas
        this.calculoHoras();

        //ocultar switch
        let switchDni = this.getElement('switchDni');
        
        if (switchDni) {
            switchDni.parentNode.hidden = true;
        }

        //extender dni y nss
        let nss = this.getElement(`${this.id}NssDiv`);
        let dni = this.getElement(`${this.id}DniDiv`);

        if (nss && dni){
            nss.parentNode.className = dni.parentNode.className = `${CSS.AON_COL_SM_12} ${CSS.AON_COL_MD_6}`;
        }

        //COLLECTIVE
        const collectiveEl = this.getElement('collective');

        if(collectiveEl && obj && obj.fra){
            collectiveEl.parentNode.style.display = 'none';
        }
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

    onChangeCtaCti({detail}){
        let regime = detail.cccRegimeCode;
        this.getElement('regime').setAttribute('value', regime);
        if(!this.isEdit()){
            let md_ctz = this.getElement("md_ctz");
            if(md_ctz && md_ctz.parentNode){
                md_ctz.parentNode.remove();
            }
            
            if(regime === "0163"){
                let aonCard = this.getElement(this.id+"ContratoCard");
                createContractDataMdCtz(aonCard.getContent());
            } 
        }
    }

    selectWorkPlace(workplace, {regime, ctaCti}){
        if (workplace && workplace.options) {
            const options = JSON.parse(workplace.options);
            for (const property in options) {
                if (property && options[property] && options[property].cccs) {
                    const res  = options[property].cccs.find(({cccRegimeCode, ccc}) => cccRegimeCode === regime && ccc === ctaCti);
                    if (res && res.type) {
                        this.selectTypeCto(res.type);
                        break;
                    }
                }
            }
        }
    }

    selectTypeCto(type) {
        const contract = this.querySelector('#contract > aon-input');
        getQuoteType(type).then(({name})=>{
            if (contract && !contract.value && name){
                contract.value = name;
            }
        });
    }

    selectTypeContract({ detail }) {
        if (detail) {
            let partial = detail.partial;
            let divParcial = this.getElement('div_parcial');
            let hourEl = this.getElement('horas_convenio');
            this.getElement('coef').value = "";
    
            if (partial){ //si es parcial
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
                groupedGeozone.forEach((cccsOld, name)=>{
                    let cccs = [];
                    if(cccsOld && cccsOld.length){
                        cccs = cccsOld.filter( (value,index)=>cccsOld.findIndex((m) => m.ccc === value.ccc) === index );
                    }
                    
                    geozones.push({
                        cccs,
                        name,
                        value:name
                    });
                })
                let workplace = this.getElement('workplace');
                workplace.setOptions(geozones);
            }
        } catch (error) { 
            console.error(error);
        }
    }

    listCtaCti({ detail }) {
        if (detail) {
            try {
                const { cccs } = detail;
                let ctaCti = this.getElement('ctaCti');
                let options = cccs
                .map(r => ({ ...r, name: `${r.cccRegimeCode} - ${r.ccc}`, value: r.ccc }));
                ctaCti.setOptions(options);
                if(options.length===1)
                    ctaCti.setIndexOf(0);
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

    async getContractType() {
        try {
            let resp = await getContractType();

            if(!this.isManager()){
                const { APP_COMUNICA_CONTRACTS } = this.APP_PARAMS;
                if(APP_COMUNICA_CONTRACTS){
                    const contract = this.data && this.data.contract ? this.data.contract : "";
                    const enabled = APP_COMUNICA_CONTRACTS.split(',');
                    if(enabled.length){
                        resp = resp.filter(({value})=> enabled.some(v=> v == value) || value == contract );
                    }
                }
            }

            let options = resp.map(r => ({ ...r, name: `${r.value} - ${r.name}`, value: r.value}));
            
            this.querySelector('#contract').setOptions(options);
        } catch (error) { }
    }

    async getQuoteGroup() {
        try {
            let resp = await getQuoteGroup();

            if(!this.isManager()){
                const { APP_COMUNICA_QUOTE_GROUP } = this.APP_PARAMS;
                if(APP_COMUNICA_QUOTE_GROUP){
                    const gc = this.data && this.data.gc ? this.data.gc : "";
                    const enabled = APP_COMUNICA_QUOTE_GROUP.split(',');
                    if(enabled.length){
                        resp = resp.filter(({value})=> enabled.some(v=> v == value) || value == gc  );
                    }
                }
            }

            let options = sortBy(resp.map(r => ({ ...r, name: `${r.name}`, value: r.value})), 'name', 'asc');

            this.getElement('gc').setOptions(options);
        } catch (error){}
    }

    async getJourneyType() {
        let tipo_jornada = this.getElement('tipo_jornada');
        try {
            const resp = getJourneyType();
            const options = resp.map(r => ({ ...r, name: `${r.name}`, value: r.value }) );
            tipo_jornada.setOptions(options);
            tipo_jornada.value = options[0].value;
        } catch (error) { }
    }

    async getOccupation() {
        try {
            let resp = await getOccupation();
            resp = sortBy(resp, 'value', 'asc');
            this.getElement('ocup').setOptions(resp.map(r => ({ name: `${r.name}`, value: r.value})));
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

    async getWorkersCollective() {
        try {
            let collective = this.getElement('collective');
            if(collective){
                let resp = await getWorkersCollective();
                collective.setOptions( resp.map(r =>  ({ ...r, name: `${r.value} - ${r.name}`, value: r.value})) );
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
        this.getElement('coef').value = coef;
    }

    calculoHoras() {
        let horas_convenio = this.getElement('horas_convenio').value;
        let coef = this.getElement('coef').value;
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
            const {name, ipf} = resp;

            setValueName('name', name);

            if(ipf){
                setValueName('ipf', ipf.toString().substring(1));
            }

            if(resp.nss){
                setValueName('nss', resp.nss);
            }
       
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
            const resp = await sendAlta(this.getContract());

            this.applicationParentEl._movements = [];
            this.showToast({ message: MSG.PROCESSED_MOVEMENT, type: CONSTANT.SUCCESS, delay: 3000 });

            if(resp && resp.file){
                openFileBase64(resp.file, "application/pdf").catch(console.error);
            }

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
            const frv = this.getElement("frv");
            const asociativeSA = this.getElement("asociativeSA");
            const resp = await sendBaja({
                ...this.data, 
                fechaBaja: fechaBajaEl.value, 
                situation: codBajaEl.value, 
                frv: frv.value ? frv.value : undefined, 
                asociativeSA: asociativeSA && asociativeSA.value ? asociativeSA.value : undefined
            });

            this.applicationParentEl._movements = [];
            this.showToast({ message: MSG.PROCESSED_MOVEMENT_BJ, type: CONSTANT.SUCCESS, delay: 3000 });

            this.applicationEl.getOptionDialog().close();

            if(resp && resp.file){
                openFileBase64(resp.file, "application/pdf").catch(console.error);
            }

            this.back();
        } catch (error) {
            this.showToast(error);
        }
        this.applicationEl.stopLoading();
    }

    async update() {
        this.applicationEl.startLoading();
        let cto_new = this.getContract();
        const cto_old = this._contract;
        for (const property in cto_new) 
            if (cto_new[property] && (cto_old[property] != cto_new[property])) 
                cto_new[`${property}_edit`] = true;
        try {
            const resp = await updateContract(cto_new);
            let message = `No existen cambios en el contrato`;

            if( resp.errors && resp.errors.length > 0 )
                message = resp.errors.join(".");
            else if(resp.contract_edit === true) {
                message = MSG.UPDATED_CONTRACT;
                this.applicationParentEl._movements = [];
            } 

            this.showToast({ message, delay: 4500 });
        } catch (error) {
            this.showToast(error);
        }
        this.applicationEl.stopLoading();
    }

    back() {
        this.applicationParentEl.showView(PAYROLL_VIEWS.AON_MOVEMENTS_LIST);
    }

    async getNaf() {
        const contrato = this.getContract();
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
        let fields = this.querySelectorAll(`#${this.id}TrabajadorCard aon-input`);
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
            button.disabled = new Date(fechaEl.value).isValid() ? false : true;
        })

        //list cod de baja
        const codBajaEl = this.getElement("codBaja");
        getCodBaja().then(cods => {
            codBajaEl.setOptions(
                cods.map(r=>({value:r.value, name:r.value+" - "+r.name }))
            );
            codBajaEl.value = "93";
        });

        let frv = this.getElement('frv');

        let dayVacation = this.getElement('dayVacation');
        dayVacation.setAlign('left');
        dayVacation.onInput(({target}) =>{
            let value = target.value;
            
            if(value && !isNaN(parseInt(value)) ){
                value = parseInt(value);
                let fechaBaja = new Date(fechaEl.value);
                if(fechaEl && fechaBaja.isValid()){
                    frv.setDate( fechaBaja.addDay(value) );
                    createAsociativeSA(true);
                }
            } else {
                frv.value = "";
                createAsociativeSA();
            }
        });

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

    openDialogReports(ev){
        ev.preventDefault();
        let rect = ev.target.getBoundingClientRect();
        let x = ev.clientX - rect.left + 180;
        let y = ev.clientY - rect.top;

        const top  = rect.top + y;
        const left = rect.left + x;

        let d = this.getApplication().getOptionDialog();
        d.getContent().style.width = "133px";
        let moreActions = [];

        //---IDC
        moreActions.push({
            ...CONTRACT_OPTIONS.IDC,
            fn: () =>  this.applicationParentEl.getIdc(this.data)
        });

        //----TA
        moreActions.push({
            ...CONTRACT_OPTIONS.TA,
            fn: () =>  this.applicationParentEl.getTa(this.data)
        });

        d.setMenuOptions(moreActions, top, left);
        d.open();
    }

    duplicateMov(){
        this.applicationParentEl.showView(PAYROLL_VIEWS.AON_ALTA_DIRECTA, {...this.data, fra:null, status:null, situation:"AL"}).then(el=>
            disabledForm(`${el.id}TrabajadorCard`)
        );
    }

    isAlta(){
        return this.data && this.data.situation && this.data.situation.indexOf("AL")>=0;
    }

    async getAppParams(){
		if(!this.APP_PARAMS.length){
			try {
				await getApplicationParameters({
					params:[
                        APP_PARAMS_PAYROLL.APP_COMUNICA_CONTRACTS,
                        APP_PARAMS_PAYROLL.APP_COMUNICA_QUOTE_GROUP
					]
				}).then(params=>{
					let newResp = [];
					params
					.filter(p => p.value)
					.forEach(p => 
						newResp[p.name] = p.value
					);
					this.APP_PARAMS = newResp;
				});
			} catch (e) {
				console.log("error getAppParams", e);
			}
		}
		return this.APP_PARAMS;
	}

    isEdit(){
        return this.data && this.data.fra;
    }

    isManager(){
        let dur = this.applicationParentEl.getDur();
        return dur.isComunicaManager() || dur.isSaltraManager();
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
}

window.customElements.define('aon-alta-directa', AonAltaDirecta);
