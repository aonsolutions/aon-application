import {AonElement} from '../components/AonElement.js';
import { MSG, EVENT, TAG } from '../environments/environments.js';
import { AonCard } from '../components/aon-card';
import { AonApplication } from '../components/aon-application.js';
import { getContratado, getDomainUserRoles, sendFormData } from '../services/companyService.js';
import { getAuth } from '../services/authService.js';
import { DomainUserRoles } from '../models/DomainUserRoles.js';
import * as GWT from '../gwt/gwt.js';
export class AonPlans extends AonElement {
  dur;
  plans = [
    {
      name: "Control básico",
      subtitle: "Incluye herramientas básicas de facturación y gestión laboral.",
      description: "Desde la emisión de facturas hasta el control de facturas recibidas, cobros y pagos. Ofrece estadísticas e informes, control horario, solicitudes y portal del empleado.\n\n",
      price: "Gratuito o incluido", 
    },
    {
      name: "Control total",
      subtitle: "<strong>PLAN BÁSICO</strong><br> + herramientas de tesorería.",
      description: "Desde la creación de facturas y cuotas recurrentes hasta el control de la previsión de cuentas, remesas de cobros y pagos, suplidos y anticipos. Dispone también de ficheros SEPA y conciliación bancaria.",
      price: "9,95€ (con 1 usuario)"
    },
    {
      name: "Control absoluto",
      subtitle: "<strong>PLAN TOTAL</strong><br> + herramientas comerciales y de marketing.",
      description: "Desde la gestión de presupuestos, clientes potenciales y operaciones hasta el envío de comunicaciones a tus clientes. Incluye agenda comercial, plantillas de correos, boletines y cuestionarios.",
      price: "19,95€ (con 1 usuario)"
    },
    {
      name: "Control supremo",
      subtitle: "<strong>PLAN ABSOLUTO</strong><br> + herramientas de almacén y organización.",
      description: "Desde albaranes, traspasos, envíos de pedidos y propuestas de compra hasta organización de tareas, procesos y campañas. Incluye inventario, elaboraciones de packaging y productos y bandeja de tareas.",
      price: "24,95€ (con 1 usuario)"
    }
  ];

//	constructor(portalApps, portalNoApps, suiteApps, suiteNoApps) {
  constructor() {
      super();
  }

  connectedCallback () {
    getDomainUserRoles({}).then(r => {
      this.dur = new DomainUserRoles(r);
      if(this.isAyudaT() && (this.getDur().isAdmin() || this.getDur().isEnterprise())){
        this.createApplication(this.PLANES, MSG.APPLICATIONS, new AonApplication());
        this.applicationEl = this.getApplication();
        this.applicationEl.removeToolbar(); // Sin Toolbar
        this.applicationEl.removeSidenav(); // Sin Menu
        this.build();
      }
    });
  }

  init() {
  }

  getDur() {
    return this.dur;
  }

  build() {
    if(this.getDur().isTrial() || this.getDur().hasBeenTrial()){
      // Autocontratacion
      GWT.iLoad(GWT.PRODUCT_CATALOGUE_MODULE)
    } else {
      // Titulo
      const tittleDiv = this.createElement(TAG.DIV);
      tittleDiv.className = "title-plans";
  
      const titleH2 = this.createElement(TAG.H2);
      titleH2.innerHTML = "Mejora tu plan. Impulsa tu negocio.";
      tittleDiv.appendChild(titleH2);
  
      //Subtitulo
      const subtitleP = this.createElement(TAG.DIV);
      subtitleP.textContent = "Olvídate de invertir en recursos externos para aumentar tu productividad. Nuestro software tiene todo lo que necesitas en un único lugar ¡Descúbrelo!";
      tittleDiv.appendChild(subtitleP);
      this.applicationEl.addContent(tittleDiv);
  
      // Contenedor de las 4 cards
      const plansGrid = this.createElement(TAG.DIV);
      plansGrid.classList.add("plansGrid");
      getContratado({}).then(response => {
        const userLevel = this.getUserPlanLevel(response);
        this.plans.forEach((plan, index) => {
          this.buildPlanCard(plan, index, userLevel, plansGrid);
        });
      });
      this.applicationEl.addContent(plansGrid);
  
      // Contenedor de la card de usuarios
      const cardAddUsers      = new AonCard;
      cardAddUsers.className  = "plansFooter";
      cardAddUsers.title      = "¿Necesitas añadir usuarios?";
      // Agregamos la card
      this.applicationEl.addContent(cardAddUsers);
        // Adjuntamos datos a la card
        const usersDesc = this.createElement(TAG.DIV);
        usersDesc.textContent = `Para utilizar de forma simultánea este software con otras personas, necesitas varios usuarios. Cada usuario dispondrá de un acceso individual a las funcionalidades contratadas.`;
        cardAddUsers.addContent(usersDesc);
  
        // Adjuntamos boton a la card
        const usersButton = this.createElement(TAG.BUTTON);
        usersButton.classList.add("planButton");
        usersButton.textContent = "AÑADE USUARIOS";
        usersButton.addEventListener(EVENT.CLICK, () => {
            this.sendDataforPlan("Añadir usuarios");
        });
        cardAddUsers.addContent(usersButton);
    }
  }

