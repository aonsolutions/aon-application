import { AonDialog } from "../../components/aon-dialog.js";
import { AonSelect } from "../../components/aon-select.js";
import { AonNewSelect } from "../../components/aon-new-select.js";
import { AonNewDate } from "../../components/aon-new-date.js";
import { MSG } from "../../environments/environments.js";
import { getCategories, getScopes, getTags, uploadFileDocumental, getS3Category, postS3Document } from "../../services/documentalService.js";
import { getReader } from "../../services/utils.js";
import { AonDocumental } from "./aon-documental.js";
import {
  ASESOR_TYPE_OPTION,
  ENTERPRISE_TYPE_OPTION, EMPLOYEE_TYPE_OPTION
} from './DocumentalEnums.js';

export const uploadDocument = (file, data, success, error, isBetaDoc = false) => {
	if (file) {
		getReader(file).then(f => {
			const doc = {
				...f,
				contentName: f.name,
				contentSize: f.size,
				category: data.category,
				tag: data.tag,
				scope: data.scope,
				type: data.type,
				// AÃ±adir `date` solo si es beta y `data.date` existe
				...(isBetaDoc && data.date ? { date: data.date } : {})
			};
            const uploadDocumentsUse = isBetaDoc ? postS3Document : uploadFileDocumental;
			uploadDocumentsUse(doc)
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
      /*
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
      
     */
	});
	d.open();
}

export const uploadOption = (dur, beta) => {
	let table = beta ? S3DocumentalSelects() : oldDocumentalSelects(dur);
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

// Limpiar campos de subcategoria, administracion y modelos
function clearFields(table, fieldsToKeep = []) {
    const trElements = table.querySelectorAll('tr');
    trElements.forEach((tr) => {
        // Evitar eliminar las filas que deben permanecer (etiquetas, categoriï¿½a y el datePicker)
        if (!fieldsToKeep.includes(tr)) {
            tr.remove(); // Elimina todas las filas de la tabla excepto las que deben permanecer
        }
    });
}

function S3DocumentalSelects() {
  // Se monta la tabla
    let table = document.createElement('table');
    table.style.width = '100%';
  
  // Spinner
    let loadingOverlay = document.createElement('div');
    loadingOverlay.style.position = 'absolute';
    loadingOverlay.style.top = '0';
    loadingOverlay.style.left = '0';
    loadingOverlay.style.width = '100%';
    loadingOverlay.style.height = '100%';
    loadingOverlay.style.backgroundColor = 'rgba(255, 255, 255, 0.8)'; // Fondo semi-transparente
    loadingOverlay.style.display = 'flex';
    loadingOverlay.style.alignItems = 'center';
    loadingOverlay.style.justifyContent = 'center';
    loadingOverlay.style.zIndex = '10'; // Asegura que el overlay esté por encima de otros elementos

    // Crear el spinner de Materialize
    let spinner = document.createElement('div');
    spinner.classList.add('preloader-wrapper', 'active');
    spinner.innerHTML = `
      <span class="material-symbols-outlined">
        refresh
      </span>
    `;

    // Hacer el ícono más grande
    let icon = spinner.querySelector('.material-symbols-outlined');
    icon.style.fontSize = '48px'; // Tamaño más grande del ícono

    // Aplicar la animación de rotación
    icon.style.animation = 'rotate 2s linear infinite'; // Rotación infinita de 2 segundos

    // Crear la animación de rotación (por JavaScript)
    let style = document.createElement('style');
    style.innerHTML = `
      @keyframes rotate {
        0% {
          transform: rotate(0deg);
        }
        100% {
          transform: rotate(360deg);
        }
      }
    `;
    document.head.appendChild(style); // Insertar la animación en el head del documento

    // Agregar el spinner al overlay
    loadingOverlay.appendChild(spinner);

    // Agregar el overlay a la tabla
    table.appendChild(loadingOverlay);

  // DatePicker
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
	});

  // TAG
	let trTag = document.createElement('tr');
	table.appendChild(trTag);

	let tdTag = document.createElement('td');
	tdTag.setAttribute('colspan', '1');
	let selTag = new AonNewSelect();
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
    // Solo agregamos si tenemos TAG
    if (selTag.options && JSON.parse(selTag.options).length > 0) {
      trTag.appendChild(tdTag);
    }
  // CATEGORY
	let trCategory = document.createElement('tr');
	table.appendChild(trCategory);

	let tdCategory = document.createElement('td');
	tdCategory.setAttribute('colspan', '1');
	let selCat = new AonNewSelect();
	selCat.id = "aonDocumentalUploadCategory";
	selCat.title = MSG.CATEGORY;
	tdCategory.appendChild(selCat);

	let data = { parent: null };
	getS3Category(data).then(categories => {
		if (categories.length > 0) {
			selCat.options = JSON.stringify(categories.map(c => {
				return {
                  value: c.id,
                  name : c.name
				};
			}));
			trCategory.appendChild(tdCategory);
            // Oculta el overlay
            loadingOverlay.style.display = 'none';
            // cargado categoria, metemos funcionalidad
            uploadedCategory(selCat, table, trCategory, trTag, trDatePicker, loadingOverlay);
		} else {
          // Oculta el overlay
          loadingOverlay.style.display = 'none';
          console.log('No hay categorias disponibles.');
		}
	});
    // filtros para subir
	return table;
};

  function uploadedCategory(selCat, table, trCategory, trTag, trDatePicker, loadingOverlay){
    // Se escoge una categoria
	selCat.addEventListener('change', (event) => {
      const selectedCategoryId = event.target.value;

      // Limpiar los campos antes de generar nuevos select
      clearFields(table, [trCategory, trTag, trDatePicker]);

      if (selectedCategoryId != null) {
        // Crear un nuevo tr para Subcategoria solo si hay subcategorias
        let trSubCategory = document.createElement('tr');
        table.appendChild(trSubCategory);

        let tdSubCategory = document.createElement('td');
        tdSubCategory.setAttribute('colspan', '1');
        let selSubCat = new AonNewSelect();
        selSubCat.id = "aonDocumentalUploadSubCategory";
        selSubCat.title = MSG.SUBCATEGORY;
        tdSubCategory.appendChild(selSubCat);

        let data = { parent: selectedCategoryId };
        
        // Mostrar el overlay
        loadingOverlay.style.display = 'flex';
        getS3Category(data).then(subcategories => {
          if (subcategories.length > 0) {
            selSubCat.options = JSON.stringify(subcategories.map(sc => {
              return {
                value: sc.id,
                name: sc.name
              }
            }));
            trSubCategory.appendChild(tdSubCategory);
            // Oculta el overlay
            loadingOverlay.style.display = 'none';
///////////////////////////////////////////////////
			// Event listener para la selecciÃ³n de la subcategorÃ­a
			selSubCat.addEventListener('change', (event) => {
				const selectedSubCategoryId = event.target.value;

				// Limpiar los campos de administraciÃ³n y modelos antes de generar nuevos
				clearFields(table, [trCategory, trTag, trDatePicker, trSubCategory]);
				if (selectedSubCategoryId != null) {
					// Crear un nuevo tr para AdministraciÃ³n solo si hay administraciones
					let trAdministration = document.createElement('tr');
					table.appendChild(trAdministration);

					let tdAdministration = document.createElement('td');
					tdAdministration.setAttribute('colspan', '1');
					let selAdministration = new AonNewSelect();
					selAdministration.id = "aonDocumentalAdministration";
					selAdministration.title = "AdministraciÃ³n";
					tdAdministration.appendChild(selAdministration);

					let data = { parent: selectedSubCategoryId };
                    // Mostrar el overlay
                    loadingOverlay.style.display = 'flex';
					getS3Category(data).then(administrations => {
						if (administrations.length > 0) {
							selAdministration.options = JSON.stringify(administrations.map(adm => {
								return {
									value: adm.id,
									name: adm.name
								}
							}));
							trAdministration.appendChild(tdAdministration);
                            // Oculta el overlay
                            loadingOverlay.style.display = 'none';
                        ///////////////////////////////////////////////////
                            // Event listener para la selecciÃ³n de la administraciÃ³n
                            selAdministration.addEventListener('change', (event) => {
                                const selectedAdministrationId = event.target.value;

                                // Limpiar el campo de modelos antes de generar nuevos
                                clearFields(table, [trCategory, trTag, trDatePicker, trSubCategory, trAdministration]);

                                if (selectedAdministrationId != null) {
                                    // Crear un nuevo tr para Modelos solo si hay modelos
                                    let trModel = document.createElement('tr');
                                    table.appendChild(trModel);

                                    let tdModel = document.createElement('td');
                                    tdModel.setAttribute('colspan', '1');
                                    let selModel = new AonNewSelect();
                                    selModel.id = "aonDocumentalModels";
                                    selModel.title = "Modelos";
                                    tdModel.appendChild(selModel);

                                    let data = { parent: selectedAdministrationId };
                                    // Mostrar el overlay
                                    loadingOverlay.style.display = 'flex';
                                    getS3Category(data).then(models => {
                                        if (models.length > 0) {
                                            selModel.options = JSON.stringify(models.map(mod => {
                                                return {
                                                    value: mod.id,
                                                    name: mod.name
                                                }
                                            }));
                                            trModel.appendChild(tdModel);
                                            // Oculta el overlay
                                            loadingOverlay.style.display = 'none';
                                        } else {
                                          // Oculta el overlay
                                          loadingOverlay.style.display = 'none';
                                          console.log('No hay modelos disponibles.');
                                        }
                                    });
                                }
                            });
                        ///////////////////////////////////////////////////
						} else {
                          // Oculta el overlay
                          loadingOverlay.style.display = 'none';
                          console.log('No hay administraciones disponibles.');
						}
					});
				}
			});
///////////////////////////////////////////////////
          } else {
            // Oculta el overlay
            loadingOverlay.style.display = 'none';
            console.log('No hay subcategorÃ­as disponibles.');
          }
        });
      }
	});
  }

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