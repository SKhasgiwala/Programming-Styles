package Week7;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import Week7.*;
import Week7.plugins1.*;
import Week7.plugins2.*;


public class Nineteen {
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Nineteen nineteen = new Nineteen();
		Properties prop = new Properties();
		
	String fileName = "Week7/config.properties";
		InputStream is = null;
		is = new FileInputStream(fileName);
		
		prop.load(is);
		
		ClassLoader classLoader = Nineteen.class.getClassLoader();
		HashMap<String,Integer> wordFreqs=new HashMap<>();
		String wordName=prop.getProperty("words");
		String frequencyName=prop.getProperty("frequencies");
		String printName=prop.getProperty("print");
		
		TFWords tfwords = (TFWords) classLoader.loadClass(wordName).newInstance() ;
		TFFreqs tffreqs = (TFFreqs) classLoader.loadClass(frequencyName).newInstance();
		TFPrint tfprint = (TFPrint) classLoader.loadClass(printName).newInstance();
		
		tfprint.printFreqs(tffreqs.top25(tfwords.extract_words(args[0])));
		
//		for (Map.Entry<String, Integer> entry : wordFreqs.entrySet()) {
//			System.out.println(entry.getKey()+"  "+"-"+"  "
//					+ entry.getValue());       
//		}
	}
}
