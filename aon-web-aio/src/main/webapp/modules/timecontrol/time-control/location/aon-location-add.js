import { AonElement } from "../../../../components/AonElement.js";
import { setValueName, serializeForm, waitEl } from "../../../../services/utils.js";
import { deleteLocation, getTastHolders, saveLocation } from "../../../../services/service.js";
import { ToolbarType } from "../../../../models/enums.js";
import { SIGNIN_VIEWS } from "../../signinEnums.js";
import * as ACTION from '../../../actions.js';
import { CONSTANT, CSS, EVENT, MSG, TAG } from "../../../../environments/environments.js";
import { AonMap } from "../../../../components/aon-map.js";
import { CreateComponent, createInput, createSelect } from "../../../../components/CreateComponent.js";
import { IN_REASON } from "../../../../models/timecontrol/TimeControlReason.js";


export class AonLocationAdd extends AonElement {
	NAME;
	static get observedAttributes() {
		return [CONSTANT.DATA, CONSTANT.ADD];
	}

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	get add() {
		return this.getAttribute(CONSTANT.ADD) == CONSTANT.TRUE;
	}

	set add(add) {
		this.setAttribute(CONSTANT.ADD, add);
	}

	get data() {
		return JSON.parse(this.getAttribute(CONSTANT.DATA));
	}

	set data(value) {
		this.setAttribute(CONSTANT.DATA, JSON.stringify(value));
	}


	constructor() {
		super();
		this.id = this.id || SIGNIN_VIEWS.AON_LOCATION_ADD;
		this.NAME = MSG.LOCATION;
		this.TOOLBAR = this.id + "Toolbar";
		this.applicationEl = this.getApplication();
	}

	connectedCallback() {
		this.build();
	}

	attributeChangedCallback(name, oldValue, newValue) {
		if (CONSTANT.DATA == name && newValue)
			this.setFormValues();
		else if (CONSTANT.ADD == name && newValue)
			this.paintViewMap(undefined);
	}


	build() {
		this.applicationEl.removeToolbarOptions();
		this.paintView();
		this.listTypes();
		this.listTaskHolder();
		this.buildToolbar();
		this.eventListener();
		
		if(this.data === null)
			this.setFormValues();
	}

	listTypes() {
		let typeSelect = this.getElement("type");
		typeSelect.options = JSON.stringify(
			IN_REASON.map((r) => ({ ...r, name: `${r.name}`, value: r.value }))
		);
	}

	async listTaskHolder() {
		let registrySelect = this.getElement("registry");
		try {
			const resp = await getTastHolders();
			registrySelect.options = JSON.stringify(
				resp.map((r) => {
					return {
						name: `${r.name}`,
						value: `${r.id}`,
					};
				})
			);
		} catch (error) { }
	}

	eventListener() {
		let typeSelect = this.getElement("type");
		let registrySelect = this.getElement("registry");
		typeSelect.addEventListener(EVENT.CHANGE, ({ detail }) => {
			registrySelect.style.display = detail && detail.value !== "1" ? 'none' : '';
		});
	}

	paintView() {
		CreateComponent.createAonToolbar({ id: this.TOOLBAR, type: ToolbarType.SECONDARY }, this);

		const form = CreateComponent.createForm(this.id + "Form");
		this.appendChild(form);

		let div = this.createElement(TAG.DIV);
		div.id = this.id + "Div";

		const className = this.isMobile() ? "" : CSS.AON_SUB_CONTENT;
		div.className = className;
		form.appendChild(div);

		let div2 = this.createElement(TAG.DIV);
		div2.classList.add(CSS.AON_COL_XS_12);
		div.appendChild(div2);

		const aonCard = CreateComponent.createAonCard({ id: this.id + "Card", title: "Datos de la " + this.NAME, flex: "true" }, div2).getContent();

		let divG = this.createElement(TAG.DIV);
		divG.classList.add(CSS.AON_COL_SM_4, CSS.AON_COL_XS_10);
		aonCard.appendChild(divG);

		createInput("description", MSG.NAME, divG);

		divG = this.createElement(TAG.DIV);
		divG.classList.add(CSS.AON_COL_SM_2, CSS.AON_COL_XS_2);
		aonCard.appendChild(divG);

		createInput("radio", MSG.RADIO, divG);

		divG = this.createElement(TAG.DIV);
		divG.classList.add(CSS.AON_COL_SM_3, CSS.AON_COL_XS_2);
		aonCard.appendChild(divG);

		createSelect("type", "Tipo", divG);

		divG = this.createElement(TAG.DIV);
		divG.classList.add(CSS.AON_COL_SM_3, CSS.AON_COL_XS_2);
		aonCard.appendChild(divG);

		let registry = createSelect("registry", "Operario", divG);
		registry.autocomplete = true;

		divG = this.createElement(TAG.DIV);
		divG.classList.add(CSS.AON_COL_XS_12);
		aonCard.appendChild(divG);

		createInput("latitude", "Latitud", divG);

		createInput("longitude", "Longuitud", divG);

		createInput("id", "Id", divG);

		const divMap = this.createElement(TAG.DIV);
		divMap.classList.add(CSS.AON_COL_XS_12);
		divMap.id = "divMap";
		div.appendChild(divMap);
	}

