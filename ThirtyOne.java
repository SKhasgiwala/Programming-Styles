import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class TemporaryMethods{
	
	List<String> scan(String str_data) {
		String data;
		data = str_data.replaceAll("[^A-Za-z0-9]+", " ");
		data = data.toLowerCase();
		String[] stringArray = data.split(" ");
		return Arrays.asList(stringArray);
	}
	
	List<String> remove_stop_words(List<String> word_list){
		File file=new File("../stop_words.txt");
		List<String> finalList = new ArrayList<>();
		List<String> stopWordsFinal = new ArrayList<>();
		String[] stop_words = null;
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
		 List<String> stopWordsWords = Arrays.asList(stop_words);
		 String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");
		 List<String> alpha = Arrays.asList(alphabet);
		 stopWordsFinal.addAll(stopWordsWords);
		 stopWordsFinal.addAll(alpha);
		 HashSet<String> stopSet = new HashSet<>(stopWordsFinal);
		 for(String s:word_list) {
			 if(!stopSet.contains(s)) {
				 finalList.add(s);
			 }
		 }
		 return finalList;		 
	}
	
//	int add(int x,int y) {
//		return x+y;
//	}
}

public class ThirtyOne {
	
	static TemporaryMethods tempObj;
	Queue<String> queue = new LinkedList<>();
	HashMap<String,Integer> globalMap = new HashMap<>();
	
	public ThirtyOne() {
		tempObj = new TemporaryMethods();
	}
	
	Queue<String> partition(String data_str,int nLines) {
		String[] lines;
		String queueString="";
		lines=data_str.split("\n");
		for(int i=0;i<lines.length;i++) {
			queueString=queueString+" "+lines[i];
			if(i!=0&&i%200==0) {
				queue.add(queueString);
				queueString="";
			}	
		}
		return queue;
	}
		
	static List<HashMap<String,Integer>> split_words(String data_str) {
		List<HashMap<String,Integer>> result = new ArrayList<>();
		
		List<String> words = new ArrayList<>();
		words=tempObj.remove_stop_words(tempObj.scan(data_str));
		for(String w:words) {
			HashMap<String,Integer> tempMap = new HashMap<>();
			if(w.length()>1) {
			tempMap.put(w, 1);
			result.add(tempMap);}
		}
		return result;		
	}
	
	List<List<HashMap<String,Integer>>> regroup(List<List<HashMap<String,Integer>>> pairs_list) {

		List<List<HashMap<String,Integer>>> outerList = new ArrayList<>();
		List<HashMap<String,Integer>> inner1 = new ArrayList<>();
		List<HashMap<String,Integer>> inner2 = new ArrayList<>();
		List<HashMap<String,Integer>> inner3 = new ArrayList<>();
		List<HashMap<String,Integer>> inner4 = new ArrayList<>();
		List<HashMap<String,Integer>> inner5 = new ArrayList<>();
		
		for(List<HashMap<String,Integer>> pairs:pairs_list) {
			for(HashMap<String,Integer> p:pairs) {
				for ( String key : p.keySet()) {
					if(key.charAt(0)>='a'&&key.charAt(0)<='e') {		
						inner1.add(p);
					}
					if(key.charAt(0)>='f'&&key.charAt(0)<='j') {
						inner2.add(p);
					}
					if(key.charAt(0)>='k'&&key.charAt(0)<='o') {
						inner3.add(p);
					}
					if(key.charAt(0)>='p'&&key.charAt(0)<='t') {
						inner4.add(p);
					}
					if(key.charAt(0)>='u'&&key.charAt(0)<='z') {
						inner5.add(p);
					}					
				}
			}
		}
		
		outerList.add(inner1);
		outerList.add(inner2);
		outerList.add(inner3);
		outerList.add(inner4);
		outerList.add(inner5);
		
//		
		return outerList;
	}
	
	HashMap<String,Integer> count_words(List<HashMap<String,Integer>> mapping) {
		
		for(HashMap<String,Integer> map:mapping) {
			for ( String key : map.keySet()) {
				globalMap.put(key, globalMap.getOrDefault(key, 0)+1);
			}
		}
		
		return globalMap;		
	}
	
	String read_file(String path_to_file) {
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
	    String data = sb.toString();
	    return data;
	}
	
	HashMap<String,Integer> sort() {
		List<Map.Entry<String, Integer>> list =
              new LinkedList<Map.Entry<String, Integer>>(globalMap.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
          public int compare(Map.Entry<String, Integer> o1,
                             Map.Entry<String, Integer> o2) {
              return (o2.getValue()).compareTo(o1.getValue());
          }
		});

		HashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public static void main(String[] args) {
		ThirtyOne thOne = new ThirtyOne();
		Queue<String> finalQueue = new LinkedList<>();
		List<List<HashMap<String,Integer>>> splits;
		List<List<HashMap<String,Integer>>> splits_per_word = new ArrayList<>();
		List<HashMap<String,Integer>> finalMapList = new ArrayList<>();
		
		
		String data = thOne.read_file(args[0]);		
		finalQueue=thOne.partition(data, 200);
				
		ArrayList<String> block = new ArrayList<>(finalQueue);

		splits= new ArrayList(block.stream().map(ThirtyOne::split_words).collect(Collectors.toCollection(ArrayList::new)));
		
		splits_per_word=thOne.regroup(splits);
		for(List<HashMap<String,Integer>> list : splits_per_word) {
			thOne.count_words(list);
		}
		
		int count=0;
		Map<String, Integer> sortedMap = thOne.sort();
		for (Map.Entry<String, Integer> reEntry : sortedMap.entrySet()) {
			if(count>24) {
				break;
			}
            System.out.println(reEntry.getKey()+"  "+"-  "+ reEntry.getValue());
            count++;
		}
	}
}
