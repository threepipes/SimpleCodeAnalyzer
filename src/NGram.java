import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;

public class NGram {
	static final String REPLACE = "$";
	
	HashSet<String> whitelist;
	HashMap<String, Integer> mapping;
	List<String> table;
	public NGram(String whitelistFile) {
		whitelist = new HashSet<>();
		try(BufferedReader in = new BufferedReader(new FileReader(new File(whitelistFile)))){
			for(String line=in.readLine(); line!=null; line=in.readLine()){
				whitelist.add(line);
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + whitelistFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		initIdTable();
	}
	
	public void initIdTable(){
		mapping = new HashMap<>();
		table = new ArrayList<>();
	}
	
	private int toId(String token) {
		if(mapping.containsKey(token)){
			return mapping.get(token);
		}else{
			int id = table.size();
			mapping.put(token, id);
			table.add(token);
			return id;
		}
	}
	
	static final int BASE = 257;
	private int toHash(Queue<Integer> ids) {
		int hash = 0;
		for(int id: ids){
			hash *= BASE;
			hash += id;
		}
		return hash;
	}
	
	public double[][] getDistanceTable(List<String> filelist, int n){
		final int size = filelist.size();
		double[][] dist = new double[size][size];
		List<MultiSet<Integer>> vectors = new ArrayList<>();
		for(String filename: filelist){
			Lexer lex = new Lexer(new File(filename));
			List<String> tokens = lex.getTokenList();
			vectors.add(getVectorList(tokens, n));
//			System.err.println(filename+":"+vectors.get(vectors.size()-1).toString());
		}
//		System.err.println("table: "+mapping);
		for(int i = 0; i < size; i++){
			for(int j = i+1; j < size; j++){
				double d = getDistance(vectors.get(i), vectors.get(j));
				dist[i][j] = dist[j][i] = 1 - d;
//				if(Math.abs(i-j) < 5){
//					System.err.printf("(%s,%s)=(%f)\n", filelist.get(i), filelist.get(j), dist[i][j]);
//				}
			}
		}
		return dist;
	}
	
	private double getDistance(MultiSet<Integer> v1, MultiSet<Integer> v2){
		double numer = 0;
		double denom1 = 0, denom2 = 0;
		for(Entry<Integer, Integer> e: v1.entrySet()){
			final int id = e.getKey();
			final int freq = e.getValue();
			denom1 += freq * freq;
			numer += freq * v2.get(id);
		}
		for(int freq: v2.values()){
			denom2 += freq * freq;
		}
		double denom = Math.sqrt(denom1) * Math.sqrt(denom2);
		if(denom == 0) return 0;
		return numer / denom;
	}
	
	public MultiSet<Integer> getVectorList(List<String> tokens, int n){
		MultiSet<Integer> vector = new MultiSet<>();
		Queue<Integer> tmpVec = new ArrayDeque<>();
		for(String token: tokens){
			if(skip(token)) continue;
			if(needReplace(token)) token = REPLACE;
			final int id = toId(token);
			tmpVec.add(id);
			if(tmpVec.size() > n) tmpVec.poll();
			final int hash = toHash(tmpVec);
			vector.add(hash);
		}
		return vector;
	}
	
	private boolean skip(String token){
		return token.indexOf('#') >= 0;
	}
	
	private boolean needReplace(String token){
		if(whitelist.contains(token)) return false;
		char c = token.charAt(0);
		return c == '"' || Character.isLetterOrDigit(c);
	}
}

class NGramResult{
	List<MultiSet<Integer>> vectors;
	
}

class MultiSet<T> extends HashMap<T, Integer>{
	@Override public Integer get(Object key){return containsKey(key)?super.get(key):0;}
	public void add(T key,int v){put(key,get(key)+v);}
	public void add(T key){put(key,get(key)+1);}
	public void sub(T key){final int v=get(key);if(v==1)remove(key);else put(key,v-1);}
	public MultiSet<T> merge(MultiSet<T> set)
	{MultiSet<T>s,l;if(this.size()<set.size()){s=this;l=set;}else{s=set;l=this;}
	for(Entry<T,Integer>e:s.entrySet())l.add(e.getKey(),e.getValue());return l;}
}