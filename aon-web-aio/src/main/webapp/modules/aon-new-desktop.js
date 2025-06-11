import {AonElement} from '../components/AonElement.js';

import { MSG, CONSTANT, AON_ICONS, CSS, EVENT, MATERIAL_ICONS, TAG } from '../environments/environments.js';
import { AonIcon } from '../components/aon-icon.js';
import { AonApplication } from '../components/aon-application.js';
import * as LS from '../services/localStorageService.js';
import { getContratado, getDomainUserRoles, sendFormData } from '../services/companyService.js';
import { getAuth } from '../services/authService.js';
import { DomainUserRoles } from '../models/DomainUserRoles.js';

export class AonNewDesktop extends AonElement {
	
	AON_DESKTOP;
	
	portalApps;
	portalNoApps;
	suiteApps;
	suiteNoApps;
	dur;
	
	constructor(portalApps, portalNoApps, suiteApps, suiteNoApps) {
		super();
		this.portalApps = portalApps;
		this.portalNoApps = portalNoApps;
		this.suiteApps = suiteApps;
		this.suiteNoApps = suiteNoApps;
	}
	
	connectedCallback () {
		this.init();
		getDomainUserRoles({}).then(r => {
					this.dur = new DomainUserRoles(r);
					this.build();
			});
	}
	
	init() {
		this.AON_DESKTOP = 'aonDesktop';
	}

	async build() {
      // Se compruebe de esta manera : this.getDur().isAdmin() && this.getDur().isAdmin() 
      // Que especificamente seas uno de ellos, ya que siendo admin tambien devuelve que eres empleado en algunos casos
		if(this.isAyudaT() && (this.getDur().isAdmin() || this.getDur().isEnterprise())){
			let app = this.createApplication(this.AON_DESKTOP, 'Planes', new AonApplication());
			app.main = "true";
			this.appendChild(app);
			app.closeSidenav();

			let div = this.createDiv();
			div.appendChild(this.buildPlans());
			app.setContent(div);
		}else{
			let app = this.createApplication(this.AON_DESKTOP, MSG.APPLICATIONS, new AonApplication());
			app.main = "true";
			app.closeSidenav();

			let div = this.createDiv();	
			div.appendChild(this.buildNormal());
			div.appendChild(this.buildPrueba());
			app.setContent(div);
		}
	}

	getDur() {
		return this.dur;
	}

	buildPrueba(){
		let div = this.createDiv();
		div.classList.add("aonNewDesktopBeta");

		let headerDiv = this.createElement(TAG.DIV);
		headerDiv.classList.add("aonNewDesktopHeaderDiv");
		div.appendChild(headerDiv);		

		let divActivas = this.createElement(TAG.DIV);
		divActivas.classList.add("aonNewDesktopDesktopDiv");
		
		let bannerAppsDiv = this.createElement(TAG.DIV);
		let titleActivas = this.createElement(TAG.H1);
		titleActivas.innerHTML = MSG.APPLICATIONS + " " + MSG.ACTIVES;
		titleActivas.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv.appendChild(titleActivas);
		divActivas.appendChild(bannerAppsDiv);

		let appsActivas =  this.createElement(TAG.DIV);
		appsActivas.classList.add('aonDesktopAppsContainer');		
		
		for ( const app in this.portalApps ) {
			if(this.portalApps[app].app!= "aonApplication")
				appsActivas.appendChild(this.buildApp(this.portalApps[app]));
		}
		
		for ( const app in this.suiteApps ) {
			appsActivas.appendChild(this.buildApp(this.suiteApps[app]));
		}

		divActivas.appendChild(appsActivas);
		div.appendChild(divActivas);

		let divMas = this.createElement(TAG.DIV);
		divMas.classList.add("aonNewDesktopDesktopDiv");

		let bannerAppsDiv2 = this.createElement(TAG.DIV);
		bannerAppsDiv2.style.marginTop = "20px";
		bannerAppsDiv2.style.display = "flex";

		let titleMas = this.createElement(TAG.H1);
		titleMas.innerHTML = "Más " + MSG.APPLICATIONS;
		titleMas.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv2.appendChild(titleMas);

		let titleGratis = this.createElement(TAG.H1);
		titleGratis.innerHTML = "(Prueba GRATIS durante 15 días)";
		titleGratis.style.marginLeft = "5px";
		titleGratis.style.fontSize = "15px";
		titleGratis.style.marginTop = "17px";
		titleGratis.style.fontWeight = "400";
		titleGratis.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv2.appendChild(titleGratis);

		divMas.appendChild(bannerAppsDiv2);

		let appsMas = this.createElement(TAG.DIV);
		appsMas.classList.add('aonDesktopAppsContainer');

		for ( const app in this.portalNoApps ) {
			if(this.portalNoApps[app].app!= "aonApplication")
				appsMas.appendChild(this.buildMasApp(this.portalNoApps[app]));
		}
		
		for ( const app in this.suiteNoApps ) {
			appsMas.appendChild(this.buildMasApp(this.suiteNoApps[app]));
		}

		divMas.appendChild(appsMas);
		div.appendChild(divMas);
		return div;

	}

