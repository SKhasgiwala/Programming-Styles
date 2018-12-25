import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import java.util.function.Function;


public class Fourteen {

	public static void main(String args[]) {
		
		WordFrequencyFramework wfapp=new WordFrequencyFramework();
		StopWordFilter stop_word_filter=new StopWordFilter(wfapp);
		DataStorage data_storage=new DataStorage(wfapp,stop_word_filter);
		WordFrequencyCounter word_freq_counter=new WordFrequencyCounter(wfapp, data_storage);
		CountWordWithZ countWordWithZ=new CountWordWithZ(wfapp,word_freq_counter);
		wfapp.run(args[0]);
		
	}
	
}

 class WordFrequencyFramework<T>{
	 
	List<T> load_event_handlers;
	List<T> dowork_event_handlers;
	List<T> end_event_handlers;
	
	public WordFrequencyFramework(){
		load_event_handlers=new ArrayList<>();
		dowork_event_handlers=new ArrayList<>();
		end_event_handlers=new ArrayList<>();
	}
	
	void register_for_load_event(Function<T,T> handler) {
		this.load_event_handlers.add((T) handler);
	}
	
	void register_for_dowork_event(T handler) {
		this.dowork_event_handlers.add((T) handler);
	}
	
	void register_for_end_event(T handler) {
		this.end_event_handlers.add((T) handler);
	}
	
	void run(String path_to_file) {
		for(int i=0;i<load_event_handlers.size();i++) {
			((Function<String,Object>)load_event_handlers.get(i)).apply(path_to_file);
		} 
		for(int i=0;i<dowork_event_handlers.size();i++) {
			((Function<Void,Void>)dowork_event_handlers.get(i)).apply(null);
		}
		for(int i=0;i<end_event_handlers.size();i++) {
			((Function<Void,Void>)end_event_handlers.get(i)).apply(null);
		}
	}	
}
 
 class DataStorage<T>{
	 String data = "";
	 StopWordFilter stop_word_filter;
	 List<T> word_event_handlers;
	 
	 public DataStorage(WordFrequencyFramework wfapp,StopWordFilter stop_word_filter) {
		this.stop_word_filter=stop_word_filter;
		word_event_handlers=new ArrayList<>();
		wfapp.register_for_load_event(this.load);
		wfapp.register_for_dowork_event(this.produce_words);
	}
	 
	 public Function<String, Void> load = (String path_to_file) -> {
		 StringBuilder sb = new StringBuilder();
	        try
	        {
	            BufferedReader reader = new BufferedReader(new FileReader(path_to_file));
	            String line;
	            while ((line = reader.readLine()) != null)
	            {
	                sb.append(line).append("\n");
	            }
	            reader.close();
	        }
	        catch (Exception e)
	        {
	            e.printStackTrace();
	        }
	        data = sb.toString().replaceAll("[^A-Za-z0-9]+", " ");
	        data = data.toLowerCase();
	        return null;
	    };
	 
	 public Function<Void,Void> produce_words=(Void) -> {
		 String[] wordList;
		 wordList=data.split(" ");
		 for(int i=0;i<wordList.length;i++) {
			 if(!this.stop_word_filter.is_stop_word(wordList[i])) {
				 for(T h : word_event_handlers) {
					 ((Function<String,Object>) h).apply(wordList[i]);
				 }
			 }
		 }
		 return null;
	 };
	 
	public void register_for_word_event(Function<T,T> handler) {
		 this.word_event_handlers.add((T) handler);
	 }
	 
 }
 

 class StopWordFilter<T>{
	 String[] stop_words;
		 
	 public StopWordFilter(WordFrequencyFramework wfapp) {
		
	 wfapp.register_for_load_event(this.load);
	}
	 
	  public Function<String,Void> load = (String ignore) -> {
		 File file=new File("../stop_words.txt");
		 Scanner sc = null;
		try {
			sc = new Scanner(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 while(sc.hasNext()) {
			 stop_words=sc.nextLine().split(",");
		 }
		 for(int i=0;i<stop_words.length;i++) {
			 stop_words[i]=stop_words[i].toLowerCase();
		 }
		 List<String> stop_words_list = Arrays.asList(stop_words);
		 return null;
	 };
	 
	 boolean is_stop_word(String word) {
		 Set<String> mySetStop=new HashSet<String>(Arrays.asList(stop_words));
		 return mySetStop.contains(word);
	 }
 }
 
 class WordFrequencyCounter{
	 HashMap<String,Integer> word_freqs;
	 
	 public WordFrequencyCounter(WordFrequencyFramework wfapp,DataStorage data_storage) {
		 word_freqs=new HashMap<>();
		data_storage.register_for_word_event(this.increment_count);
		wfapp.register_for_end_event(this.print_freqs);
	}
	 
	 public Function<String,Void> increment_count=(String word)-> {
		if(!word.equals("s")&&word_freqs.containsKey(word)) {
			 word_freqs.put(word, word_freqs.get(word)+1);
		 }
		 if(!word.equals("s")&&!word_freqs.containsKey(word)) {
			 word_freqs.put(word, 1);
		 }
		 return null;
	 };
	 
	 public Function<Void,Void> print_freqs=(Void)-> {
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
	        
	        int count=0;
			
	        for (Map.Entry<String, Integer> reEntry : sortedMap.entrySet()) {
	        	count++;
	            System.out.println(reEntry.getKey()+"  "+"-"+"  "
	                    + reEntry.getValue());
	            if(count==25) {
	            	break;
	            }
	        
			}
	        return null;
 };
 }
 
 class CountWordWithZ{
	 WordFrequencyCounter wordFrequencyCounter;
	 
	 CountWordWithZ(WordFrequencyFramework wfapp,WordFrequencyCounter wordFrequencyCounter){
		 this.wordFrequencyCounter=wordFrequencyCounter;
		 wfapp.register_for_end_event(this.calculateWordWithZ);
		}
	 
	 public Function<Void,Void> calculateWordWithZ = (Void) -> {
		 
		 int countWords = 0;
		 for (Map.Entry<String, Integer> Entry : wordFrequencyCounter.word_freqs.entrySet()) {
			 if(Entry.getKey().indexOf("z")!=-1) {
				 countWords++;
			 }
		 }
		 System.out.println("The number of words with the letter \"z\" is: "+ countWords);
		 return null;
		 
	 };
	 
 }