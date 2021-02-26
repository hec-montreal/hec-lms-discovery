import Requests from 'data/requests';

var baseUrl = '/hec-lms-discovery-ws/api/v1';

if (window.location.href.indexOf(':1000') >= 0) {
	baseUrl = 'http://localhost:8081' + baseUrl;
}

class Services {
	static search(query) {
		return Requests.getData(baseUrl + '/resources/search?query=' + encodeURIComponent(query));
	}
}

export default Services;
