import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { getTimeControl, getEmployeeSalaries, getDomainUserRoles, getDomainNotice, getUserNotice, request, getTaskCount, getSalaryPdf, getBanks } from '../../services/service.js';
import { AonElement } from '../../components/AonElement';
import '../invoice/aon-invoice-panel.js';
import * as LS from '../../services/localStorageService.js';
import { AonDragLeftNotification } from './aon-dragleft-notification.js';
import { AonSignMobile } from '../timecontrol/aon-sign-mobile.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';
import { getTaskHolder } from '../../services/service.js';
import { getAuth } from '../../services/service.js';
import { MESSENGER_VIEWS, TASK_STATUS } from '../messenger/MessengerEnums.js';
import { AonMessengerChat } from '../messenger/aon-messeger-chat.js';
import { AonMessengerList } from '../messenger/aon-messenger-list.js';
import { AonMessenger } from '../messenger/aon-messenger.js';
import { AonLaboral } from '../laboral/aon-laboral.js';
import { AonPayrollList } from '../laboral/payroll/aon-payroll-list.js';
import { AonInvoice } from '../invoice/aon-invoice.js';
import { AonIcon } from '../../components/aon-icon.js';
import { AonAccounting } from '../accounting/aon-accounting.js';
import { formatNumber, sortBy } from '../../services/utils.js';
import { AonApps } from '../aon-apps.js';

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
    BANKS = [];
    taskHolder;

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
        this.taskHolder = {};
	}

    async build() {
        let divGeneral = this.createElement(TAG.DIV);
        divGeneral.id = this.DIV_GENERAL;

        let tituloNotis = this.buildTitle("Notificaciones");
        divGeneral.appendChild(tituloNotis);
    
        let notificationDrag = new AonDragLeftNotification();
        notificationDrag.id = this.NOTIFICATION_DRAGLEFT;
        notificationDrag.className = "aonMobileHomeNotificationDrag";
        divGeneral.appendChild(notificationDrag);

        let tituloWidgets = this.buildTitle("Información");
        divGeneral.appendChild(tituloWidgets);
    
        let divWidgets = this.createElement(TAG.DIV);
        divWidgets.id = this.DIV_WIDGETS;
        divWidgets.className = "aonDivWidgetsMobile";
    
        const widgetOrder = [
            { id: this.WIDGET_TC, builder: this.widgetTimeControl },
            { id: this.WIDGET_NOMINA, builder: this.widgetNomina },
            { id: this.WIDGET_SOLICITUDES_RECIBIDAS, builder: this.widgetSolicitudesRecbidas },
            { id: this.WIDGET_SOLICITUDES_ENVIADAS, builder: this.widgetSolicitudesEnviadas },
            { id: this.WIDGET_FACTURAS_PENDIENTES, builder: this.widgetFacturasPendientes },
            { id: this.WIDGET_BANKS, builder: this.widgetBanks },
        ];

        for (const widget of widgetOrder) {
            try {
                const builtWidget = await widget.builder.call(this);
                if (builtWidget) {
                    divWidgets.appendChild(builtWidget);
                }
            } catch (error) {
                console.log(`Widget ${widget.id} no está disponible`);
            }
        }
    
        divGeneral.appendChild(divWidgets);    

        let apps = new AonApps();
        divGeneral.appendChild(apps);

        this.appendChild(divGeneral);
    }

    async widgetTimeControl() {
        let widgetTC = this.createElement(TAG.DIV);
        widgetTC.id = this.WIDGET_TC;
        widgetTC.className = "aonWidgetTCMobile";

        let aonSign = new AonSignMobile();
        const r = await getTimeControl();
        aonSign.setTimeControl(r);
        widgetTC.appendChild(aonSign);

        return widgetTC;
    }

    async widgetNomina() {
        const widgetNomina = this.createElement(TAG.DIV);
        widgetNomina.id = this.WIDGET_NOMINA;
        widgetNomina.className = "aonWidgetNominaMobile";
    
        const ultimoSalario = await this.obtenerDatosNominas();
        let salario = ultimoSalario.totalLiquid;
        let date = ultimoSalario.date; 
        let filter = ultimoSalario.filter; 
    
        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetNominaMobileTitulo";

        let nominaTitulo = this.createElement(TAG.SPAN);
        nominaTitulo.innerHTML = "Nómina";
        nominaTitulo.style.fontWeight = "bold";
        titulo.appendChild(nominaTitulo);

        let fecha = this.createElement(TAG.SPAN);
        fecha.innerHTML = date;
        fecha.className = "aonWidgetNominaMobileFecha";
        titulo.appendChild(fecha); 
    
        let nomina = this.createElement(TAG.DIV);

        let salarioNumerico = parseFloat(salario.toString().replace(/[^\d.-]/g, ""));
        if (isNaN(salarioNumerico)) {
            salarioNumerico = 0; 
        }
    
        let salarioFormateado = this.formatCurrency(salarioNumerico);
        nomina.innerHTML = `${salarioFormateado}€`;
        nomina.className = "aonWidgetNominaMobileNomina";

        widgetNomina.addEventListener('click', async () => {
            this.rootPanel(new AonLaboral());
        });
    
        widgetNomina.appendChild(titulo);    
        widgetNomina.appendChild(nomina);
    
        return widgetNomina;
    }  

    async widgetSolicitudesRecbidas() {
        const widgetSolicitudesRecibidas = this.createElement(TAG.DIV);
        widgetSolicitudesRecibidas.id = this.WIDGET_SOLICITUDES_RECIBIDAS;
        widgetSolicitudesRecibidas.className = "aonWidgetSolicitudesMobile";

        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetSolicitudesMobileTitulo";
        widgetSolicitudesRecibidas.appendChild(titulo);

        let notice = undefined;
			if(localStorage.getItem('company')) {
				if(!this.getDur().isEmployee()){
					notice = await getDomainNotice();
				}
			} else {
				await this.buildCompany();
				if(!this.getDur().isEmployee()) {
					notice = await getUserNotice();
				}
			}

        let requestCount = 0;

        if(notice.solicitudes && notice.solicitudes.task_holder) {
            requestCount = notice.solicitudes.task_holder;
        }

        let textoRecibidas = this.createElement(TAG.SPAN)
        textoRecibidas.innerHTML = "Solicitudes Recibidas";
        titulo.appendChild(textoRecibidas);

        let segundaLinea = this.createElement(TAG.DIV);
        segundaLinea.className = "aonWidgetSolicitudesMobileSegundaLinea";
        widgetSolicitudesRecibidas.appendChild(segundaLinea);

        let icon = this.createElement(TAG.I);
        icon.className = CSS.MATERIAL_ICONS;
        icon.innerHTML = "move_to_inbox";
        icon.classList.add("aonWidgetSolicitudesIconRecibidas");
        segundaLinea.appendChild(icon);

        let numeroRecibidas = this.createElement(TAG.DIV);
        numeroRecibidas.innerHTML = " "+requestCount;
        numeroRecibidas.className = "aonWidgetSolicitudesMobileLineaNumero";
        segundaLinea.appendChild(numeroRecibidas);

        widgetSolicitudesRecibidas.addEventListener('click', () => {
            let aonMessenger = new AonMessenger();
            aonMessenger._filter.task_holder = this.taskHolder.id;
            this.rootPanel(aonMessenger);
        });

        if(requestCount == 0)
            widgetSolicitudesRecibidas = null;

        return widgetSolicitudesRecibidas;
    }


    async widgetSolicitudesEnviadas() {
        const widgetSolicitudesEnviadas = this.createElement(TAG.DIV);
        widgetSolicitudesEnviadas.id = this.WIDGET_SOLICITUDES_ENVIADAS;
        widgetSolicitudesEnviadas.className = "aonWidgetSolicitudesMobile";

        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetSolicitudesMobileTitulo";
        widgetSolicitudesEnviadas.appendChild(titulo);

        let textoEnviadas = this.createElement(TAG.SPAN)
        textoEnviadas.innerHTML = "Solicitudes Enviadas";
        titulo.appendChild(textoEnviadas);

        let segundaLinea = this.createElement(TAG.DIV);
        segundaLinea.className = "aonWidgetSolicitudesMobileSegundaLinea";
        widgetSolicitudesEnviadas.appendChild(segundaLinea);

        let icon = this.createElement(TAG.I);
        icon.className = CSS.MATERIAL_ICONS;
        icon.innerHTML = "outbox";
        icon.classList.add("aonWidgetSolicitudesIconEnviadas");
        segundaLinea.appendChild(icon);

        let enviadas = await this.getEnviadas();

        let numeroEnviadas = this.createElement(TAG.DIV);
        numeroEnviadas.innerHTML = enviadas;
        numeroEnviadas.className = "aonWidgetSolicitudesMobileLineaNumero";
        segundaLinea.appendChild(numeroEnviadas);
        let taskHolder =  await getTaskHolder({ workgroups: true });

        widgetSolicitudesEnviadas.addEventListener('click', () => {
            let aonMessenger = new AonMessenger();
            aonMessenger.TASK_HOLDER =  taskHolder;
            aonMessenger._filter = {
                sender: taskHolder.id,
                task_holder: undefined
            };
            this.rootPanel(aonMessenger);
        });

        if(enviadas == 0)
            widgetSolicitudesEnviadas = null;

        return widgetSolicitudesEnviadas;
    }

    async widgetFacturasPendientes() {
        const widgetFacturasPendientes = this.createElement(TAG.DIV);
        widgetFacturasPendientes.id = this.WIDGET_FACTURAS_PENDIENTES;
        widgetFacturasPendientes.className = "aonWidgetSolicitudesMobile";

        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetSolicitudesMobileTitulo";
        widgetFacturasPendientes.appendChild(titulo);

        let notice = undefined;
			if(localStorage.getItem('company')) {
				if(!this.getDur().isEmployee()){
					notice = await getDomainNotice();
				}
			} else {
				await this.buildCompany();
				if(!this.getDur().isEmployee()) {
					notice = await getUserNotice();
				}
			}

        let requestCount = 0;

        if(notice.invoice && notice.invoice.inbox.count) {
            requestCount = notice.invoice.inbox.count;
        }

        let textoFacturas = this.createElement(TAG.SPAN)
        textoFacturas.innerHTML = "Facturas Pendientes";
        titulo.appendChild(textoFacturas);

        let segundaLinea = this.createElement(TAG.DIV);
        segundaLinea.className = "aonWidgetSolicitudesMobileSegundaLinea";
        widgetFacturasPendientes.appendChild(segundaLinea);

        let icon = new AonIcon();
        icon.icon = "aon_new_invoice";
        icon.color = "var(--aonInvoice)";
        icon.size = "33px";
        icon.className = "aonWidgetFacturasIcon";
        segundaLinea.appendChild(icon);

        let numeroFacturas = this.createElement(TAG.DIV);
        numeroFacturas.innerHTML = " "+requestCount;
        numeroFacturas.className = "aonWidgetSolicitudesMobileLineaNumero";
        numeroFacturas.style.marginTop = "-1px";
        segundaLinea.appendChild(numeroFacturas);

        widgetFacturasPendientes.addEventListener('click', () => {
            this.rootPanelHtml('<aon-invoice-panel></aon-invoice-panel>');
        });

        if(requestCount == 0)
            widgetFacturasPendientes = null;

        return widgetFacturasPendientes;
    }

    async widgetBanks() {
        const widgetBanks = this.createElement(TAG.DIV);
        widgetBanks.id = this.WIDGET_BANKS;
        widgetBanks.className = "aonWidgetNominaMobile";
    
        let banks = await this.getCompanyBanks();
    
        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetNominaMobileTitulo";

        let bancosTitulo = this.createElement(TAG.SPAN);
        bancosTitulo.innerHTML = "Bancos";
        bancosTitulo.style.fontWeight = "bold";
        titulo.appendChild(bancosTitulo);

        let bancos = this.createElement(TAG.SPAN);
        bancos.innerHTML = "("+banks.length+")";
        bancos.className = "aonWidgetNominaMobileFecha";
        titulo.appendChild(bancos); 
    
        let total = this.createDiv();
        total.innerHTML = `${this.getTotal(banks)}`;
        total.className = "aonWidgetNominaMobileNomina";
        total.style.marginTop = "10px";

        widgetBanks.addEventListener('click', async () => {
            this.rootPanel(new AonAccounting());
        });
    
        widgetBanks.appendChild(titulo);    
        widgetBanks.appendChild(total);

        if(banks.length == 0)
            widgetBanks = null;
    
        return widgetBanks;
    }  

    async getCompanyBanks() {
        let company = JSON.parse(localStorage.getItem("company"));
        if(!this.BANKS.length){
            try {
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

    getTotal(banks){
        let total = banks.reduce((t, bank) => t + bank.balance, 0);
        return total > 0 ? formatNumber(total, 0, "EUR") : "No disponible";
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
            let datos = await getEmployeeSalaries(filter); 
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

    async getRecibidas() {
        let filterCount = {};
    
        let taskHolder = await getTaskHolder({ workgroups: true });
        let id = taskHolder.id;
        let workgroups = this.getWorkgroupsStr(taskHolder.workgroups);
    
        let auth = await getAuth();
        let email = auth.email;
    
        filterCount.email = email;
        filterCount.taskHolder = id;
        filterCount.workgroups = workgroups;
    
        let count = await getTaskCount(filterCount);
    
        let recibidas = count.task_holder !== undefined && count.task_holder !== null
            ? count.task_holder + ""
            : "0";
    
        return recibidas;
    }
    
    async getEnviadas() {
        let filterCount = {};
    
        let taskHolder = await getTaskHolder({  });
        let id = taskHolder.id;
        let workgroups = this.getWorkgroupsStr(taskHolder.workgroups);
    
        let auth = await getAuth();
        let email = auth.email;
    
        filterCount.email = email;
        filterCount.taskHolder = id;
        filterCount.workgroups = workgroups;
    
        let count = await getTaskCount(filterCount);
            
        let enviadas = count.task_holder !== undefined && count.task_holder !== null
            ? count.task_holder + ""
            : "0";
    
        return enviadas;
    }
     
    formatCurrency(valor) {
        const partes = valor.toFixed(2).split("."); 
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
