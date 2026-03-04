import { CSS, MATERIAL_ICONS, TAG } from '../../environments/environments.js';
import { getTimeControl, getDomainUserRoles, getDomainNotice, getUserNotice, getTaskCount, getBanks, getEmployeeSalary} from '../../services/service.js';
import { AonElement } from '../../components/AonElement';
import '../invoice/aon-invoice-panel.js';
import * as UTILS from '../accounting/AccountingUtils.js'
import { AonDragLeftNotification } from './aon-dragleft-notification.js';
import { AonSignMobile } from '../timecontrol/aon-sign-mobile.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { getTaskHolder } from '../../services/service.js';
import { getAuth } from '../../services/service.js';
import { getAccounting, getPeriods } from '../../services/accountingService.js';
import { AonLaboral } from '../laboral/aon-laboral.js';
import { AonIcon } from '../../components/aon-icon.js';
import { isEmptyObject, sortBy, waitEl } from '../../services/utils.js';
import { AonApps } from '../aon-apps.js';
import { getWorkgroups } from '../../services/workgroupService.js';
import { AonTimecontrol } from '../timecontrol/aon-timecontrol.js';
import { AonCalendarContainer } from '../timecontrol/time-control/agenda/aon-calendar-container.js';

export class AonMobileHome extends AonElement {

    DIV_GENERAL;
    NOTIFICATION_DRAGLEFT;
    DIV_WIDGETS;
    WIDGET_TC;
    WIDGET_NOMINA;
    WIDGET_SOLICITUDES_RECIBIDAS;
    WIDGET_SOLICITUDES_ENVIADAS;
    WIDGET_FACTURAS_PENDIENTES;
    WIDGET_BANKS;
    WIDGET_INGRESOS;
    WIDGET_GASTOS;
    selectedElement;
    selectedPeriod
    accounts;
    arrIncome;
    arrOutgoings;
    outgoings;
    selAccounts;
    ACCOUNTS;
    accounts;
    PERIODS;
    params = {
        domain: localStorage.getItem("aon_domain_id"),
        domainName: localStorage.getItem("aon_domain_name"),
        user: "",
        level: 5,
        byMonth: true,
      };
    BANKS = [];
    _filter;

	constructor () {
		super();
	}

    connectedCallback () {
        this.initialize();
        getDomainUserRoles({}).then(r => {
            this.dur = new DomainUserRoles(r);
            this.build();
        });
    }
    

	initialize() {
		this.id = 'aonMobileHome';
        this.DIV_GENERAL = this.id + "DivGeneral";
        this.NOTIFICATION_DRAGLEFT = this.id + "NotificationDragLeft";
        this.DIV_WIDGETS = this.id + "DivWidgets";
        this.WIDGET_TC = this.id + "WidgetTC";
        this.WIDGET_NOMINA = this.id + "WidgetNomina";
        this.WIDGET_SOLICITUDES_RECIBIDAS = this.id + "WidgetSolicitudesRecibidas";
        this.WIDGET_SOLICITUDES_ENVIADAS = this.id + "WidgetSolicitudesEnviadas";
        this.WIDGET_FACTURAS_PENDIENTES = this.id + "WidgetFacturasPendientes";
        this.WIDGET_BANKS = this.id + "WidgetBanks";
        this.WIDGET_INGRESOS = this.id + "WidgetIngresos";
        this.WIDGET_GASTOS = this.id + "WidgetGastos";
        
	}

