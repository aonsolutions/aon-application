import {AonElement} from '../../components/AonElement.js';
import {getDomainUserRoles} from  '../../services/service.js';
import {DomainUserRoles} from '../../models/DomainUserRoles.js';

import '../../components/aon-application.js';
import '../../components/aon-card.js';
import '../../components/aon-input.js';
import {AonUserList} from '../user/aon-user-list.js';
import '../user/aon-user.js';
import { MSG } from '../../environments/environments.js';
import { AonUser } from '../user/aon-user.js';
import 'aoncss';

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
		this.innerHTML = `
			<aon-application id="${this.AON_USER_PANEL}" title="${MSG.USERS}"></aon-application>
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
		aonUserPanel.addToolbarOption('UserAdd', 'add', () => this.buildCreateUser(false));

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

		aonUserPanel.setContent(new AonUserList());
		aonUserPanel.closeSidenav();
	}

	init(type){
		this.getApplication().cleanSearchValue();
		let aonUserPanel = this.getElement(this.AON_USER_PANEL);
		
		let userList = new AonUserList();
		userList.setType(type);
		aonUserPanel.setContent(userList);
		userList.init();
		// aonUserPanel.setContent(`<aon-user-list filter="${filter}"> </aon-user-list>`);
	}

	buildCreateUser(share) {
		let aonUser = new AonUser();
		aonUser.id = 'aonUserCreate';
		aonUser.setShowApps(true);
		aonUser.setShowToolbar(true);
		aonUser.style.width = "100%";
		if(share) aonUser.setAttribute('share', share);
		
		this.getApplication().setContent(aonUser);	
	}

	getApplication() {
		return this.getElement(this.AON_USER_PANEL);
	}
}
if(!window.customElements.get('aon-user-panel')){
	window.customElements.define('aon-user-panel', AonUserPanel);
}

