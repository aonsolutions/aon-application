// Creamos json nuevo (LocalStorage en pruebas) - C
function createJsonApi(data){
    /*
     * El data debe ser un array con los siguientes datos:
     * - name -> será el nombre que se indicará para recoger el json (Ej.: messenger)
     * - json -> JSON que se guardará bajo el nombre indicado antes
     */
    localStorage.setItem(data['name'], data['json']);
    
    // Una vez creado, lo devolvemos
    return localStorage.getItem(data['name']);
}

// Recogemos json de API (LocalStorage en pruebas) - R
function readJsonApi(name){
    /*
     * Nombre del objeto que queramos leer
     */
    
    return localStorage.getItem(name);
}

// Modificamos json (LocalStorage en pruebas) - U
function updateJsonApi(data){
    /*
     * El data debe ser un array con los siguientes datos:
     * - name -> nombre del JSON a editar
     * - action -> 3 opciones: 'add', 'remove', 'update'
     * - index -> índice de dónde se encuentra el elemento
     * - json -> en el caso de UPDATE o ADD, json para realizar la acción. En caso de REMOVE, enviar NULL
     */
    //localStorage.setItem(data['name'], data['json']);
}

// Eliminamos elemento del json (LocalStorage en pruebas) - D
function deleteJsonApi(name){
    /*
     * Nombre del objeto que queramos leer
     */
    localStorage.removeItem(name);
}