	buildToolbar() {
		const toolbarEl = this.getElement(this.TOOLBAR);
		toolbarEl.removeButtons();
		if (this.data && this.data.id) {
			toolbarEl.addButton2(ACTION.DELETE, () => this.delete());
			toolbarEl.title = MSG.EDIT;
		} else {
			toolbarEl.title = MSG.REGISTER;
		}
		toolbarEl.addButton2(ACTION.SAVE, () => this.save());
		toolbarEl.addButton2(ACTION.BACK, () => this.back());
	}

	async paintViewMap(data) {
		let divMap = await waitEl(`#divMap`);
		divMap.innerHTML = "";

		let position = null;
		if (data && data.latitude && data.longitude)
			position = { lat: data.latitude, lng: data.longitude }

		if (!position)
			position = { lat: "40.41849734317215", lng: "-3.7008337076347084" }

		let aonMap = new AonMap();
		aonMap.POSITION = position;
		aonMap.geocoder = true;
		aonMap.addEventListener(EVENT.COORDINATES, ({ detail }) => {
			this.setCoordinates(detail);
		});

		aonMap.addEventListener(EVENT.GEOCODE, ({ detail }) => {
			if (detail && detail.name)
				this.getElement("direction").value = detail.name
		});

		const cardContentMap = CreateComponent.createAonCard({ id: this.id + "Map", title: "Mapa", flex: "true" }, divMap).getContent();
		cardContentMap.appendChild(aonMap);
	}

	getFormValues() {
		let serialize = {};

		let descriptionInput = this.getElement("description");
		if (descriptionInput) serialize.description = descriptionInput.getValue();

		let radioInput = this.getElement("radio");
		if (radioInput) serialize.radio = radioInput.getValue();

		let latitudeInput = this.getElement("latitude");
		if (latitudeInput) serialize.latitude = latitudeInput.getValue();

		let longitudeInput = this.getElement("longitude");
		if (longitudeInput) serialize.longitude = longitudeInput.getValue();

		let typeSelect = this.getElement("type");
		serialize.type = typeSelect.getValueObject() && typeSelect.getValueObject().value;

		let registrySelect = this.getElement("registry");
		serialize.registry = registrySelect.getValueObject() && registrySelect.getValueObject().value;

		let idInput = this.getElement("id");
		if (idInput.getValue() !== 'undefined') serialize.id = idInput.getValue();

		return serialize;
	}

	async setFormValues() {
		await waitEl('#latitude');
		const data = this.data;

		let descriptionInput = this.getElement("description");
		let radioInput = this.getElement("radio");
		let latitudeInput = this.getElement("latitude");
		let longitudeInput = this.getElement("longitude");
		let typeSelect = this.getElement("type");
		let registrySelect = this.getElement("registry");
		let idInput = this.getElement("id");
		
		if (data) {

			descriptionInput && descriptionInput.setValue(data.description);
			radioInput && radioInput.setValue(data.radio);
			latitudeInput && latitudeInput.setValue(data.latitude);
			longitudeInput && longitudeInput.setValue(data.longitude);
			typeSelect.setValueZero(data.type);
			registrySelect.setValue(data.registry);
			idInput && idInput.setValue(data.id);

		}

		if (!data || !data.type || data.type != "1") {
			registrySelect.style.display = 'none';
		}

		latitudeInput.setDisabled(true);
		latitudeInput.style.display = 'none';

		longitudeInput.setDisabled(true);
		longitudeInput.style.display = 'none';

		idInput.setDisabled(true);
		idInput.style.display = 'none';
		
		this.paintViewMap(data);
	}

	async save() {
		const data = this.getFormValues();
		const count = Object.keys(data).length;
		if (count > 3) {
			this.applicationEl.startLoading();
			try {
				const { id } = await saveLocation({
					...data,
					coordinates: `${data.latitude},${data.longitude}`,
				});
				this.showToast({ message: MSG.SAVED_DATA, type: CONSTANT.SUCCESS });
				if (id) { setValueName("id", id); }
			} catch (error) {
				this.showToast(error);
			}
			this.applicationEl.stopLoading();
		}
	}

	async delete() {
		this.applicationEl.confirmDialog(MSG.DELETE, `${MSG.DELETE_CONFIRM} ${this.NAME}?`, async () => {
			this.applicationEl.startLoading();
			try {
				const data = this.getFormValues();
				await deleteLocation(data);
				this.showToast({ message: MSG.DELETED_DATA });
				this.back();
			} catch (error) {
				this.showToast(error);
			}
			this.applicationEl.stopLoading();
		});
	}

	deleteManual(id) {
		deleteLocation({ id });
	}

	setCoordinates(data) {
		let lat = data.latitude || data.lat;
		let lng = data.longitude || data.lng;
		if (lat && lng) {
			let latitude = this.getElement("latitude");
			latitude.setValue(lat);
			let longitude = this.getElement("longitude");
			longitude.setValue(lng);
		}
	}

	back() {
		this.applicationEl.getParent().showView(SIGNIN_VIEWS.AON_LOCATION_LIST);
	}
}

window.customElements.define("aon-location-add", AonLocationAdd);
