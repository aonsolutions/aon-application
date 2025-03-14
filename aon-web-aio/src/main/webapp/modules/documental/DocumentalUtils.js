import { AonDialog } from "../../components/aon-dialog.js";
import { AonSelect } from "../../components/aon-select.js";
import { MSG } from "../../environments/environments.js";
import { getCategories, getScopes, getTags, uploadFileDocumental, getS3Category, postS3Document } from "../../services/documentalService.js";
import { getReader } from "../../services/utils.js";
import { AonDocumental } from "./aon-documental.js";
import { AonNewDate } from "../../components/aon-new-date.js";
// import { AonDocumental } from "./aon-documental.js";
import {
	ASESOR_TYPE_OPTION,
	ENTERPRISE_TYPE_OPTION, EMPLOYEE_TYPE_OPTION
} from './DocumentalEnums.js';

export const uploadDocument = (file, data, success, error) => {
	if (file) {
		getReader(file).then(f => {
			const doc = {
				...f,
				contentName: f.name,
				contentSize: f.size,
				category: data.category,
				tag: data.tag,
				scope: data.scope,
				type: data.type
			};
//			uploadFileDocumental(doc)
			postS3Document(doc)
				.then(r => success(file))
				.catch((e) => error(file, e));
		}).catch((e) => error(file, e));
	}
}


export const uploadDocuments = (el, files, dur) => {
	let d = new AonDialog();
	let rootPanel = document.getElementById("rootPanel");
	rootPanel.appendChild(d);
	d.clear();
	// if(isMobile()) d.width = '400px';
	d.setTitle(MSG.UPLOAD_FILE);
	d.setContent(uploadOption(dur));
	d.addAcceptAction(async () => {
		let arr = [];
		let data = {
			category: document.getElementById("aonDocumentalUploadCategory").value,
			scope: document.getElementById("aonDocumentalUploadScope").value,
			tag: document.getElementById("aonDocumentalUploadTag").value,
			type: document.getElementById("aonDocumentalUploadType").value
		}
		for await (let file of files) {
			let reader = await getReader(file).catch(() => null);
			if (reader) {
				let doc = await attach(reader, data);
				if (doc)
					arr.push(doc);
			}
		}
		el.value = null;
		if (arr.length > 0) {
			let aonComponent = new AonDocumental();
			aonComponent.value = arr[0].id;
			rootPanel.innerHTML = "";
			rootPanel.appendChild(aonComponent);
		}
	});
	d.open();
}

export const uploadOption = (dur, beta) => {
	let table = null;
	if (beta) {
		table = S3DocumentalSelects();
	} else {
		table = oldDocumentalSelects(dur);
	}
	return table;
}

function oldDocumentalSelects(dur) {
	let table = document.createElement('table');
	table.style.width = '100%';

	let tr2 = document.createElement('tr');
	table.appendChild(tr2);

	// CATEGORY
	let tdCategory = document.createElement('td');
	tdCategory.setAttribute('colspan', '1');

	let selCat = new AonSelect();
	selCat.id = "aonDocumentalUploadCategory";
	selCat.title = MSG.CATEGORY;
	tdCategory.appendChild(selCat);
	getCategories({ domain: localStorage.getItem('aon_domain_id') }).then(categories => {
		selCat.options = JSON.stringify(categories.map(c => {
			return {
				value: c.id,
				name: c.name
			}
		}));
	});

	tr2.appendChild(tdCategory);

	let tr3 = document.createElement('tr');
	table.appendChild(tr3);
	// SCOPE
	let tdScope = document.createElement('td');
	tdScope.setAttribute('colspan', '1');

	let selScp = new AonSelect();
	selScp.id = "aonDocumentalUploadScope";
	selScp.title = MSG.SCOPE;
	tdScope.appendChild(selScp);
	getScopes().then(scopes => {
		selScp.options = JSON.stringify(scopes.map(s => {
			return {
				value: s.id,
				name: s.name
			}
		}));
	});

	tr3.appendChild(tdScope);

	let tr4 = document.createElement('tr');
	table.appendChild(tr4);

	// TAG

	let tdTag = document.createElement('td');
	tdTag.setAttribute('colspan', '1');
	let selTag = new AonSelect();
	selTag.id = "aonDocumentalUploadTag";
	selTag.title = MSG.TAG;
	tdTag.appendChild(selTag);
	getTags({ domain: localStorage.getItem('aon_domain_id') }).then(tags => {
		selTag.options = JSON.stringify(tags.map(t => {
			return {
				value: t.id,
				name: t.name
			}
		}));
	});

	tr4.appendChild(tdTag);

	let tr5 = document.createElement('tr');
	table.appendChild(tr5);

	// TYPE

	let tdType = document.createElement('td');
	tdType.setAttribute('colspan', '1');
	let selType = new AonSelect();
	selType.id = "aonDocumentalUploadType";
	selType.title = MSG.TYPE;

	let typeOptions = EMPLOYEE_TYPE_OPTION;
	if (dur.isDocumentalManager()) {
		typeOptions = ASESOR_TYPE_OPTION;
	} else if (dur.isDocumentalPortal()) {
		typeOptions = ENTERPRISE_TYPE_OPTION;
	}
	selType.setOptions(typeOptions);

	tdType.appendChild(selType);
	tr5.appendChild(tdType);
	return table;
}

