import { AonElement } from '../../components/AonElement.js';
import { getLocationByCoordinates, getPeriod, getTaskHolder, getTaskHoldersUser, getTaskHolderTimeControl, getTimeControl, saveTimeControl, saveTimeControlDetail } from '../../services/service.js';
import { getPosition } from '../../services/maps.js';
import { AonSelect } from '../../components/aon-select.js';
import { SIGNIN_VIEWS } from "./signinEnums.js";
import { CONSTANT, EVENT, MATERIAL_ICONS, MSG, TAG, } from '../../environments/environments.js';
import { timeHour } from './time-control/utils.js';
import { AonDateUtils } from '../utils/AonDateUtils.js';
import * as LS from "../../services/localStorageService.js";
import { IN_REASON, PAUSE_REASON } from '../../models/timecontrol/TimeControlReason.js';
import { createInput } from '../../components/CreateComponent.js';

export class AonSign extends AonElement {
	_taskHolders;
	_taskHolder;
	parent;
	tc;
	AON_SIGN;
	CONTENT;
	TIME;
	TIME_ID;
	// TOTAL_HOUR;

	DIALOG;

	get id() {
		return this.getAttribute(CONSTANT.ID);
	}

	set id(id) {
		this.setAttribute(CONSTANT.ID, id);
	}

	constructor() {
		super();
	}

	connectedCallback() {
		this.initialize();
	}

	initialize() {
		this.AON_SIGN = SIGNIN_VIEWS.AON_SIGN;
		this.id = this.id || this.AON_SIGN;
		this.CONTENT = this.id + 'Content';
		this.TIME = this.id + 'Time';
		this.TOTAL_HOUR = "totalHour";
		this.TIME_ID = "TIME_ID";
		this.DIALOG = 'aonTimeControlReasonDialog';

		this.applicationEl = this.getApplication();
		this.parent = this.parent || false;
		getTaskHolder({ reload: true });
		if (this.isMobile()) {
			getPosition().catch(console.error);  // GET POSITION
		}
		if (this.parent) {
			getTaskHoldersUser().then(r => {
				if (r.length > 0) {
					this._taskHolders = r;
					this._taskHolder = r[0].id;
					this.build();
				}
			});
		} else this.build();
	}

	disconnectedCallback() {
		localStorage.removeItem(this.TIME_ID);
	}

	setParent(parent) {
		this.parent = parent;
	}

	setTimeControl(tc) {
		this.tc = tc;
	}

	build() {
		let divGeneral = this.createElement(TAG.DIV);
		divGeneral.style.display = "flex";
		divGeneral.style.flexDirection = "column";
		divGeneral.style.alignItems = "center";
		divGeneral.style.gap = ".5rem";

		this.appendChild(divGeneral);

		// TimeControl Reason Dialog
		let aonDialog = this.getElement(this.DIALOG);
		if (this.isMobile()) aonDialog.type = "fullscreen";
		else aonDialog.width = '600px';
		aonDialog.addEventListener(EVENT.CLOSE, (e) => e.stopPropagation());
		aonDialog.onclick = (e) => {
			e.stopPropagation();
			aonDialog.close();
			this.disabledButton(false);
		}

		if (this.isMobile()) {
			this.parentNode.style.marginLeft = 0;
		} else {
			this.parentNode.style.paddingLeft = 0;
		}

		if (this.parent && this._taskHolders.length > 1) {
			let company = this.createElement(TAG.DIV);
			company.style.marginLeft = '20px';
			company.style.width = '200px';
			divGeneral.appendChild(company);

			let select = new AonSelect();
			select.id = this.AON_SIGN + 'Select2';
			select.title = MSG.COMPANY;
			select.setOptions(this._taskHolders.map(c => ({ value: c.id, name: c.company })));
			company.appendChild(select);

			select.addEventListener(EVENT.CHANGE, () => {
				this._taskHolder = select.value;
				getTimeControl({ parent: true, task_holder: this._taskHolder }).then(r => this.buildSignin(r));
			});

			select.value = this._taskHolders[0].id
		}

		if (!this.getElement(this.TIME) || this.isMobile()) {
			let time = this.createElement(TAG.DIV);
			time.style.fontSize = '1.6rem';
			time.id = this.TIME;
			time.innerHTML = "00:00:00";
			divGeneral.appendChild(time);
		}

		let div = this.createElement(TAG.DIV);
		div.id = this.CONTENT;
		div.style.display = "flex";
		div.style.flexDirection = "column";
		div.style.alignItems = "center";
		div.style.gap = ".5rem";

		divGeneral.appendChild(div);

		if (this.tc) {
			this.buildSignin(this.tc);
		}
	}

