import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;
import java.util.*;
public class Five {
    

	//Each function takes only one argument
	String read_file(String path_to_file) throws IOException{
		File file = new File(path_to_file);
		
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = br.readLine();
		while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = br.readLine();
        }
		String data=sb.toString();
		
		return data;
	}
	
	
	String filter_chars_and_normalize(String str_data) {
		str_data=str_data.replaceAll("'s","");
        str_data=str_data.toLowerCase().replaceAll("[^a-zA-Z0-9\\\\s+]"," ");
      
        return str_data;
	}
	
	
	List<String> scan(String str_data) {		
		List<String> words=new ArrayList<>();
		 StringTokenizer stringTokenizer=new StringTokenizer(str_data," ");
         while(stringTokenizer.hasMoreTokens())
         {
        	 words.add(stringTokenizer.nextToken());
         }
         
         return words;
	}
	
	public static Function<List<String>, Function<String , List<String> >> remove_stop_words() {
	    return new Function<List<String>,Function<String,List<String> >>(){

			@Override
			public Function<String, List<String>> apply(List<String> word_list) {
				
				return new Function<String,List<String>>(){

					@Override
					public List<String> apply(String pathName) {
						
						File file = new File(pathName);  
						Scanner sc = null;
						try {
							sc = new Scanner(file);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						String[] stopWords= null;
						HashMap<String,Integer> stopWordMap=new HashMap<>();
						
						while (sc.hasNext()) { 
							stopWords=sc.nextLine().split(",");
							}
						
						for(int i=0;i<stopWords.length;i++) {
							if(stopWordMap.containsKey(stopWords[i])) {
								stopWordMap.put(stopWords[i], stopWordMap.get(stopWords[i])+1 );
							}
							else {
								stopWordMap.put(stopWords[i], 1);
							}
						}
						Iterator itr = word_list.iterator(); 
				        while (itr.hasNext()) {
				        	String iteratorString = (String)itr.next(); 
				        	if(stopWordMap.containsKey(iteratorString)) {
				        		itr.remove();
				        	}
				        }
				        
				        return word_list;
					}
					
				};
			}
	    	
	    };
	}
	            
	
	HashMap<String,Integer> frequencies(List<String> word_list) {	
		HashMap<String, Integer> frequencyHashMap=new HashMap<>();
		for(int i=0;i<word_list.size();i++) {
			if(frequencyHashMap.containsKey(word_list.get(i))) {
				frequencyHashMap.put(word_list.get(i), frequencyHashMap.get(word_list.get(i))+1 );
			}
			else {
				frequencyHashMap.put(word_list.get(i), 1);
			}
		}
		
		return frequencyHashMap;
	}
	
	List<ArrayList<Object>> sort(HashMap<String, Integer> word_freq) {
		List<ArrayList<Object>> outerList=new ArrayList<ArrayList<Object>>();
		ArrayList<Object> innerList=new ArrayList<>();
		
		List<Map.Entry<String, Integer>> list =
                new ArrayList<Map.Entry<String, Integer>>(word_freq.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
		
		
		Map<String, Integer> finalFrequencyHashMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : list) {
            finalFrequencyHashMap.put(entry.getKey(), entry.getValue());
        }
		finalFrequencyHashMap.remove("s");
		
		for (Map.Entry<String, Integer> entry : finalFrequencyHashMap.entrySet()) {
			innerList.add(entry.getKey());
			innerList.add(entry.getValue());
            
            outerList.add(new ArrayList<>(innerList));
            innerList.clear();
        }
		return outerList;		
	}
	
	void print_all(List<ArrayList<Object>> word_freqs) {
		
		int count=0;
		
		while(count<25) {        	
            System.out.println(word_freqs.get(count).get(0)+"  "+"-"+"  "
                    + word_freqs.get(count).get(1));
            count++;      
		}
	}
	
	//Currying in the function calls
	public static void main(String args[]) throws IOException {
		Five obj=new Five();		
		obj.print_all(obj.sort(obj.frequencies(obj.remove_stop_words().apply(obj.scan(obj.filter_chars_and_normalize(obj.read_file(args[0])))).apply(args[1]))));
	}

}

