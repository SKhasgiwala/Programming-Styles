package Week7.plugins1;

import java.util.Map;

import Week7.TFPrint;

public class print1 implements TFPrint{

	public void printFreqs(Map<String,Integer> wordFreqs) {
		for (Map.Entry<String, Integer> entry : wordFreqs.entrySet()) {
			System.out.println(entry.getKey()+"  "+"-"+"  "
					+ entry.getValue());       
		}
	}
	
}
