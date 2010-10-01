var marker;
var map;
var geocoder;
var expirationDays = 356;
var timeZone;
var address;

function loadParams()
{
    timeZone = readCookie('timeZone');

    var timeZoneList = document.getElementById('timeZoneList');

    for (i = 0; i < timeZoneList.length; i++) {

        if (timeZoneList.options[i].value == timeZone) {
            timeZoneList.selectedIndex = i;
            break;
        }
    }

    var marajaList = document.getElementById('marajaList');
    var marajaValue = readCookie('maraja');

    if(marajaValue == null){
        marajaList.selectedIndex = 0;
    }else{
        for (i = 0; i < marajaList.length; i++) {

            if (marajaList.options[i].value == marajaValue) {
                marajaList.selectedIndex = i;
                break;
            }
        }
    }

    $("#address").val(readCookie('address'));

    initialize();

//showLocation();

}

function initialize() {
    geocoder = new google.maps.Geocoder();
    var lat = readCookie('latitude');
    var lng = readCookie('longitude');

    if(lat == null || lat == '' || lng == null || lng == ''){
        lat = 50.833;
        lng = 4.333;
    }
    var latlng = new google.maps.LatLng(lat, lng);
    var myOptions = {
        zoom: 5,
        center: latlng,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    };
    map = new google.maps.Map(document.getElementById("map"),
        myOptions);

    marker = new google.maps.Marker({
        position: latlng,
        map: map
    });
}

function selectAddress(result){
    
}

function codeAddress() {

    var address = $('#address').val();
    geocoder.geocode( {
        'address': address
    }, function(results, status) {
        if (status == google.maps.GeocoderStatus.OK) {
            if(results.length > 1){
                $("#results").html("");
                for(i = 0;i < results.length; i++){
                    var res = results[i];
                    var link = $('<a/>');
                    link.attr('href', '#');
                    link.html(""+results[i].formatted_address);
                    link.click(function (){
                        marker.setMap(null);

                        map.setCenter(res.geometry.location);
                        marker = new google.maps.Marker({
                            map: map,
                            position: res.geometry.location
                        });
                        $("#address").val(res.formatted_address);
                        $("#resContainer").slideUp();
                    });

                    $("#results").append(link);
                    $("#results").append('<br/>');
                }
                $("#resContainer").slideDown();
            }else{
                marker.setMap(null);

                map.setCenter(results[0].geometry.location);
                marker = new google.maps.Marker({
                    map: map,
                    position: results[0].geometry.location
                });
                $("#address").val(results[0].formatted_address);
            }
        } else {
            alert("Geocode was not successful for the following reason: " + status);
        }
    });
}

function save() {

    setCookie('timeZone', $('#timeZoneList').val(), 365, '/', '', '');
    setCookie('maraja', $('#marajaList').val(), 365, '/', '', '');
    var lat = marker.getPosition().lat();
    var lng = marker.getPosition().lng();
    setCookie('longitude', lng, expirationDays, '/', '', '');
    setCookie('latitude',lat, expirationDays, '/', '', '');
    setCookie('address',$('#address').val(), expirationDays, '/', '', '');

    if(window.opener){
        window.opener.document.location=homePageURL;
        window.close();
    }
}



