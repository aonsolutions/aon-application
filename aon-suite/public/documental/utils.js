// Función obtenida de: https://davidwalsh.name/javascript-debounce-function
// Devuelve una función que no será ejecutada mientras siga siendo llamada,
// esta solo se ejecutará tras dejar de ser llamada durante N milisegundos.
// De esta manera, podemos limitar el número de veces que se puede llamar a una función
// durante un período de tiempo.
// Si se le pasa el parámetro `immediate`, la función será ejecutada inmediatamente,
// en lugar de una vez transcurridos N milisegundos
export function debounce(func, wait, immediate) {
    var timeout;
    return function() {
        var context = this, args = arguments;
        var later = function() {
            timeout = null;
            if (!immediate) func.apply(context, args);
        };
        var callNow = immediate && !timeout;
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
        if (callNow) func.apply(context, args);
    };
}
