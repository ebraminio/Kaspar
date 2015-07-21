package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

	public static String in(){
		try{
			System.out.print("> ");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			return in.readLine();
		}catch(Exception e){return "";}
	}
	
	public static String implode(Object[] o, String glue) {
		String s = "";
		for(int i = 0; i < o.length; i++){
			s += o[i];
			if(o.length-i > 1)
				s += glue;
		}
		return s;
	}

	public static int indexOf(Pattern pattern, String s) {
	    Matcher matcher = pattern.matcher(s);
	    return matcher.find() ? matcher.start() : -1;
	}
}
