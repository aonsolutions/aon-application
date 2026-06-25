import { AonElement } from '../components/AonElement.js';
import { AonHeader } from '../modules/aon-header.js';
import { MSG, CSS, EVENT, TAG, AON_SYMBOLS } from '../environments/environments.js'; 
import * as LS from '../services/localStorageService.js';
import { AonNewMenu } from './aon-new-menu.js';
import { AonConfig } from './aon-config.js';
import { AonHelp } from './aon-help.js';
import { AonRightPanel } from './aon-right-panel.js';
import { AonLoginPanel } from './aon-login-panel.js';
import { AonNotificationPanel } from './aon-notification-panel.js';
import { APPLICATIONS, APPS, NEW_APPS } from '../services/app.js';

import { AonMobileHeader } from '../modules/aon-mobile-header.js';
import { AonMobileMenu } from '../modules/aon-mobile-menu.js';
import { AonTooltip } from '../components/aon-tooltip.js';
import { AonDialog } from '../components/aon-dialog.js';
import { AonDialogMenu } from '../components/aon-dialog-menu.js';
import { AonNewDialogMenu } from '../components/aon-new-dialog-menu.js';
import { AonNewFixedButton } from '../components/aon-new-fixed-button.js';

export class AonHome extends AonElement {

	AON_MENU;
	AON_MOBILE_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;
	OPTION_DIALOG;
	
	// Right Panel, needed for hide on click outside
	rightPanel;
	editButton;
	configButton;
	helpButton;
	notificationButton;
	
	closeRightPanelHandler;

	constructor () {
		super();
	}

	connectedCallback () {
		this.clear();
		this.initialize();
		if(this.isMobile()) {
			this.buildMobile();
		} else {
			this.build();
			this.customize();
		}
	}

	initialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_MOBILE_MENU = 'aonMobileMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
		this.OPTION_DIALOG = 'newOptionDialog';
	}

	buildMobile() {
		let aonMobileHeader = new AonMobileHeader();
		aonMobileHeader.id = this.AON_HEADER;
		this.appendChild(aonMobileHeader);
		
		// TimeControl Reason Dialog
	    let aonDialog = new AonDialog();
	    aonDialog.id = 'aonTimeControlReasonDialog';
	    this.appendChild(aonDialog);

		let rootPanel = this.createElement(TAG.DIV);
		rootPanel.id = this.ROOT_PANEL;
		rootPanel.className = CSS.AON_MOBILE_ROOT_PANEL;
		
		let lastScroll = rootPanel.scrollTop;

		rootPanel.addEventListener('scroll', () => {
 			const currentScroll = rootPanel.scrollTop;
			let aonMobileMenuSidenav = this.getElement("aonMobileMenuSidenav");
  			if (aonMobileMenuSidenav && currentScroll > lastScroll) {
				aonMobileMenuSidenav.style.left = "40px";
				aonMobileMenuSidenav.style.right = "40px";
				aonMobileMenuSidenav.style.height = "50px";
    			console.log('scroll hacia abajo');
  			} else if (aonMobileMenuSidenav && currentScroll < lastScroll) {
				aonMobileMenuSidenav.style.left = "20px";
				aonMobileMenuSidenav.style.right = "20px";
				aonMobileMenuSidenav.style.height = "60px";
				console.log('scroll hacia arriba');
  			}

  			lastScroll = currentScroll;
		});
		this.appendChild(rootPanel);

		let aonMobileMenu = new AonMobileMenu();
		aonMobileMenu.id = this.AON_MOBILE_MENU;
		this.appendChild(aonMobileMenu);
	}

	build() {

		let gradiantHeader = this.createElement(TAG.DIV);
		gradiantHeader.className = 'aonRootGradiantHeader';
		gradiantHeader.id = 'aonRootGradiantHeader';
		//this.appendChild(gradiantHeader);
		let gradiantHeaderTop = this.createElement(TAG.DIV);
		gradiantHeaderTop.className = 'aonRootGradiantHeaderTop';
		gradiantHeaderTop.id = 'aonRootGradiantHeaderTop';
		//this.appendChild(gradiantHeaderTop);
		let gradiantHeaderBottom = this.createElement(TAG.DIV);
		gradiantHeaderBottom.className = 'aonRootGradiantHeaderBottom';
		gradiantHeaderBottom.id = 'aonRootGradiantHeaderBottom';
		//this.appendChild(gradiantHeaderBottom);
		let gradiantHeaderBlur = this.createElement(TAG.DIV);
		gradiantHeaderBlur.className = 'aonRootGradiantHeaderBlur';
		gradiantHeaderBlur.id = 'aonRootGradiantHeaderBlur';
		//this.appendChild(gradiantHeaderBlur);

		let aonHeader = new AonHeader();
		aonHeader.id = this.AON_HEADER;
		aonHeader.newTheme = true;
		this.appendChild(aonHeader);

		aonHeader.setVisibleHomeButton(false);
		aonHeader.setVisibleCompanyListButton(false);
		
		// TimeControl Reason Dialog
	    let aonDialog = new AonDialog();
	    aonDialog.id = 'aonTimeControlReasonDialog';
	    this.appendChild(aonDialog);
		
		let aonMenu = new AonNewMenu();
		aonMenu.id = this.AON_MENU;
		aonMenu.className = CSS.AON_MENU;
		aonMenu.addEventListener(EVENT.AON_APPLICATION_SELECT, (e) => {
			let app = e.detail.app;
			let sidenav = e.detail.sidenav; 
			console.log(JSON.stringify(e.detail));
			const appColor = app.newColor || app.color;
			if ( !app.home ){
				let appEl = aonMenu.buildApp(app, 
				{
					height: '32px',
					color: '#ffffff',
					flexDirection: 'row'
				}, sidenav);
				aonHeader.buildApp(app,sidenav);
				aonHeader.setVisibleLogo(!appEl);
				aonHeader.setVisibleApp(appEl);
			} else if(app == APPS || app == APPLICATIONS){
				let appEl = aonMenu.buildApp(NEW_APPS, 
					{
						height: '32px',
						color: '#ffffff',
						flexDirection: 'row'
					}, sidenav);
					aonHeader.buildApp(NEW_APPS,sidenav);
					aonHeader.setVisibleLogo(!appEl);
					aonHeader.setVisibleApp(NEW_APPS);
			} else {
				aonHeader.setVisibleApp(false);
				aonHeader.setVisibleLogo(true);
			}
			
			aonHeader.setColor();
			aonHeader.setBackgroundColor();
			let appName = app.app[0].toUpperCase() + app.app.slice(1);
			aonHeader.setClassName(`${CSS.AON_HEADER} ${CSS.AON_HEADER}${appName}`); 
			
		});

		
		let rootPanel = this.createElement(TAG.DIV);
		rootPanel.id = this.ROOT_PANEL;
		rootPanel.className = "rootPanel";
		rootPanel.style.overflowY = "auto";
		this.appendChild(rootPanel);
		this.appendChild(aonMenu);

		this.rightPanel = new AonRightPanel();
		this.rightPanel.addEventListener(EVENT.CLOSE, () => {
			// CLose Right Sidenav
			document.removeEventListener('click', this.closeRightPanelHandler);
		});

		this.appendChild(this.rightPanel);

		this.editButton = this.getElement('aonRightPanelEditButton');
		this.configButton = this.getElement('aonRightPanelConfigButton');
		this.helpButton = this.getElement('aonRightPanelHelpButton');
		this.notificationButton = this.getElement('aonRightPanelNotificationButton');
		
		if(LS.isRightPanel()) {
			LS.removeRightPanel();
			this.rightPanel.clear();
			this.rightPanel.setContent(new AonConfig());
			this.rightPanel.setTitle(MSG.CONFIGURATION);
			this.rightPanel.open();
		}

		let headerConfig = this.getElement('aonHeaderConfig');
		if (headerConfig) {
			headerConfig.addEventListener(EVENT.CLICK, () => {
				let config = this.getElement('aonConfig');
				if (!this.rightPanel.isClose() && config) {
					this.closeRightPanel();
				} else {
					let rightPanelContent = document.querySelector(".rightPanel");
					if(rightPanelContent) this.closeRightPanel();
					
					this.rightPanel.clear();
					this.rightPanel.setContent(new AonConfig());
					this.rightPanel.setTitle(MSG.CONFIGURATION);
					
					this.configButton.style.display='block';
					this.helpButton.style.display='none';
					this.editButton.style.display='none';
					this.notificationButton.style.display='none';
					
					this.rightPanel.open();
					this.closeRightPanelHandler = this.closePopupOnOutsideClick.bind(this, headerConfig);
					document.addEventListener('click', this.closeRightPanelHandler); // Agregar evento de cerrar al hacer 
				}
			});
		}
		
		let headerHelp = this.getElement('aonHeaderHelp');
		if (headerHelp) {
			headerHelp.addEventListener(EVENT.CLICK, () => {
				let help = this.getElement('aonHelp');
				if(!this.rightPanel.isClose() && help) {
					this.closeRightPanel();
				} else {
					let rightPanelContent = document.querySelector(".rightPanel");
					if(rightPanelContent) this.closeRightPanel();
					
					this.rightPanel.clear();
					let aonHelp = new AonHelp();
					this.rightPanel.setContent(aonHelp);
					this.rightPanel.setTitle(MSG.HELP);
					
					this.helpButton.style.display='block';
					this.editButton.style.display='none';
					this.notificationButton.style.display='none';
					this.configButton.style.display='none';
					
					this.rightPanel.open();
					this.closeRightPanelHandler = this.closePopupOnOutsideClick.bind(this, headerHelp);
					document.addEventListener('click', this.closeRightPanelHandler); // Agregar evento de cerrar al hacer
					aonHelp.addEventListener(EVENT.AON_APPLICATION_OPEN, () => this.closeRightPanel() ); 
				}
			});
		}
		
		let headerUser = this.getElement('aonHeaderUser');
		if (headerUser) {
			headerUser.addEventListener(EVENT.CLICK, () => {
				let user = this.getElement('aonLoginPanel');
				if(!this.rightPanel.isClose() && user) {
					this.closeRightPanel();
				} else  {
					let rightPanelContent = document.querySelector(".rightPanel");
					if(rightPanelContent) this.closeRightPanel();
					
					this.rightPanel.clear();
					this.rightPanel.setContent(new AonLoginPanel());
					this.rightPanel.setTitle(MSG.USER);
					
					this.editButton.style.display = 'block';
					this.helpButton.style.display = 'none';
					this.configButton.style.display = 'none';
					this.notificationButton.style.display='none';
					
					this.rightPanel.open("200px","0px","0 24px 54px rgba(0,0,0,.15),0 4.5px 13.5px rgba(0,0,0,.08)");
					this.closeRightPanelHandler = this.closePopupOnOutsideClick.bind(this, headerUser);
					document.addEventListener('click', this.closeRightPanelHandler); // Agregar evento de cerrar al hacer 
				}
			});
		}

		let headerNotification = this.getElement('aonHeaderNotification');
		if (headerNotification) {
			headerNotification.addEventListener(EVENT.CLICK, () => {
				let notification = this.getElement('aonNotificationPanel');
				if(!this.rightPanel.isClose() && notification) {
					this.closeRightPanel();
				} else  {
					let rightPanelContent = document.querySelector(".rightPanel");
					if(rightPanelContent) this.closeRightPanel();
					
					this.rightPanel.clear();
					this.rightPanel.setContent(new AonNotificationPanel());
					this.rightPanel.setTitle(MSG.NOTIFICATIONS);
					
					this.editButton.style.display = 'none';
					this.helpButton.style.display = 'none';
					this.configButton.style.display = 'none';
					this.notificationButton.style.display='block';
					
					this.rightPanel.open();
					this.closeRightPanelHandler = this.closePopupOnOutsideClick.bind(this, headerNotification);
					document.addEventListener('click', this.closeRightPanelHandler); // Agregar evento de cerrar al hacer 
				}
			});
		}
		
		// Menu button collapse
		let topnav = this.getElement("aonMenuTopnav");
		topnav && topnav.classList.remove("closeSidenav");
		let aonMenuButton = this.createMenuButton();
		this.appendChild(aonMenuButton);
		
		let menuAnchorIcon = this.getElement("aonMenuAnchorDivIcon");
		new AonTooltip(
			menuAnchorIcon,
			'Cerrar Menu',
			{ position: 'right' }
		);

		// New Option Dialog
		if(LS.isFutureTheme()){
			let newDialogMenu = new AonNewDialogMenu();
		    newDialogMenu.id = 'newDialogMenu';
		    this.appendChild(newDialogMenu);
		    
		    let newFixedButton = new AonNewFixedButton();
			newFixedButton.id = 'newFixedButton';
			newFixedButton.classList.add('hidden');
		    this.appendChild(newFixedButton);
		 
		} else {
			let dialogMenu = new AonDialogMenu();
		    dialogMenu.id = this.OPTION_DIALOG;
		    this.appendChild(dialogMenu);	
		}
		
	}
	
	createMenuButton(){
		let menuAnchor = this.createElement(TAG.A);
		menuAnchor.id = "aonMenuAnchor";
		menuAnchor.addEventListener(EVENT.CLICK, () => {
			this.collapseSidenavMenu();
		});
		
		let div = this.createElement(TAG.DIV);
		div.id = "aonMenuAnchorDiv";
		div.classList.add("aonMenuAnchorDiv");
		
		let icon = this.createElement(TAG.SPAN);
		icon.id = "aonMenuAnchorDivIcon";
		icon.classList.add(CSS.AON_SYMBOLS_OUTLINED);
		icon.textContent = AON_SYMBOLS.ARROW_LEFT_CIRCLE;
		icon.classList.add("aonNewMenuAppIcon");
		div.appendChild(icon);
		
		let span = this.createElement(TAG.SPAN);
		span.classList.add("aonMenuAnchorDivSpan");
		span.textContent = 'Menu'; 
		div.appendChild(span);
		
		menuAnchor.appendChild(div);
		
		return menuAnchor;
	}
	
	collapseSidenavMenu(){
		let menuAnchor = this.getElement("aonMenuAnchor");
		menuAnchor.classList.toggle("close");
		
		let aonMenuSidenav = this.getElement("aonMenuSidenav");
		aonMenuSidenav.classList.toggle("close");
		
		let rootPanel = this.getElement("rootPanel");
		rootPanel.classList.toggle("closeSidenav");
		
		let topnav = this.getElement("aonMenuTopnav");
		topnav.classList.toggle("closeSidenav");
		
		let menuAnchorIcon = this.getElement("aonMenuAnchorDivIcon");
		new AonTooltip(
			menuAnchorIcon,
			menuAnchor.classList.contains('close') ? 'Abrir Menu' : 'Cerrar Menu',
			{ position: 'right' }
		);
		
	}
	
	closeRightPanel(){
		this.editButton.style.display='none';
		this.helpButton.style.display='none';
		this.configButton.style.display='none';
		this.notificationButton.style.display='none';
		
		this.rightPanel.close();
	}
	
	// Cerrar el popup si se hace clic fuera del popup-content
 	closePopupOnOutsideClick(openpBtn, event) {
		 let rightPanelContent = document.querySelector(".rightPanel");
		
		 if (!rightPanelContent.contains(event.target) && !openpBtn.contains(event.target)) {
		
	       	this.editButton.style.display='none';
			this.configButton.style.display='none';
			this.notificationButton.style.display='none';
			this.helpButton.style.display='none';
			this.rightPanel.close();
			
			// Its remove on this.rightPanel.addEventListener(EVENT.CLOSE, ...)
	        //document.removeEventListener('click', this.closeRightPanelHandler); // Remover el evento una vez cerrado
	        
	    }
	}
	
	customize(){
		let aonHeader = this.getElement(this.AON_HEADER);
		let aonSearchDiv = aonHeader.getElement("aon-search-div");
		aonSearchDiv.style.height = '32px';
		aonSearchDiv.style.border = 'none';
		aonSearchDiv.style.borderRadius = '4px';
		aonSearchDiv.style.backgroundColor = "var(--aonSearchBar)";
		aonSearchDiv.style.boxShadow = '0 2px 4px rgba(0,0,0,.14),0 0 2px rgba(0,0,0,.12)';
		aonSearchDiv.style.alignItems = 'center';
	}
	
	showMenu(bool) {
		
	}

}
window.customElements.define('aon-home', AonHome);
