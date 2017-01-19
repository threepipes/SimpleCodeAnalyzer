import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
			in =new BufferedReader(
					new InputStreamReader(
							new FileInputStream(filename)));
		} catch (FileNotFoundException e) {
			System.err.println("No such file: "+ filename);
		}
	}
	
	public List<String> getTokenList(){
		try{
			StringBuilder sb = new StringBuilder();
			for(String line=in.readLine(); line!=null; line=in.readLine()){
				sb.append(line).append('\n');
			}
			in.close();
			return lexer(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void outputResult(String outfile){
		try{
			StringBuilder sb = new StringBuilder();
			for(String line=in.readLine(); line!=null; line=in.readLine()){
				sb.append(line).append('\n');
			}
			in.close();
			List<String> tokens = lexer(sb.toString());
			PrintWriter out = new PrintWriter(
					new BufferedWriter(
							new FileWriter(outfile)));
			for(String t: tokens){
				out.println(t);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
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
