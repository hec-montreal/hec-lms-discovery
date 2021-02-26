'use strict';

const path = require('path');

const { VueLoaderPlugin } = require('vue-loader')

require('babel-polyfill');

module.exports = {
	mode: 'development',
	entry: ['babel-polyfill', path.resolve(__dirname, 'src/index.js')],
	output: {
		path: path.resolve(__dirname, 'dist'),
		filename: 'bundle.js'
	},
	resolve: {
	    alias: {
	      'Vue': 'vue/dist/vue.esm.js',
	      'components': path.resolve(__dirname, 'src/components'),
	      'data': path.resolve(__dirname, 'src/data')
	    },

	    extensions: ['.vue', '.js']
  	},
  	module: {
  		rules: [
	  		{
	  			test: /\.vue$/,
	  			loader: 'vue-loader'
	  		},
     		{
				test: /\.js$/,
				loader: 'babel-loader',
				options: {
					presets: ['@babel/preset-env']
				}
		    },
			{
				test: /\.css$/,
				use: [
					'vue-style-loader',
					'css-loader'
				]
			}
  		]
  	},
  	plugins: [
  		new VueLoaderPlugin()
  	]
};
