import { AonNumber } from "../../components/aon-number.js";
import { AonSwitch } from "../../components/aon-switch.js";
import { CONSTANT, CSS, MSG, TAG } from "../../environments/environments.js";
import { setAttributes, createDiv } from "../../services/utilsComponents.js";
import { CreateComponent } from "../../components/CreateComponent.js";
import { AonDateUtils } from "../utils/AonDateUtils.js";
import '../../css/aon-grid.css';
import '../../css/aon-css-utils.css';


export const createBajaDialogContent = () => {
	const div = document.createElement(TAG.DIV);
	div.style.margin = "0 9px";
	//---FORM------
	CreateComponent.createAonDate({
		attributes: {
			name: "fechaBaja",
			id: "fechaBaja",
			title: "Fecha de baja"
		}
	}, div);

	const select = CreateComponent.createAonSelect({
		attributes: {
			name: "codBaja",
			id: "codBaja",
			title: "Causa de baja"
		}
	}, div);
	select.style.textAlign = "left";

	CreateComponent.createAonNumber({
		attributes: {
			name: "dayVacation",
			id: "dayVacation",
			description: "Días de vacaciones (Opcional)"
		}
	}, div);

	CreateComponent.createAonDate({
		attributes: {
			name: "frv",
			id: "frv",
			title: "Fecha de vacaciones",
			hidden: true
		}
	}, div);

	let divAsociative = document.createElement(TAG.DIV);
	divAsociative.id = "divAsociative";
	div.appendChild(divAsociative);

	return div;
}

export const createAsociativeSA = (create) => {
	const divAsociative = document.getElementById("divAsociative");
	const frv = document.getElementById("frv");
	const id = "asociativeSA";
	let select = document.getElementById(id);
	if (!create) {
		frv.setVisible(true);
		divAsociative.innerHTML = "";
	} else if (!select) {
		frv.setVisible(false);
		select = CreateComponent.createAonSelect({
			attributes: {
				id,
				name: id,
				title: "Situación"
			}
		}, divAsociative);
		let options = [
			{ value: "001", name: "Retribuidas y no disfrutadas" },
			{ value: "015", name: "No disfrutadas, retribuidas y cotizadas" }
		];
		select.style.textAlign = "left";
		select.setOptions(options);
		select.setIndexOf(0);
	}
}

export const createFormComunica = (id, parent) => {
	const form = CreateComponent.createForm(id + "Form");
	parent.appendChild(form);

	const className = parent.isMobile() ? CSS.AON_MOBILE_SUB_CONTENT : CSS.AON_SUB_CONTENT;
	const div = createDiv({ id: id + "Div", classes: [className] });
	form.appendChild(div.element);

	let divC;
	divC = createDiv({ classes: [CSS.AON_COL_SM_12] })
	divC.appendTo(div.element);
	CreateComponent.createAonCard({ id: id + "EmpresaCard", title: "Datos de la empresa" }, divC.element);

	divC = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] });
	divC.appendTo(div.element);
	CreateComponent.createAonCard({ id: id + "TrabajadorCard", title: "Datos del trabajador" }, divC.element);

	divC = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] });
	divC.appendTo(div.element);
	CreateComponent.createAonCard({ id: id + "ContratoCard", title: "Datos del contrato" }, divC.element);

	return form;
}

export const createEnterpriseData = (parent) => {
	let divC;
	divC = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] })
	divC.appendTo(parent);
	CreateComponent.createAonSelect({
		attributes: {
			name: "workplace",
			id: "workplace",
			title: "Centro de trabajo"
		}
	}, divC.element);


	divC = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] })
	divC.appendTo(parent);
	CreateComponent.createAonSelect({
		attributes: {
			name: "ctaCti",
			id: "ctaCti",
			title: "Cuenta de cotización"
		}
	}, divC.element);

	// divC = createDiv({classes:[CSS.AON_COL_SM_12, CSS.AON_COL_MD_4]})
	// divC.appendTo(parent);
	// let aonConvenio = setAttributes(new AonSuggestion(),{
	//     id:"convenio",
	//     title:"Convenio (opcional)",
	//     name:"convenio"
	// });
	// aonConvenio.addEventListener(EVENT.KEYUP, ({target}) =>  target.value = target.value.replace(/\D/g,''));
	// divC.appendChild(aonConvenio);

	CreateComponent.createAonInput({
		attributes: {
			name: "regime",
			id: "regime",
			description: MSG.REGIME,
			visible: CONSTANT.FALSE,
		}
	}, parent);
}