	entrada() {
		let content = this.getElement(this.CONTENT);
		if (content) {
			this.clearElement(content);
			let button = this.createElement(TAG.BUTTON);
			button.id = this.id + "Entrada";
			button.className = 'aonButton';
			button.classList.add('aonTimeControlButton');
			button.style.backgroundColor = '#86D364';
			button.innerHTML = MSG.ENTRY.toUpperCase();
			if (this.isMobile()) {
				button.style.width = "60%";
				button.style.borderRadius = "12px";
			}
			button.addEventListener(EVENT.CLICK, (event) => {
				event.stopPropagation();
				this.saveTimeCtrl('in');
			});
			content.appendChild(button);
		}
	}

	vuelta() {
		let content = this.getElement(this.CONTENT);
		if (content) {
			this.clearElement(content);

			let buttons = this.createElement(TAG.DIV);
			buttons.style.display = "flex";
			buttons.style.justifyContent = "center";
			buttons.style.gap = "1rem";

			let button2 = this.createElement(TAG.BUTTON);
			button2.id = this.id + "Salida";
			button2.className = 'aonButton';
			button2.classList.add('aonTimeControlButton');
			button2.style.backgroundColor = '#DC4D30';
			button2.innerHTML = MSG.EXIT.toUpperCase();
			button2.addEventListener(EVENT.CLICK, (event) => {
				event.stopPropagation();
				this.saveTimeCtrl('out');
			});
			buttons.appendChild(button2);

			let button = this.createElement(TAG.BUTTON);
			button.id = this.id + "Vuelta";
			button.className = 'aonButton';
			button.classList.add('aonTimeControlButton');
			button.style.backgroundColor = '#86D364';
			button.innerHTML = 'VUELTA';
			button.addEventListener(EVENT.CLICK, (event) => {
				event.stopPropagation();
				this.saveTimeCtrl('return');
			});
			buttons.appendChild(button);

			content.appendChild(buttons);
		}
	}

	salida() {
		let content = this.getElement(this.CONTENT);
		if (content) {
			this.clearElement(content);

			let buttons = this.createElement(TAG.DIV);
			buttons.style.display = "flex";
			buttons.style.justifyContent = "center";
			buttons.style.gap = "1rem";

			let button = this.createElement(TAG.BUTTON);
			button.id = this.id + "Salida";
			button.className = 'aonButton';
			button.classList.add('aonTimeControlButton');
			button.style.backgroundColor = '#DC4D30';
			button.innerHTML = MSG.EXIT.toUpperCase();
			button.addEventListener(EVENT.CLICK, (event) => {
				event.stopPropagation();
				this.saveTimeCtrl('out');
			});
			buttons.appendChild(button);

			let button2 = this.createElement(TAG.BUTTON);
			button2.className = 'aonButton';
			button2.classList.add('aonTimeControlButton');
			button2.style.backgroundColor = '#F39F1D';
			button2.innerHTML = 'PAUSA';
			button2.addEventListener(EVENT.CLICK, (event) => {
				event.stopPropagation();
				this.saveTimeCtrl('pause');
			});
			buttons.appendChild(button2);

			content.appendChild(buttons);
		}
	}