	buildNormal(){
		let div = this.createDiv();
		div.classList.add("aonNewDesktopNormal");

		let headerDiv = this.createElement(TAG.DIV);
		headerDiv.classList.add("aonNewDesktopHeaderDiv");
		div.appendChild(headerDiv);		


		let desktopDiv = this.createElement(TAG.DIV);
		desktopDiv.classList.add("aonNewDesktopDesktopDiv");
		
		let bannerAppsDiv = this.createElement(TAG.DIV);
		let titleH1 = this.createElement(TAG.H1);
		titleH1.innerHTML = MSG.APPLICATIONS +  " Portal";
		titleH1.classList.add("aonNewDesktopTitleH1");
		bannerAppsDiv.appendChild(titleH1);
		desktopDiv.appendChild(bannerAppsDiv);

		let desktopAppsDiv =  this.createElement(TAG.DIV);
		desktopAppsDiv.classList.add('aonDesktopAppsContainer');		
		
		for ( const app in this.portalApps ) {
			desktopAppsDiv.appendChild(this.buildApp(this.portalApps[app]));
		}
		
		desktopDiv.appendChild(desktopAppsDiv);
		div.appendChild(desktopDiv);
		return div;
	}
	
	buildApp(app) {
		
		let cardDiv  = this.createElement(TAG.DIV);
		cardDiv.id = `aonDesktop-${app.app}`;
		cardDiv.classList.add(CSS.AON_CARD);
		cardDiv.classList.add("aonNewDesktopCardDiv");
		cardDiv.title = "Ejecutar";
		
		let appA = this.createElement(TAG.A);
		appA.addEventListener(EVENT.CLICK, () => {
			this.appSelection(app);
		});

		let appDiv = this.createElement(TAG.DIV);
		appDiv.classList.add("aonNewDesktopAppDiv");
		
		if (app.symbol && app.app!="payroll") {
			let icon = this.createElement(TAG.SPAN);
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.id = `aonDesktopAppImg-${app.app}`;
			icon.innerHTML = app.symbol;
			icon.style.fontSize = "24px";
			appDiv.appendChild(icon);
		}
		else if (app.icon) {
			let aonIcon = new AonIcon();
			aonIcon.id = `aonDesktopAppImg-${app.app}`;
			aonIcon.icon = app.newIcon || app.icon;
			aonIcon.color = app.newColor || app.color;
			aonIcon.size = app.iconSize || "32px";
			appDiv.appendChild(aonIcon);
		} 

		else if (app.logo) {
		 	let img = this.createElement(TAG.IMG);
		 	img.id = `aonDesktopAppImg-${app.app}`;
			img.classList.add("aonNewMenuAppImg");
		 	img.src = app.logo;
		 	img.title = app.title;
		 	appDiv.appendChild(img);
		}

		let titleSpan = this.createElement(TAG.SPAN);
		titleSpan.id = `aonDesktopAppTitle-${app.app}`;
		titleSpan.classList.add("aonNewDesktopAppDiv");
		titleSpan.innerHTML = app.title;
		appDiv.appendChild(titleSpan);

		appA.appendChild(appDiv);

		// if(app.app == "accounting"|| app.app == "fiscal" || app.app == "payroll")
		// 	appDiv.className = "appDiv";

		cardDiv.appendChild(appA);

		return cardDiv;		
	}

