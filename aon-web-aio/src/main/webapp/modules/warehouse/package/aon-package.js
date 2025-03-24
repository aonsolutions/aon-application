import {AonElement} from '../../../components/AonElement.js';
import { ToolbarType} from '../../../models/enums.js';
import {AonToolbar} from "../../../components/aon-toolbar.js";
import { CONSTANT, MSG, TAG } from '../../../environments/environments.js'; 
import * as ACTION from '../../actions.js';
import { createCard, createInput } from '../../../components/CreateComponent.js';

export class AonPackage extends AonElement {

	TOOLBAR;
	CARD;
	INPUT;
	DIV;
	object;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor () {
		super();
	}

	connectedCallback () {
		this.initialize();
		this.build();
    }

	initialize() {
		this.id = this.id || 'aonPackage';
		this.TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.CARD = this.id + CONSTANT.CARD.initCap();
		this.DATE = this.id + CONSTANT.DATE.initCap();
		this.INPUT = this.id + CONSTANT.INPUT.initCap();
		this.DIV = this.id + 'Div';
		this.object = this.object || {};
	}

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = MSG.PACKAGE; 
		this.appendChild(toolbar);

		toolbar.addButton2(ACTION.ADD, () => this.add());
		toolbar.addButton2(ACTION.BACK, () => this.back());

		let div = this.createDiv();
		div.id = this.DIV;
		div.style.width = "100%";
		this.appendChild(div);
		this.buildCard(div);
	}

	buildCard(parent) {
		let card = createCard(this.CARD, MSG.PACKAGE, parent);

		let div = this.createDiv();
		card.setContent(div);
	
		let example = createInput(this.INPUT, MSG.PACKAGE, div);
		example.setValue(this.object.name);	
	}

	setPackage(object) {
		this.object = object;
	}

	// ACTIONS

	back() {
		alert("BACK EXAMPLE");
	}

	add() {
		alert("ADD EXAMPLE");
	}
}

if(!window.customElements.get(TAG.AON_PACKAGE)) {
	window.customElements.define(TAG.AON_PACKAGE, AonPackage);
}