	async saveTimeCtrl(status) {
		// Spinner
		if(this.applicationEl) this.applicationEl.startLoading();

		this.disabledButton(true);

		let signin = { status, task_holder: this._taskHolder, parent: this.parent };
		let timeOutPosition = false;

		await getPosition()
			.then(position => {
				if (position) {
					signin.coordinates = position.latitude + ',' + position.longitude;
				}
			})
			.catch(error => {
				timeOutPosition = error && error.timeout;
			});

		if (signin.status == 'out' || signin.status == 'return') {

			if (signin.status == 'return') signin.status = 'in';

			if (signin.status == 'in' && signin.coordinates && signin.coordinates.length > 0) {

				const locationResp = await getLocationByCoordinates(signin);

				if (locationResp && locationResp.id) {
					const resp = await saveTimeControl(signin);
					this.setTimeControl(resp);
					this.buildSignin(resp);

					this.disabledButton(false);
					this.showToast({ code: 3, message: 'Marcaje realizado con exito', timeout: false });
					if(this.applicationEl) this.applicationEl.stopLoading();
				} else
					this.openReasonDialog(signin, timeOutPosition);

			} else if (signin.status == 'in') {

				this.openReasonDialog(signin, timeOutPosition);

			} else {

				const resp = await saveTimeControl(signin);

				if (timeOutPosition && this.isMobile() && resp && resp.id) {
					getPosition()
						.then(position => {
							if (position) {
								resp.coordinates = position.latitude + ',' + position.longitude;
								saveTimeControl({ ...resp, ...signin })
									.then(console.log)
									.catch(console.error);
							}
						})
						.catch(console.error);
				}

				this.setTimeControl(resp);
				this.buildSignin(resp);

				this.disabledButton(false);
				this.showToast({ code: 3, message: 'Marcaje realizado con exito', timeout: false });
				if(this.applicationEl) this.applicationEl.stopLoading();
			}

		} else if (signin.status == 'in' && signin.coordinates && signin.coordinates.length > 0) {
			const locationResp = await getLocationByCoordinates(signin);

			if (locationResp && locationResp.id) {
				const resp = await saveTimeControl(signin);
				this.setTimeControl(resp);
				this.buildSignin(resp);

				this.disabledButton(false);
				this.showToast({ code: 3, message: 'Marcaje realizado con exito', timeout: false });
				if(this.applicationEl) this.applicationEl.stopLoading();
			} else
				this.openReasonDialog(signin, timeOutPosition);

		} else {
			this.openReasonDialog(signin, timeOutPosition);
		}


	}

	openReasonDialog(signin, timeOutPosition) {
		let d = document.getElementById(this.DIALOG);
		d.clear();

		if (this.isMobile()) {
			d.type = "fullscreen";
			d.addAction(
				{
					id: '',
					title: 'Control Horario',
					icon: MATERIAL_ICONS.ALARM,
					position: "right",
				},
				(e) => e.stopPropagation()
			);
		} else d.width = '600px';
		d.addEventListener(EVENT.CLOSE, (e) => e.stopPropagation());
		d.onclick = (e) => {
			e.stopPropagation();
			this.disabledButton(false);
			if(this.applicationEl) this.applicationEl.stopLoading();
		}



		let reasonTitle = !signin || !signin.status ? 'N/D' : signin.status === 'in' ? 'Entrada' : 'Pausa';
		d.setTitle(`Tipo ${reasonTitle}`);

		let content = document.createElement(TAG.DIV);
		content.style.display = "flex";
		content.style.flexDirection = "column";
		content.style.overflowY = 'scroll';

		let options = !signin || !signin.status
			? []
			: signin.status === 'in' ? IN_REASON
				: signin.status === 'pause' ? PAUSE_REASON
					: [];

		let nameInput;

		options.forEach(opt => {
			content.appendChild(this.buildReasonOption(opt, async (value) => {
				//console.log('value', value);

				signin.cause = value;

				const resp = await saveTimeControl(signin);

				if (timeOutPosition && this.isMobile() && resp && resp.id) {
					getPosition()
						.then(position => {
							if (position) {
								resp.coordinates = position.latitude + ',' + position.longitude;
								saveTimeControl({ ...resp, ...signin })
									.then(console.log)
									.catch(console.error);
							}
						})
						.catch(console.error);
				}

				this.buildSignin(resp);

				this.disabledButton(false);
				this.showToast({ code: 3, message: 'Marcaje realizado con exito', timeout: false });
				if(this.applicationEl) this.applicationEl.stopLoading();

				d.close();
			}));

			if (!opt.clickable) {
				nameInput = createInput(
					"ReasonInput" + opt.value,
					opt.value === '2' ? 'Indicar lugar' : 'Indicar motivo'
				);

				nameInput.type = 'text';
				nameInput.maxLength = 50;
				nameInput.style.display = 'none';
				nameInput.dataset.reasonValue = opt.value;

				content.appendChild(nameInput);

				nameInput.addEventListener('click', e => e.stopPropagation());
			}
		});

		d.setContent(content);

		nameInput && nameInput.addIcon(MATERIAL_ICONS.DONE, undefined, async () => {
			if (nameInput.value.trim().length <= 3) return;

			signin.cause = nameInput.dataset.reasonValue;
			signin.comments = nameInput.value.trim();

			const resp = await saveTimeControl(signin);
			this.buildSignin(resp);

			this.disabledButton(false);
			this.showToast({ code: 3, message: 'Marcaje realizado con exito', timeout: false });
			if(this.applicationEl) this.applicationEl.stopLoading();

			d.close();
		});

		nameInput && nameInput.addEventListener("keydown", async (event) => {
			if (event.key === "Enter") {
				if (nameInput.value.trim().length <= 3) return;

				signin.cause = nameInput.dataset.reasonValue;
				signin.comments = nameInput.value.trim();

				const resp = await saveTimeControl(signin);
				this.buildSignin(resp);

				this.disabledButton(false);
				this.showToast({ code: 3, message: 'Marcaje realizado con exito', timeout: false });
				if(this.applicationEl) this.applicationEl.stopLoading();

				d.close();
			}
		});

		d.open();

	}

