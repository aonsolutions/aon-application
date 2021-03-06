import {AonElement} from '../../components/AonElement.js';
import {getDomainUserRoles} from  '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

import '../../components/aon-application.js';
import '../../components/aon-card.js';
import '../../components/aon-input.js';
import '../user/aon-user-list.js';
import '../user/aon-user.js';


import * as CONSTANT from "../../environments/constants.js";
import * as MSG from "../../environments/msg.js";
import * as MATERIAL_ICONS from "../../environments/materialIcons.js";

export class AonUserPanel extends AonElement {

	AON_USER_PANEL;
	get id() {
		return this.getAttribute('id');
	}

	set id(id) {
		this.setAttribute('id', id);
	}

	constructor () {
		super();
		this.AON_USER_PANEL = 'aonUserPanel';
	}

	connectedCallback () {
		// this.innerHTML = `
		// 	<aon-application id="${this.AON_USER_PANEL}" title="${MSG.AON_MSG_USERS}" sidenav="block"></aon-application>
		// `;
		this.innerHTML = `
			<aon-application id="${this.AON_USER_PANEL}" title="${MSG.AON_MSG_USERS}"></aon-application>
		`;
		getDomainUserRoles({}).then(r => {
				this._roles = new DomainUserRoles(r);
				this.build();
		});

  }

	build() {
		this.buildUser()
	}

	buildUser() {
		let aonUserPanel = this.getElement(this.AON_USER_PANEL);
		aonUserPanel.removeToolbarOptions();

		aonUserPanel.addToolbarOption('UserShare', 'share', () => this.buildCreateUser(true));
		aonUserPanel.addToolbarOption('UserAdd', 'add', () => this.buildCreateUser(true));

		let filterOptions = this._roles.isParentUser() ? [{
				name: 'Empresa',
				icon: 'domain',
				fn: () => this.init('company')
			}, {
				name: 'Entorno',
				icon: 'apartment',
				fn: () => this.init('entorno')
			}, {
				name: 'Compartidas',
				icon: 'share',
				fn: () => this.init('shared')
			}
		]: [{
				name: 'Empresa',
				icon: 'domain',
				fn: () => this.init('company')
			}, {
				name: 'Compartidas',
				icon: 'share',
				fn: () => this.init('shared')
			}
		];
		aonUserPanel.addSidenavOptions('Usuarios', filterOptions);

		aonUserPanel.setContentHTML('<aon-user-list> </aon-user-list>');
		aonUserPanel.closeSidenav();
	}

	init(filter){
		let aonUserPanel = this.getElement(this.AON_USER_PANEL);
		aonUserPanel.setContentHTML(`<aon-user-list filter="${filter}"> </aon-user-list>`);
	}

	buildCreateUser(share) {
		let content = document.getElementById('aonUserPanelContent');
		content.innerHTML = '<aon-user id="aonUserCreate" showApps="true" showToolbar="true"><aon-user>';
		let aonUser = document.getElementById('aonUserCreate');
		aonUser.style.width = "100%";
		if(share)	aonUser.setAttribute('share', share);
	}

	getApplication() {
		return this.getElement(this.AON_USER_PANEL);
	}
}

window.customElements.define('aon-user-panel', AonUserPanel);
