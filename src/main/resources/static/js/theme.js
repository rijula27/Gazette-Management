
var style_cookie_name = "style";
var style_cookie_duration = 30 * 30 * 7;

function switch_style(css_title) {
	var i, link_tag;
	for (i = 0, link_tag = document.getElementsByTagName("link"); i < link_tag.length; i++) {
		if ((link_tag[i].rel.indexOf("stylesheet") != -1) && link_tag[i].title) {
			link_tag[i].disabled = true;
			if (link_tag[i].title == css_title) {
				link_tag[i].disabled = false;
			}
		}
		set_cookie(style_cookie_name, css_title, style_cookie_duration);
	}
}
function set_style_from_cookie() {
	var css_title = get_cookie(style_cookie_name);
	if (css_title.length) {
		switch_style(css_title);
	}
}
function set_cookie(name, value, days, domain) {
	var path = domain ? ("; domain=" + domain) : '';

	var expires, date;
	if (typeof days == "number") {
		date = new Date();
		date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
		expires = date.toGMTString();
	}

	document.cookie = name + "=" + encodeURIComponent(value) +
		((expires) ? "; expires=" + expires : "") +
		((path) ? "; path=" + path : "") +
		((domain) ? "; domain=" + domain : "");

}
function get_cookie(cookie_name) {
	var nameq = cookie_name + "=";
	var c_ar = document.cookie.split(';');
	for (var i = 0; i < c_ar.length; i++) {
		var c = c_ar[i];
		while (c.charAt(0) == ' ') c = c.substring(1, c.length);
		if (c.indexOf(nameq) == 0) return decodeURIComponent(decodeURIComponent(c.substring(nameq.length, c.length)));
	}
	return '';
}