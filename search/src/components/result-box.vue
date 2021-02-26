<template>	
	<div class="result-box">
		<div class="error" v-if="resultHasError">
			Une erreur s'est produite lors de la recherche (requête: q={{ lastQuery }})
		</div>

		<div v-if="!resultHasError && result.resources">
			<div class="enable-debug" style="margin-bottom: 20px;">
				<label for="a"><input id="a" type="checkbox" v-model="showDebugInfo" /> Afficher les informations de debug</label>
			</div>

			<div class="debug" v-if="showDebugInfo">
				<div class="query">
					<label>Requête</label>
					<input type="text" :value="result.query" style="width: 100%;" />
				</div>

				<a href="#" @click="toggleShowRawOclcResult()">[DEBUG] {{rawOclcMessage}}</a>
				<textarea v-if="showRawOclcResult">{{result.raw}}</textarea>
			</div>

			<div class="meta" v-if="hasResults()">
				<div>{{result.totalResults}} résultats</div>
				<div><a href="#" :title="result.startIndex + ' - ' + result.itemsPerPage" @click="updatePage(result.startIndex - result.itemsPerPage, $event)">&lt;</a> Page {{result.currentPage + 1}} <a href="#" @click="updatePage(result.startIndex + result.itemsPerPage, $event)">&gt;</a></div>
			</div>

			<div class="result lr">
				<div class="menu left">
					<h2>Options de recherche</h2>

					<div class="clear" v-if="filters.length > 0">
						<a class="filter" href="#" @click="resetFilters($event)">Retirer les filtres</a>
					</div>

					<div class="date">
						<h3>Date de publication</h3>

						<ul>
							<li v-for="range in dateFilters">
								<a class="filter" :class="{ active : hasFilter('dateRange', range.low + '-' + range.high) }" href="#" @click="addFilter('dateRange', range.low + '-' + range.high, $event)">{{ range.low }} - {{ range.high }}</a></li>
							</li>
						</ul>
					</div>

					<div class="lang">
						<h3>Langue</h3>

						<ul>
							<li v-for="languageFilter in languageFilters">
								<a :class="{ active: hasFilter('language', languageFilter.key) }" href="#" @click="addFilter('language', languageFilter.key, $event)">{{ languageFilter.label }}</a>
							</li>
						</ul>
					</div>

					<div class="type">
						<h3>Type de document</h3>

						<ul>
							<li v-for="typeFilter in typeFilters">
								<a :class="{ active: hasFilter('type', typeFilter.key) }" href="#" @click="addFilter('type', typeFilter.key, $event)">{{ typeFilter.label }}</a>
							</li>
						</ul>
					</div>
				</div>

				<div class="entries right working" v-if="working">
					Chargement...
				</div>

				<div class="entries right" :class="{ invisible: working }">
					<div class="no-results" v-if="!hasResults()">
						Aucuns résultats
					</div>

					<div class="entry" v-for="entry in result.resources">
						<div class="title">{{ entry.name }}</div>

						<div class="link">
							<a :href="entry.link" target="_blank">{{ entry.link }}</a>
						</div>

						<div class="raw" v-if="showDebugInfo">
							<textarea>Type: {{ entry.type + '\n' }}ImportUrl: {{ generateImportUrl(entry) + '\n\n'}}{{entry.raw}}</textarea>
						</div>

						<div class="authors" v-if="entry.authorsLine.length > 0">
							par {{ entry.authorsLine }}
						</div>

						<div class="date" v-if="entry.type === 'BOOK' && entry.copyrightYears.length > 0">
							<label>Date: </label> {{ entry.copyrightYears[0] }}
						</div>

						<div class="partOfTitle" v-if="entry.type === 'ARTICLE'">
							{{ findPartTitle(entry) }} Vol {{ findVolumeNumber(entry) }}, Issue {{ findIssueNumber(entry) }}. Date: <span v-if="entry.copyrightYears.length > 0">{{ entry.copyrightYears[0] }}</span>, Pages: {{ entry.pageStart }}-{{ entry.pageEnd }}
						</div>

						<div class="type">
							<label>Type de document: </label> {{ entry.typeName }}
						</div>

						<div clas="import">
							<a :href="generateImportUrl(entry)">Importer dans ZoneCours</a>
						</div>
					</div>

					<div class="meta" v-if="hasResults()">
						<div>{{result.totalResults}} résultats</div>
						<div><a href="#" :title="result.startIndex + ' - ' + result.itemsPerPage" @click="updatePage(result.startIndex - result.itemsPerPage, $event)">&lt;</a> Page {{result.currentPage + 1}} <a href="#" @click="updatePage(result.startIndex + result.itemsPerPage, $event)">&gt;</a></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import { Params } from 'data/params';

var messages = {
	'show': 'Afficher',
	'hide': 'Cacher',
	'suffix': ' le résultat brut d\'OCLC'
};

