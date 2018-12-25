

	import java.io.BufferedReader;
	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileReader;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.nio.file.Files;
	import java.nio.file.Paths;
	import java.util.*;
	import java.util.concurrent.ConcurrentLinkedQueue;
	import java.util.concurrent.CopyOnWriteArrayList;


	public class TwentyEight {		
		public static void main(String args[]) throws IOException, InterruptedException {		
			WordFrequencyController wfcontroller = new WordFrequencyController();
			StopWordManager stop_word_manager=new StopWordManager();
								
			List<Object> temp0 =new CopyOnWriteArrayList<>();
			temp0.add("init");
			temp0.add(wfcontroller);
			send(stop_word_manager, temp0);
			
			DataStorageManager storage_manager = new DataStorageManager();
			List<Object> temp1 =new ArrayList<>();
			temp1.add("init");
			temp1.add(args[0]);
			temp1.add(stop_word_manager);
			send(storage_manager, temp1);
			
		
			List<Object> temp2 =new ArrayList<>();
			temp2.add("run");
			temp2.add(storage_manager);
			send(wfcontroller, temp2);
			
//			System.out.println("code ends");
			List<ActiveWFObject> activeWFObjects = new CopyOnWriteArrayList<>();
	      
	        activeWFObjects.add(stop_word_manager);
	        activeWFObjects.add(wfcontroller);
	        activeWFObjects.add(storage_manager);
			for(ActiveWFObject T: activeWFObjects){
	            T.join();
	        }
		}
		
		public static void send(Object receiver, List<Object> message) {
			
			((ActiveWFObject)receiver).queue.add(message);
			
	}
	}
	
	
	 class ActiveWFObject extends Thread {
		 
		 boolean stop;
		 Queue<List<Object>> queue;
		 String name;
		 
		 
		public ActiveWFObject() {
			
			this.queue = new LinkedList<>();
			this.name = this.getClass().getName();
			this.stop=false;
				
//			System.out.println("Thread started:");
//			System.out.println(this.getClass());
			this.start();	
		}
		
		@Override
		public void run() {
			while(!this.stop) {
//				System.out.println("Run method called"+this.getClass());
				List<Object> message =  queue.poll();
				System.out.print("\u0000");
//				System.out.println("Queue size"+queue.size());
				if(message!=null)
				{
				    //if(message.get(0) != "filter" && message.get(0) != "word") System.out.println("**************Message is : "+message+ "Name: "+this.name);
					try {
						this.dispatch((List<Object>) message);
					} catch ( Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(message.get(0)=="die") {
					    
						this.stop= true;
					}
				}
						
			}
		}
		
		public void dispatch(List<Object> message) throws Exception {
		    }
		
		
	}
	
	
	class DataStorageManager extends ActiveWFObject {
		
		String data;
		Object stop_word_manager;
		
		
		@Override
		public void dispatch(List<Object> message) throws IOException {
			if( message.get(0)=="init") {
				this.init( new CopyOnWriteArrayList<>(message.subList(1, message.size())));
			}
			else if(message.get(0)=="send_word_freqs") {
				this.process_words(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
			}
			else {
				TwentyEight.send(stop_word_manager, message);
			}
		}

		
		void init(List<Object> message) throws IOException {
			String path_to_file = (String)message.get(0);
//			for(int i=0;i<1000;i++) {
//				System.out.println(i);
//			}
			stop_word_manager= message.get(1);
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
	        
		}
		
		void process_words(List<Object> message) throws IOException {
			Object recipient = message.get(0);		
			List<String> list=new ArrayList<>();
			StringTokenizer stringTokenizer=new StringTokenizer(data," ");
			while(stringTokenizer.hasMoreTokens())
			{  	
				list.add(stringTokenizer.nextToken());    	        
			}
			for(String w:list) {
				CopyOnWriteArrayList<Object> temp1 = new CopyOnWriteArrayList<>();
				temp1.add("filter");
				temp1.add(w);			
				TwentyEight.send(stop_word_manager, temp1 );
			}
			List<Object> temp2 = new ArrayList<>();
			temp2.add("top25");
			temp2.add(recipient);
			TwentyEight.send(stop_word_manager, temp2);
		
		}
	}
	
	
	class StopWordManager extends ActiveWFObject {
		String[] stop_words;
		Object word_freq_manager;
		
		@Override
		public void dispatch(List<Object> message) throws FileNotFoundException  {
			if( message.get(0)=="init") {
				
				this.init(new CopyOnWriteArrayList<>(message.subList(1, message.size())) );
			}
			else if( message.get(0)=="filter") {
				this.filter( new CopyOnWriteArrayList<>(message.subList(1, message.size())));
			}
			else {
				TwentyEight.send(word_freq_manager, message);
			}
		}
		
		
		void init(List<Object> message) throws FileNotFoundException {
			File file = new File("../stop_words.txt");  
//			for(int i=0;i<1000;i++) {
//				System.out.println(i);
//			}
			Scanner sc = new Scanner(file); 	
		    		
			   while (sc.hasNext()) { 
				  stop_words=sc.nextLine().split(",");
				}
//			 System.out.println(Arrays.asList(stop_words));
			word_freq_manager = message.get(0);		
			   
		}
		
		void filter(List<Object> message) {
			String word = (String)message.get(0);
			Set<String> mySetStop = new HashSet<String>(Arrays.asList(stop_words)); 
			List<Object> temp2 = new ArrayList<>();
			temp2.add("word");
			temp2.add(word);
			if(!mySetStop.contains(word)) {
				TwentyEight.send(word_freq_manager,temp2);
			}			
		}
	}


	
	class WordFrequencyController extends ActiveWFObject {	
		Object storage_manager;
		HashMap<String,Integer> word_freqs = new HashMap<>();
		
		
		public void dispatch(List<Object> message) throws IOException  {
			if( message.get(0)=="run") {
				this.run( new CopyOnWriteArrayList<>(message.subList(1, message.size())));
			}
			else if( message.get(0)=="top25_controller") {
				this.display( new CopyOnWriteArrayList<>(message.subList(1, message.size())));
			}
			else if( message.get(0)=="word") {
				this.increment_count(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
			}
			else if( message.get(0)=="top25") {
				this.top25(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
			}
			else {
				throw new java.lang.Error("Message not understood "+message.get(0));
			}
		}
		
		
		void run(List<Object> message) throws IOException {
//			for(int i=0;i<1000;i++) {
//				System.out.println(i);
//			}
			storage_manager = message.get(0);
			List<Object> temp5=new ArrayList<>();
			temp5.add( "send_word_freqs");
			temp5.add( this);
			TwentyEight.send(storage_manager, temp5);		
		}
		
		void display(List<Object> message) {
			HashMap<String,Integer> sortedMap=new HashMap<>();
			sortedMap=(HashMap<String, Integer>) message.get(0);
			int count=0;
			for (Map.Entry<String, Integer> reEntry : sortedMap.entrySet()) {
	        	count++;
	            System.out.println(reEntry.getKey()+"  "+"-"+"  "
	                    + reEntry.getValue());
	            if(count==25) {
	            	break;
	            }        
			}
			List<Object> temp6=new ArrayList<>();
			temp6.add("die");
			TwentyEight.send(storage_manager, temp6);
			this.stop=true;
		}
			
		void increment_count(List<Object> message) {
			String word = (String) message.get(0);
			if(!word.equals("s")&&word_freqs.containsKey(word)) {
				word_freqs.put(word, word_freqs.get(word)+1);
			}
			if(!word.equals("s")&&!word_freqs.containsKey(word)) {
				word_freqs.put(word, 1);
			}		
		}
		
		void top25(List<Object> message) {
			Object recipient = message.get(0);
			List<Map.Entry<String, Integer>> list =
	                new LinkedList<Map.Entry<String, Integer>>(word_freqs.entrySet());
			
			Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
	            @Override
				public int compare(Map.Entry<String, Integer> o1,
	                               Map.Entry<String, Integer> o2) {
	                return (o2.getValue()).compareTo(o1.getValue());
	            }
	        });
			Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
	        for (Map.Entry<String, Integer> entry : list) {
	            sortedMap.put(entry.getKey(), entry.getValue());
	        }
	        List<Object> temp4=new ArrayList<>();
	        temp4.add("top25_controller");
	        temp4.add(sortedMap);
	        TwentyEight.send( recipient,temp4 );		
		}		
}
