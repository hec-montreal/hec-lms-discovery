<template>
	<div class="search-box">
		<div class="sort">
			<label for="sort-by">Tri</label>

			<select id="sort-by" v-model="sortBy" @change="onSortChange">
				<option value="relevance">Pertinence</option>
				<option value="title">Titre</option>
				<option value="creator">Auteur (A-Z)</option>
				<option value="year">Date</option>
			</select>
		</div>

		<div class="tab-host">
			<div class="tabs">
				<div class="tab" :class="{ active: mode === 'simple' }" @click="setMode('simple')">Recherche Simple</div>
				<div class="tab" :class="{ active: mode === 'complex' }" @click="setMode('complex')">Recherche Avancée</div>
			</div>
		</div>

		<div class="tab-content">
			<div class="simple" v-if="mode === 'simple'">
				<div class="query-box">
					<label for="query">Mot clés</label>

					<input type="text" id="query" v-model="query" placeholder="Recherche" :disabled="working" @keyup.enter="doSearch(true)" />
				</div>

				<div class="action-box">
					<button type="button" @click="doSearch(true)">Rechercher</button>
				</div>

				<div class="query-box-isbn">
					<label for="query-isbn">Recherche par ISBN</label>

					<input type="text" id="query-isbn" v-model="isbn" placeholder="ISBN" :disabled="working" @keyup.enter="doSearch(true)" />
				</div>

				<div class="action-box">
					<button type="button" @click="doSearch(true)">Rechercher</button>
				</div>
			</div>

			<div class="complex" v-if="mode === 'complex'">
				<div class="pairs">
					<div class="pair lr">
						<div class="type c">
							<label>Critère</label>
						</div>

						<div class="value c">
							<label>Valeur</label>
						</div>
					</div>

					<div class="pair lr" v-for="(pair, index) in advancedPairs">
						<select v-model="pair.type" class="type c" :disabled="working">
							<option value="creator">Auteur</option>
							<option value="name">Titre</option>
							<option value="name">Titre de la publication</option>
							<option value="isbn">ISBN</option>
							<option value="issn">ISSN</option>
						</select>

						<input type="text" class="value c" v-model="pair.value" :disabled="working" />

						<div class="add c" @click="addAdvancedPair()">
							<img src="resources/images/plus.png" alt="Ajouter un critère" title="Ajouter un critère" />
						</div>

						<div class="remove c" v-if="pair.removeable" @click="removeAdvancedPair(index)">
							<img src="resources/images/x.png" alt="Enlever ce critère" title="Enlever ce critère" />
						</div>
					</div>
				</div>

				<div class="opts">
					<div class="lr">
						<label for="publication-year">Année de publication</label>

						<div class="left">
							<select id="publication-year" v-model="publicationYear.type" :disabled="working">
								<option value="before">Avant</option>
								<option value="after">Après</option>
							</select>
						</div>

						<div class="left">
							<input type="text" v-model="publicationYear.year" :disabled="working" />
						</div>
					</div>

					<div class="lang">
						<label for="lang">Langue</label>

						<select id="lang" v-model="language" :disabled="working">
							<option value="_">Toutes</option>
							<option value="fre">Français</option>
							<option value="eng">Anglais</option>
						</select>
					</div>

					<div class="fmt">
						<label for="format">Type</label>

						<select id="format" v-model="format" :disabled="working">
							<option value="all">Tous les types</option>
							<option value="Book">Livre</option>
							<option value="ArtChap">Article</option>
						</select>
					</div>
				</div>

				<div class="action-box">
					<button type="button" @click="doSearch(true)">Rechercher</button>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import { SearchFields, SortBy, SearchQuery } from 'data/search-query';

export default {
	data () {
		return {
			sortBy: 'relevance',
			query: '',
			isbn: '',
			mode: 'simple',
			advancedPairs: [{
				type: 'name',
				value: '',
				removeable: false
			}],
			language: '_',
			publicationYear: {
				year: '',
				type: 'before'
			},
			format: 'all'
		};
	},

	props: ['working'],

	methods: {
		doSearch (reset) {
			var query = this.buildQuery();

			this.$emit('resetFilters', {
				noSearch: true
			});
			
			this.$emit('query', query);
		},

		setMode (mode) {
			if (this.mode !== mode) {
				this.query = '';
				this.isbn = '';
				this.advancedPairs = [{
					type: 'name',
					value: '',
					removeable: false
				}];
				this.language = '_';
				this.publicationYear.year = '';
				this.publicationYear.type = 'before';
				this.format = 'all';
			}

			this.mode = mode;
		},

		addAdvancedPair () {
			this.advancedPairs.push({
				type: 'title',
				value: '',
				removeable: true
			});
		},

		removeAdvancedPair (at) {
			this.advancedPairs.splice(at, 1);
		},

		buildQuery () {
			var query = new SearchQuery({
				sortBy: SortBy[this.sortBy],
				language: this.mode === 'simple' ? '' : this.language,
				publicationYear: this.publicationYear
			});

			if (this.mode === 'simple') {
				if (this.isbn.length > 0) {
					query.addField(SearchFields.isbn, this.isbn);
				} else {
					query.addField(SearchFields.none, this.query);
				}		
			} else {
				this.advancedPairs.forEach(function (pair) {
					query.addField(SearchFields[pair.type], pair.value);
				});

				if (this.format !== 'all') {
					query.addField(SearchFields.format, {
						x0: this.format
					});
				}
			}

			return query;
		},

		onSortChange () {
			this.$emit('updateSort', SortBy[this.sortBy]);
		}
	}
}

</script>

<style>

label {
	display: block;
	font-weight: bold;
	margin-bottom: 5px;
}

.sort {
	margin-bottom: 20px;
}

.search-box {
	width: 100%;
}

#query {
	width: 100%;
}

.action-box {
	margin-top: 10px;
}

.opts {
	margin-top: 10px;
}

.query-box-isbn .label {
	font-size: 0.9em;
	margin-bottom: 5px;
}

.query-box-isbn {
	width: 20%;
	margin-top: 40px;
}

.pair {
	padding-bottom: 10px;
}

.pair .c {
	float: left;
	margin-right: 10px;
}

.pair .type {
	width: 20%;
}

.pair .value {
	width: 65%;
}

.add img {
	width: 28px;
	height: 28px;
	cursor: pointer;
}

.remove img {
	width: 28px;
	height: 28px;
	cursor: pointer;
}

.opts .pub-year-lbl {
	padding-top: 5px;
}

.opts div {
	margin-right: 10px;
}

.opts > div {
	margin-bottom: 20px;
}

.opts .u-lbl {
	margin-bottom: 5px;
}

.tabs {

}

.tabs:after {
	content: '';
	display: table;
	clear: both;
}

.tab {
	float: left;
	margin-right: 10px;
	cursor: pointer;
	padding: 10px;
	background-color: #ddd;
}

.tab.active {
	font-weight: bold;
	background-color: #eee;
	border: 1px solid #ddd;
	border-bottom: 0;
}

.tab-content {
	background-color: #eee;
	padding: 20px;
	border: 1px solid #ddd;
	border-top: 0;
}

</style>
