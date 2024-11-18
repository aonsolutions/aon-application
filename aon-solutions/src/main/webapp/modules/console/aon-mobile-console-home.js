import { AonElement } from '../../components/AonElement.js';
import { CONSTANT, CSS, EVENT, TAG } from "../../environments/environments";
import * as LS from '../../services/localStorageService.js';

export class AonMobileConsoleHome extends AonElement {

    get id() {
		return this.getAttribute(CONSTANT.ID);
	   }

	set id(id) {
	   this.setAttribute(CONSTANT.ID, id);
	}

    connectedCallback () {
        this.initialize();
        this.build();
    }

    initialize() {
        this.id = this.id || 'aonConsoleHome';
    }

    build() {
		let div = this.createElement(TAG.DIV);
        this.appendChild(div);
	
		let titleA = this.createElement(TAG.DIV);
        titleA.className = CSS.AON_SIDENAV_TITLE;
	    titleA.innerHTML = 'UTILIDADES';
		div.appendChild(titleA);

		let ul = this.createElement(TAG.UL);
		ul.classList.add(CSS.AON_UL);
		ul.classList.add(CSS.AON_CLIP);
		div.appendChild(ul);
	
        ul.appendChild(this.buildNotificationsLi("Instalar Nueva Versión", "input_circle", () => {
            let d = this.getApplication().getDialog();
            d.clear();
            if(!this.isMobile()) d.width = '400px';
            d.setTitle("Instalar Nueva Versión");
            d.setContentHTML(`Estás seguro de instalar una nueva versión.`);
            d.addAcceptAction(() => {
                alert("En desarrollo");
            });
            d.open();
		}));
				
		ul.appendChild(this.buildNotificationsLi("Restaurar Versión Anterior", "settings_backup_restore", () => {
            let d = this.getApplication().getDialog();
            d.clear();
            if(!this.isMobile()) d.width = '400px';
            d.setTitle("Instalar Nueva Versión");
            d.setContentHTML(`Estás seguro de restaurar a la versión anterior.`);
            d.addAcceptAction(() => {
                alert("En desarrollo");
            });
            d.open();
		}));
	}

    buildNotificationsLi(name, icon, fn) {
		let li = this.createElement(TAG.LI);
		li.className = 'aonAppMenuSidenavList aonOpacity';
		li.style.height = '40px';
		li.style.lineHeight = '40px';
		// li.style.borderBottom = '1px solid #ddd';

		let i = this.createElement(TAG.I);
		i.className = 'material-icons aonVerticalMiddle';
		i.innerHTML = icon;
		li.appendChild(i);

		let span = this.createElement(TAG.SPAN);
		span.className = 'aonMenuItemSpan';
		span.innerHTML = name;

		li.appendChild(span);
		li.addEventListener(EVENT.CLICK, () => fn());
		return li;
	}
}
if(!window.customElements.get("aon-mobile-console-home")){
	window.customElements.define("aon-mobile-console-home", AonMobileConsoleHome);
}
