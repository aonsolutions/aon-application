import {ToolbarType} from '../../models/enums.js';

import {AonToolbar} from "../../components/aon-toolbar.js";

import { TAG } from '../../environments/environments.js'; 

import * as ACTION from '../actions.js';
import { AonReg } from './aon-reg.js';

export class AonMobileReg extends AonReg {

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.REGISTRY_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = this.registry.id ? this.registry.name : 'NUEVO REGISTRO';
		this.appendChild(toolbar);
		toolbar.addButton2(ACTION.SAVE, () => this.save());
		toolbar.addButton2(ACTION.BACK, () => this.back());

		this.buildGeneralData();
	}

	buildGeneralData() {
		this.buildGeneralCard(this);
		this.buildMediaCard(this);
	}
}

if(!window.customElements.get(TAG.AON_MOBILE_REG)){
	window.customElements.define(TAG.AON_MOBILE_REG, AonMobileReg);
}
