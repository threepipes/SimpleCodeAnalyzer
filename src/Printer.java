import java.io.Closeable;
import java.io.IOException;
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
	public void print(HashMap<String, Integer> map);
}

class JsonPrinter implements Printer, Closeable{
	PrintWriter out;
	public JsonPrinter(PrintStream stream) {
		out = new PrintWriter(stream);
	}
	@Override
	public void print(List list) {
		out.println(list);
	}
	
	@Override
	public void print(HashMap<String, Integer> map) {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		for(Entry<String, Integer> e: map.entrySet()){
			if(sb.length()>1) sb.append(",");
			sb.append(String.format("\"%s\": %d", e.getKey(), e.getValue()));
		}
		sb.append("}");
		out.println(sb.toString());
	}
	@Override
	public void close() throws IOException {
		out.close();
	}
}

class StdPrinter implements Printer, Closeable{
	PrintWriter out;
	public StdPrinter(PrintStream stream) {
		out = new PrintWriter(stream);
	}
	@Override
	public void print(List list) {
		out.println(list);
	}
	@Override
	public void print(HashMap<String, Integer> map) {
		for(Entry<String, Integer> e: map.entrySet()){
			out.println(e.getKey()+"\t: "+e.getValue());
		}
	}
	@Override
	public void close() throws IOException {
		out.close();
	}
}