/**
 * Convierte cualquier objeto o array a una cadena JSON con indentaci�n.
 * Si el objeto no es serializable, retorna un mensaje de error.
 * @param {any} input El objeto o array a convertir.
 * @returns {string} El texto JSON con formato o un mensaje de error si no se puede convertir.
 */
function convertirAJsonLegible(input) {
  try {
    return JSON.stringify(input, null, 2);
  } catch (e) {
    return `[No serializable]`;
  }
}

/**
 * Funcion para imprimir texto en la consola con color y negrita.
 * @global
 * @param {any} texto El texto, objeto, array o cualquier valor que se quiera imprimir en la consola.
 * @param {string|boolean} [color='black'] El color del texto. Si solo se pasa un valor booleano, 
 * se considera como negrita y el color se establece en negro por defecto.
 * @param {boolean} [bold=false] Indica si el texto debe estar en negrita. Si no se pasa, no se aplicara negrita.
 */
function consoleLog(texto, color = 'black', bold = false) {
  // Solo mostramos logs si estamos en desarrollo
  if (process.env.NODE_ENV !== 'development') {
    return;  // Si no es desarrollo, no mostramos nada en consola
  }
  if (typeof color === 'boolean') {
    bold = color;
    color = 'black';
  }
  if (Array.isArray(texto) || typeof texto === 'object') {
    console.log(texto);
    texto = convertirAJsonLegible(texto);
  }
  const estilo = `color: ${color}; font-weight: ${bold ? 'bold' : 'normal'}`;
  console.log(`%c${texto}`, estilo);
};
window.consoleLog = consoleLog;

/**
 * Espera a que un elemento del DOM exista y lo devuelve.
 * Si el elemento ya existe en el momento de la llamada, resuelve inmediatamente.
 * 
 * @global
 * @param {string} selector - Selector CSS del elemento a esperar.
 * @returns {Promise<Element>} Promesa que se resuelve con el elemento encontrado.
 */
function waitForElement (selector) {
  return new Promise((resolve) => {
    // ¿Ya existe el elemento?
    const element = document.querySelector(selector);
    if (element) {
      resolve(element);
      return;
    }

    // Si no, escuchamos hasta que aparezca
    const observer = new MutationObserver(() => {
      const el = document.querySelector(selector);
      if (el) {
        resolve(el);
        observer.disconnect(); // dejamos de observar
      }
    });

    observer.observe(document.body, {
      childList: true,
      subtree: true
    });
  });
}
window.waitForElement = waitForElement;

/**
 * 
 * @returns {boolean}
 * Funcion para hacer que al entrar en el documental cargue la nueva vista de primeras
 */
export function isNewView() {
  return window.new_view ?? true;
}
/**
 * 
 * @param {boolean} value 
 * Funcion para controlar el valor que muestra la nueva vista o la antigua en el documental
 */
export function setNewView(value) {
  window.new_view = value;
}
