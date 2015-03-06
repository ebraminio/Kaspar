package util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Util {

	public static String in(){
		try{
			System.out.print("> ");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			return in.readLine();
		}catch(Exception e){return "";}
	}
}
