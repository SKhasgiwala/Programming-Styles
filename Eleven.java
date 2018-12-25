import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

public class Eleven {
    
public static void main(String args[]) throws IOException {
    
	WordFrequencyController wfController=new WordFrequencyController();
	String[] arr= {"init",args[0]};
	String[] fArr= {"run"};
	wfController.dispatch(arr);
	wfController.dispatch(fArr);
	
    }
}

class DataStorageManager {
	
	String data;
	List<String> list;
	
	Object dispatch(String[] arr1) throws IOException {
		if(arr1[0]=="init") {
			 init(arr1[1]);
		}
		else if(arr1[0]=="words") {
			return words();
		}
		else {
			throw new java.lang.Error("Message not understood "+arr1[0]);
		}
		return null;
	}
	
	void init(String path_to_file) throws IOException {
		data = new String ( Files.readAllBytes( Paths.get(path_to_file) ) );
		data=data.replaceAll("'s","");
		data=data.toLowerCase().replaceAll("[^a-zA-Z0-9\\\\s+]"," ");
	
        
     
}
	
	List<String> words() throws IOException {
		List<String> list=new ArrayList<>();
		StringTokenizer stringTokenizer=new StringTokenizer(data," ");
      while(stringTokenizer.hasMoreTokens())
      {
      	
      	list.add(stringTokenizer.nextToken());
      	        
      }
		 
		 
	
		return list;
	
}
}
class StopWordManager{
	String[] stop_words;
	
	
	boolean dispatch(String[] arr2) throws FileNotFoundException {
		if(arr2[0]=="init") {
			init();
		}
		else if(arr2[0]=="is_stop_word") {
			return is_stop_word(arr2[1]);
		}
		else {
			throw new java.lang.Error("Message not understood "+arr2[0]);
		}
		return true;
	}
	
	void init() throws FileNotFoundException {
		File file = new File("../stop_words.txt");  
		Scanner sc = new Scanner(file); 	
	    		
		   while (sc.hasNext()) { 
			  stop_words=sc.nextLine().split(",");
			}
		   
	}
	
	boolean is_stop_word(String word) {
		Set<String> mySetStop = new HashSet<String>(Arrays.asList(stop_words)); 
		return mySetStop.contains(word);
	}
}

class WordFrequencyManager{
	
	HashMap<String,Integer> word_freqs;
	public WordFrequencyManager() {
		word_freqs=new HashMap<>();
	}
	
	Map<String,Integer> dispatch(String[] message) {
		if(message[0]=="increment_count") {
			 increment_count(message[1]);
		}
		else if(message[0]=="sorted") {
			return sorted();
		}
		else {
			throw new java.lang.Error("Message not understood "+message[0]);
		}
		return null;
	}
	
	void increment_count(String word) {
		if(word_freqs.containsKey(word)) {
			word_freqs.put(word, word_freqs.get(word)+1);
		}
		else {
			word_freqs.put(word, 1);
		}
	
	
	}
	
	Map<String,Integer> sorted() {
		List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(word_freqs.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
		
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
		
		return sortedMap;
	}
	
	
}

class WordFrequencyController{
	DataStorageManager storage_manager;
	StopWordManager stop_word_manager;
	WordFrequencyManager word_freq_manager;
	
	void dispatch(String[] arr) throws IOException{
		if(arr[0]=="init") {
			init(arr[1]);
		}
		else if(arr[0]=="run"){
			run();
		}
		else {
			throw new java.lang.Error("Message not understood "+arr[0]);
		}
	}
	
	void init(String path_to_file) throws IOException {
		storage_manager=new DataStorageManager();
		stop_word_manager=new StopWordManager();
		word_freq_manager=new WordFrequencyManager();
		String[] arr1= {"init",path_to_file};
		String[] arr2= {"init"};
		storage_manager.dispatch(arr1);
		stop_word_manager.dispatch(arr2);
	}
	
	void run() throws IOException {
		String[] arr3= {"words"};
		
		List<String> word_list=(List<String>) storage_manager.dispatch(arr3);
		for(String w : word_list) {
			String[] arr4= {"is_stop_word",w};
			boolean a=(boolean) stop_word_manager.dispatch(arr4);
			if(!a) {
				String[] arr5= {"increment_count",w};
				word_freq_manager.dispatch(arr5);
			}
		}
		String[] arr6= {"sorted"};
		HashMap<String,Integer> sortedMap=(HashMap<String, Integer>) word_freq_manager.dispatch(arr6);
		int count=0;
		
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
        	count++;
            System.out.println(entry.getKey()+"  "+"-"+"  "
                    + entry.getValue());
            if(count==25) {
            	break;
            }     
		}
	}
}
