
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Four {
	//Class Members which are accessible to its member functions
	private List<Character> data;
	private List<String> words;
	private HashMap<String, Integer> frequencyHashMap;
	private HashMap<String, Integer> finalFrequencyHashMap;
	
	//Default constructor to initialise the member variables
	Four(){
		data=new ArrayList<>();
		words=new ArrayList<>();
		frequencyHashMap=new HashMap<>();
		finalFrequencyHashMap=new LinkedHashMap<>();
	}
	
	//Every method does not return any value as expected
	void read_file(String path_to_file) throws IOException{
		File file = new File(path_to_file);
        FileInputStream fis = new FileInputStream(file);
        char current;
        if (file.length() != 0)
        {
        while (fis.available() > 0) {
             current = (char) fis.read();
             data.add(current);   
        }
    }

}
	
	void filter_chars_and_normalize() {
		for(int i=0;i<data.size();i++) {
			if(Character.toString(data.get(i)).matches("[^a-zA-Z0-9\\\\s+]")) {
				data.set(i, ' ');
			}
			else {
				data.set(i, Character.toLowerCase(data.get(i)));
			}
		}		
	}
	
	void scan() {
		StringBuilder builder = new StringBuilder(data.size());
		for(int i=0;i<data.size();i++) {
			builder.append(data.get(i));
		}
		String stringToTokenize=builder.toString();
		 StringTokenizer stringTokenizer=new StringTokenizer(stringToTokenize," ");
         while(stringTokenizer.hasMoreTokens())
         {
        	 words.add(stringTokenizer.nextToken());
         }
	}
	
	void remove_stop_words() throws IOException {
		File file = new File("../stop_words.txt");  
		Scanner sc = new Scanner(file); 
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
		
		Iterator itr = words.iterator(); 
        while (itr.hasNext()) {
        	String iteratorString = (String)itr.next(); 
        	if(stopWordMap.containsKey(iteratorString)) {
        		itr.remove();
        	}
        }		
	}
	
	void frequencies() {		
		for(int i=0;i<words.size();i++) {
			if(frequencyHashMap.containsKey(words.get(i))) {
				frequencyHashMap.put(words.get(i), frequencyHashMap.get(words.get(i))+1 );
			}
			else {
				frequencyHashMap.put(words.get(i), 1);
			}
		}
	}
	
	void sort() {
		List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(frequencyHashMap.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
		
		
        for (Map.Entry<String, Integer> entry : list) {
            finalFrequencyHashMap.put(entry.getKey(), entry.getValue());
        }
         finalFrequencyHashMap.remove("s");
             
	}
	
	public static void main(String args[]) throws IOException {
		Four obj=new Four();		
		obj.read_file(args[0]);
		obj.filter_chars_and_normalize();
		obj.scan();
		obj.remove_stop_words();
		obj.frequencies();
		obj.sort();
		
		int count=0;
				
        for (Map.Entry<String, Integer> entry : obj.finalFrequencyHashMap.entrySet()) {
        	count++;
            System.out.println(entry.getKey()+"  "+"-"+"  "
                    + entry.getValue());
            if(count==25) {
            	break;
            }        
		}
	}
}

