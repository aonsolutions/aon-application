import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../../environments/environments.js';
import { getTimeControl, getEmployeeSalaries, getDomainUserRoles, getDomainNotice, getUserNotice, request, getTaskCount } from '../../services/service.js';
import { AonElement } from '../../components/AonElement';
import '../invoice/aon-invoice-panel.js';
import * as LS from '../../services/localStorageService.js';
import { AonDragLeftNotification } from './aon-dragleft-notification.js';
import { AonSignMobile } from '../timecontrol/aon-sign-mobile.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';
import { DomainUserRoles } from '../../models/DomainUserRoles.js';

export class AonMobileHome extends AonElement {

    DIV_GENERAL;
    NOTIFICATION_DRAGLEFT;
    DIV_WIDGETS;
    WIDGET_TC;
    WIDGET_NOMINA;
    WIDGET_SOLICITUDES_RECIBIDAS;
    WIDGET_SOLICITUDES_ENVIADAS;
    _list;
    TASK_HOLDER;
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
        this._list=[];
        this.TASK_HOLDER = {};
	}

    build() {
        let divGeneral = this.createElement(TAG.DIV);
        divGeneral.id = this.DIV_GENERAL;
    
        let notificationDrag = new AonDragLeftNotification();
        notificationDrag.id = this.NOTIFICATION_DRAGLEFT;
        notificationDrag.style.marginBottom = "20px";
        divGeneral.appendChild(notificationDrag);
    
        let divWidgets = this.createElement(TAG.DIV);
        divWidgets.id = this.DIV_WIDGETS;
        divWidgets.className = "aonDivWidgetsMobile";
    
        const widgetOrder = [
            { id: this.WIDGET_TC, builder: this.widgetTimeControl },
            { id: this.WIDGET_NOMINA, builder: this.widgetNomina },
            { id: this.WIDGET_SOLICITUDES_RECIBIDAS, builder: this.widgetSolicitudesRecbidas },
            { id: this.WIDGET_SOLICITUDES_ENVIADAS, builder: this.widgetSolicitudesEnviadas },
        ];
    
        widgetOrder.forEach((widget) => {
            const placeholder = this.createElement(TAG.DIV);
            placeholder.id = widget.id;
            placeholder.className = "widgetPlaceholder";
            divWidgets.appendChild(placeholder);
        });
    
        widgetOrder.forEach((widget) => {
            widget.builder.call(this).then((builtWidget) => {
                const placeholder = this.getElement(widget.id);
                placeholder.replaceWith(builtWidget);
    
                if (widget.id === this.WIDGET_TC) {
                    let container = this.getElement("aonDragLeftContainer");
                    if (container) container.style.marginTop = "25px";
    
                    let time = this.getElement("aonSignTime");
                    if (time) time.style.marginTop = "5px";
                }
            });
        });
    
        divGeneral.appendChild(divWidgets);
    
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
    
        let [_, mes, anio] = date.split("/"); 
    
        let titulo = this.createElement(TAG.DIV);
        titulo.className = "aonWidgetNominaMobileTitulo";

        let nominaTitulo = this.createElement(TAG.SPAN);
        nominaTitulo.innerHTML = "Nómina";
        nominaTitulo.style.fontWeight = "bold";
        titulo.appendChild(nominaTitulo);

        let fecha = this.createElement(TAG.SPAN);
        fecha.innerHTML = `${mes}/${anio}`;
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
        segundaLinea.style.display = "flex";
        segundaLinea.style.marginTop = "17px";
        segundaLinea.style.marginLeft = "60px";
        widgetSolicitudesRecibidas.appendChild(segundaLinea);

        let icon = this.createElement(TAG.I);
        icon.className = CSS.MATERIAL_ICONS;
        icon.innerHTML = "move_to_inbox";
        icon.style.color = "red";
        icon.style.marginTop = "6px";
        icon.style.fontSize = "25px";
        segundaLinea.appendChild(icon);

        let numeroRecibidas = this.createElement(TAG.DIV);
        numeroRecibidas.innerHTML = " "+requestCount;
        numeroRecibidas.className = "aonWidgetSolicitudesMobileLineaNumero";
        numeroRecibidas.style.marginTop = "0px";
        segundaLinea.appendChild(numeroRecibidas);

        widgetSolicitudesRecibidas.addEventListener('click', () => {
            if(this.TASK_HOLDER && this.TASK_HOLDER.id){
                this._filter.task_holder = undefined;
                this._filter.sender = this.TASK_HOLDER.id;
            }

            this.addListFilter(this._filter);
            this.updateStatusCount();
            this.showView(MESSENGER_VIEWS.AON_MESSENGER_LIST, undefined, this.getListFilter());
        });

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
        segundaLinea.style.display = "flex";
        segundaLinea.style.marginTop = "17px";
        segundaLinea.style.marginLeft = "60px";
        widgetSolicitudesEnviadas.appendChild(segundaLinea);

        let icon = this.createElement(TAG.I);
        icon.className = CSS.MATERIAL_ICONS;
        icon.innerHTML = "outbox";
        icon.style.marginTop = "6px";
        icon.style.fontSize = "25px";
        segundaLinea.appendChild(icon);

        // let enviadas = 0;

        // getTaskCount(filterCount).then(count=>{
        //     enviadas =  count.sender || 0;
        // });	

        let numeroEnviadas = this.createElement(TAG.DIV);
        numeroEnviadas.innerHTML = enviadas;
        numeroEnviadas.className = "aonWidgetSolicitudesMobileLineaNumero";
        numeroEnviadas.style.marginTop = "0px";
        numeroEnviadas.style.color = "#33A9A9";
        segundaLinea.appendChild(numeroEnviadas);

        widgetSolicitudesEnviadas.addEventListener('click', () => {this.rootPanelHtml('<aon-messenger></aon-messenger>');});

        return widgetSolicitudesEnviadas;
    }
    
    async obtenerDatosNominas() {
        try {
            const filter = this.getApplicationParent()?._filter || {};
            let datos = await getEmployeeSalaries(filter); 
            datos = datos.sort((a, b) => new Date(b.endDate) - new Date(a.endDate));
            const ultimoSalario = datos[0]; 
    
            if (ultimoSalario) {
                return {
                    totalLiquid: ultimoSalario.totalLiquid,
                    date: AonDateUtils.getMonthYear(ultimoSalario.endDate), 
                };
            } else {
                return null; 
            }
        } catch (error) {
            console.error("Error obteniendo el último salario:", error);
            return null;
        }
    }

    formatCurrency(valor) {
        const partes = valor.toFixed(2).split("."); 
        const parteEnteraConMiles = partes[0].replace(/\B(?=(\d{3})+(?!\d))/g, "."); 
        return `${parteEnteraConMiles},${partes[1]}`; 
    }

    getDur() {
		return this.dur;
	}

    addListFilter(filter) {
		this.setListFilter({...this.getListFilter(), ...filter});
	}

    setListFilter(filter) {
		this._listFilter = filter;
	}
 
    
    
}
if(!window.customElements.get(TAG.AON_MOBILE_HOME)){
	window.customElements.define(TAG.AON_MOBILE_HOME, AonMobileHome);
}
