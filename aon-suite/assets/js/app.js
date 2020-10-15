$(document).ready(function(){
    $('#atp_cerrar_menu_izquierda').on('click', function(){
        $('#atp_menu_izquierda').toggleClass('atp_abierto');
    });
    $('#atp_cerrar_menu_derecha').on('click', function(){
        if($("#atp_menu_derecha").hasClass('atp_abierto')){
            $('#atp_cerrar_menu_derecha').css('left', '-40px');
        } else {
            $('#atp_cerrar_menu_derecha').css('left', '-20px');
        }
        $('#atp_menu_derecha').toggleClass('atp_abierto');
    });
});
