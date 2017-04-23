import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Analyzer {
	static boolean debug = false;
	static boolean lexOnly = false;
	static boolean fileList = false;
	static boolean test = false;
	static boolean json = false;
	static boolean noerror = false;
	static boolean compareFiles = false;
	static int ngram = 0;

	static String lang = "cpp";
	static String keywordFile = "keyword";
	
	static Printer printer;
	
	public static void main(String[] args) {
		// --- for debug ---
		if(debug){
			String[] newarg = {
					"-j",
					"-l",
					"C:/Work/Python/Lab/CodeForcesCrawler/data/src/.ahuja_22153882_253.src"
			};
			args = newarg;
		}
		// -----------------
		
		if(args.length==0){
			usage();
			return;
		}
//		mkdir();
		int i;
		out:for(i=0; i<args.length; i++){
			switch(args[i]){
			case "-test":
				test = true;
				break;
			case "-l":
				lexOnly = true;
				break;
			case "-comp":
				compareFiles = true;
				break;
			case "-s":
				fileList = true;
				break;
			case "-k":
				i++;
				if(i>=args.length-1){
					log("Error: Wrong arg size. -k option must have filename.");
					return;
				}
				keywordFile = args[i];
				break;
			case "-j":
				json = true;
				break;
			case "-e":
				noerror = true;
				break;
			case "--ngram":
				i++;
				if(i>=args.length-1){
					log("Error: Wrong arg size. -k option must have filename.");
					return;
				}
				ngram = Integer.parseInt(args[i]);
				break;
			default:
				break out;
			}
		}
		if(i!=args.length-1){
			log("Error: Wrong arg size.");
			return;
		}
		if(json){
			printer = new JsonPrinter(System.out);
		}else{
			printer = new StdPrinter(System.out);
		}
		if(ngram > 0){
			ngramDistance(args[i], ngram);
		}else if(compareFiles){
			List<List<FileData>> filelists = makeFileLists(args[i]);
			printDistances(filelists);
		}else{
			List<FileData> files = makeFileList(args[i]);
			doAnalyze(files);
		}
		printer.close();
	}
	
	static void doAnalyze(List<FileData> files){
		TokenAnalyzer ta = null;
		if(!lexOnly){
			List<String> keywords = loadKeywords(keywordFile);
			ta = new TokenAnalyzer(keywords);
		}
		for(FileData file: files){
			if(test){
				test(file.getFile());
				continue;
			}
			List<String> tokens = lexerWithResult(file);
			if(debug) System.out.println(tokens);
			if(!lexOnly){
				HashMap<String, Integer> count = ta.countTokens(tokens);
				printer.print(file.getFilename(), count);
			}
		}
	}
	
	static final String whitelist = "whitelist.txt";
	static void ngramDistance(String listname, int n){
		List<String> filelist = new ArrayList<>();
		try(BufferedReader in = new BufferedReader(new FileReader(new File(listname)))){
			for(String line = in.readLine(); line != null; line = in.readLine()){
				filelist.add(line);
			}
			NGram ngram = new NGram(whitelist);
			double[][] dist = ngram.getDistanceTable(filelist, n);
			for(double[] row : dist){
				printer.print(row);
			}
		}catch(FileNotFoundException e){
			System.err.println("File not found: " + listname);
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static void printDistances(List<List<FileData>> filelists){
		List<List<Integer>> result = calcDistances(filelists);
		if(!test) for(List<Integer> list: result) printer.print(list);
	}
	
	static List<List<Integer>> calcDistances(List<List<FileData>> filelists){
		List<List<Integer>> result = new ArrayList<>();
		for(List<FileData> list: filelists){
			List<Integer> diffs = new ArrayList<>();
			if(list.size()<2){
				result.add(diffs);
				continue;
			}
			String[] preToken = null;
			for(FileData file: list){
				if(preToken == null){
					preToken = toArray(lexerWithResult(file));
					continue;
				}
				String[] tokens = toArray(lexerWithResult(file));
				LevenshteinDistance<String> dist = new LevenshteinDistance<>(preToken, tokens, 1, 1, 2);
				diffs.add(dist.getDist());
			}
			result.add(diffs);
		}
		return result;
	}
	
	static String[] toArray(List<String> list){
		return list.toArray(new String[list.size()]);
	}
	
	// C++であることを仮定(後程変更が必要)
	static List<List<FileData>> makeFileLists(String filename){
		List<List<FileData>> filelists = new ArrayList<>();
		try {
			BufferedReader in =
					new BufferedReader(
					new InputStreamReader(
					new FileInputStream(filename)
					));
			
			for(String line=in.readLine();
					line!=null; line=in.readLine()){
				String[] data = line.split(" ");
				List<FileData> list = new ArrayList<>();
				try{
					for(String file: data){
						list.add(new FileData(file, "C++"));
					}
				}catch(FileNotFoundException e){
					log("Failed to find file: "+data[0]);
					continue;
				}
				filelists.add(list);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return filelists;
	}
	
	static List<FileData> makeFileList(String filename){
		List<FileData> files;
		if(fileList){
			files = loadFilenames(filename);
		}else{
			files = new ArrayList<>();
			try{
				files.add(new FileData(filename, lang));
			}catch(FileNotFoundException e){
				log("Failed to find file: "+filename);
			}
		}
		return files;
	}
	
	static void test(File file){
		Lexer lex = new Lexer(file);
		lex.test();
	}
	
	static void mkdir(){
		String dirname = "data";
		File dir = new File(dirname);
		if(!dir.exists()){
			dir.mkdirs();
		}
	}
	
	static void lexer(FileData file){
		String filename = file.getFilename();
		Lexer lex = new Lexer(file.getFile());
		lex.outputResult("data/"+filename+"_out.txt");
	}

	static List<String> lexerWithResult(FileData file){
//		String filename = file.getFilename();
		if(test) System.err.println(file.getFilename());
		Lexer lex = new Lexer(file.getFile());
		List<String> result = lex.getTokenList();
		if(result.isEmpty()) System.err.println("Token list is empty!");
		return result;
	}
	
	static List<FileData> loadFilenames(String filelist){
		/*
		 * file format (each line):
		 * <filepath> <lang>
		 */
		List<FileData> list = new ArrayList<>();
		try {
			BufferedReader in =
					new BufferedReader(
					new InputStreamReader(
					new FileInputStream(filelist)
					));
			
			for(String line=in.readLine();
					line!=null; line=in.readLine()){
				String[] data = line.split(" ");
				if(data.length!=2){
					log("Error: wrong file line: "+line);
					continue;
				}
				try{
					list.add(new FileData(data[0], data[1]));
				}catch(FileNotFoundException e){
					log("Failed to find file: "+data[0]);
					continue;
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	static List<String> loadKeywords(String filename){
		List<String> list = new ArrayList<>();
		try {
			BufferedReader in =
					new BufferedReader(
					new InputStreamReader(
					new FileInputStream(filename)
					));
			for(String line=in.readLine();
					line!=null; line=in.readLine()){
				list.add(line);
			}
			in.close();
		} catch (FileNotFoundException e) {
			log("Error file not found: "+filename);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	static void usage(){
		String out =
				"Analyzer [-option] <filename>" +
				"  -l: only use lexer(default)" +
				"  -s: input file list" +
				"  -k <keyword_filename>: input keyword file" +
				"  -j: output with json-like format";
		System.err.println(out);
	}
	
	static void log(String str){
		if(noerror) return;
		System.err.println(str);
	}
}

class FileData{
	String lang;
	File file;
	FileData(String filename, String lang) throws FileNotFoundException{
		file = new File(filename);
		if(!file.exists()){
			throw new FileNotFoundException();
		}
		this.lang = lang;
	}
	File getFile(){
		return file;
	}
	String getFilename(){
		return file.getName();
	}
	String getLang(){
		return lang.toString();
	}
}