	plans = [
		{
			name: "CONTROL BÁSICO",
			subtitle: "Incluye herramientas básicas de facturación y gestión laboral.",
			description: "Desde la emisión de facturas hasta el control de facturas recibidas, cobros y pagos. Ofrece estadísticas e informes, control horario, solicitudes y portal del empleado.\n\n",
			price: "Gratuito o incluido", 
		},
		{
			name: "CONTROL TOTAL",
			subtitle: "<strong>PLAN BÁSICO</strong><br> + herramientas de tesorería.",
			description: "Desde la creación de facturas y cuotas recurrentes hasta el control de la previsión de cuentas, remesas de cobros y pagos, suplidos y anticipos. Dispone también de ficheros SEPA y conciliación bancaria.",
			price: "9,95€ (con 1 usuario)"
		},
		{
			name: "CONTROL ABSOLUTO",
			subtitle: "<strong>PLAN TOTAL</strong><br> + herramientas comerciales y de marketing.",
			description: "Desde la gestión de presupuestos, clientes potenciales y operaciones hasta el envío de comunicaciones a tus clientes. Incluye agenda comercial, plantillas de correos, boletines y cuestionarios.",
			price: "19,95€ (con 1 usuario)"
		},
		{
			name: "CONTROL SUPREMO",
			subtitle: "<strong>PLAN ABSOLUTO</strong><br> + herramientas de almacén y organización.",
			description: "Desde albaranes, traspasos, envíos de pedidos y propuestas de compra hasta organización de tareas, procesos y campañas. Incluye inventario, elaboraciones de packaging y productos y bandeja de tareas.",
			price: "24,95€ (con 1 usuario)"
		}
	];

	

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


