import { AonDialog } from "../../components/aon-dialog.js";
import { AonSelect } from "../../components/aon-select.js";
import { AonNewSelect } from "../../components/aon-new-select.js";
import { AonNewDate } from "../../components/aon-new-date.js";
import { EVENT, MSG, CONSTANT } from "../../environments/environments.js";
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
				...(isBetaDoc && data.date ? { date: data.date } : {}),
                // agregar `tags` solo si es beta y `data.tags` existe
                ...(isBetaDoc && data.tags ? { tags: data.tags } : {})
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
			category: document.getElementById("aonDocumentalUploadCategoryU").value,
			scope: document.getElementById("aonDocumentalUploadScopeU").value,
			tag: document.getElementById("aonDocumentalUploadTagU").value,
			type: document.getElementById("aonDocumentalUploadTypeU").value
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

    if (radioId === 's3CategoriesRadioU') {
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
	const visibleEmpleadoCheckbox = document.getElementById('visibleEmpleadoCheckboxU');
	const visibleEmpresaCheckbox = document.getElementById('visibleEmpresaCheckboxU');


	// Si el checkbox marcado es el 'visibleEmpleadoCheckbox', desmarcar 'visibleEmpresaCheckbox'
	if (event.target.id === 'visibleEmpleadoCheckboxU' && visibleEmpleadoCheckbox.checked) {
		visibleEmpresaCheckbox.checked = false;
	}

	// Si el checkbox marcado es el 'visibleEmpresaCheckbox', desmarcar 'visibleEmpleadoCheckbox'
	if (event.target.id === 'visibleEmpresaCheckboxU' && visibleEmpresaCheckbox.checked) {
		visibleEmpleadoCheckbox.checked = false;
	}
}

export function handleRadioButtonChange(event) {
    // Primero eliminamos las filas generadas anteriormente
    let table = document.getElementById("tableU");
    let trScope = document.getElementById("trScopeU");
    let trCategory = document.getElementById("trCategoryU");
    let trTag = document.getElementById("trTagU");
    let trDatePicker = document.getElementById("trDatePickerU");

    // Llamamos a clearFields para eliminar las filas generadas (subcategoría, administración, etc.)
    clearFields(table, [trScope, trCategory, trTag, trDatePicker], 
        ['visibleEmpleadoCheckboxU', 'visibleEmpresaCheckboxU','oldCategoriesRadioU','s3CategoriesRadioU']);

    // Si se selecciona 'oldCategoriesRadio', desmarcamos 's3CategoriesRadio' y cargamos categorías antiguas
    if (event.target.id === 'oldCategoriesRadioU') {
        getCategories({ domain: localStorage.getItem('aon_domain_id') }).then(oldCategories => {
            clearPreviousSelectOptions(oldCategories, 'old');
        }).catch(err => {
            console.error("Error al obtener categorías:", err);
        });
    }
    // Si se selecciona 's3CategoriesRadio', desmarcamos 'oldCategoriesRadio' y cargamos categorías S3
    if (event.target.id === 's3CategoriesRadioU') {
        let data = { parent: null };
        getS3Category(data).then(categories => {
            clearPreviousSelectOptions(categories, 's3');
        }).catch(err => {
            console.error("Error al obtener categorías:", err);
        });
    }
}


