import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

class ActiveThread extends Thread{
	
	HashSet<String> stopWords;
	ConcurrentLinkedQueue<String> word_space;
	ConcurrentLinkedQueue<ConcurrentHashMap<String,Integer>> freq_space;
	boolean stop;
	
	
	ActiveThread(HashSet<String> stopWords, ConcurrentLinkedQueue<String> word_space, ConcurrentLinkedQueue<ConcurrentHashMap<String,Integer>> freq_space){
		this.stopWords = stopWords;
		this.word_space = word_space;
		this.freq_space = freq_space;
		this.stop = false;
		
		this.start();
	}
	
	public void run() {
		process_words();
	}
	
	public void process_words() {
	
		ConcurrentHashMap<String,Integer> word_freqs = new ConcurrentHashMap<>();
		while(!this.stop) {
			 if(word_space.isEmpty()) {
				 this.stop = true;
				 break;
			 }
			
			String word=word_space.poll();
			if((!stopWords.contains(word)) && word!=null) {
//				if(word.length()>1&&word_freqs.containsKey(word)) {
//					word_freqs.put(word, word_freqs.get(word)+1);
//				}
//				else {
//					word_freqs.put(word, 1);
				
//				}
//				System.out.println(word_freqs);
//				System.out.println(word);


				try
				{
					word_freqs.put(word, word_freqs.getOrDefault(word, 0)+1);
				}
				catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();

				}
				

			
				
			}
		}
		freq_space.add(word_freqs);
	}
}

class ActiveThreadFrequency extends Thread{
	ConcurrentLinkedQueue<ConcurrentHashMap<String,Integer>> freq_space;
	ConcurrentHashMap<String,Integer> wordFreqs;
	boolean stop1;
	
	public ActiveThreadFrequency(ConcurrentLinkedQueue<ConcurrentHashMap<String,Integer>> freq_space, ConcurrentHashMap<String,Integer> wordFreqs) {
		this.freq_space = freq_space;
		this.wordFreqs = wordFreqs;
		this.stop1 = false;
		
		this.start();
	}
	
	public void run() {
		frequencyMerge();
	}
	
	public void frequencyMerge() {
		
		ConcurrentHashMap<String,Integer> freqs=new ConcurrentHashMap<>();
		while(!this.stop1) {
			if(freq_space.isEmpty()) {
				 this.stop1 = true;
				 break;
			 }
		
			freqs = freq_space.poll();
			
			for (Map.Entry<String,Integer> entry : freqs.entrySet()) {
				int count=0;
				
				if(entry.getKey().length()>1&&wordFreqs.containsKey(entry.getKey())) {
				
					count = entry.getValue()+wordFreqs.get(entry.getKey());
				}
				else {
					count = entry.getValue();
				}
				wordFreqs.put(entry.getKey(), count);
			}
		}		
	}	
}

public class TwentyNine{
	
	private HashSet<String> stopWords;
	private ConcurrentLinkedQueue<String> word_space;
	private ConcurrentLinkedQueue<ConcurrentHashMap<String,Integer>> freq_space;
	private ConcurrentHashMap<String,Integer> wordFreqs;
	
	TwentyNine(){
		stopWords = new HashSet<>();
		word_space = new ConcurrentLinkedQueue<>();
		freq_space = new ConcurrentLinkedQueue<>();
		wordFreqs = new ConcurrentHashMap<>();
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		TwentyNine obj = new TwentyNine();
		StringBuilder text1 = new StringBuilder();
		try {
          BufferedReader reader1 = new BufferedReader(new FileReader("../stop_words.txt"));
          String line1;
          while ((line1 = reader1.readLine()) != null) {
              text1.append(line1).append("\n");
          }
          reader1.close();
		} catch (Exception e) {
          e.printStackTrace();
		}
		String[] stopWordsText = text1.toString().split(",");
		List<String> stopWordsWords = Arrays.asList(stopWordsText);
		String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");
	    List<String> alpha = Arrays.asList(alphabet);
	    obj.stopWords.addAll(stopWordsWords);
	    obj.stopWords.addAll(alpha);
	    
		
//     ********
		
		StringBuilder text2 = new StringBuilder();
		String data;
		String[]  wordList;
		try
		{
          BufferedReader reader2 = new BufferedReader(new FileReader(args[0]));
          String line2;
          while ((line2 = reader2.readLine()) != null)
          {
              text2.append(line2).append("\n");
          }
          reader2.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		data = text2.toString().replaceAll("[^A-Za-z0-9]+", " ");
		data = data.toLowerCase();
		wordList = data.split(" ");
		
		for(int i=0;i<wordList.length;i++) {
			obj.word_space.add(wordList[i]);
		}
		
//		*****
		
		CopyOnWriteArrayList<ActiveThread> workers = new CopyOnWriteArrayList<>();
		for(int i=0;i<5;i++) {
			workers.add(new ActiveThread(obj.stopWords,obj.word_space,obj.freq_space));
		}
		
		for(ActiveThread t: workers) {
			t.join();
		}
		
	
		CopyOnWriteArrayList<ActiveThreadFrequency> freqWorkers = new CopyOnWriteArrayList<>();
		for(int i=0;i<5;i++) {
			freqWorkers.add(new ActiveThreadFrequency(obj.freq_space,obj.wordFreqs));
		}
		
		for(ActiveThreadFrequency t1: freqWorkers) {
			t1.join();
		}
		

		List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(obj.wordFreqs.entrySet());
		
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
        
		
		for (Map.Entry<String, Integer> reEntry : sortedMap.entrySet()) {
            System.out.println(reEntry.getKey()+"  "+"-  "+ reEntry.getValue());
		}		
	}
}
