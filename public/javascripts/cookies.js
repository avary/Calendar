// set cookie
function setCookie( name, value, expires, path, domain, secure) {
    // set time, it's in milliseconds
    var today = new Date();
    today.setTime( today.getTime() );

    /*
		if the expires variable is set, make the correct
		expires time, the current script below will set
		it for x number of days, to make it for hours,
		delete * 24, for minutes, delete * 60 * 24
		*/
    if (expires)
        expires = expires * 1000 * 60 * 60 * 24;
    var expires_date = new Date( today.getTime() + (expires) );

    document.cookie = name + "=" +escape( value ) +
    ( (expires) ? ";expires=" + expires_date.toGMTString() : "" ) +
    ( (path) ? ";path=" + path : "" ) +
    ( (domain) ? ";domain=" + domain : "" ) +
    ( (secure) ? ";secure" : "" );
}

// read cookie
function readCookie(name) {
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return unescape(c.substring(nameEQ.length,c.length));
    }
    return null;
}

function noplus(name){
    return name.replace("+", " ");
}