function clearPreviousSelectOptions(categoriesToLoad, categoryType) {
    let select = document.getElementById("aonDocumentalUploadCategoryU");
    let table = document.getElementById("tableU");
    let trScope = document.getElementById("trScopeU");
    let trCategory = document.getElementById("trCategoryU");
    let trTag = document.getElementById("trTagU");
    let trDatePicker = document.getElementById("trDatePickerU");
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
      uploadedCategory(select, table, trScope, trTag, trCategory, trDatePicker, loadingOverlay);
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
    table.id = 'tableU';
  
    // Cargando loader que se mete en el login - Iniciar
    const spinner = document.querySelector("#"+CONSTANT.ID_LOADER);
    spinner.startLoading();

    // Lógica de los checkboxes
    if (dur.isDocumentalPortal() && !dur.isDocumentalManager()) {
        createCheckboxRow(table, 'visibleEmpleadoCheckboxU', 'Visible Empleado');
    }

    if (dur.isDocumentalPortal() && dur.isDocumentalManager()) {
        createCheckboxRow(table, 'visibleEmpresaCheckboxU', 'No visible Empresa', handleCheckboxChange);
        createCheckboxRow(table, 'visibleEmpleadoCheckboxU', 'Visible Empleado', handleCheckboxChange);
    }

    // DatePicker
    let trDatePicker = document.createElement('tr');
    trDatePicker.id = 'trDatePickerU';
    table.appendChild(trDatePicker);
    
    let tdDatePicker = document.createElement('td');
    tdDatePicker.setAttribute('colspan', '1');
    let datePicker = new AonNewDate();
    datePicker.id = "aonDocumentalUploadDatePickerU";
    datePicker.title = "Fecha del documento";
    tdDatePicker.appendChild(datePicker);
    trDatePicker.appendChild(tdDatePicker);
  
    datePicker.addEventListener('change', (event) => {
        return datePicker.getDateValue();
    });

    // SCOPE
    let trScope = document.createElement('tr');
    trScope.id = 'trScopeU';
    table.appendChild(trScope);

    let tdScope = document.createElement('td');
    tdScope.setAttribute('colspan', '1');
    let selScope = new AonNewSelect();
    selScope.id = "aonDocumentalUploadScopeU";
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

    // TAG
    let trTag = document.createElement('tr');
    trTag.id = 'trTagU';
    trTag.style.display = 'none'; // Lo ocultamos por defecto
    table.appendChild(trTag);

    let tdTag = document.createElement('td');
    tdTag.setAttribute('colspan', '1');

    let selTag = new AonNewSelect();
    selTag.id = "aonDocumentalUploadTagU";
    selTag.title = MSG.TAG;
    selTag.multiple = true;

    tdTag.appendChild(selTag);

    getTags({ domain: localStorage.getItem('aon_domain_id') }).then(tags => {
        if (Array.isArray(tags) && tags.length > 0) {
            selTag.options = JSON.stringify(tags.map(t => ({
              value: t.id,
              name: t.name
            })));
            trTag.appendChild(tdTag);  // Mostramos solo si hay etiquetas
            trTag.style.display = '';         
          } else {
            console.log('No hay etiquetas disponibles.');
          }          
    });

    // Crear la sección de categorías
    createCategorySection(table, spinner, trScope, trTag, trDatePicker);
    // filtros para subir
    return table;
}

async function createCategorySection(table, spinner, trScope, trTag, trDatePicker) {
    let oldCategories =  await loadOldCategories();
    if (!Array.isArray(oldCategories)) {
      oldCategories = [];
    }
    // Crear la fila de categorías (Radio buttons)
    if (oldCategories.length > 0) {
        createRadioButtonRow(table, 's3CategoriesRadioU', MSG.DEFAULT_CATEGORIES, handleRadioButtonChange);
        createRadioButtonRow(table, 'oldCategoriesRadioU', MSG.USER_CATEGORIES, handleRadioButtonChange);
    }

    let trCategory = document.createElement('tr');
    trCategory.id = 'trCategoryU';
    table.appendChild(trCategory);

    let tdCategory = document.createElement('td');
    tdCategory.setAttribute('colspan', '1');
    let selCat = new AonNewSelect();
    selCat.id = "aonDocumentalUploadCategoryU";
    selCat.title = MSG.CATEGORY;

    if (oldCategories.length < 1) {
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
                        name: c.name
                    };
                }));

                // loader que se mete en el login - Parar
                spinner.stopLoading();
                // Cuando se traten las categorias, se tendrá que manejar la visibilidad de otras partes
                uploadedCategory(selCat, table, trScope, trTag, trCategory, trDatePicker, spinner);
            } else {
              // loader que se mete en el login - Parar
              spinner.stopLoading();
              console.log('No hay categorias disponibles.');
            }
        });
    } else {
      spinner.stopLoading();
    }

    // Agregar el selector de categorías a la tabla
    tdCategory.appendChild(selCat);
    trCategory.appendChild(tdCategory);
}

