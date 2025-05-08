import { AonDialog } from "../../components/aon-dialog.js";
import { AonSelect } from "../../components/aon-select.js";
import { AonNewSelect } from "../../components/aon-new-select.js";
import { AonNewDate } from "../../components/aon-new-date.js";
import { MSG } from "../../environments/environments.js";
import { getCategories, getScopes, getTags, uploadFileDocumental, getS3Category, postS3Document } from "../../services/documentalService.js";
import { getReader } from "../../services/utils.js";
import { ASESOR_TYPE_OPTION, ENTERPRISE_TYPE_OPTION, EMPLOYEE_TYPE_OPTION } from './DocumentalEnums.js';
import { AonUploadToast } from "../../components/aon-upload-toast.js";

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
                // Añadir `registryType` solo si es beta y `data.registryType` existe
                ...(isBetaDoc ? { registryType: data.registryType } : {}),
				// agregar `date` solo si es beta y `data.date` existe
				...(isBetaDoc && data.date ? { date: data.date } : {})
			};
            const uploadDocumentsUse = isBetaDoc ? postS3Document : uploadFileDocumental;
			uploadDocumentsUse(doc)
              .then(r => success(file))
              .catch((e) => error(file, e));
    	}).catch((e) => error(file, e));
	}
};

export const uploadDocuments = (el, files, dur) => {
	let d = new AonDialog();
	let rootPanel = document.getElementById("rootPanel");
	rootPanel.appendChild(d);
	d.clear();
	// if(isMobile()) d.width = '400px';
	d.setTitle(MSG.UPLOAD_FILE);
	d.setContent(uploadOption(dur));
	d.addAcceptAction(async () => {
		let data = {
			category: document.getElementById("aonDocumentalUploadCategory").value,
			scope: document.getElementById("aonDocumentalUploadScope").value,
			tag: document.getElementById("aonDocumentalUploadTag").value,
			type: document.getElementById("aonDocumentalUploadType").value
		};

		let uploadToast = document.getElementById('aonUploadToast');
		if(!uploadToast) {
			uploadToast = new AonUploadToast();
			rootPanel.appendChild(uploadToast);
		}
		for (let file of files) {
			uploadToast.addFile("documental", file, data);
		}
	});
	d.open();
};

export const uploadOption = (dur, beta) => {
	let table = beta ? S3DocumentalSelects(dur) : oldDocumentalSelects(dur);
	return table;
};

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
			};
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
			};
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
			};
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
function clearFields(table, fieldsToKeep = [], checkboxIds = []) {
	var button = document.getElementById("aonDocumentalDialogDialogActionAccept");
	button.disabled = true;
	const trElements = table.querySelectorAll('tr');
	trElements.forEach((tr) => {
	  // Verificar si el tr tiene algún checkbox dentro y si su id está en checkboxIds
	  const checkboxInTr = tr.querySelector('input[type="checkbox"]');
        const radioInTr = tr.querySelector('input[type="radio"]');
        const shouldKeep = fieldsToKeep.includes(tr) || 
                           (checkboxInTr && checkboxIds.includes(checkboxInTr.id)) || 
                           radioInTr;  
  
	  // Si el tr no debe ser mantenido, eliminarlo
	  if (!shouldKeep) {
		tr.remove();
	  }
	});
  }
  
// Función para crear filas de checkboxes
function createCheckboxRow(table, checkboxId, labelText, onChangeCallback) {
    let trCheckbox = document.createElement('tr');
    let tdCheckbox = document.createElement('td');
    tdCheckbox.setAttribute('colspan', '1');
  
    tdCheckbox.style.marginTop = '10px';
    tdCheckbox.style.marginBottom = '10px';
  
    // Crear checkbox
    let checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.id = checkboxId;
  
    // Crear el texto visible
    let label = document.createElement('label');
    label.setAttribute('for', checkboxId);
    label.textContent = labelText;
  
    // Añadir checkbox y label a la celda
    tdCheckbox.appendChild(checkbox);
    tdCheckbox.appendChild(label);
  
    // Añadir la fila con el checkbox a la tabla
    trCheckbox.appendChild(tdCheckbox);
    table.appendChild(trCheckbox);

    // Llamar al callback si se proporcionó
    if (onChangeCallback) {
        checkbox.addEventListener('change', onChangeCallback);
    }
}