	buildPlans() {
		const container = this.createElement(TAG.DIV);

		const style = document.createElement("style");
		style.textContent = `
			.plansGrid {
				display: flex;
				flex-wrap: wrap;
				gap: 16px;
				justify-content: center;
				align-items: stretch; 
				margin-top: 20px;
			}

			.planCard {
				border: 1px solid #ddd;
				border-radius: 8px;
				padding: 20px;
				width: 300px;
				box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
				background-color: #fff;
				display: flex;
				flex-direction: column;
				justify-content: space-between;
			}

			.planTitle {
				font-size: 1.2rem;
				margin-bottom: 5px;
				margin-top: 0;
				font-weight: bold;
				text-align: center;
			}

			.planSubtitle {
				font-size: 0.95rem;
				color: #666;
				font-style: italic;
				display: block;
				margin-top: 15px;
				margin-bottom: 20px;
				text-align: center;
			}

			.planDescription {
				font-size: 0.9rem;
				white-space: pre-wrap;
				margin-bottom: 20px;
				flex-grow: 1; 
			}

			.planPrice {
				font-weight: bold;
				color: #0073e6;
				text-align: center;
				min-height: 24px;
				display: flex;
				align-items: center;
				justify-content: center;
				margin-bottom: 16px;
			}

			.planButton {
				background-color: #0073e6;
				color: white;
				border: none;
				padding: 10px 16px;
				border-radius: 4px;
				cursor: pointer;
				align-self: center;
				margin-top: auto;
			}

			.planButton:hover {
				background-color: #005bb5;
			}

			.plansFooter {
				width: 100%;
				display: flex;
				justify-content: center;
				margin-top: 40px;
			}

			.fullWidthCard {
				width: calc(4 * 300px + 3 * 16px); 
				box-sizing: border-box;
			}

			.planPopup {
				position: fixed;
				top: 0;
				left: 0;
				width: 100%;
				height: 100%;
				background: rgba(0, 0, 0, 0.5);
				display: flex;
				align-items: center;
				justify-content: center;
				z-index: 9999;
			}
		}`;

		container.appendChild(style);
		// Titulo
		const tittleDiv = this.createElement(TAG.DIV);
		tittleDiv.style.textAlign = "center";
		tittleDiv.style.marginTop = "10px";
		tittleDiv.style.maxWidth = "1296px"; 
		tittleDiv.style.marginLeft = "auto";
		tittleDiv.style.marginRight = "auto";
		tittleDiv.style.marginBottom = "10px";

		const titleH1 = this.createElement(TAG.H1);
		titleH1.innerHTML = "Mejora tu plan. Impulsa tu negocio.";
		titleH1.classList.add("aonNewDesktopTitleH1");
		titleH1.style.marginTop = "20px"; 
		titleH1.style.marginBottom = "5px";
		titleH1.style.paddingBottom = "0px";
		titleH1.style.fontSize = "2rem";

		tittleDiv.appendChild(titleH1);

		//Subtitulo
		const subtitleP = this.createElement(TAG.P);
		subtitleP.textContent = "Olvídate de invertir en recursos externos para aumentar tu productividad. Nuestro software tiene todo lo que necesitas en un único lugar ¡Descúbrelo!";
		subtitleP.style.marginBottom = "5px";
		subtitleP.style.marginTop = "20px";
		subtitleP.style.fontSize = "1rem";
		subtitleP.style.color = "#444";
		subtitleP.style.maxWidth = "800px";
		subtitleP.style.marginLeft = "auto";
		subtitleP.style.marginRight = "auto";
		subtitleP.style.lineHeight = "1.5";

		tittleDiv.appendChild(subtitleP);

		container.appendChild(tittleDiv);

		// Contenedor de las 4 cards
		const plansGrid = this.createElement(TAG.DIV);
		plansGrid.classList.add("plansGrid");
		getContratado({}).then(response => {
			const userLevel = this.getUserPlanLevel(response);

			this.plans.forEach((plan, index) => {
				const card = this.buildPlanCard(plan, index, userLevel);
				plansGrid.appendChild(card);
			});
		});
		container.appendChild(plansGrid);

		// Contenedor de la card de usuarios
		const plansFooter = this.createElement(TAG.DIV);
		plansFooter.classList.add("plansFooter");

		const userCard = this.createElement(TAG.DIV);
		userCard.classList.add("planCard", "fullWidthCard");

		const userTitle = this.createElement(TAG.H3);
		userTitle.textContent = "¿Necesitas añadir usuarios?";
		userTitle.style.textAlign = "center";
		userTitle.style.fontSize = "1.2rem";
		userTitle.style.marginTop = "5px";

		const userDesc = this.createElement(TAG.P);
		userDesc.textContent = `Para utilizar de forma simultánea este software con otras personas, necesitas varios usuarios. Cada usuario dispondrá de un acceso individual a las funcionalidades contratadas.`;
		userDesc.style.textAlign = "center";
		userDesc.style.marginTop = "2px";
		userDesc.style.marginBottom = "25px";

		const userButton = this.createElement(TAG.BUTTON);
		userButton.classList.add("planButton");
		userButton.textContent = "AÑADE USUARIOS";
		userButton.addEventListener(EVENT.CLICK, () => {
			this.sendDataforPlan("Añadir usuarios");
		});

		userCard.appendChild(userTitle);
		userCard.appendChild(userDesc);
		userCard.appendChild(userButton);
		plansFooter.appendChild(userCard);

		container.appendChild(plansFooter);

		return container;
	}