    async build() {

        if (!this.params.domain || !this.params.domainName) {
              try {
                let company = JSON.parse(localStorage.getItem("company"));
                this.params.domain = company.id;
        
                this.params.domainName = company.domain;
              } catch (error) {
                console.log(error);
              }
            }
        if (this.getDur().isAccounting()){
            this.PERIODS = await getPeriods(this.params).catch((error) => {
                console.log(error);
                return [];
            });
            let currentYear = new Date().getFullYear();
            let currentYearPeriods = this.PERIODS.filter((p) => 
                new Date(p.initiationDate).getFullYear() === currentYear
            );      
            let selectedPeriod = null; 

            if (currentYearPeriods.length > 0) {
                selectedPeriod = currentYearPeriods.reduce((latest, p) => 
                    new Date(p.initiationDate) > new Date(latest.initiationDate) ? p : latest
                );
            } else if (this.PERIODS.length > 0) {
                selectedPeriod = this.PERIODS.reduce((latest, p) => 
                    new Date(p.initiationDate) > new Date(latest.initiationDate) ? p : latest
                );
            }
            this.selectedPeriod = selectedPeriod;
        }
        
        
        this.accounts = await this.getData();
        if(this.accounts)
            this.calculateYearlyData(this.accounts);

        let divGeneral = this.createElement(TAG.DIV);
        divGeneral.id = this.DIV_GENERAL;

        let notificationDrag = new AonDragLeftNotification();
        notificationDrag.id = this.NOTIFICATION_DRAGLEFT;
        notificationDrag.className = "aonMobileHomeNotificationDrag";
    
        const hasNotifs = await notificationDrag.hasNotifications(); 
        console.log("NOTIFICATIONS: " + hasNotifs);

        if (hasNotifs) {
            let tituloNotis = this.buildTitle("Notificaciones");
            divGeneral.appendChild(tituloNotis);
        }

        divGeneral.appendChild(notificationDrag);
        
        // Control horario
		if(this.isMobile() && this.getDur().isTimecontrol()){
			let timeControlTitleWidgets = this.buildTitle("Control Horario");
			divGeneral.appendChild(timeControlTitleWidgets); 
			
			let divWidgets2 = this.createElement(TAG.DIV);
		    divWidgets2.id = this.DIV_WIDGETS + "2";
		    divWidgets2.className = "aonDivWidgetsMobile";
			
			let timeControlWidget =  { id: this.WIDGET_TC, builder: this.widgetTimeControl, show: this.getDur().isTimecontrol()}
			if (timeControlWidget.show) {
		        const builtWidget = await timeControlWidget.builder.call(this);
		        if(builtWidget) divWidgets2.appendChild(builtWidget);
		    }
		    
		    divGeneral.appendChild(divWidgets2); 
		}

        let tituloWidgets = this.buildTitle("Información");
        divGeneral.appendChild(tituloWidgets); 
    
        let divWidgets = this.createElement(TAG.DIV);
        divWidgets.id = this.DIV_WIDGETS;
        divWidgets.className = "aonDivWidgetsMobile";
    
        const widgetOrder = [
            { id: this.WIDGET_TC, builder: this.widgetTimeControl, show: this.getDur().isTimecontrol() && !this.isMobile()},
            { id: this.WIDGET_NOMINA, builder: this.widgetNomina, show: this.getDur().isPayroll()},
            { id: this.WIDGET_SOLICITUDES_RECIBIDAS, builder: () => this.widgetSolicitudes("recibidas"), show: this.getDur().isMessenger() },
            { id: this.WIDGET_SOLICITUDES_ENVIADAS, builder: () => this.widgetSolicitudes("enviadas"), show: this.getDur().isMessenger() },
            { id: this.WIDGET_FACTURAS_PENDIENTES, builder: this.widgetFacturasPendientes, show: this.getDur().isInvoice() },
            { id: this.WIDGET_BANKS, builder: this.widgetBanks, show: this.getDur().isAccounting() },
            { id: this.WIDGET_INGRESOS, builder: () => this.widgetIngresosGastos("ingresos"), show: this.getDur().isAccounting() },
            { id: this.WIDGET_GASTOS, builder: () => this.widgetIngresosGastos("gastos"), show: this.getDur().isAccounting() },
        ];

        for (const widget of widgetOrder) {
            try {
                if (widget.show) {
                    const builtWidget = await widget.builder.call(this);
                    if(builtWidget) divWidgets.appendChild(builtWidget);
                }
            } catch (error) {
                //console.log(`Widget ${widget.id} no está disponible:` + error);
            }
        }
    
        divGeneral.appendChild(divWidgets);    

        let apps = new AonApps();
        divGeneral.appendChild(apps);

        this.appendChild(divGeneral);
    }

