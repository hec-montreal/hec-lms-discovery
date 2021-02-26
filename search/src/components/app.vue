<template>
	<div class="app">
		<search-box @query="onQuery" @updateSort="onUpdateSort" @resetFilters="onResetFilters" :working="working"></search-box>
		<result-box ref="resultBox" @addFilter="onAddFilter" @resetFilters="onResetFilters" @updatePage="onUpdatePage" :working="working" :result="result" :resultHasError="resultHasError" :lastQuery="lastQuery"></result-box>
	</div>
</template>

<script>
import Services from 'data/services';

import SearchBox from 'components/search-box';
import ResultBox from 'components/result-box';

export default {
	data () {
		return {
			working: false,
			searchQuery: null,
			result: [],
			resultHasError: false,
			lastQuery: ''
		}
	},

	mounted () {

	},

	methods: {
		onResetFilters () {
			this.$refs.resultBox.resetFilters();
		},

		onQuery (query) {
			this.searchQuery = query;

			this.doSearch();
		},

		onAddFilter (filter) {
			if (this.searchQuery) {
				this.searchQuery.addFilter(filter);

				this.doSearch();
			}
		},

		onResetFilters (opts) {
			if (this.searchQuery) {
				this.searchQuery.filters = [];
				this.$refs.resultBox.clearFilters();

				if (!opts.noSearch) {
					this.doSearch();
				}
			}
		},

		onUpdateSort (sortBy) {
			if (this.searchQuery) {
				this.searchQuery.sortBy = sortBy;

				this.doSearch();
			}
		},

		onUpdatePage (opts) {
			if (this.searchQuery) {
				this.searchQuery.startIndex = opts.startIndex;

				this.doSearch();
			}
		},

		doSearch () {
			this.working = true;
			this.resultHasError = false;

			var t = this;
			var query = this.searchQuery.toString();

			this.lastQuery = query;

			Services.search(query).then(function(data) {
				t.working = false;
				t.result = data;
			}).catch(function(e) {
				t.working = false;
				t.resultHasError = true;
			});
		}
	},

	components: {
		SearchBox,
		ResultBox
	}
}
</script>

<style>

#app {
	margin: 20px 5% 20px 5%;
}

#app-name {
	padding-top: 50px;
}

</style>