	showReasonInput(value, name) {
		let input = this.getElement('ReasonInput' + value);

		if (input && input.style.display == 'none') {
			input.style.display = 'block';
			input.focus();
		} else if (input)
			input.style.display = 'none';

		let inputIcon = document.querySelector(`#TimeControlReasonOption${value} > i`);
		if (inputIcon && (inputIcon.innerHTML == 'unknown_document' || inputIcon.innerHTML == 'distance')) inputIcon.innerHTML = 'close';
		else if (inputIcon && inputIcon.innerHTML == 'close') inputIcon.innerHTML = value == '2' ? 'distance' : 'unknown_document';

		let inputSpan = document.querySelector(`#TimeControlReasonOption${value} > span`);
		if (inputSpan && inputSpan.innerHTML == name) inputSpan.innerHTML = name + ' (Cerrar)';
		else if (inputSpan && inputSpan.innerHTML == name + ' (Cerrar)') inputSpan.innerHTML = name;
	}

	toggleReasonOptions(currentValue) {
		const options = document.querySelectorAll('.aonTimeControlResonOption');

		options.forEach(opt => {
			if (opt.dataset.value !== currentValue) {
				opt.classList.toggle('disabled');
			}
		});
	}

	buildReasonOption(opt, callback) {
		let div = this.createElement(TAG.DIV);
		div.id = 'TimeControlReasonOption' + opt.value;
		div.classList.add('aonTimeControlResonOption');
		div.dataset.value = opt.value;
		div.dataset.clickable = opt.clickable;
		this.appendChild(div);

		let i = this.createElement(TAG.I);
		i.className = 'material-icons';
		i.innerHTML = opt.icon;
		div.appendChild(i);

		let val = this.createElement(TAG.SPAN);
		val.innerHTML = opt.name;
		div.appendChild(val);

		div.addEventListener(EVENT.CLICK, (e) => {
			e.stopPropagation();

			// Opción normal
			if (opt.clickable) {
				callback && callback(opt.value);
			}
			// Opción con input
			else {
				this.toggleReasonOptions(opt.value);
				this.showReasonInput(opt.value, opt.name);
			}
		});

		return div;
	}

	disabledButton(disabled) {
		const content = this.getElement(this.CONTENT);
		if (content) {
			content.querySelectorAll('.aonButton')
				.forEach(element => {
					if (disabled) {
						element.setAttribute(CONSTANT.DISABLED, true);
					} else {
						element.removeAttribute(CONSTANT.DISABLED);
					}
				});
		}
	}

