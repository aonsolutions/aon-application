import {AonElement} from '../../components/AonElement.js';
import { ToolbarType} from '../../models/enums.js';
import {AonToolbar} from "../../components/aon-toolbar.js";
import { CONSTANT, MSG, TAG } from '../../environments/environments.js'; 
import * as ACTION from '../actions.js';
import { createCard, createDate, createInput } from '../../components/CreateComponent.js';

export class AonExampleObject extends AonElement {

	EXAMPLE_TOOLBAR;
	EXAMPLE_CARD;
	EXAMPLE_DATE;
	EXAMPLE_INPUT;
	DIV;
	exampleObject;

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
		this.id = this.id || 'aonExampleObject';
		this.EXAMPLE_TOOLBAR = this.id + CONSTANT.TOOLBAR.initCap();
		this.EXAMPLE_CARD = this.id + CONSTANT.CARD.initCap();
		this.EXAMPLE_DATE = this.id + CONSTANT.DATE.initCap();
		this.EXAMPLE_INPUT = this.id + CONSTANT.INPUT.initCap();
		this.DIV = this.id + 'Div';
		this.exampleObject = this.exampleObject || {};
	}

	build() {
		let toolbar = new AonToolbar();
		toolbar.id = this.EXAMPLE_TOOLBAR;
		toolbar.type = ToolbarType.SECONDARY;
		toolbar.title = MSG.EXAMPLE; 
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
		let card = createCard(this.EXAMPLE_CARD, MSG.EXAMPLE, parent);

		let div = this.createDiv();
		card.setContent(div);
	
		let date = createDate(this.EXAMPLE_DATE, MSG.DATE, div);
		date.value = this.exampleObject.date;

		let example = createInput(this.EXAMPLE_INPUT, MSG.EXAMPLE, div);
		example.setValue(this.exampleObject.example);	
	}

	setExampleObject(exampleObject) {
		this.exampleObject = exampleObject;
	}

	// ACTIONS

	back() {
		alert("BACK EXAMPLE");
	}

	add() {
		alert("ADD EXAMPLE");
	}
}

if(!window.customElements.get(TAG.AON_EXAMPLE_OBJECT)) {
	window.customElements.define(TAG.AON_EXAMPLE_OBJECT, AonExampleObject);
}