import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class TokenAnalyzer {
	HashSet<String> keyword;
	List<String> keywordList;
	public TokenAnalyzer(List<String> keywordList) {
		if(keywordList.isEmpty()){
			System.err.println("Error: keywordList must have at least one word.");
		}
		this.keywordList = keywordList;
		keyword = new HashSet<>();
		for(String word: keywordList){
			keyword.add(word);
		}
	}
	
	List<Integer> countTokens(List<String> tokens){
		MultiSet<String> counter = new MultiSet<>();
		for(String t: tokens){
			if(keyword.contains(t)){
				counter.add(t);
			}
		}
		List<Integer> result = new ArrayList<>();
		for(String key: keywordList){
			result.add(counter.get(key));
		}
		return result;
	}
}

class MultiSet<T> extends HashMap<T, Integer> {
	@Override
	public Integer get(Object key) {
		return containsKey(key) ? super.get(key) : 0;
	}

	public void add(T key, int v) {
		put(key, get(key) + v);
	}

	public void add(T key) {
		put(key, get(key) + 1);
	}

	public void sub(T key) {
		final int v = get(key);
		if (v == 1)
			remove(key);
		else
			put(key, v - 1);
	}
}