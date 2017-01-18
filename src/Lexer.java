import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Token;

import cpp.CPP14Lexer;

public class Lexer {
	BufferedReader in;
	public Lexer(String filename) {
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(filename)));
		} catch (FileNotFoundException e) {
			System.err.println("No such file: "+ filename);
		}
	}
	
	public void outputResult(String outfile){
		StringBuilder sb = new StringBuilder();
		try{
			for(String line=in.readLine(); line!=null; line=in.readLine()){
				sb.append(line).append('\n');
			}
		}catch(IOException e){
			e.printStackTrace();
		}
		List<String> tokens = lexer(sb.toString());
		for(String t: tokens){
			System.out.println(t);
		}
	}
	
	private List<String> lexer(String seq){
		CPP14Lexer lex = new CPP14Lexer(new ANTLRInputStream(seq));
		CommonTokenStream cts = new CommonTokenStream(lex);
		List<String> tokens = new ArrayList<>();
		cts.fill();
		for(Token t: cts.getTokens()){
			tokens.add(t.getText());
		}
		return tokens;
	}
	
}
