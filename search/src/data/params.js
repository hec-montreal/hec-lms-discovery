import 'url-search-params-polyfill';

class Params {
	constructor() {
		var u = new URLSearchParams(window.location.search);

		this.baseUrl = u.get('linkurl_base');
		this.callerId = u.get('linkurl_id');

		if (!this.baseUrl) {
			this.baseUrl = '';
			this.callerId = '';
		}
	}
};

export { Params };