export const createContractData = (parent, isManager) => {
	let divC;

	divC = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] })
	divC.appendTo(parent);
	const dateContract = CreateComponent.createAonDate({
		attributes: {
			name: "fecha",
			id: "fecha",
			title: "Fecha"
		}
	}, divC.element)

	divC = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] })
	divC.appendTo(parent);
	CreateComponent.createAonSelect({
		attributes: {
			name: "contract",
			id: "contract",
			title: "Tipo de contrato",
			autocomplete: CONSTANT.OFF
		}
	}, divC.element);

	divC = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] })
	divC.appendTo(parent);
	CreateComponent.createAonSelect({
		attributes: {
			name: "gc",
			id: "gc",
			title: MSG.QUOTE_GROUP
		}
	}, divC.element);

	divC = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] })
	divC.appendTo(parent);
	CreateComponent.createAonSelect({
		attributes: {
			name: "ocup",
			id: "ocup",
			title: "Ocupación"
		}
	}, divC.element);

	divC = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_12] })
	divC.appendTo(parent);
	CreateComponent.createAonSelectAutocomplete({
		attributes: {
			name: "cno",
			id: "cno",
			title: "C.N.O."
		}
	}, divC.element, "true");

	let divQuoteMonth = createDiv({
		attributes: {
			id: "divQuoteMonth"
		}
	})
	divQuoteMonth.appendTo(parent);

	let divH = createDiv({
		attributes: {
			id: "div_parcial",
			hidden: true
		}
	})
	divH.appendTo(parent);

	partTime(divH.element);

	CreateComponent.createAonInput({
		attributes: {
			name: "situation",
			id: "situation",
			description: MSG.SITUATION,
			value: "AL",
			visible: CONSTANT.FALSE
		}
	}, parent);

	if (isManager) {
		divC = createDiv({ classes: [CSS.AON_COL_XS_12] })
		divC.appendTo(parent);
		CreateComponent.createAonSelect({
			attributes: {
				name: "rlce",
				id: "rlce",
				title: "RLCE (opcional)",
				default: CONSTANT.TRUE,
				autocomplete: CONSTANT.OFF
			}
		}, divC.element);
	}

	divC = createDiv({ classes: [CSS.AON_COL_XS_12] })
	divC.appendTo(parent);
	CreateComponent.createAonSelect({
		attributes: {
			name: "artist",
			id: "artist",
			title: "Regimen de Artistas (opcional)",
			default: CONSTANT.TRUE,
			autocomplete: CONSTANT.OFF
		}
	}, divC.element);
	
	divC = createDiv({ classes: [CSS.AON_COL_XS_12] })
	divC.appendTo(parent);
	CreateComponent.createAonSelect({
		attributes: {
			name: "unemployed",
			id: "unemployed",
			title: "Condición de Desempleado (opcional)",
			default: CONSTANT.TRUE,
			autocomplete: CONSTANT.OFF
		}
	}, divC.element);



	divC = createDiv({ classes: [CSS.AON_COL_XS_12] })
	divC.appendTo(parent);
	CreateComponent.createAonSelect({
		attributes: {
			name: "collective",
			id: "collective",
			title: "Colectivo Trajabador (opcional)",
			default: CONSTANT.TRUE,
			autocomplete: CONSTANT.OFF
		}
	}, divC.element);

	dateContract.value = AonDateUtils.formatDateOrigin(new Date());
}


export const createContractDataMdCtz = (parent) => {
	let div_parcial = document.getElementById("div_parcial");

	let divC = createDiv({ classes: [CSS.AON_COL_XS_12] }).element;

	parent.insertBefore(divC, div_parcial);

	let select = CreateComponent.createAonSelect({
		attributes: {
			name: "md_ctz",
			id: "md_ctz",
			title: "Modalidad Cotización",
		}
	}, divC);

	select.setOptions([
		{
			value: "0",
			name: "-"
		},
		{
			value: "1",
			name: "1 - Cotización mensual"
		},
		{
			value: "2",
			name: "2 - Cotización por jornadas reales"
		},
	]);

	select.setIndexOf(0);
}

