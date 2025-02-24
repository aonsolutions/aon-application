import { AonElement } from "../components/AonElement.js";
import { CSS, EVENT, MATERIAL_ICONS, MSG, TAG } from '../environments/environments.js';

export class AonDomains extends AonElement {
	
}

if(!window.customElements.get(TAG.AON_DOMAINS)){
	window.customElements.define(TAG.AON_DOMAINS, AonDomains);
}