function createRadioButtonRow(table, radioId, labelText, onChangeCallback) {
    let trRadio = document.createElement('tr');
    let tdRadio = document.createElement('td');
    tdRadio.setAttribute('colspan', '1');
  
    tdRadio.style.marginTop = '10px';
    tdRadio.style.marginBottom = '10px';
  
    // Crear radiobutton
    let radio = document.createElement('input');
    radio.type = 'radio';
    radio.name = 'categoryRadioGroup'; 
    radio.id = radioId;
  
    // Crear el texto visible
    let label = document.createElement('label');
    label.setAttribute('for', radioId);
    label.textContent = labelText;
  
    // Añadir el radio y el label a la celda
    tdRadio.appendChild(radio);
    tdRadio.appendChild(label);
  
    // Añadir la fila con el radio a la tabla
    trRadio.appendChild(tdRadio);
    table.appendChild(trRadio);

    if (radioId === 's3CategoriesRadio') {
        radio.checked = true; // Marcar las categorias del despacho por defecto si existen
        // Disparar el evento 'change' después de un pequeño retraso
        setTimeout(() => {
            radio.dispatchEvent(new Event('change')); // Disparar el evento para cargar las opciones
        }, 0);
    }
    // Llamar al callback si se proporcionó
    if (onChangeCallback) {
        radio.addEventListener('change', onChangeCallback);
    }
}
function handleCheckboxChange(event) {
	const visibleEmpleadoCheckbox = document.getElementById('visibleEmpleadoCheckbox');
	const visibleEmpresaCheckbox = document.getElementById('visibleEmpresaCheckbox');


	// Si el checkbox marcado es el 'visibleEmpleadoCheckbox', desmarcar 'visibleEmpresaCheckbox'
	if (event.target.id === 'visibleEmpleadoCheckbox' && visibleEmpleadoCheckbox.checked) {
		visibleEmpresaCheckbox.checked = false;
	}

	// Si el checkbox marcado es el 'visibleEmpresaCheckbox', desmarcar 'visibleEmpleadoCheckbox'
	if (event.target.id === 'visibleEmpresaCheckbox' && visibleEmpresaCheckbox.checked) {
		visibleEmpleadoCheckbox.checked = false;
	}
}

export function handleRadioButtonChange(event) {
    // Primero eliminamos las filas generadas anteriormente
    let table = document.getElementById("table");
    let trScope = document.getElementById("trScope");
    let trCategory = document.getElementById("trCategory");
    // let trTag = document.getElementById("trTag");
    let trDatePicker = document.getElementById("trDatePicker");

    // Llamamos a clearFields para eliminar las filas generadas (subcategoría, administración, etc.)
    clearFields(table, [trScope, trCategory, trDatePicker], 
        ['visibleEmpleadoCheckbox', 'visibleEmpresaCheckbox','oldCategoriesRadio','s3CategoriesRadio']);

    // Si se selecciona 'oldCategoriesRadio', desmarcamos 's3CategoriesRadio' y cargamos categorías antiguas
    if (event.target.id === 'oldCategoriesRadio') {
        getCategories({ domain: localStorage.getItem('aon_domain_id') }).then(oldCategories => {
            clearPreviousSelectOptions(oldCategories, 'old');
        }).catch(err => {
            console.error("Error al obtener categorías:", err);
        });
    }
    // Si se selecciona 's3CategoriesRadio', desmarcamos 'oldCategoriesRadio' y cargamos categorías S3
    if (event.target.id === 's3CategoriesRadio') {
        let data = { parent: null };
        getS3Category(data).then(categories => {
            clearPreviousSelectOptions(categories, 's3');
        }).catch(err => {
            console.error("Error al obtener categorías:", err);
        });
    }
}