export const createEmployeeData = (parent, id) => {
	let divT;
	divT = createDiv({
		classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_4],
		styles: {
			paddingTop: "18px",
			paddingBottom: "10px"
		}
	})
	divT.appendTo(parent);
	let aonSwitch = setAttributes(new AonSwitch(), {
		id: "switchDni",
		title: "Por DNI"
	})
	divT.appendChild(aonSwitch);

	let divReiniciar = createDiv({
		attributes: {
			id: id + "Reiniciar",
			hidden: CONSTANT.TRUE,
		},
		styles: {
			marginTop: "-15px"
		}
	});
	divReiniciar.appendTo(divT);
	let span = document.createElement(TAG.SPAN);
	span.textContent = MSG.RESTORE;
	divReiniciar.appendChild(span);
	CreateComponent.createAonIconButton({ attributes: { id: id + "IconReset", icon: "cached" } }, divReiniciar);

	divT = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_4] })
	divT.appendTo(parent);
	let divNss = createDiv({ attributes: { id: id + "NssDiv" } });
	divNss.appendTo(divT.element);
	CreateComponent.createAonInput({
		attributes: {
			name: "nss",
			id: id + "Nss",
			description: "NSS/NAF",
			autocomplete: "on"
		}
	}, divNss.element);

	divT = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_4] })
	divT.appendTo(parent);
	let divDni = createDiv({ attributes: { id: id + "DniDiv" } });
	divDni.appendTo(divT.element);
	CreateComponent.createAonInput({
		attributes: {
			name: "ipf",
			id: id + "Dni",
			description: "DNI/NIE",
			autocomplete: "on",
			disabled: true
		}
	}, divDni.element);

	let divSurnames = createDiv({ attributes: { id: "div_apellidos", hidden: true } });
	divSurnames.appendTo(parent);
	divT = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] })
	divT.appendTo(divSurnames.element);
	CreateComponent.createAonInput({
		attributes: {
			name: "apellido1",
			id: "apellido1",
			description: "1er Apellido",
			type: "text",
		}
	}, divT.element);

	divT = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_6] })
	divT.appendTo(divSurnames.element);
	CreateComponent.createAonInput({
		attributes: {
			name: "apellido2",
			id: "apellido2",
			description: "2do Apellido",
			type: "text",
		}
	}, divT.element);

	divT = createDiv({ classes: [CSS.AON_COL_SM_12, CSS.AON_COL_MD_12, CSS.AON_COL_XS_12] })
	divT.appendTo(parent);
	CreateComponent.createAonInput({
		attributes: {
			name: "name",
			id: "name",
			description: MSG.NAME,
			type: "text",
			disabled: CONSTANT.TRUE
		}
	}, divT.element);

	addIconSurname();
}

const addIconSurname = () => {
	const surnameTwo = document.getElementById("apellido2");
	if (surnameTwo) {
		surnameTwo.addAonIcon("aon_seg_social");
		if (surnameTwo.getIcon()) {
			let iass = surnameTwo.getIcon().querySelector("aon-icon");
			if (iass)
				iass.size = "18px";
		}
	}
}

/**
 * 
 * @param {HTMLElement} divH parent 
 */
const partTime = (divH) => {
	let divC = createDiv({ classes: [CSS.AON_COL_XS_6, CSS.AON_COL_SM_3] })
	divC.appendTo(divH);
	CreateComponent.createAonSelect({
		attributes: {
			name: "tipo_jornada",
			id: "tipo_jornada",
			title: "Jornada"
		}
	}, divC.element);

	divC = createDiv({ classes: [CSS.AON_COL_XS_6, CSS.AON_COL_SM_3] })
	divC.appendTo(divH);
	let numberC = setAttributes(new AonNumber(), {
		id: "horas_convenio",
		name: "horas_convenio",
		description: "Hrs/convenio",
		format: CONSTANT.TRUE,
		decimals: "2"
	})
	divC.appendChild(numberC);


	divC = createDiv({ classes: [CSS.AON_COL_XS_6, CSS.AON_COL_SM_3] })
	divC.appendTo(divH);
	numberC = setAttributes(new AonNumber(), {
		id: "horas",
		description: MSG.HOURS,
		format: CONSTANT.TRUE,
		decimals: "2"
	})
	divC.appendChild(numberC);

	divC = createDiv({ classes: [CSS.AON_COL_XS_6, CSS.AON_COL_SM_3] })
	divC.appendTo(divH);
	numberC = setAttributes(new AonNumber(), {
		id: "coef",
		name: "coef",
		description: "Coef. Parcial"
	})
	divC.appendChild(numberC);
	addSpanDecimal(numberC);
	return divC;
}


export const createQuoteMonthly = (detail, isManager) => {

	let show = detail && detail.quoteMonth;

	let parent = document.getElementById("divQuoteMonth");
	if (parent) {
		parent.innerHTML = "";
		if (show) {
			let divC = createDiv({
				classes: [CSS.AON_COL_XS_12],
				styles: {
					paddingBottom: "12px"
				}
			});

			divC.appendTo(parent);

			const id = "quoteMonth";

			const aonSwitch = setAttributes(new AonSwitch(), { id, name: id, title: `Cotización mensual`, checked: false });

			divC.appendChild(aonSwitch);
		}
	}
}

export const addSpanDecimal = (input) => {
	let coefInput = document.getElementById(input.INPUT);
	if (coefInput) {
		let span = document.createElement(TAG.SPAN);
		span.innerHTML = '0,';
		span.style.position = "absolute";
		span.style.top = "50%";
		coefInput.parentNode.insertBefore(span, coefInput);
	}
}
