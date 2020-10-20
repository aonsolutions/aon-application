
    // Creamos json nuevo (LocalStorage en pruebas) - C
    function createJsonApi(data){
        /*
        * El data debe ser un Objeto con los siguientes datos:
        * - name -> será el nombre que se indicará para recoger el json (Ej.: messenger)
        * - json -> JSON que se guardará bajo el nombre indicado antes (Este Json lo convertimos en un string para poder almacenarlo)
        */
        localStorage.setItem(data.name, JSON.stringify(data.json));
        
        // Una vez creado, lo devolvemos
        return readJsonApi(data.name);
    }

    // Recogemos json de API (LocalStorage en pruebas) - R
    function readJsonApi(name){
        /*
        * Nombre del objeto que queramos leer, le tenemos que hacer un JSON.parse para obtenerlo
        */
        return JSON.parse(localStorage.getItem(name));
    }

    // Modificamos json (LocalStorage en pruebas) - U
    function updateJsonApi(data){
        /*
        * El data debe ser un Objeto con los siguientes datos:
        * - name        -> nombre del JSON a editar
        * - action      -> 3 opciones: 'add', 'remove', 'update'
        * - keyLevel1   -> Key dentro donde modificamos tiene un nivel mas
        * - keyLevel2   -> Key dentro donde modificamos tiene un nivel mas
        * - keyUpdate   -> Key donde realizamos los cambios 3 opciones: 'add', 'remove', 'update'
        * - json        -> en el caso de UPDATE o ADD, json para realizar la acción. En caso de REMOVE, enviar NULL
        */

        // Cogemos sobre cual vamos a realizar los cambios
        let dataLocalStorage = readJsonApi(data.name);

        //
        // Realizamos los cambios
        //
        if (data.action) {
            if(data.keyLevel1){
                if(data.keyLevel2){
                    // Si tenemos 1 nivel en la KEY
                    if(data.action == 'remove'){
                        // Removemos
                        delete dataLocalStorage[data.keyLevel1][data.keyUpdate];
                    } else {
                        // Si tenemos 2 nivel en la KEY
                        dataLocalStorage[data.keyLevel1][data.keyLevel2][data.keyUpdate] = data.json;
                    }
                } else {
                    // Si tenemos 1 nivel en la KEY
                    if(data.action == 'remove'){
                        // Removemos
                        delete dataLocalStorage[data.keyLevel1][data.keyUpdate];
                    } else {
                        // Agregamos o modificamos
                        dataLocalStorage[data.keyLevel1][data.keyUpdate] = data.json;
                    }
                }
            } else {
                // No tiene niveles
                if(data.action == 'remove'){
                    // Removemos
                    delete dataLocalStorage[data.keyUpdate];
                } else {
                    // Agregamos o modificamos
                    dataLocalStorage[data.keyUpdate] = data.json;
                }
            }

            // Llamamos para hacer los cambios
            return createJsonApi({
                name: data.name,
                json: dataLocalStorage
            });
        }
    }

    // Eliminamos elemento del json (LocalStorage en pruebas) - D
    function deleteJsonApi(name){
        /*
        * Nombre del objeto que queramos leer
        */
        localStorage.removeItem(name);
    }

    // Limpiar Datos almacenados
    function clearJsonApi(){
        /*
        * Eliminamos todo los datos en el localStorage
        */
        localStorage.clear();
    }

    /*
    * Ejemplo de llamadas que irian en tickets/index.html
    *
        console.log('1 - listado');
            // Mostramos
            console.log(list);

        console.log('2 - Creado');
            // Creamos
            var dataCreate = {
                name : 'messenger', // tickets
                json : list
            };
            respuesta1 = createJsonApi(dataCreate)
            console.log(respuesta1);

        console.log('3 - Leerlo');
            // Leemos lo guardado a ver 
            console.log(readJsonApi('messenger'));
        
        console.log('4 - Actualizamos');
            // Actualizamos
            var dataUpdate = {
                name        : 'messenger', // tickets
                action      : 'add',
                //action      : 'update',
                //keyLevel1   : 'list',
                //keyLevel2   : 1,
                keyUpdate   : 'agregado',
                json        : 'Titulo cambiado',
            };
            
            console.log(updateJsonApi(dataUpdate));

        console.log('5 - Actualizamos Eliminando');
            // Actualizamos
            var dataUpdate = {
                name        : 'messenger', // tickets
                action      : 'remove',
                //keyLevel1   : 'list',
                //keyLevel2   : 1,
                keyUpdate   : 'agregado',
                json        : 'Titulo cambiado',
            };
            
            console.log(updateJsonApi(dataUpdate));

        console.log('6 - Eliminar');
            deleteJsonApi('messenger');

    */
