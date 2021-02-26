var prepareValue = (value) => {
	var ret = value.replace(/ /g, '+').replace(/\"/g, '').replace(/\:/g, '');

	return ret;
};

var SearchFields = {
	name: {
		key: 'name',

		render: (value) => {
			return 'ti:' + prepareValue(value);
		}
	},

	isbn: {
		key: 'isbn',

		render: (value) => {
			return 'isbn:' + prepareValue(value);
		}
	},

	issn: {
		key: 'issn',

		render: (value) => {
			return 'issn:' + prepareValue(value);
		}
	},

	creator: {
		key: 'creator',

		render: (value) => {
			return 'creator:' + prepareValue(value);
		}
	},

	format: {
		key: 'format',

		render: (value) => {
			return 'dt=x0:' + prepareValue(value['x0']) + (value['x4'] ? 'x4:' + prepareValue(value['x4']) : '');
		}
	},

	none: {
		key: 'none',

		render: (value) => {
			return 'kw:' + prepareValue(value);
		}
	}
};

var SortBy = {
	relevance: 'relevance',
	year: 'year',
	title: 'title',
	creator: 'creator'
};

class SearchQuery {
	constructor (opts) {
		this.fields = [];
		this.sortBy = !opts.sortBy ? SortBy.relevance : opts.sortBy;
		this.language = !opts.language ? '' : opts.language;
		this.publicationYear = opts.publicationYear ? opts.publicationYear : { year: '' };
		this.dateRange = '';
		this.startIndex = 0;

		this.filters = [];
	}

	addField (searchField, value) {
		this.fields.push({
			searchField: searchField,
			value: value
		});
	}

	addFilter (filter) {
		this.filters = this.filters.filter((f) => f.type !== filter.type);

		this.filters.push(filter);
	}

	update (updates) {
		var t = this;

		updates.forEach((update) => {
			if (update.dateRange) {
				t.dateRange = update.dateRange;
			}

			if (update.language) {
				t.language = update.language;
			}

			if (update.format) {
				t.addField(SearchFields.format, update.format);
			}

			if (update.startIndex || update.startIndex === 0) {
				t.startIndex = update.startIndex;
			}
		});
	}

	hasFormatField () {
		return this.fields.find((f) => f.key === 'format');
	}

	toString () {
		var ret = '';

		var dateRange = this.dateRange;
		var language = this.language;
		var fields = this.fields.filter(() => true);

		console.log(fields);

		this.filters.forEach((filter) => {
			if (filter.type === 'dateRange') {
				dateRange = filter.value;
			} else if (filter.type === 'language') {
				language = filter.value;
			} else if (filter.type === 'type') {
				fields.push({
					searchField: SearchFields.format, 
					value: { x0: filter.value }
				});
			}
		});

		var noneField = fields.find((f) => {
			return f.searchField.key === 'none';
		});

		if (noneField) {
			ret += noneField.searchField.render(noneField.value);
		}
 	
		var fieldCounters = {};

		fields.forEach(function (field, index) {
			if (field.searchField.key !== 'none') {
				if (fieldCounters[field.searchField.key] >= 0) {
					fieldCounters[field.searchField.key]++;
				} else {
					fieldCounters[field.searchField.key] = 0;
				}

				var operator = '';

				if (fieldCounters[field.searchField.key] > 0) {
					operator = '+OR+';
				} else {
					operator = '+AND+';
				}

				if (index > 0 || ret.length > 0) {
					ret += operator;
				}

				ret += field.searchField.render(field.value);
			}
		});

		ret += '&sortBy=' + this.sortBy;

		if (language.length > 0 && language !== '_') {
			ret += '&inLanguage=' + language;
		}

		if (this.publicationYear.year.length > 0) {
			if (this.publicationYear.type === 'before') {
				this.dateRange = '1900-' + this.publicationYear.year;
			} else {
				this.dateRange = this.publicationYear.year + '-2100';
			}
		}

		if (dateRange.length > 0) {
			ret += '&datePublished=' + dateRange;
		}

		ret += '&startIndex=' + this.startIndex;

		return ret;
	}
}

export {
	SearchFields,
	SortBy,
	SearchQuery
}
