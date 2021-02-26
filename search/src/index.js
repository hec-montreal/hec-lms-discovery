import 'babel-polyfill';
import Vue from 'Vue';

import App from 'components/app';

const app = new Vue({
	el: '#app',

	components: {
		App
	}
});
