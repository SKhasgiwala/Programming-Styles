import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;


import java.util.*;
public class Eight
{		
	public static void main(String args[]) throws IOException {
		ReadFile obj=new ReadFile();
		obj.call(args[0], new FilterChars() );
	}
}

interface IFunction{
	void call(Object arg,IFunction func) throws FileNotFoundException;
}

class ReadFile implements IFunction{
		public void call(Object arg,IFunction func) throws FileNotFoundException {
			String path_to_file=(String)arg;
			File file = new File(path_to_file);
			
			StringBuilder sb = new StringBuilder();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line=null;
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            try {
					line = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
			String data=sb.toString();
			
			func.call(data, new Normalize());
		}
		
	}
	
	class FilterChars implements IFunction{
		public void call(Object arg,IFunction func) throws FileNotFoundException {
			String str_data=(String)arg;
			str_data=str_data.replaceAll("'s","");
	        str_data=str_data.replaceAll("[^a-zA-Z0-9\\\\s+]"," ");
	        
			func.call(str_data, new Scan());
			
		}
	}
	
	class Normalize implements IFunction{
		public void call(Object arg,IFunction func) throws FileNotFoundException {
			String str_data=(String)arg;
			str_data=str_data.toLowerCase();
			func.call(str_data, new RemoveStopWords());
		}
	}
	
	class Scan implements IFunction{
		public void call(Object arg,IFunction func) throws FileNotFoundException {
			String str_data=(String)arg;
			List<String> words=new ArrayList<>();
			 StringTokenizer stringTokenizer=new StringTokenizer(str_data," ");
	         while(stringTokenizer.hasMoreTokens())
	         {
	        	 words.add(stringTokenizer.nextToken());
	         }
	         
	         func.call(words, new Frequencies());
			
		}
	}
	
	class RemoveStopWords implements IFunction{
		public void call(Object arg,IFunction func) throws FileNotFoundException {
			List<String> words=(ArrayList<String>)arg;
			File file = new File("../stop_words.txt");  
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
			
			Iterator itr = words.iterator(); 
	        while (itr.hasNext()) {
	        	String iteratorString = (String)itr.next(); 
	        	if(stopWordMap.containsKey(iteratorString)) {
	        		itr.remove();
	        	}
	        }	
	        func.call(words, new Sort());
		}
	}
	
	class Frequencies implements IFunction{
		public void call(Object arg,IFunction func) throws FileNotFoundException {
		List<String> word_list=(List<String>)arg;
		HashMap<String, Integer> frequencyHashMap=new HashMap<>();
		for(int i=0;i<word_list.size();i++) {
			if(frequencyHashMap.containsKey(word_list.get(i))) {
				frequencyHashMap.put(word_list.get(i), frequencyHashMap.get(word_list.get(i))+1 );
			}
			else {
				frequencyHashMap.put(word_list.get(i), 1);
			}
		}
		func.call(frequencyHashMap, new Print_All());
		
		}
	}
	            
	class Sort implements IFunction{
		public void call(Object arg,IFunction func) throws FileNotFoundException {
		HashMap<String,Integer> word_freq=(HashMap<String,Integer>)arg;
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
		func.call(outerList, new No_Op());
		}
	}
	
	class Print_All implements IFunction{
		public void call(Object arg,IFunction func) throws FileNotFoundException {
		List<ArrayList<Object>> word_freqs=(List<ArrayList<Object>>)arg;
		int count=0;
		
		while(count<25) {        	
            System.out.println(word_freqs.get(count).get(0)+"  "+"-"+"  "
                    + word_freqs.get(count).get(1));
            count++;      
		}
	}
}
	
	class No_Op implements IFunction{
		public void call(Object arg,IFunction func) throws FileNotFoundException {
			return;
		}
	}
	
	