function uploadedCategory(selCat, table, trScope, trTag, trCategory, trDatePicker, spinner){
  // Botton de aceptar oculto
  var button = document.getElementById("aonDocumentalDialogDialogActionAccept");
  button.disabled = true;
  // Se escoge una categoria
  selCat.addEventListener('change', (event) => {
    button.disabled = true;
    const selectedCategoryId = event.target.value;
    // Limpiar los campos antes de generar nuevos select
    clearFields(table, [trScope, trTag, trCategory, trDatePicker] ,
       ['visibleEmpleadoCheckboxU', 'visibleEmpresaCheckboxU','oldCategoriesRadioU','s3CategoriesRadioU']);
    if (selectedCategoryId !== null && selectedCategoryId !== undefined && selectedCategoryId !== 'Seleccione una categoria') {
      // Crear un nuevo tr para Subcategoria solo si hay subcategorias
      let trSubCategory = document.createElement('tr');
      table.appendChild(trSubCategory);
      let tdSubCategory = document.createElement('td');
      tdSubCategory.setAttribute('colspan', '1');
      let selSubCat = new AonNewSelect();
      selSubCat.id = "aonDocumentalUploadSubCategoryU";
      // selSubCat.title = MSG.SUBCATEGORY; //TODO
      selSubCat.title = "Subcategoria"; //TODO
      tdSubCategory.appendChild(selSubCat);

      let data = { parent: selectedCategoryId };
      // loader que se mete en el login - Iniciar
      spinner.startLoading();
    
      getS3Category(data).then(subcategories => {
        if (subcategories.length > 0) {
          selSubCat.options = JSON.stringify(subcategories.map(sc => {
            return {
              value: sc.id,
              name: sc.name
            };
          }));
          trSubCategory.appendChild(tdSubCategory);
          // loader que se mete en el login - Parar
          spinner.stopLoading();
///////////////////////////////////////////////////
          // Event listener para la selección de la subcategoría
          selSubCat.addEventListener('change', (event) => {
              button.disabled = true;
              const selectedSubCategoryId = event.target.value;

              // Limpiar los campos de administración y modelos antes de generar nuevos
              clearFields(table, [trScope, trTag, trCategory,  trDatePicker, trSubCategory] ,
                   ['visibleEmpleadoCheckboxU', 'visibleEmpresaCheckboxU','oldCategoriesRadioU','s3CategoriesRadioU']);

              if (selectedSubCategoryId != null && selectedSubCategoryId != undefined) {
                  // Crear un nuevo tr para Administración solo si hay administraciones
                  let trAdministration = document.createElement('tr');
                  table.appendChild(trAdministration);

                  let tdAdministration = document.createElement('td');
                  tdAdministration.setAttribute('colspan', '1');
                  let selAdministration = new AonNewSelect();
                  selAdministration.id = "aonDocumentalAdministrationU";
                  selAdministration.title = "Administración";
                  tdAdministration.appendChild(selAdministration);

                  let data = { parent: selectedSubCategoryId };
                  // loader que se mete en el login - Iniciar
                  spinner.startLoading();
                  getS3Category(data).then(administrations => {
                      if (administrations.length > 0) {
                          selAdministration.options = JSON.stringify(administrations.map(adm => {
                              return {
                                  value: adm.id,
                                  name: adm.name
                              };
                          }));
                          trAdministration.appendChild(tdAdministration);
                          // loader que se mete en el login - Parar
                          spinner.stopLoading();
                      ///////////////////////////////////////////////////
                          // Event listener para la selección de la administración
                          selAdministration.addEventListener('change', (event) => {
                              button.disabled = true;
                              const selectedAdministrationId = event.target.value;

                              // Limpiar el campo de modelos antes de generar nuevos
                              clearFields(table, [trScope, trTag, trCategory,  trDatePicker, trSubCategory, trAdministration] ,
                                   ['visibleEmpleadoCheckboxU', 'visibleEmpresaCheckboxU','oldCategoriesRadioU','s3CategoriesRadioU']);

                              if (selectedAdministrationId !== null && selectedAdministrationId !== undefined) {
                                  // Crear un nuevo tr para Modelos solo si hay modelos
                                  let trModel = document.createElement('tr');
                                  table.appendChild(trModel);

                                  let tdModel = document.createElement('td');
                                  tdModel.setAttribute('colspan', '1');
                                  let selModel = new AonNewSelect();
                                  selModel.id = "aonDocumentalModelsU";
                                  selModel.title = "Modelos";
                                  tdModel.appendChild(selModel);

                                  let data = { parent: selectedAdministrationId };
                                  // loader que se mete en el login - Parar
                                  spinner.stopLoading();
                                  getS3Category(data).then(models => {
                                      if (models.length > 0) {
                                          selModel.options = JSON.stringify(models.map(mod => {
                                              return {
                                                  value: mod.id,
                                                  name: mod.name
                                              };
                                          }));
                                          trModel.appendChild(tdModel);
                                          // loader que se mete en el login - Parar
                                          spinner.stopLoading();
                                          // Cuando se escoge un modelo
                                          selModel.addEventListener('change', (event) => {
                                              button.disabled = false;
                                          });
                                      } else {
                                        button.disabled = false;
                                        // loader que se mete en el login - Parar
                                        spinner.stopLoading();
                                        console.log('No hay modelos disponibles.');
                                      }
                                  });
                              }
                          });
                      ///////////////////////////////////////////////////
                      } else {
                        button.disabled = false;
                        // loader que se mete en el login - Parar
                        spinner.stopLoading();
                        console.log('No hay administraciones disponibles.');
                      }
                  });
              }
          });
///////////////////////////////////////////////////
        } else {
          button.disabled = false;
          // loader que se mete en el login - Parar
          spinner.stopLoading();
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