import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

public class Fifteen {
		
	public static void main(String args[]) {
		EventManager em = new EventManager();
		DataStorage dataStorage = new DataStorage(em);
		StopWordFilter stopWordFilter = new StopWordFilter(em);
		WordFrequencyCounter wordFrequencyCounter = new WordFrequencyCounter(em);
		WordFrequencyApplication wordFrequencyApplication =new WordFrequencyApplication(em);
		
		ArrayList<String> array = new ArrayList<>();
		array.add("run");
		array.add(args[0]);
		em.publish(array);
		WordWithZ wordWithZ=new WordWithZ(em,wordFrequencyCounter);
		}
}

class EventManager<T>{
	
	HashMap<String,List<T>> subscriptions;
	
	
	public EventManager() {
		subscriptions = new HashMap<>();
		
	}
	
	void subscribe(String event_type , Function<T,T> handler) {
		
		if(subscriptions.containsKey(event_type)) {
			subscriptions.get(event_type).add((T)handler);
			
		}
		else {
			List<T> innerList = new ArrayList<>();
			innerList.add((T)handler);
			subscriptions.put(event_type, innerList);
		}
	}
	
	void publish(ArrayList<String> event) {
		String event_type=event.get(0);
//		System.out.println(subscriptions.get(event_type));
		if(subscriptions.containsKey(event_type)) {
			for(T h: subscriptions.get(event_type)){
                ((Function<T, T>) h).apply((T) event);
            }
			
		}
	}
}

class DataStorage{
	EventManager em;
	String data;
	
	public DataStorage(EventManager em) {
		this.em = em;
		this.em.subscribe("load",this.load);
		this.em.subscribe("start",this.produce_words);
	}
	
	Function<List,Void> load=(List event) -> {
		String path_to_file = (String)event.get(1);
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
	
	Function<List,Void> produce_words=(List event)->{
		String[] wordList;
		 wordList=data.split(" ");
		 
		 for(int i=0;i<wordList.length;i++) {
			 String[] temp = {"word",wordList[i]};
			 ArrayList<String> array3= new ArrayList<>(Arrays.asList(temp));
			 this.em.publish(array3);
			 
		 }
		 String[] temp2= {"eof",null};
		 ArrayList<String> array4= new ArrayList<>(Arrays.asList(temp2));
		 this.em.publish(array4);
		 return null;
	};
}

class StopWordFilter {
	String[] stop_words;
	List<String> stop_word_list;
	EventManager em;
	
	public StopWordFilter(EventManager em) {
		stop_word_list=new ArrayList<>();
		this.em=em;
		this.em.subscribe("load", this.load);
		this.em.subscribe("word", this.is_stop_word);
	}
	Function<List,Void> load=(List event)-> {
		File file=new File("../stop_words.txt");
		 Scanner sc = null;
		try {
			sc = new Scanner(file);
			while(sc.hasNext()) {
				
				stop_words=sc.nextLine().split(",");
			 }
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
		 
		 for(int i=0;i<stop_words.length;i++) {
			 stop_words[i]=stop_words[i].toLowerCase();
		 }
		 
		 stop_word_list = Arrays.asList(stop_words);
		 
	     
		 return null;
		
	};
	
	Function<List,Void> is_stop_word=(List event)-> {
		String word=(String)event.get(1);
		if(!stop_word_list.contains(word)) {
			String[] temp= {"valid_word",word};
			 ArrayList<String> array5= new ArrayList<>(Arrays.asList(temp));
			this.em.publish(array5);
		}
		return null;
	};
}

class WordFrequencyCounter{
	HashMap<String,Integer> word_freqs;
	EventManager em;
	public WordFrequencyCounter(EventManager em) {
		word_freqs=new HashMap<>();
		this.em=em;
		this.em.subscribe("valid_word",this.increment_count);
		this.em.subscribe("print", this.print_freqs);
	}
	
	Function<List,Void> increment_count=(List event)-> {
		String word=(String)event.get(1);
		
		if(!word.equals("s")&&word_freqs.containsKey(word)) {
			word_freqs.put(word, word_freqs.get(word)+1);
		}
		if(!word.equals("s")&&!word_freqs.containsKey(word)) {
			word_freqs.put(word,1);
		} 
		
		return null;
	};
	
	Function<List,Void> print_freqs=(List event)-> {
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

class WordWithZ{
	EventManager em;
	WordFrequencyCounter wordFrequencyCounter;
	
	public WordWithZ(EventManager em,WordFrequencyCounter wordFrequencyCounter) {
		this.em=em;
		this.wordFrequencyCounter=wordFrequencyCounter;
		this.em.subscribe("wordWithZ", this.wordWithZFunction);
		String[] temp7= {"wordWithZ",null};
		ArrayList<String> array7= new ArrayList<>(Arrays.asList(temp7));
		this.em.publish(array7);
		
	}
	
	Function<ArrayList<String>,Void> wordWithZFunction =(ArrayList<String> event)->{
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

class WordFrequencyApplication{
	EventManager em;
	public WordFrequencyApplication(EventManager em) {
		this.em=em;
		em.subscribe("run", this.run);
		em.subscribe("eof", this.stop);
	}
	
	Function<List,Void> run =(List event) -> {
		String path_to_file = (String) event.get(1);
		
		String[] temp= {"load",path_to_file};
		ArrayList<String> array1= new ArrayList<>(Arrays.asList(temp));

		String[] temp1= {"start", null};
		ArrayList<String> array2= new ArrayList<>(Arrays.asList(temp1));
		this.em.publish(array1);
		this.em.publish(array2);
		return null;
	};
	
	Function<List,Void> stop =(List event) ->{
		
		String[] temp= {"print",null};
		ArrayList<String> array6= new ArrayList<>(Arrays.asList(temp));
		this.em.publish(array6);
		return null;
	};
}