export default {
	data () {
		return {
			showDebugInfo: false,
			showRawOclcResult: false,
			rawOclcMessage: messages['show'] + messages['suffix'],

			filters: [],

			languageFilters: [{
				key: 'fre',
				label: 'Français'
			}, {
				key: 'eng',
				label: 'Anglais'
			}, {
				key: 'esp',
				label: 'Espagnol'
			}],

			typeFilters: [{
				key: 'Book',
				label: 'Livre'
			}, {
				key: 'ArtChap',
				label: 'Article'
			}]
		};
	},

	props: ['working', 'result', 'resultHasError', 'lastQuery'],

	computed: {
		dateFilters () {
			var ret = [];
			var low = 1970;
			var now = new Date().getFullYear();

			while (low < now) {
				var high = low + 10;

				if (high >= now) {
					high = now;
				}

				ret.push({
					low,
					high
				});

				low += 10;
			}

			return ret.reverse();
		}
	},

	mounted () {

	},

	methods: {
		toggleShowRawOclcResult () {
			this.showRawOclcResult = !this.showRawOclcResult;

			if (!this.showRawOclcResult) {
				this.rawOclcMessage = messages['show'] + messages['suffix'];
			} else {
				this.rawOclcMessage = messages['hide'] + messages['suffix'];
			}
		},

		addFilter (type, value, e) {
			// Remove filters of same type
			this.filters = this.filters.filter((f) => f.type !== type)

			var filter = {
				type,
				value
			};

			this.filters.push(filter);

			this.$emit('addFilter', filter);

			e.preventDefault();
		},

		updatePage (index, e) {
			if (index >= 0) {
				this.$emit('updatePage', { startIndex: index });
			}
			
			e.preventDefault();
		},

		generateImportUrl (resource) {
			var get = [];

			var addParam = (key, value) => {
				get.push({
					key,
					value
				});
			};

			addParam('ctx_ver', '1.0');
			addParam('ctx_enc', 'info:ofi/enc:UTF-8');
			addParam('rfr_id', 'info:sid/hec.ca');
			addParam('rft_val_fmt', 'info:ofi/fmt:kev:mtx:' + resource.openUrlType);

			addParam('rft.genre', resource.sakaiType);
			addParam('rft.issn', '');

			resource.isbns.forEach((isbn) => {
				if (isbn.indexOf('9') === 0) {
					addParam('rft.isbn', isbn);
				}
			});

			addParam('rft.au', resource.authorsLine);
			addParam('rft.date', resource.copyrightYears.length > 0 ? resource.copyrightYears[0] : '');
			addParam('externalDocID', resource.link);
			addParam('hecUrl', resource.link);

			if (resource.type === 'ARTICLE') {
				addParam('rft.jtitle', this.findPartTitle(resource));
				addParam('rft.atitle', resource.name);
				addParam('rft.volume', this.findVolumeNumber(resource));
				addParam('rft.issue', this.findIssueNumber(resource));
				addParam('rft.spage', resource.pageStart);
				addParam('rft.epage', resource.pageEnd);
			} else{
				addParam('rft.title', resource.name);
				addParam('rft.btitle', resource.name);
			}

			var params = new Params();
			var base = params.baseUrl;

			get.forEach((g) => {
				base += '&' + g.key + '=' + encodeURIComponent(g.value);
			});

			return base;
		},

		resetFilters (e) {
			this.clearFilters();

			this.$emit('resetFilters');

			e.preventDefault();
		},

		clearFilters () {
			this.filters = [];
		},

		findPartTitle (entry) {
			if (entry.partOfLabel && entry.partOfLabel !== '?') {
				return entry.partOfLabel;
			}

			if (entry.partOf) {
				return this.findPartTitle(entry.partOf);
			}

			return '?';
		},

		findVolumeNumber (entry) {
			if (entry.volumeNumber && entry.volumeNumber !== '?') {
				return entry.volumeNumber;
			}

			if (entry.partOf) {
				return this.findVolumeNumber(entry.partOf);
			}

			return '?';
		},

		findIssueNumber (entry) {
			if (entry.issueNumber && entry.issueNumber !== '?') {
				return entry.issueNumber;
			}

			if (entry.partOf) {
				return this.findIssueNumber(entry.partOf);
			}

			return '?';
		},

		hasFilter (type, value) {
			return this.filters.find((filter) => type === filter.type && value === filter.value);
		},

		hasResults () {
			return this.result.resources && this.result.resources.length > 0;
		}
	}
}

</script>

<style>

.result-box {
	margin-top: 20px;
	padding-top: 20px;
	width: 100%;
}

.debug {
	padding-bottom: 10px;
}

.debug .query {
	margin-bottom: 10px;
}

.result-box textarea {
	width: 100%;
	height: 300px;
	padding: 5px;
	border: 1px solid #aaa;
}

.result {
	border-top: 1px solid #aaa;
}

.no-results {
	text-align: center;
}

.meta {
	padding-bottom: 20px;
}

.menu {
	width: 30%;
	margin-top: 20px;
}

.menu > div {
	margin-top: 20px;
}

.entries {
	margin-top: 20px;
	width: 70%;
}

.entries.working {
	text-align: center;
}

.entries.invisible {
	visibility: hidden;
}

.entry {
	border-bottom: 1px solid #aaa;
	margin-bottom: 20px;
}

.entry > div {
	margin-bottom: 20px;
}

.entry .title {
	font-weight: bold;
	font-size: 1.5em;
	margin-bottom: 5px;
}

.entry .summary {
	text-align: justify;
}

.entry label {
	float: left;
	margin-right: 5px;
}

.entry .link {
	font-size: 0.8em;
}

a.active {
	color: #000;
}

.error {
	text-align: center;
	color: #f88;
}

</style>