  buildPlanCard(plan, index, userLevel, plansGrid) {
    const card     = new AonCard;
    card.id        = "card-plan-"+index;
    card.className = "planTitle";
    card.title     = plan.name;
    plansGrid.appendChild(card);

    const subtitle = this.createElement(TAG.SPAN);
    subtitle.classList.add("planSubtitle");
    subtitle.innerHTML = plan.subtitle;
    card.addContent(subtitle);

    const description = this.createElement(TAG.DIV);
    description.classList.add("planDescription");
    description.textContent = plan.description;
    card.addContent(description);

    const price = this.createElement(TAG.DIV);
    card.addContent(price);
    price.classList.add("planPrice");
    const isAlreadyContracted = index === userLevel;
    if (isAlreadyContracted) {
        price.className   = "contracted";
        price.textContent = "Ya contratado";
    } else {
        price.textContent = plan.price;
    }

    // Boton solo si no esta contratado
    if (!isAlreadyContracted) {
        const button = this.createElement(TAG.BUTTON);
        button.classList.add("planButton");
        button.textContent = "SOLICITAR";
        button.addEventListener(EVENT.CLICK, () => {
            this.sendDataforPlan(plan.name);
        });
        card.addContent(button);
    }
  }

  getUserPlanLevel(contratado) {
    const hasTotal = contratado.includes('treasury');
    const hasAbsoluto = contratado.includes('commercial') && contratado.includes('marketing');
    const hasSupremo = contratado.includes('warehouse') && contratado.includes('groupware');

    // Si no tiene TOTAL, no puede tener ABSOLUTO ni SUPREMO
    if (!hasTotal) {
      return 0;
    }

    // Si tiene todos los niveles completos es SUPREMO
    if (hasTotal && hasAbsoluto && hasSupremo) {
      return 3;
    }

    // Si tiene TOTAL y ABSOLUTO es ABSOLUTO
    if (hasTotal && hasAbsoluto) {
      return 2;
    }

    // Si solo tiene TOTAL es nivel TOTAL
    if (hasTotal) {
      return 1;
    }

    // Por defecto es BÁSICO
    return 0;
  }

  sendDataforPlan(planName){
    // Barra loader de AonApplication - Iniciar
    this.getApplication().startLoading();
    getAuth().then(auth => {
        const dataUser = auth;
        const data = {
            email: dataUser.email,
            phone: dataUser.phone,
            name: dataUser.name,
            surname: dataUser.surname,
            domainUrl: window.location.href,
            companyData: localStorage.getItem('company'),
            plan: planName
        };
        sendFormData(data).then(response => {
          this.getApplication().stopLoading();
          this.showPopupMessage("Su solicitud ha sido enviada con exito, en breve un agente contactará con usted.");
        }).catch(error => {
          this.getApplication().stopLoading();
          this.showPopupMessage("Fallo al enviar la solicitud");
        });
    });
  }

  createPopup() {
      if (this.popup) return;

      const popup = document.createElement("div");
      popup.classList.add("planPopup");
      popup.style.display = "none";

      const content = document.createElement("div");
      content.classList.add("planCard");
      content.style.maxWidth = "400px";
      content.style.textAlign = "center";

      this.popupMessage = document.createElement("p");
      this.popupMessage.style.marginBottom = "20px";
      content.appendChild(this.popupMessage);

      const closeBtn = document.createElement("button");
      closeBtn.classList.add("planButton");
      closeBtn.textContent = "CERRAR";
      closeBtn.style.margin = "0 auto";
      closeBtn.style.display = "block";
      closeBtn.addEventListener("click", () => {
          popup.style.display = "none";
      });
      content.appendChild(closeBtn);

      popup.appendChild(content);
      document.body.appendChild(popup);

      this.popup = popup;
  }

  showPopupMessage(message) {
      if (!this.popup) this.createPopup();
      this.popupMessage.textContent = message;
      this.popup.style.display = "flex";
  }
}

if(!window.customElements.get('aon-plans')){
	window.customElements.define('aon-plans', AonPlans);
}