function clearPreviousSelectOptions(categoriesToLoad, categoryType) {
    let select = document.getElementById("aonDocumentalUploadCategory");
    let table = document.getElementById("table");
    let trScope = document.getElementById("trScope");
    let trCategory = document.getElementById("trCategory");
    // let trTag = document.getElementById("trTag");
    let trDatePicker = document.getElementById("trDatePicker");
    let loadingOverlay = document.getElementById("aonDocumentalLoadingOverlay");

    let defaultOption = {
        value: "Seleccione una categoria",
        name: "Seleccione una categoria",
        id: "Seleccione una categoria",    
        is_deletable: 0
    };
    categoriesToLoad.unshift(defaultOption);
    // Crear las nuevas opciones basadas en el tipo de categoría seleccionada
    if (categoryType === 'old') {
            select.options = JSON.stringify(categoriesToLoad.map(c => {
                return {
                    value: c.id,
                    name: c.name
                };
            }));
            select.value = categoriesToLoad[0].id;
            select.options = JSON.stringify(categoriesToLoad.filter(c => c.id !== 'Seleccione una categoria').map(c => {
                return {
                    value: c.id,
                    name: c.name
                };
            }));
    } 
    if (categoryType === 's3') {
        select.options = JSON.stringify(categoriesToLoad.filter(c => c.is_deletable === 0).map(c => {
            return {
                value: c.id,
                name: c.name
            };
        }));
        select.value = categoriesToLoad.filter(c => c.is_deletable === 0)[0].id; 
        select.options = JSON.stringify(categoriesToLoad.filter(c => c.id !== 'Seleccione una categoria' && c.is_deletable === 0).map(c => {
            return {
                value: c.id,
                name: c.name
            };
        }));
    }

    if (categoryType === 's3' && select.options) {
      uploadedCategory(select, table, trScope, trCategory, trDatePicker, loadingOverlay);
    }
}

export async function loadOldCategories() {
	try {
	  return await getCategories({ domain: localStorage.getItem('aon_domain_id') });
	} catch (error) {
	  console.log('Error al obtener categorías:', error);
	}
  }

function S3DocumentalSelects(dur) {
    // Se monta la tabla
    let table = document.createElement('table');
    table.style.width = '100%';
    table.id = 'table';
  
    // Spinner
    let loadingOverlay = document.createElement('div');
    loadingOverlay.id = 'aonDocumentalLoadingOverlay';
    loadingOverlay.style.position = 'absolute';
    loadingOverlay.style.top = '0';
    loadingOverlay.style.left = '0';
    loadingOverlay.style.width = '100%';
    loadingOverlay.style.height = '100%';
    loadingOverlay.style.backgroundColor = 'rgba(218, 209, 209, 0.8)';
    loadingOverlay.style.display = 'flex';
    loadingOverlay.style.alignItems = 'center';
    loadingOverlay.style.justifyContent = 'center';
    loadingOverlay.style.zIndex = '10';
  
    let spinner = document.createElement('div');
    spinner.classList.add('preloader-wrapper', 'active');
    spinner.innerHTML = `
        <span class="material-symbols-outlined">
            refresh
        </span>
    `;
  
    let icon = spinner.querySelector('.material-symbols-outlined');
    icon.style.fontSize = '48px';
    icon.style.animation = 'rotate 2s linear infinite';
  
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
    document.head.appendChild(style);

    loadingOverlay.appendChild(spinner);
    table.appendChild(loadingOverlay);

    // Lógica de los checkboxes
    if (dur.isDocumentalPortal() && !dur.isDocumentalManager()) {
        createCheckboxRow(table, 'visibleEmpleadoCheckbox', 'Visible Empleado');
    }

    if (dur.isDocumentalPortal() && dur.isDocumentalManager()) {
        createCheckboxRow(table, 'visibleEmpresaCheckbox', 'No visible Empresa', handleCheckboxChange);
        createCheckboxRow(table, 'visibleEmpleadoCheckbox', 'Visible Empleado', handleCheckboxChange);
    }

    // DatePicker
    let trDatePicker = document.createElement('tr');
    trDatePicker.id = 'trDatePicker';
    table.appendChild(trDatePicker);
    
    let tdDatePicker = document.createElement('td');
    tdDatePicker.setAttribute('colspan', '1');
    let datePicker = new AonNewDate();
    datePicker.id = "aonDocumentalUploadDatePicker";
    datePicker.title = "Fecha del documento";
    tdDatePicker.appendChild(datePicker);
    trDatePicker.appendChild(tdDatePicker);
  
    datePicker.addEventListener('change', (event) => {
        return datePicker.getDateValue();
    });

    // SCOPE
    let trScope = document.createElement('tr');
    trScope.id = 'trScope';
    table.appendChild(trScope);

    let tdScope = document.createElement('td');
    tdScope.setAttribute('colspan', '1');
    let selScope = new AonNewSelect();
    selScope.id = "aonDocumentalUploadScope";
    selScope.title = MSG.SCOPE + ' (Solo visible...)';
    tdScope.appendChild(selScope);

    getScopes({ domain: localStorage.getItem('aon_domain_id') }).then(scopes => {
        if (scopes && scopes.length > 0) {
            selScope.options = JSON.stringify(scopes.map(s => {
                return {
                    value: s.id,
                    name: s.name
                };
            }));

            trScope.appendChild(tdScope);
        }
    });

    //     // TAG
//     // let trTag = document.createElement('tr');
//     // trTag.id = 'trTag';
//     // table.appendChild(trTag);
  
//     // let tdTag = document.createElement('td');
//     // tdTag.setAttribute('colspan', '1');
//     // let selTag = new AonNewSelect();
//     // selTag.id = "aonDocumentalUploadTag";
//     // selTag.title = MSG.TAG;
//     // tdTag.appendChild(selTag);
  
//     // getTags({ domain: localStorage.getItem('aon_domain_id') }).then(tags => {
// 	// 	if (tags && tags.length > 0) {		
//     //     selTag.options = JSON.stringify(tags.map(t => {
//     //         return {
//     //             value: t.id,
//     //             name: t.name
//     //         };
//     //     }));
// 	// 	trTag.appendChild(tdTag);
// 	// }
//     // });

    loadingOverlay.style.display = 'none';
    // Crear la sección de categorías
    createCategorySection(table, loadingOverlay, trScope, trDatePicker);
    // filtros para subir
    return table;
}


