import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Analyzer {
	static boolean debug = true;
	public static void main(String[] args) {
		// --- for debug ---
		if(debug){
			String[] newarg = {
//					"-l",
					"data/example.cpp"
			};
			args = newarg;
		}
		// -----------------
		
		if(args.length==0){
			usage();
			return;
		}
		mkdir();
		boolean lexOnly = false;
		boolean fileList = false;
		String lang = "cpp";
		String filename = args[args.length-1];
		String keywordFile = "keyword";
		boolean test = false;
		boolean json = false;
		int i;
		for(i=0; i<args.length; i++){
			switch(args[i]){
			case "-test":
				test = true;
				break;
			case "-l":
				lexOnly = true;
				break;
			case "-s":
				fileList = true;
				break;
			case "-k":
				i++;
				if(i>=args.length-1){
					System.err.println("Error: Wrong arg size. -k option must have filename.");
					return;
				}
				keywordFile = args[i];
				break;
			case "-j":
				json = true;
				break;
			default:
				break;
			}
		}
		List<FileData> files;
		if(fileList){
			files = loadFilenames(filename);
		}else{
			files = new ArrayList<>();
			files.add(new FileData(filename, lang));
		}
		TokenAnalyzer ta = null;
		List<String> keywords = loadKeywords(keywordFile);
		if(!lexOnly){
			ta = new TokenAnalyzer(keywords);
		}
		for(FileData file: files){
			if(test){
				test(file.filename);
				continue;
			}
			List<String> tokens = lexerWithResult(file);
			if(!lexOnly){
				List<Integer> count = ta.countTokens(tokens);
				outputCountResult(keywords, count);
			}
		}
	}
	
	static void test(String file){
		Lexer lex = new Lexer(file);
		lex.test();
	}
	
	static void outputCountResult(List<String> keywords, List<Integer> count){
		if(keywords.size() != count.size()){
			System.err.println("Error: keywords size or counter size are wrong.");
			return;
		}
		final int size = keywords.size();
		System.out.println("output results...");
		for(int i=0; i<size; i++){
			System.out.printf("%s\t: %d\n", keywords.get(i), count.get(i));
		}
		System.out.println("finish.\n");
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
		File fo = new File(filename);
		Lexer lex = new Lexer(filename);
		lex.outputResult("data/"+fo.getName()+"_out.txt");
	}

	static List<String> lexerWithResult(FileData file){
		String filename = file.getFilename();
		Lexer lex = new Lexer(filename);
		return lex.getTokenList();
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
					System.err.println("Error: wrong file line: "+line);
					continue;
				}
				list.add(new FileData(data[0], data[1]));
			}
			in.close();
		} catch (FileNotFoundException e) {
			System.err.println("Error file not found: "+filelist);
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
			System.err.println("Error file not found: "+filename);
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
				"  -k <keyword_filename>: input keyword file";
		System.out.println(out);
	}
}

class FileData{
	String filename, lang;
	FileData(String filename, String lang){
		this.filename = filename;
		this.lang = lang;
	}
	String getFilename(){
		return filename.toString();
	}
	String getLang(){
		return lang.toString();
	}
}