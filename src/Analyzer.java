
public class Analyzer {
	public static void main(String[] args) {
		// --- for debug ---
		String[] newarg = {
			"-l",
			"data/test.cpp"
		};
		args = newarg;
		// -----------------
		
		if(args.length==0){
			usage();
			return;
		}
		if(args[0].equals("-l")){
			if(args.length==1){
				System.out.println("Pleas add arg of input file: like 'sample.cpp'");
				return;
			}
			Lexer lex = new Lexer(args[1]);
			lex.outputResult(args[1]+"out.txt");
		}
	}
	
	static void usage(){
		String out = "You need some args.";
		System.out.println(out);
	}
}
