import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/*
 *  output data for some purpose: for human, json, and so on..
 */
public interface Printer {
	public void print(List list);
	public void print(String name, HashMap<String, Integer> map);
	public void close();
}

class JsonPrinter extends PrintWriter implements Printer {
//	boolean first = true;
	public JsonPrinter(PrintStream stream) {
		super(stream);
//		print("[");
	}
	@Override
	public void print(List list) {
//		if(first) first = false;
//		else print(",");
		println(list);
	}
	
	@Override
	public void print(String name, HashMap<String, Integer> map) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("{\"src_name\": \"%s\"", name));
		for(Entry<String, Integer> e: map.entrySet()){
			sb.append(String.format(", \"%s\": %d", e.getKey(), e.getValue()));
		}
		sb.append("}");
//		if(first) first = false;
//		else print(",");
		println(sb.toString());
//		flush();
	}
	@Override
	public void close() {
//		println("]");
		super.close();
	}
}

class StdPrinter extends PrintWriter implements Printer{
	public StdPrinter(PrintStream stream) {
		super(stream);
	}
	@Override
	public void print(List list) {
		println(list);
	}
	@Override
	public void print(String name, HashMap<String, Integer> map) {
		println("output results of file: "+name+" ...");
		for(Entry<String, Integer> e: map.entrySet()){
			println(e.getKey()+"\t: "+e.getValue());
		}
	}
}