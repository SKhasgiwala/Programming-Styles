
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Seventeen {
	//Class Members which are accessible to its member functions
	private List<Character> data;
	private List<String> words;
	private HashMap<String, Integer> frequencyHashMap;
	private HashMap<String, Integer> finalFrequencyHashMap;
	
	//Default constructor to initialise the member variables
	Seventeen(){
		data=new ArrayList<>();
		words=new ArrayList<>();
		frequencyHashMap=new HashMap<>();
		finalFrequencyHashMap=new LinkedHashMap<>();
	}
	
	//Every method does not return any value as expected
	public void read_file(String path_to_file) throws IOException{
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
	
	public void filter_chars_and_normalize() {
		for(int i=0;i<data.size();i++) {
			if(Character.toString(data.get(i)).matches("[^a-zA-Z0-9\\\\s+]")) {
				data.set(i, ' ');
			}
			else {
				data.set(i, Character.toLowerCase(data.get(i)));
			}
		}
		 
	}
	
	public void scan() {
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
	
	public void remove_stop_words() throws IOException {
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
	
	public void frequencies() {		
		for(int i=0;i<words.size();i++) {
			if(frequencyHashMap.containsKey(words.get(i))) {
				frequencyHashMap.put(words.get(i), frequencyHashMap.get(words.get(i))+1 );
			}
			else {
				frequencyHashMap.put(words.get(i), 1);
			}
		}
	}
	
	public void sort() {
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
	
	public static void main(String args[]) throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException {
		
		Seventeen seventeen = new Seventeen();
		Class className = Seventeen.class;
		Method readFile = className.getMethod("read_file" , String.class );
		Method filterCharsAndNormalize = className.getMethod("filter_chars_and_normalize" , null );
		Method scan = className.getMethod("scan" , null );
		Method removeStopWords = className.getMethod("remove_stop_words" , null );
		Method frequency = className.getMethod("frequencies" , null );
		Method sort = className.getMethod("sort" , null);
		
		readFile.invoke(seventeen, args[0]);
		filterCharsAndNormalize.invoke(seventeen, null);
		scan.invoke(seventeen, null);
		removeStopWords.invoke(seventeen, null);
		frequency.invoke(seventeen, null);
		sort.invoke(seventeen, null);
		
		int count=0;
		Field field = className.getDeclaredField("finalFrequencyHashMap");
		field.setAccessible(true);
		HashMap<String,Integer> dfinalFrequencyHashMap = (HashMap) field.get(seventeen);
		//Class<?> finalFrequencyHashMap=field.getType();
		
		for (Map.Entry<String, Integer> entry : dfinalFrequencyHashMap.entrySet()) {
			count++;
			System.out.println(entry.getKey()+"  "+"-"+"  "
					+ entry.getValue());
			if(count==25) {
				break;
			}        
		}
				
		
	}
	
	
}