    async widgetTimeControl() {
		let timeControlPanel = this.createElement(TAG.DIV);
        timeControlPanel.id = 'timeControlPanel';
		
        let widgetTC = this.createElement(TAG.DIV);
        widgetTC.id = this.WIDGET_TC;
        widgetTC.className = "aonWidgetTCMobile";

        let aonSign = new AonSignMobile();
        aonSign.showInfo = false;
        if (!this.getDur().isTimecontrol()) return null;
        const r = await getTimeControl();
        aonSign.setTimeControl(r);
        
        if(this.isMobile())
			timeControlPanel.classList.add('aonWidgetTCMobileBig');
        
        widgetTC.appendChild(aonSign);
        
        if(this.isMobile())
        	this.createTimeControlApps(widgetTC);
        
        timeControlPanel.appendChild(widgetTC);
        
        timeControlPanel.addEventListener('click', () => {
            this.rootPanel(new AonTimecontrol());
        });
        
        return timeControlPanel;
	}
	
	createTimeControlApps(parent){
		let ul = this.createElement(TAG.UL);
		ul.id = "timeControlPanelAppSelection";
		ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.AON_LIST_GROUP);
		
		ul.appendChild(this.createTimeControlAppInfo());
		ul.appendChild(this.createTimeControlAppSchedule());
		