export const S3DocumentalSelects = () => {

	let table = document.createElement('table');
	table.style.width = '100%';

	// TAG - Este campo no se debe eliminar
	let trTag = document.createElement('tr');
	table.appendChild(trTag);

	let tdTag = document.createElement('td');
	tdTag.setAttribute('colspan', '1');
	let selTag = new AonSelect();
	selTag.id = "aonDocumentalUploadTag";
	selTag.title = MSG.TAG;
	tdTag.appendChild(selTag);
	getTags({ domain: localStorage.getItem('aon_domain_id') }).then(tags => {
		selTag.options = JSON.stringify(tags.map(t => {
			return {
				value: t.id,
				name: t.name
			}
		}));
	});

	trTag.appendChild(tdTag);

	// CATEGORY
	let trCategory = document.createElement('tr');
	table.appendChild(trCategory);

	let tdCategory = document.createElement('td');
	tdCategory.setAttribute('colspan', '1');

	let selCat = new AonSelect();
	selCat.id = "aonDocumentalUploadCategory";
	selCat.title = MSG.CATEGORY;
	tdCategory.appendChild(selCat);

	let data = { parent: null };
	getS3Category(data).then(categories => {
		if (categories.length > 0) {
			selCat.options = JSON.stringify(categories.map(c => {
				return {
					value: c.id,
					name: c.name
				}
			}));
			trCategory.appendChild(tdCategory);
		} else {
			console.log('No hay categorías disponibles.');
		}
	});
	
	// Limpiar campos de subcategoría, administración y modelos
	function clearFields(fieldsToKeep = []) {
		const trElements = table.querySelectorAll('tr');
		trElements.forEach((tr) => {
			// Evitar eliminar las filas que deben permanecer (etiquetas, categoría y el datePicker)
			if (!fieldsToKeep.includes(tr) && !tr.contains(tdDatePicker)) {
				tr.remove(); // Elimina todas las filas de la tabla excepto las que deben permanecer
			}
		});
	}

	// Añadir un DatePicker aquí como una columna más (al final)
	let trDatePicker = document.createElement('tr');
	table.appendChild(trDatePicker);
	
	// Celda para el datePicker
	let tdDatePicker = document.createElement('td');
	tdDatePicker.setAttribute('colspan', '1');
	let datePicker = new AonNewDate();
	datePicker.id = "aonDocumentalUploadDatePicker";
	datePicker.title = "Fecha";
	tdDatePicker.appendChild(datePicker);
	trDatePicker.appendChild(tdDatePicker);

	// Event listener para el datePicker (si se necesita)
	datePicker.addEventListener('change', (event) => {
		return datePicker.getDateValue();
//		console.log('Fecha seleccionada:', datePicker.getDateValue());
	});

	// Event listener para la selección de la categoría
	selCat.addEventListener('change', (event) => {
		const selectedCategoryId = event.target.value;
		console.log('Categoría seleccionada:', selectedCategoryId);

		// Limpiar los campos antes de generar nuevos select
		clearFields([trCategory, trTag, trDatePicker]);

		if (selectedCategoryId != null) {
			// Crear un nuevo tr para Subcategoría solo si hay subcategorías
			let trSubCategory = document.createElement('tr');
			table.appendChild(trSubCategory);

			let tdSubCategory = document.createElement('td');
			tdSubCategory.setAttribute('colspan', '1');
			let selSubCat = new AonSelect();
			selSubCat.id = "aonDocumentalUploadSubCategory";
			selSubCat.title = MSG.SUBCATEGORY;
			tdSubCategory.appendChild(selSubCat);

			let data = { parent: selectedCategoryId };
			getS3Category(data).then(subcategories => {
				if (subcategories.length > 0) {
					selSubCat.options = JSON.stringify(subcategories.map(sc => {
						return {
							value: sc.id,
							name: sc.name
						}
					}));
					trSubCategory.appendChild(tdSubCategory);
				} else {
					console.log('No hay subcategorías disponibles.');
				}
			});

			// Event listener para la selección de la subcategoría
			selSubCat.addEventListener('change', (event) => {
				const selectedSubCategoryId = event.target.value;
				console.log('Subcategoría seleccionada:', selectedSubCategoryId);

				// Limpiar los campos de administración y modelos antes de generar nuevos
				clearFields([trCategory, trTag, trSubCategory]);
				if (selectedSubCategoryId != null) {
					// Crear un nuevo tr para Administración solo si hay administraciones
					let trAdministration = document.createElement('tr');
					table.appendChild(trAdministration);

					let tdAdministration = document.createElement('td');
					tdAdministration.setAttribute('colspan', '1');
					let selAdministration = new AonSelect();
					selAdministration.id = "aonDocumentalAdministration";
					selAdministration.title = "Administración";
					tdAdministration.appendChild(selAdministration);

					let data = { parent: selectedSubCategoryId };
					getS3Category(data).then(administrations => {
						if (administrations.length > 0) {
							selAdministration.options = JSON.stringify(administrations.map(adm => {
								return {
									value: adm.id,
									name: adm.name
								}
							}));
							trAdministration.appendChild(tdAdministration);
						} else {
							console.log('No hay administraciones disponibles.');
						}
					});

					// Event listener para la selección de la administración
					selAdministration.addEventListener('change', (event) => {
						const selectedAdministrationId = event.target.value;
						console.log('Administración seleccionada:', selectedAdministrationId);

						// Limpiar el campo de modelos antes de generar nuevos
						clearFields([trCategory, trTag, trSubCategory, trAdministration]);

						if (selectedAdministrationId != null) {
							// Crear un nuevo tr para Modelos solo si hay modelos
							let trModel = document.createElement('tr');
							table.appendChild(trModel);

							let tdModel = document.createElement('td');
							tdModel.setAttribute('colspan', '1');
							let selModel = new AonSelect();
							selModel.id = "aonDocumentalModels";
							selModel.title = "Modelos";
							tdModel.appendChild(selModel);

							let data = { parent: selectedAdministrationId };
							getS3Category(data).then(models => {
								if (models.length > 0) {
									selModel.options = JSON.stringify(models.map(mod => {
										return {
											value: mod.id,
											name: mod.name
										}
									}));
									trModel.appendChild(tdModel);
								} else {
									console.log('No hay modelos disponibles.');
								}
							});
						}
					});
				}
			});
		}
	});
	
	return table;
};





const attach = async (reader, d) => {
	const data = {
		...reader,
		contentName: reader.name,
		contentSize: reader.size,
		category: d.category,
		tag: d.tag,
		scope: d.scope,
		type: d.type
	};
	return await uploadFileDocumental(data).catch(e => null);
}