	buildSignin(signin) {
		this._taskHolder = signin.task_holder.id;
		const aonUserConnected = this.getElement('aonHeaderUserConnected');
		const timeEl = this.getElement(this.TIME);
		timeEl.style.cursor = "default";
		
		let time = signin.time;
		localStorage.removeItem(this.TIME_ID);

		let color = '#DC4D30';

		if (signin.status === 'in') {
			color = '#86D364';
			//time = signin.time + (new Date().getTime() - signin.in_date);
			this.salida();
			let timeId = Math.random();
			localStorage.setItem(this.TIME_ID, timeId);
			this.timeAction(time, timeId);
		} else if (signin.status === 'pause') {
			color = '#F39F1D';
			this.vuelta();
		} else {
			this.entrada();
		}

		if (aonUserConnected) {
			aonUserConnected.style.backgroundColor = color;
		}

		this.changeTime(time);
		this.divLastTime(signin);
	}

	async timeAction(time, id) {

		this.changeTime(time);

		this.updateHour();

		await new Promise((resolve) => setTimeout(resolve, 1000));

		const aonSign = document.querySelector(`#` + this.id);

		const timeIdStorage = parseFloat(localStorage.getItem(this.TIME_ID));

		if (aonSign && (id === timeIdStorage)) {
			this.timeAction(time + 1000, id);
		}
	}

	changeTime(time) {
		let timeDiv = this.getElement(this.TIME);
		if (timeDiv && time >= 0) timeDiv.innerHTML = AonDateUtils.timeParser(time);
	}

	divLastTime(signin) {
		let content = this.getElement(this.CONTENT);
		if (content && signin && signin.last_date) {
			let textStatus = null;
			switch (signin.status) {
				case "pause":
					textStatus = MSG.PAUSE.toLowerCase();
					break;
				case "out":
					textStatus = MSG.EXIT.toLowerCase();
					break;
				default:
					textStatus = MSG.ENTRY.toLowerCase();
					break;
			}
			const id = 'lastTimeUser';
			const div = this.getElement(id) || this.createElement(TAG.DIV);
			div.id = id;
			div.style.color = "grey";
			div.style.cursor = "default";
			div.style.fontSize = ".8rem";
			div.innerHTML = `${MSG.LAST} ${textStatus} ${AonDateUtils.setDateTimestampDay(signin.last_date)}`;
			content.appendChild(div);
			this.totalHourWeek();
		}
	}

	async totalHourWeek() {
		try {
			if (this._taskHolder) {
				const period = getPeriod("this_week");
				let filter = {
					taskHolderId: this._taskHolder,
					group: "DAY",
					startDate: period.startDate,
					endDate: period.endDate
				};
				let datos = await getTaskHolderTimeControl(filter);
				if (datos) {
					//let sumHour = datos.reduce((total, { time, status, in_date }) => status && status.indexOf("in") >= 0 && in_date ? ((total + (new Date().getTime() - in_date)) + time) : total + time, 0);
					let sumHour = datos.reduce((total, { time, status, in_date }) => total + time, 0);
					
					if (sumHour > 0) {
						let content = this.getElement(this.CONTENT);
						const div = this.getElement(this.TOTAL_HOUR) || this.createElement(TAG.DIV);
						div.id = this.TOTAL_HOUR;
						div.style.color = LS.isDarkTheme() ? "#ffffff" : "gray";
						div.style.fontSize = "12px";
						div.style.cursor = "default";
						div.dataset.sumHour = sumHour;
						div.innerHTML = "Horas semana actual: ";
						const span = this.createElement(TAG.SPAN);
						span.style.fontWeight = 800;
						span.id = this.TOTAL_HOUR + "Span";
						div.appendChild(span);
						content.append(div);
						this.updateHour();
					}
				}
			}
		} catch (error) { console.log(error); }
	}

	updateHour() {
		try {
			const div = this.getElement(this.TOTAL_HOUR);
			const dataset = div.dataset;
			if (dataset) {
				const hour = Number(dataset.sumHour);
				const span = this.getElement(div.id + "Span");
				span.innerHTML = timeHour(hour);
				span.style.color = LS.isDarkTheme() ? "#ffffff" : "black";
				dataset.sumHour = hour + 1000;
			}
		} catch (error) { }
	}
}
window.customElements.define('aon-sign', AonSign);
