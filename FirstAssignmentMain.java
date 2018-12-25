import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

public class FirstAssignmentMain {
	
	public HashMap<String,Integer> computeWordFrequency(Set<String> mySetStop) throws IOException {
		
		HashMap<String,Integer> hashMap=new HashMap<>();
		File readFile = new File("output.txt");
		FileReader fileReader = new FileReader(readFile);
		BufferedReader bufferedReader = new BufferedReader(fileReader);
		String line;
		
		while ((line = bufferedReader.readLine()) != null) {
			
			if(!mySetStop.contains(line)&&!hashMap.containsKey(line)) {
				hashMap.put(line, 1);
			}
			else
			if(!mySetStop.contains(line)&&hashMap.containsKey(line)) 
			{
				hashMap.put(line, hashMap.get(line)+1);
			}
		}
		
		bufferedReader.close();
		
		return hashMap;
		
		
	}
	
	public Map<String,Integer> sortByValues(HashMap<String,Integer> unSortedHashMap){
		
      
		List<Map.Entry<String, Integer>> list =
                new LinkedList<Map.Entry<String, Integer>>(unSortedHashMap.entrySet());
		
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
	
	public <K, V> void printMap(Map<K, V> map) {
	    
		int count=0;
		
        for (Map.Entry<K, V> entry : map.entrySet()) {
        	count++;
            System.out.println(entry.getKey()+"  "+"-"+"  "
                    + entry.getValue());
            if(count==25) {
            	break;
            }     
		}
    }
    public static void main(String args[]) throws IOException {
	
	   FirstAssignmentMain obj=new FirstAssignmentMain();
	   File file = new File("../stop_words.txt");  
	   Scanner sc = new Scanner(file); 	
       String[] stopWords= null;
		
	   while (sc.hasNext()) { 
		  stopWords=sc.nextLine().split(",");
		} 
	
	   Set<String> mySetStop = new HashSet<String>(Arrays.asList(stopWords)); 
	
	   try{
		  String outfilename = "output.txt"; 
    	  File file2 =new File(outfilename);
    	  if(file2.exists()){
    		  file2.delete();}
    	  file2.createNewFile();
    	
          File file1 = new File(args[0]);
          BufferedReader br = new BufferedReader(new FileReader(file1));
          String st;
          FileWriter tokData = new FileWriter(outfilename,true);
          BufferedWriter tokDataB = new BufferedWriter(tokData);
            
          if (file1.length() != 0)
          {
            while ((st = br.readLine()) != null)
            {   
                if (st != null && !st.equals(""))
                {
                    st=st.replaceAll("'s","");
                    st=st.toLowerCase().replaceAll("[^a-zA-Z0-9\\\\s+]"," ");
                }
                StringTokenizer stringTokenizer=new StringTokenizer(st," ");
                while(stringTokenizer.hasMoreTokens())
                {     	
                    tokDataB.write(stringTokenizer.nextToken());
                    tokDataB.write("\n");         
                }
            }
          }
          else
          {
            System.out.println("File is Empty");
            System.exit(0);
          }
          tokDataB.close();   
          br.close();
        }

        catch (FileNotFoundException e1){
            System.out.println("File does not exists");
            e1.printStackTrace();
            
        } catch (IOException e) {
            e.printStackTrace();
    }
    
	HashMap<String,Integer> hashMap=obj.computeWordFrequency(mySetStop);
	
	Map<String,Integer> sortedHashMap=obj.sortByValues(hashMap);
	
	obj.printMap(sortedHashMap);
	        
}
	
}
       
      

	
	
	
	

