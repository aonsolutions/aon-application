/**
 * Convierte cualquier objeto o array a una cadena JSON con indentación.
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
 * Función para imprimir texto en la consola con color y negrita.
 * @param {any} texto El texto, objeto, array o cualquier valor que se quiera imprimir en la consola.
 * @param {string|boolean} [color='black'] El color del texto. Si solo se pasa un valor booleano, 
 * se considera como negrita y el color se establece en negro por defecto.
 * @param {boolean} [bold=false] Indica si el texto debe estar en negrita. Si no se pasa, no se aplicará negrita.
 */
window.consoleLog = function (texto, color = 'black', bold = false) {
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
