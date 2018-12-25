package Week7.plugins1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import Week7.TFFreqs;

public class frequencies1 implements TFFreqs {

	
	public Map<String,Integer> top25(String[] wordList) {
		HashMap<String,Integer> word_freqs = new HashMap<>();

		for(int i=0;i<wordList.length;i++) {
			if(word_freqs.containsKey(wordList[i])) {
				word_freqs.put(wordList[i], word_freqs.get(wordList[i])+1);
			}
			else {
				word_freqs.put(wordList[i], 1);
			}
		}
		
		List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(word_freqs.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
		
		
		int count=0;
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
        	count++;
        	if(count>=26) break;
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        
        return sortedMap;
		
	}
	
	public static void main(String[] args) {
		frequencies1 obj=new frequencies1();		
	}
}