	buildPlanCard(plan, index, userLevel) {
		const cardDiv = this.createElement(TAG.DIV);
		cardDiv.classList.add("planCard");

		const title = this.createElement(TAG.H3);
		title.classList.add("planTitle");
		title.textContent = plan.name;

		const subtitle = this.createElement(TAG.SPAN);
		subtitle.classList.add("planSubtitle");
		subtitle.innerHTML = plan.subtitle;

		const description = this.createElement(TAG.P);
		description.classList.add("planDescription");
		description.textContent = plan.description;

		const price = this.createElement(TAG.DIV);
		price.classList.add("planPrice");
		const isAlreadyContracted = index === userLevel;
		if (isAlreadyContracted) {
			price.textContent = "Ya contratado";
			price.style.color = "#28a745";
		} else {
			price.textContent = plan.price;
		}

		// Añadir elementos en orden
		cardDiv.appendChild(title);
		cardDiv.appendChild(subtitle);
		cardDiv.appendChild(description);
		cardDiv.appendChild(price);

		// Botón solo si no está contratado
		if (!isAlreadyContracted) {
			const button = this.createElement(TAG.BUTTON);
			button.classList.add("planButton");
			button.textContent = "SOLICITAR";
			button.addEventListener(EVENT.CLICK, () => {
				this.sendDataforPlan(plan.name);
			});
			cardDiv.appendChild(button);
		}

		return cardDiv;
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
	buildMasApp(app) {
		
		let cardDiv  = this.createElement(TAG.DIV);
		cardDiv.id = `aonDesktop-${app.app}`;
		cardDiv.classList.add(CSS.AON_CARD);
		cardDiv.classList.add("aonNewDesktopCardDiv");
		cardDiv.title = "Contratar";

		let cardButton = this.createElement(TAG.SPAN);
		cardButton.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
		cardButton.innerHTML = 'add_shopping_cart';
		cardButton.style.position = 'absolute';
		cardButton.style.fontSize = "20px";
		cardButton.style.right = '10px';
		cardButton.style.top = "10px";
		cardButton.classList.add('aonAppMoreBtn');
		cardButton.style.visibility = 'hidden';

		cardDiv.addEventListener(EVENT.MOUSEOVER, () => {
			cardButton.style.visibility = 'visible';
		});

		cardDiv.addEventListener(EVENT.MOUSELEAVE, () => {
			cardButton.style.visibility = 'hidden';
		});

		cardDiv.append(cardButton);

		let appA = this.createElement(TAG.A);
		// appA.style.width = '100%';
		// appA.style.height = '100%';

		appA.addEventListener(EVENT.CLICK, () => {
			this.appSelection(app);
		});

		let appDiv = this.createElement(TAG.DIV);
		appDiv.classList.add("aonNewDesktopAppDiv");
		
		if (app.symbol) {
			let icon = this.createElement(TAG.SPAN);
			icon.classList.add(CSS.MATERIAL_SYMBOLS_OUTLINED);
			icon.id = `aonDesktopAppImg-${app.app}`;
			icon.innerHTML = app.symbol;
			icon.style.fontSize = "24px";
			appDiv.appendChild(icon);
		}
		else if (app.icon) {
			let aonIcon = new AonIcon();
			aonIcon.id = `aonDesktopAppImg-${app.app}`;
			aonIcon.icon = app.newIcon || app.icon;
			aonIcon.color = app.newColor || app.color;
			aonIcon.size = app.iconSize || "32px";
			appDiv.appendChild(aonIcon);
		} 

		else if (app.logo) {
		 	let img = this.createElement(TAG.IMG);
		 	img.id = `aonDesktopAppImg-${app.app}`;
			img.classList.add("aonNewMenuAppImg");
		 	img.src = app.logo;
		 	img.title = app.title;
		 	appDiv.appendChild(img);
		}

		let titleSpan = this.createElement(TAG.SPAN);
		titleSpan.id = `aonDesktopAppTitle-${app.app}`;
		titleSpan.classList.add("aonNewDesktopAppDiv");
		titleSpan.innerHTML = app.title;
		appDiv.appendChild(titleSpan);

		appA.appendChild(appDiv);

		cardDiv.appendChild(appA);

		cardDiv.style.paddingRight = '32px';
		


		return cardDiv;		
	}
	
	appSelection(app) {
		document.querySelector(TAG.AON_NEW_MENU).appSelection(app);
	}

	createApplication(id, title, application, main) {
		const app = this.createAonElement(application, id, title, main);
		this.appendChild(app);
		return app;
	}

	createAonElement(el, id, title, main){
		el.id = id || '';
		el.title = title || '';
		el.description = title || '';
		if(main)
		  el.main = true;
		return el;
	}
	
	
}


if(!window.customElements.get('aon-new-desktop')){
	window.customElements.define('aon-new-desktop', AonNewDesktop);
}