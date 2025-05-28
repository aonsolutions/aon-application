/**
 * 
 */


export const AonStringUtils = {
	
	/**
	 * Get normalized text (no accents)
	 * @param text
	 * @return the text without accents
	 */
	normalized: function ( text = '') {

		const sensible = [
				"\u00C1",
				"\u00C9",
				"\u00CD",
				"\u00D3",
				"\u00DA",
				"\u00D1",
				
				"\u00E1",
				"\u00E9",
				"\u00ED",
				"\u00F3",
				"\u00FA",
				"\u00F1",
				
		];
		const normalized = ["A","E","I","O","U","N","a","e","i","o","u","n"];
		
		for (let i = 0; i < normalized.length; i++) {
			text = text.replaceAll(sensible[i], normalized[i]);
		}
		
		return text;
	},

	/**
	 * Returns a phrase matching with typo tolerance
	 * @param text
	 * @param searcher
	 * @param tolerance
	 * @return
	 */
	getMatching: function (text = '', searcher = '', tolerance = 2) {
		text = text.trim();
		searcher = searcher.trim();
		
		const index = text.toUpperCase().indexOf(searcher.toUpperCase());
		if(index >= 0) {
			return [text.substring(index, index + searcher.length )];
		}
		
		let matches = new Array();

		let words = searcher.split(/\s+/);
		
		for (let word of words) {
			this.getMatchingWord(text, word, tolerance)?.forEach( w => matches.push(w));
		}
		
		return matches;
	},

	/**
	 * Returns a word matching with typo tolerance
	 * @param text The text to search in
	 * @param searcher The word to search for
	 * @param tolerance The tolerance to use 
	 * @return
	 */
	getMatchingWord: function (text = '', searcher ='', tolerance = 2) {
		text = text.trim();
		searcher = searcher.trim();

		let normalizedText = this.normalized(text.toUpperCase());
		let normalizedSearcher = this.normalized(searcher.toUpperCase());
		
		const index = normalizedText.indexOf(normalizedSearcher);
		if(index >= 0) {
			return [text.substring(index, index + searcher.length )];
		}
		
		let matches = new Array();
		const words = text.split(/\s+/);
		for (let word of words) {
			
			let normalizedWord = this.normalized(word.toUpperCase());

			let currentDistance = this.getLevenshteinDistance(searcher, normalizedWord);
			let realTolerance = tolerance;
			
			if(currentDistance < realTolerance) {
				matches.push(word);
			}
		
		}
		
		return matches;
	},

	/**
	 * Returns if a text contains a phrase with typo tolerance
	 * @param text
	 * @param searcher
	 * @param tolerance
	 * @return
	 */
	containsMatching: function (text = '', searcher = '', tolerance = 2) {
		text = text.trim();
		searcher = searcher.trim();
		
		if(text.toUpperCase().includes(searcher.toUpperCase())) {
			return true;
		}
		
		const words = searcher.split(/\s+/);
		
		for (let word of words) {		
			 if ( !this.containsMatchingWord(text, word, tolerance) ) {
				 return false;
			 }
		}
		
		return true;
	},

	/**
	 * Returns if a text contains a word with typo tolerance
	 * @param text The text to search in
	 * @param searcher The word to search for
	 * @param tolerance The tolerance to use 
	 * @return
	 */
	containsMatchingWord: function (text = '', searcher ='', tolerance = 2) {
		text = text.trim();
		searcher = searcher.trim();

		text = this.normalized(text.toUpperCase());
		searcher = this.normalized(searcher.toUpperCase());
		
		if(text.includes(searcher)){
			return true;
		}

		const words = text.split(/\s+/);
		for (let word of words) {
			
			
			let currentDistance = this.getLevenshteinDistance(searcher, word);
			let realTolerance = tolerance;
			
			if(currentDistance < realTolerance) {
				return true;
			}
		
		}
		
		return false;
	},

		/**
	 * <p>
	 * Find the Levenshtein distance between two Strings.
	 * </p>
	 *
	 * <p>
	 * This is the number of changes needed to change one String into another,
	 * where each change is a single character modification (deletion, insertion
	 * or substitution).
	 * </p>
	 *
	 * <pre>
	 * AonStringUtils.getLevenshteinDistance(null, *) = IllegalArgumentException
	 * AonStringUtils.getLevenshteinDistance(*, null) = IllegalArgumentException
	 * AonStringUtils.getLevenshteinDistance("","") = 0
	 * AonStringUtils.getLevenshteinDistance("","a") = 1
	 * AonStringUtils.getLevenshteinDistance("aaapppp", "") = 7
	 * AonStringUtils.getLevenshteinDistance("frog", "fog") = 1
	 * AonStringUtils.getLevenshteinDistance("fly", "ant") = 3
	 * AonStringUtils.getLevenshteinDistance("elephant", "hippo") = 7
	 * AonStringUtils.getLevenshteinDistance("hippo", "elephant") = 7
	 * AonStringUtils.getLevenshteinDistance("hippo", "zzzzzzzz") = 8
	 * AonStringUtils.getLevenshteinDistance("hello", "hallo") = 1
	 * </pre>
	 *
	 * @param s
	 *            the first String, must not be null
	 * @param t
	 *            the second String, must not be null
	 * @return result distance
	 */
	getLevenshteinDistance: function ( s = '' , t = '' ) {
		
		let n = s.length;
		let m = t.length;
		
		if ( n == 0 ){
			return m;
		} else if ( m == 0 ){
			return n;
		}
		if (n > m) {
			// swap the input strings to consume less memory
			let tmp = s;
			s = t;
			t = tmp;
			n = m;
			m = t.length;
		}
		let p = new Array(n + 1); // 'previous' cost array, horizontally
		let d = new Array(n + 1); // cost array, horizontally
		let _d = []; // placeholder to assist in swapping p and d
		// indexes into strings s and t
		let i; // iterates through s
		let j; // iterates through t
		let t_j; // jth character of t
		let cost; // cost
		for (i = 0; i <= n; i++) {
			p[i] = i;
		}
		for (j = 1; j <= m; j++) {
			t_j = t.charAt(j - 1);
			d[0] = j;
			for (i = 1; i <= n; i++) {
				cost = s.charAt(i - 1) == t_j ? 0 : 1;
				// minimum of cell to the left+1, to the top+1, diagonally left
				// and up +cost
				d[i] = Math.min(Math.min(d[i - 1] + 1, p[i] + 1), p[i - 1]
						+ cost);
			}
			// copy current distance counts to 'previous row' distance counts
			_d = p;
			p = d;
			d = _d;
		}
		// our last action in the above loop was to switch d and p, so p now
		// actually has the most recent cost counts
		return p[n];

	}
}
	
