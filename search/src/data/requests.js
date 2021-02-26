import $ from 'jquery';

var addCacheBuster = (url) => {
	if (url.indexOf('?') >= 0) {
		return url + '&_cachebuster=' + Date.now();
	} else {
		return url + '?_cachebuster=' + Date.now();
	}	
}

class Requests {
	static getData (url) {
		return new Promise(function(resolve, reject) {
			url = addCacheBuster(url);

			$.get(url).done(function(data) {
				resolve(data);
			}).fail(function(e) {
				reject(e);
			});
		});
	}

	static postData (url, data) {
		return new Promise(function(resolve, reject) {
			url = addCacheBuster(url);

			$.post(url, data).done(function(data) {
				resolve(data);
			}).fail(function(e) {
				reject(e.responseJSON);
			});
		});		
	}
}

export default Requests;