function createCategorySection(table, loadingOverlay, trScope, trDatePicker) {
    let oldCategories = loadOldCategories();
    
    // Crear la fila de categorías (Radio buttons)
    if (oldCategories) {
        createRadioButtonRow(table, 's3CategoriesRadio', MSG.OFFICE_CATEGORIES, handleRadioButtonChange);
        createRadioButtonRow(table, 'oldCategoriesRadio', MSG.USER_CATEGORIES, handleRadioButtonChange);
    }

    let trCategory = document.createElement('tr');
    trCategory.id = 'trCategory';
    table.appendChild(trCategory);

    let tdCategory = document.createElement('td');
    tdCategory.setAttribute('colspan', '1');
    let selCat = new AonNewSelect();
    selCat.id = "aonDocumentalUploadCategory";
    selCat.title = MSG.CATEGORY;

    if (!oldCategories) {
        let defaultOption = {
            value: "Seleccione una categoria",
            name: "Seleccione una categoria",
            id: "Seleccione una categoria",
            is_deletable: 0
        };

        let data = { parent: null };

        getS3Category(data).then(categories => {
            if (categories.length > 0) {
                categories.unshift(defaultOption);
                selCat.options = JSON.stringify(categories.filter(c => c.is_deletable === 0).map(c => {
                    return {
                        value: c.id,
                        name: c.name
                    };
                }));
                selCat.value = categories.filter(c => c.is_deletable === 0)[0].id;

                selCat.options = JSON.stringify(categories.filter(c => c.id !== 'Seleccione una categoria' && c.is_deletable === 0).map(c => {
                    return {
                        value: c.id,
                        name: c.name,
                    };
                }));

                loadingOverlay.style.display = 'none';
                
                // Cuando se traten las categorias, se tendrá que manejar la visibilidad de otras partes
                uploadedCategory(selCat, table, trScope, trCategory, trDatePicker, loadingOverlay);
            } else {
                loadingOverlay.style.display = 'none';
                console.log('No hay categorias disponibles.');
            }
        });
    }
    
    // Agregar el selector de categorías a la tabla
    tdCategory.appendChild(selCat);
    trCategory.appendChild(tdCategory);
}
  function uploadedCategory(selCat, table, trScope, trCategory, trDatePicker, loadingOverlay){
    // Botton de aceptar oculto
    var button = document.getElementById("aonDocumentalDialogDialogActionAccept");
    button.disabled = true;
    // Se escoge una categoria
	selCat.addEventListener('change', (event) => {
      button.disabled = true;
      const selectedCategoryId = event.target.value;
      // Limpiar los campos antes de generar nuevos select
      clearFields(table, [trScope, trCategory, trDatePicker] ,
         ['visibleEmpleadoCheckbox', 'visibleEmpresaCheckbox','oldCategoriesRadio','s3CategoriesRadio']);
      if (selectedCategoryId !== null && selectedCategoryId !== undefined && selectedCategoryId !== 'Seleccione una categoria') {
        // Crear un nuevo tr para Subcategoria solo si hay subcategorias
        let trSubCategory = document.createElement('tr');
        table.appendChild(trSubCategory);
        let tdSubCategory = document.createElement('td');
        tdSubCategory.setAttribute('colspan', '1');
        let selSubCat = new AonNewSelect();
        selSubCat.id = "aonDocumentalUploadSubCategory";
        // selSubCat.title = MSG.SUBCATEGORY; //TODO
		selSubCat.title = "Subcategoria"; //TODO
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
              };
            }));
            trSubCategory.appendChild(tdSubCategory);
            // Oculta el overlay
            loadingOverlay.style.display = 'none';
