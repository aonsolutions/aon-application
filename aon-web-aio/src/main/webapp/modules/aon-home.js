import { AonElement } from '../components/AonElement.js';
import { AonHeader } from '../modules/aon-header.js';
import { MSG, CSS, EVENT, TAG } from '../environments/environments.js';
import { AonNewMenu } from './aon-new-menu.js';
import { AonConfig } from './aon-config.js';
import { AonHelp } from './aon-help.js';
import { AonRightPanel } from './aon-right-panel.js';
import { AonLoginPanel } from './aon-login-panel.js';
import { AonNotificationPanel } from './aon-notification-panel.js';

import { AonMobileHeader } from '../modules/aon-mobile-header.js';
import { AonMobileMenu } from '../modules/aon-mobile-menu.js';

export class AonHome extends AonElement {
	AON_MENU;
	AON_MOBILE_MENU;
	AON_HEADER;
	ROOT_PANEL;
	RIGHT_PANEL;

	// Right Panel, needed for hide on click outside
	rightPanel;
	editButton;
	configButton;
	helpButton;
	notificationButton;

	closeRightPanelHandler;
  currentRightPanelSection = null;

  constructor () {
      super();
	}

	connectedCallback () {
		this.clear();
		this.initialize();
    this.build();
	}

	initialize() {
		this.AON_MENU = 'aonMenu';
		this.AON_MOBILE_MENU = 'aonMobileMenu';
		this.AON_HEADER = 'aonHeader';
		this.ROOT_PANEL = 'rootPanel';
		this.RIGHT_PANEL = 'rightPanel';
	}

	buildMobile() {
		let aonMobileHeader = new AonMobileHeader();
		aonMobileHeader.id = this.AON_HEADER;
		this.appendChild(aonMobileHeader);

		let rootPanel = this.createElement(TAG.DIV);
		rootPanel.id = this.ROOT_PANEL;
		rootPanel.className = CSS.AON_MOBILE_ROOT_PANEL;
		this.appendChild(rootPanel);

		let aonMobileMenu = new AonMobileMenu();
		aonMobileMenu.id = this.AON_MOBILE_MENU;
		this.appendChild(aonMobileMenu);
	}

	build() {
		let aonHeader = new AonHeader();
		aonHeader.id = this.AON_HEADER;
		aonHeader.newTheme = true;
		this.appendChild(aonHeader);

		aonHeader.setVisibleCompanyListButton(false);

		let content = this.createElement(TAG.DIV);
		content.id  = "homeContent";

		let aonMenu = new AonNewMenu();
		aonMenu.id = this.AON_MENU;
		aonMenu.className = CSS.AON_MENU;
        aonMenu.addEventListener(EVENT.AON_APPLICATION_SELECT, (e) => {
          let app = e.detail.app;
          let sidenav = e.detail.sidenav; 
          // console.log(JSON.stringify(e.detail));
          const appColor = app.newColor || app.color;
          if ( !app.home ){
              let appEl = aonMenu.buildApp(app, {
                  height: '32px',
                  color: '#ffffff',
                  flexDirection: 'row'
              }, sidenav);
              aonHeader.buildApp(app, sidenav);
              aonHeader.setVisibleLogo(!appEl);
              aonHeader.setVisibleApp(appEl);
          } else {
              aonHeader.setVisibleApp(false);
              aonHeader.setVisibleLogo(true);
          }

          aonHeader.setColor();
          aonHeader.setBackgroundColor();
          let appName = app.app[0].toUpperCase() + app.app.slice(1);
          aonHeader.setClassName(`${CSS.AON_HEADER} ${CSS.AON_HEADER}${appName}`); 
        });
        content.appendChild(aonMenu);

		let rootPanel = this.createElement(TAG.DIV);
		rootPanel.id = this.ROOT_PANEL;
		rootPanel.className = "rootPanel";
		// rootPanel.style.overflowY = "auto";
		content.appendChild(rootPanel);
		this.appendChild(content);

    //
    // Botones de la cabecera
    //  
      this.rightPanel = new AonRightPanel();
      this.appendChild(this.rightPanel);
      // Botones con opcion de abrir un AonRightPanel()
      // Cada boton por lo que he visto se crea en aon-header.js
      const panelButtons = [
        { id: 'aonHeaderUser',         title: MSG.USER,          content: () => new AonLoginPanel(), key: 'user' },
        { id: 'aonHeaderNotification', title: MSG.NOTIFICATIONS, content: () => new AonNotificationPanel(), key: 'notifications' },
        { id: 'aonHeaderConfig',       title: MSG.CONFIGURATION, content: () => new AonConfig(), key: 'config' },
        { id: 'aonHeaderHelp',         title: MSG.HELP,          content: () => new AonHelp(), key: 'help' }
      ];
      // Montamos funcionalidad para cada boton.
      panelButtons.forEach(({ id, title, content, key }) => {
        const el = this.getElement(id);
        if (el) {
          el.addEventListener(EVENT.CLICK, (e) => {
            e.stopPropagation();
            this.toggleRightPanel(key, title, content());
          });
        }
      });
    //
    // FIN Botones de la cabecera
    //
	}

// Logica comun de apertura/cierre
    toggleRightPanel(sectionKey, title, contentInstance) {
      if (this.currentRightPanelSection === sectionKey && this.rightPanel.isOpen()) {
        this.rightPanel.close();
        this.currentRightPanelSection = null;
      } else {
        this.rightPanel.clear();
        this.rightPanel.setTitle(title);
        this.rightPanel.setContent(contentInstance);
        this.rightPanel.open();
        this.currentRightPanelSection = sectionKey;
      }
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
		}
	}
	
	customize(){}
	
	showMenu(bool) {}

}

window.customElements.define('aon-home', AonHome);