		parent.appendChild(ul);
	}
	
	createTimeControlAppInfo(){
		let li = this.createElement(TAG.LI);
		li.id = "timeControlPanelAppSelection-Info";
		li.classList.add(CSS.AON_LIST_GROUP_ITEM);
		li.classList.add(CSS.AON_APP_LI);
		li.classList.add("fixLi");
		li.style.borderRight = '0px';
		li.style.borderLeft = '0px';
		li.style.cursor = 'pointer';
		
		li.addEventListener('click', (ev) => {
			ev.stopPropagation();
			
			let aonAgendaContainer = new AonCalendarContainer();
			this.rootPanel(aonAgendaContainer);
			
			/*
			let timeControl = new AonTimecontrol();
            this.rootPanel(timeControl);
			waitEl('#aonMobileTcAppHoy').then(el => el.click());
			*/
			
		});
		
		let span = this.createElement(TAG.SPAN);
		
		span.style.margin = '20px';
		
		let icon = this.createSpan();
		icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		icon.id = "aonMobileSelectionIcon-Info";
		icon.innerHTML = MATERIAL_ICONS.ALARM;
		icon.style.backgroundColor = "#f0f0f0";
		icon.style.color = "black";
		icon.style.fontVariationSettings = "'FILL' 0, 'wght' 300, 'GRAD' 0, 'opsz' 24";
		icon.style.borderRadius = "5px";
		span.appendChild(icon);	
		
		li.appendChild(span);
		
		return li;
	}
	
	createTimeControlAppSchedule(){
		let li = this.createElement(TAG.LI);
		li.id = "timeControlPanelAppSelection-Schedule";
		li.classList.add(CSS.AON_LIST_GROUP_ITEM);
		li.classList.add(CSS.AON_APP_LI);
		li.classList.add("fixLi");
		li.style.borderRight = '0px';
		li.style.borderLeft = '0px';
		li.style.cursor = 'pointer';
		
		li.addEventListener('click', (ev) => {
			ev.stopPropagation();
			this.rootPanelHtml('<aon-messenger></aon-messenger>');
		});
		
		let span = this.createElement(TAG.SPAN);
		
		span.style.margin = '20px';
		
		let icon = this.createSpan();
		icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		icon.id = "aonMobileSelectionIcon-Schedule";
		icon.innerHTML = MATERIAL_ICONS.SPEAKER_NOTES;
		icon.style.backgroundColor = "#f0f0f0";
		icon.style.color = "black";
		icon.style.fontVariationSettings = "'FILL' 0, 'wght' 300, 'GRAD' 0, 'opsz' 24";
		icon.style.borderRadius = "5px";
		span.appendChild(icon);
		
		li.appendChild(span);
		
		return li;
	}

    widgetNomina() {
        const widgetNomina = this.createElement(TAG.DIV);
        widgetNomina.id = this.WIDGET_NOMINA;
        widgetNomina.className = "aonWidgetNominaMobile";
    
        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetNominaMobileTitulo";
    
        let nominaTitulo = this.createElement(TAG.SPAN);
        nominaTitulo.innerHTML = "Nómina";
        nominaTitulo.style.fontWeight = "bold";
        titulo.appendChild(nominaTitulo);
    
        let fecha = this.createElement(TAG.SPAN);
        fecha.className = "aonWidgetNominaMobileFecha";
        titulo.appendChild(fecha);
    
        let nomina = this.createElement(TAG.DIV);
        nomina.className = "aonWidgetNominaMobileNomina";
        nomina.innerHTML = `<i class="${CSS.AON_COMPANY_FILTER_LOADING}"></i>`;
    
        widgetNomina.appendChild(titulo);
        widgetNomina.appendChild(nomina);
    
        this.obtenerDatosNominas()
            .then(ultimoSalario => {
                if (!ultimoSalario || !ultimoSalario.totalLiquid || !ultimoSalario.date) {
                   widgetNomina.remove();
                }
    
                let salario = ultimoSalario.totalLiquid;
                let date = ultimoSalario.date; 
    
                let salarioNumerico = parseFloat(salario.toString().replace(/[^\d.-]/g, ""));
                if (isNaN(salarioNumerico)) {
                    salarioNumerico = 0; 
                }
                let salarioFormateado = this.formatCurrency(salarioNumerico);
    
                fecha.innerHTML = date;
                nomina.innerHTML = `${salarioFormateado}€`;
                nomina.classList.remove(CSS.AON_COMPANY_FILTER_LOADING);
            })
            .catch(error => {
                console.error("Error obteniendo datos de nómina:", error);
                nomina.innerHTML = "0";
            });
    
        widgetNomina.addEventListener('click', () => {
            this.rootPanel(new AonLaboral());
        });
    
        return widgetNomina;
    }
    
    widgetSolicitudes(tipo) {
        const widgetSolicitudes = this.createElement(TAG.DIV);
        widgetSolicitudes.id = this.id+"Widget"+tipo.charAt(0).toUpperCase() + tipo.slice(1).toLowerCase();
        widgetSolicitudes.className = "aonWidgetSolicitudesMobile";
    
        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetSolicitudesMobileTitulo";
        widgetSolicitudes.appendChild(titulo);
    
        let texto = this.createElement(TAG.SPAN);
        texto.innerHTML = "Solicitudes "+tipo.charAt(0).toUpperCase() + tipo.slice(1).toLowerCase();
        titulo.appendChild(texto);
    
        let segundaLinea = this.createElement(TAG.DIV);
        segundaLinea.className = "aonWidgetSolicitudesMobileSegundaLinea";
        widgetSolicitudes.appendChild(segundaLinea);
    
        let icon = this.createElement(TAG.I);
        icon.className = CSS.MATERIAL_ICONS;
        icon.innerHTML = "outbox";
        icon.classList.add("aonWidgetSolicitudesIcon"+tipo.charAt(0).toUpperCase() + tipo.slice(1).toLowerCase());
        segundaLinea.appendChild(icon);
    
        let numero = this.createElement(TAG.DIV);
        numero.className = "aonWidgetSolicitudesMobileLineaNumero";
        numero.innerHTML = `<i class="${CSS.AON_COMPANY_FILTER_LOADING}"></i>`;
        segundaLinea.appendChild(numero);
    
        this.getSolicitudes().then(solicitudes => {
            numero.innerHTML = tipo == "enviadas" ? solicitudes.enviadas : solicitudes.recibidas;
            numero.classList.remove(CSS.AON_COMPANY_FILTER_LOADING);
        });
    
        return widgetSolicitudes;
    }
    
    widgetFacturasPendientes() {
        const widgetFacturasPendientes = this.createElement(TAG.DIV);
        widgetFacturasPendientes.id = this.WIDGET_FACTURAS_PENDIENTES;
        widgetFacturasPendientes.className = "aonWidgetSolicitudesMobile";
    
        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetSolicitudesMobileTitulo";
        widgetFacturasPendientes.appendChild(titulo);
    
        let textoFacturas = this.createElement(TAG.SPAN);
        textoFacturas.innerHTML = "Facturas Pendientes";
        titulo.appendChild(textoFacturas);
    
        let segundaLinea = this.createElement(TAG.DIV);
        segundaLinea.className = "aonWidgetSolicitudesMobileSegundaLinea";
        widgetFacturasPendientes.appendChild(segundaLinea);
    
        let icon = new AonIcon();
        icon.className = CSS.MATERIAL_ICONS;
        icon.innerHTML = "monitoring";
        icon.classList.add("aonWidgetSolicitudesIconPendientes");
        segundaLinea.appendChild(icon);
    
        let numeroFacturas = this.createElement(TAG.DIV);
        numeroFacturas.className = "aonWidgetSolicitudesMobileLineaNumero";
        numeroFacturas.innerHTML = `<i class="${CSS.AON_COMPANY_FILTER_LOADING}"></i>`;
        segundaLinea.appendChild(numeroFacturas);
    
        let requestPromise = localStorage.getItem('company')
            ? (!this.getDur().isEmployee() ? getDomainNotice() : Promise.resolve(null))
            : this.buildCompany().then(() => !this.getDur().isEmployee() ? getUserNotice() : null);

        requestPromise.then(notice => {
            numeroFacturas.innerHTML = notice && notice.invoice && notice.invoice.inbox.count
                    ? " " + notice.invoice.inbox.count : " 0";
                    numeroFacturas.classList.remove(CSS.AON_COMPANY_FILTER_LOADING);
        }).catch(e => {
            numeroFacturas.innerHTML = " 0";
            numeroFacturas.classList.remove(CSS.AON_COMPANY_FILTER_LOADING);
        });
    
        widgetFacturasPendientes.addEventListener('click', () => {
            this.rootPanelHtml('<aon-invoice-panel></aon-invoice-panel>');
        });
    
        return widgetFacturasPendientes;
    }   

    widgetBanks() {
        let widgetBanks = this.createElement(TAG.DIV);
        widgetBanks.id = this.WIDGET_BANKS;
        widgetBanks.className = "aonWidgetNominaMobile";
    
        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetNominaMobileTitulo";
    
        let bancosTitulo = this.createElement(TAG.SPAN);
        bancosTitulo.innerHTML = "Bancos";
        bancosTitulo.style.fontWeight = "bold";
        titulo.appendChild(bancosTitulo);
    
        let bancos = this.createElement(TAG.SPAN);
        bancos.innerHTML = "(...)"; 
        bancos.className = "aonWidgetNominaMobileFecha";
        titulo.appendChild(bancos);
    
        let total = this.createDiv();
        total.innerHTML = `<i class="${CSS.AON_COMPANY_FILTER_LOADING}"></i>`; 
        total.className = "aonWidgetNominaMobileNomina";
        total.style.marginTop = "10px";
    
        widgetBanks.appendChild(titulo);
        widgetBanks.appendChild(total);
    
        this.getCompanyBanks().then((banks) => {
            if (banks.length == 0) {
                widgetBanks.remove();
            }
            let totalBanks = this.getTotal(banks);
            if (totalBanks > 1000000) 
                total.style.fontSize = "1.3rem";
            bancos.innerHTML = `(${banks.length})`;
            total.innerHTML = totalBanks+"€"; 
        });
    
        return widgetBanks;
    }

    widgetIngresosGastos(tipo) {
        const widgetIG = this.createElement(TAG.DIV);
        widgetIG.id = this.id+"Widget"+tipo.charAt(0).toUpperCase() + tipo.slice(1).toLowerCase();
        widgetIG.className = "aonWidgetNominaMobile";
    
        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetNominaMobileTitulo";

        let tituloTexto = this.createElement(TAG.SPAN);
        tituloTexto.innerHTML = tipo.charAt(0).toUpperCase() + tipo.slice(1).toLowerCase();
        tituloTexto.style.fontWeight = "bold";
        titulo.appendChild(tituloTexto);

        let fecha = this.createElement(TAG.SPAN);
        fecha.innerHTML = new Date().getFullYear();
        fecha.className = "aonWidgetNominaMobileFecha";
        titulo.appendChild(fecha); 
    
        let numero = this.createElement(TAG.DIV);
        let amount = tipo === "ingresos" ? this.income : this.outgoings;

        numero.innerHTML = `${this.formatCurrency(amount)}€`;

        if (amount > 1000000)
            numero.style.fontSize = "1.3rem";
        if (amount === 0)
            numero.innerHTML = "0"

        numero.className = "aonWidgetNominaMobileNomina";
    
        widgetIG.appendChild(titulo);    
        widgetIG.appendChild(numero);

        return widgetIG;
    }  
    
    async getCompanyBanks() {
        let company = JSON.parse(localStorage.getItem("company"));
        if(!this.BANKS.length){
            try {
                if (!this.getDur().isAccounting()) return null;
                const banks = await getBanks({id: company.registry});
                if(banks){
                    this.BANKS = sortBy(banks,'alias')
                    .filter((bank) => bank.active == true);
                }
                return this.BANKS;
                } catch (error) {
                    console.error(error);
                    this.showError(error);
            }
        } else {
            return this.BANKS;
        }
    }

    buildTitle(title) {
		let div = this.createElement(TAG.DIV);
		div.style.color = 'gray';
        div.style.paddingLeft = "20px";
        div.style.paddingTop = "20px";
		div.innerHTML = title;
		return div;
	}

    calculateYearlyData(accountsData) {
        let accounts = accountsData.intervals || [];
        this.selectedElement = accounts.filter((acc) =>
          /31\/12\/d*/.test(acc.interval.fromDate)
        )[0];
    
        accounts = UTILS.getOnly6and7(accounts);
    
        if (accounts.length > 1) {
          this.selAccounts = this.selectedElement.statements.filter(
            (state) =>
              state.account &&
              state.account.code &&
              (state.account.code.substring(0, 1) == "6" ||
                state.account.code.substring(0, 1) == "7")
          );
    
          this.arrIncome = this.selAccounts
            .filter(
              (a) =>
                a.account.code != null &&
                a.account.code.length > 2 &&
                a.account.code.substring(0, 1) == "7"
            )
            .sort((a, b) => b.credit - b.debit - (a.credit - a.debit));
    
          this.income =
            this.arrIncome.length != 0
              ? this.arrIncome
                  .map((a) => a.credit - a.debit)
                  .reduce((a, b) => a + b)
              : 0;
    
          this.arrOutgoings = this.selAccounts
            .filter(
              (a) =>
                a.account.code != null &&
                a.account.code.length > 2 &&
                Number.parseInt(a.account.code.substring(0, 2)) >= 62 &&
                Number.parseInt(a.account.code.substring(0, 2)) <= 67
            )
            .sort((a, b) => b.debit - b.credit - (a.debit - a.credit));
    
          this.outgoings =
            this.arrOutgoings.length != 0
              ? this.arrOutgoings
                  .map((a) => a.debit - a.credit)
                  .reduce((a, b) => a + b)
              : 0;
        }
    }

    async getData() {
        if (this.filter) {
            this.selectedPeriod = this.PERIODS.find(
            (p) => p.name == this.filter.year
            );
            this.params.level = this.filter.detail;
        }
        console.log(this.selectedPeriod);

        if (!isEmptyObject(this.PERIODS)) {
            if (this.PERIODS && this.PERIODS.length > 0) {
                this.params.period = this.selectedPeriod.id;
                this.params.fromDate = this.selectedPeriod.initiationDate;
                this.params.toDate = this.selectedPeriod.deadline;
            }

            if(!this.params.fromDate) { return []; }
            if (!this.getDur().isAccounting()) return null;
            this.ACCOUNTS = await getAccounting(this.params).catch((err) => {
                this.showError(err);
                return null;
            });
        }
        return this.ACCOUNTS;
    }

    getTotal(banks){
        let total = banks.reduce((t, bank) => t + bank.balance, 0);
        return total > 0 ? this.formatCurrency(total) : "0";
    }

    getWorkgroupsStr(workgroups) {
        if (!Array.isArray(workgroups)) {
            return 0;
        }
        
        let all = false;
        const wps = workgroups.map(({ id }) => id);
        if (wps.length) {
            let join = wps.join(",");
            return all ? join + ",all" : join;
        }
        return 0;
    }

    async obtenerDatosNominas() {
        try {
            const filter = this.getApplicationParent()?._filter || {};
            if (!this.getDur().isPayroll()) return null;
            let datos = await getEmployeeSalary(filter); 
            datos = datos.sort((a, b) => new Date(b.endDate) - new Date(a.endDate));
            const ultimoSalario = datos[0]; 
    
            if (ultimoSalario) {
                const date = new Date(ultimoSalario.endDate);
                const opcionesFormato = { year: 'numeric', month: 'short' };
                let fechaFormateada = date.toLocaleDateString('es-ES', opcionesFormato); 
                fechaFormateada = fechaFormateada.replace(
                    /^(.*?)\s/,
                    (mes) => `${mes.trim().charAt(0).toUpperCase() + mes.trim().slice(1).toLowerCase()}. `
                );
    
                return {
                    totalLiquid: ultimoSalario.totalLiquid,
                    date: fechaFormateada.trim(), 
                    filter: filter,
                };
            } else {
                return null; 
            }
        } catch (error) {
            console.error("Error obteniendo el último salario:", error);
            return null;
        }
    }
    
    async getSolicitudes() {
        let filterCount = {};
        if (!this.getDur().isMessenger()) return null;
    
        let taskHolder = await getTaskHolder({});
        let id = taskHolder.id;
    
        let wg = await getWorkgroups({status:"ACTIVE", task_holder: id});
        let workgroups = this.getWorkgroupsStr(wg);
    
        let auth = await getAuth();
        let email = auth.email;
    
        filterCount.email = email;
        filterCount.task_holder = id;
        filterCount.workgroups = workgroups;
    
        let count = await getTaskCount(filterCount);
    
        let enviadas = count.sender !== undefined && count.sender !== null
            ? count.sender + ""
            : "0";

        let recibidas = count.task_holder !== undefined && count.task_holder !== null
        ? count.task_holder + ""
        : "0";
    
        return {enviadas, recibidas};
    }

    formatCurrency(valor) {
        if (isNaN(valor) || valor === null || valor === undefined) 
            return "0";
        const partes = Number(valor).toFixed(2).split(".");
        const parteEnteraConMiles = partes[0].replace(/\B(?=(\d{3})+(?!\d))/g, ".");
        return `${parteEnteraConMiles},${partes[1]}`;
    }
    
    getDur() {
		return this.dur;
	}
}
if(!window.customElements.get(TAG.AON_MOBILE_HOME)){
	window.customElements.define(TAG.AON_MOBILE_HOME, AonMobileHome);
}