///////////////////////////////////////////////////
			// Event listener para la selección de la subcategoría
			selSubCat.addEventListener('change', (event) => {
                button.disabled = true;
				const selectedSubCategoryId = event.target.value;

				// Limpiar los campos de administración y modelos antes de generar nuevos
				clearFields(table, [trScope, trCategory,  trDatePicker, trSubCategory] ,
                     ['visibleEmpleadoCheckbox', 'visibleEmpresaCheckbox','oldCategoriesRadio','s3CategoriesRadio']);

				if (selectedSubCategoryId != null && selectedSubCategoryId != undefined) {
					// Crear un nuevo tr para Administración solo si hay administraciones
					let trAdministration = document.createElement('tr');
					table.appendChild(trAdministration);

					let tdAdministration = document.createElement('td');
					tdAdministration.setAttribute('colspan', '1');
					let selAdministration = new AonNewSelect();
					selAdministration.id = "aonDocumentalAdministration";
					selAdministration.title = "Administración";
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
								};
							}));
							trAdministration.appendChild(tdAdministration);
                            // Oculta el overlay
                            loadingOverlay.style.display = 'none';
                        ///////////////////////////////////////////////////
                            // Event listener para la selección de la administración
                            selAdministration.addEventListener('change', (event) => {
                                button.disabled = true;
                                const selectedAdministrationId = event.target.value;

                                // Limpiar el campo de modelos antes de generar nuevos
                                clearFields(table, [trScope, trCategory,  trDatePicker, trSubCategory, trAdministration] ,
                                     ['visibleEmpleadoCheckbox', 'visibleEmpresaCheckbox','oldCategoriesRadio','s3CategoriesRadio']);

                                if (selectedAdministrationId !== null && selectedAdministrationId !== undefined) {
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
                                                };
                                            }));
                                            trModel.appendChild(tdModel);
                                            // Oculta el overlay
                                            loadingOverlay.style.display = 'none';
                                            // Cuando se escoge un modelo
                                            selModel.addEventListener('change', (event) => {
                                                button.disabled = false;
                                            });
                                        } else {
                                          button.disabled = false;
                                          // Oculta el overlay
                                          loadingOverlay.style.display = 'none';
                                          console.log('No hay modelos disponibles.');
                                        }
                                    });
                                }
                            });
                        ///////////////////////////////////////////////////
						} else {
                          button.disabled = false;
                          // Oculta el overlay
                          loadingOverlay.style.display = 'none';
                          console.log('No hay administraciones disponibles.');
						}
					});
				}
			});
///////////////////////////////////////////////////
          } else {
            button.disabled = false;
            // Oculta el overlay
            loadingOverlay.style.display = 'none';
            console.log('No hay subcategorías disponibles.');
          }
        });
      } else if(selectedCategoryId === 'Seleccione una categoria') {
        button.disabled = true;
      }else{
        button.disabled = false;
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
};