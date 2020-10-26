
    // Creamos json nuevo (LocalStorage en pruebas) - C
    function createJsonApi(data){
        /*
        * El data debe ser un Objeto con los siguientes datos:
        * - name -> será el nombre que se indicará para recoger el json (Ej.: messenger)
        * - json -> JSON que se guardará bajo el nombre indicado antes (Este Json lo convertimos en un string para poder almacenarlo)
        */

        // Cogemos si tenemos ya datos almacenados
        let dataLoad = readJsonApi(data.name);
        
        if(!dataLoad){
            // No tenemos es el primero, creamos el objeto con el primer dato
            dataLoad = {
                1 : data.json
            }
        } else {
            // Tenemos datos, agregamos este dato al resto
            dataLoad[returNextID('messenger')] = data.json;
        }

        // Almacenamos
        localStorage.setItem(data.name, JSON.stringify(dataLoad));

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
        * - name    -> nombre del JSON a editar
        * - json    -> Del campo que actualizamos, el JSON tendra que contener el campo id
        */

        // Cogemos si tenemos ya datos almacenados
        let dataLoad = readJsonApi(data.name);
                
        if(dataLoad){
            // Modificamos el valor que queremos
            let key       = data.json.id
            dataLoad[key] = data.json;

            // Almacenamos
            localStorage.setItem(data.name, JSON.stringify(dataLoad));

            // Una vez creado, lo devolvemos
            return readJsonApi(data.name);
        }

    }

    // Eliminamos elemento del json (LocalStorage en pruebas) - D
    function deleteJsonApi(data){
        /*
        * Eliminamos el valor escogido de la variable
        * - name    -> nombre del JSON a editar
        * - id      -> ID del dato que removemos
        */

        // Cogemos si tenemos ya datos almacenados
        let dataLoad = readJsonApi(data.name);
                
        if(dataLoad){
            // Modificamos el valor que queremos
            let key       = data.id
            delete dataLoad[key];

            // Almacenamos
            localStorage.setItem(data.name, JSON.stringify(dataLoad));

            // Una vez creado, lo devolvemos
            return readJsonApi(data.name);
        }
    }

    // Eliminamos varable almacenada en local
    function deleteStorage(name){
        /*
        * Eliminamos este dato del localStorage
        */
        localStorage.removeItem(name);
    }

    // Limpiar toda las variables almacenadas
    function clearStorage(){
        /*
        * Eliminamos todo los datos en el localStorage
        */
        localStorage.clear();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                            //
    // Funciones utiles                                                                                                           //
    //                                                                                                                            //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        
        //
        // Retornar el ID por el que vamos
        //
        function returNextID(name){
            if (name != ''){
                // Cogemos los datos
                let data = readJsonApi(name);
                // Retornamos el id por el que vamos
                if(!data){
                    // No tenemos es el primero
                    return 1;
                } else {
                    // Retornamos la siguiente key (que sería el siguiente ID)
                    let myObj = data, key; 
                    for (key in myObj);
                    return Number(key) + 1;
                }
            }
        }

        //
        // Coger parametros por get
        //
        function getAllGetParams() {
            let result  = [];
            let parts   = [];

            location.search
                .substr(1)
                .split("&")
                .forEach(function (item) {
                    parts = item.split("=");
                    if(parts[0]!=""){     
                        result.push(parts);
                    }
            });

            return result;
        }

        //
        // Generar nombres aleatorios
        //
        function NameRandom(type = 1){
            // nombre aleatorio.. que serían los UID
            let name_a = ['Manolo', 'Ambrosio', 'Abelardo', 'Fulgensio', 'Alejandro', 'Jesús', 'Julio', 'David', 'Alberto', 'Juan'];
            let name_b = ['Carmen', 'Angela', 'Manuela', 'Margarita', 'Maribel', 'Desi', 'Inma', 'Jessica', 'Estefanía', 'Nieves'];
            
            return type == 1 ? name_a[Math.floor((Math.random() * 10))] : name_b [Math.floor((Math.random() * 10))];
        }

        //
        // Coger fecha actual
        //
        function Today(){
            // Fecha de hoy
            let date      = new Date();
            let today     = date.getDate()+'-'+(date.getMonth()+1)+'-'+date.getFullYear();
            let todayHour = date.getHours()+':'+date.getMinutes()+':'+date.getSeconds();
            return today+' '+todayHour;
        }

        