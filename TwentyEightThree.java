

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

public class TwentyEightThree {

    public static void main(String args[]) throws InterruptedException {
        Words wordsObject = new Words();
        NonStopWords nonStopWordsObject = new NonStopWords();
        CountAndSort countAndSortObject = new CountAndSort();
        PrintResults printResultsObject = new PrintResults();

        CopyOnWriteArrayList<Object> tempMessage1 = TwentyEightThree.messageCreation("init", nonStopWordsObject, countAndSortObject, args[0]);
        send(wordsObject, tempMessage1);

        CopyOnWriteArrayList<Object> tempMessage2 = TwentyEightThree.messageCreation("init", wordsObject, countAndSortObject);
        send(nonStopWordsObject, tempMessage2);

        CopyOnWriteArrayList<Object> tempMessage3 = TwentyEightThree.messageCreation("init", nonStopWordsObject, printResultsObject);
        send(countAndSortObject, tempMessage3);

        CopyOnWriteArrayList<Object> tempMessage4 = TwentyEightThree.messageCreation("init", countAndSortObject);
        send(printResultsObject, tempMessage4);

        List<ActiveWFObject2> activeWFObjects = new CopyOnWriteArrayList<>();
        activeWFObjects.add(wordsObject);
        activeWFObjects.add(nonStopWordsObject);
        activeWFObjects.add(countAndSortObject);
        activeWFObjects.add(printResultsObject);

        for (ActiveWFObject2 T : activeWFObjects) {
            T.join();
        }
    }
    
    static CopyOnWriteArrayList<Object> messageCreation(Object... args) {
        return new CopyOnWriteArrayList<>(Arrays.asList(args));
    }

    public static void send(Object receiver, List<Object> message) {
        ((ActiveWFObject2) receiver).queue.add(message);
    }
}

class ActiveWFObject2 extends Thread {
    boolean stop;
    Queue<List<Object>> queue;

    ActiveWFObject2() {
        this.queue = new LinkedList<>();
        this.stop = false;
        this.start();
    }

    @Override
    public void run() {
        while (!this.stop) {
            System.out.print("");
            List<Object> message = queue.poll();

            if (message != null) {
                if(message.get(0)=="die") {
                    this.stop = true;
                } else {
                    try {
                        this.dispatch(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void dispatch(List<Object> message) throws Exception {
    }
}

class Words extends ActiveWFObject2 {

    private ConcurrentLinkedDeque<String> words;
    private Object nonStopWords;
    private Object countAndSort;

    @Override
    public void dispatch(List<Object> message) {
        if (message.get(0).equals("init")) {
            this.init(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("get_word")) {
            this.getWord();
        } else if (message.get(0).equals("ask_for_word")) {
            this.askForWord(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("finish")) {
            this.finish();
        }
    }

    private void init(List<Object> message) {

        String path_to_file = (String) message.get(2);
        nonStopWords = message.get(0);
        countAndSort = message.get(1);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path_to_file));
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.words = new ConcurrentLinkedDeque<String>(Arrays.asList(text.toString().replaceAll("[^A-Za-z0-9]+", " ").toLowerCase().split(" ")));
    }

    private void finish() {
        CopyOnWriteArrayList<Object> tempMessage1 = TwentyEightThree.messageCreation("die");
        TwentyEightThree.send(this, tempMessage1);
    }

    private void askForWord(List<Object> message) {
        CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("get_word");
        TwentyEightThree.send(this, tempMessage);
    }

    private void getWord() {
        if (!words.isEmpty()) {
            String word = this.words.remove();
            CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("filter_words", word);
            TwentyEightThree.send(this.nonStopWords, tempMessage);
        } else {
            CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("print_final_result");
            TwentyEightThree.send(countAndSort, tempMessage);
        }
    }
}

class NonStopWords extends ActiveWFObject2 {
    private List<String> stop_words = new CopyOnWriteArrayList<>();
    private Object wordsObject;
    private Object countAndSort;

    @Override
    public void dispatch(List<Object> message) {
        if (message.get(0).equals("init")) {
            this.init(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("filter_words")) {
            this.filterStopWords(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("ask_filter_word")) {
            this.askFilterWord(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("finish")) {
            this.finish();
        }
    }

    private void init(List<Object> message) {

        this.wordsObject = message.get(0);
        this.countAndSort = message.get(1);

        StringBuilder text = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("../stop_words.txt"));
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] stopWordsText = text.toString().split(",");
        List<String> stop_words_words = Arrays.asList(stopWordsText);
        String[] alphabet = "abcdefghijklmnopqrstuvwxyz".split("");
        List<String> alpha = Arrays.asList(alphabet);
        stop_words.addAll(stop_words_words);
        stop_words.addAll(alpha);
    }

    private void finish() {
        CopyOnWriteArrayList<Object> tempMessage1 = TwentyEightThree.messageCreation("die");
        TwentyEightThree.send(this, tempMessage1);

        CopyOnWriteArrayList<Object> tempMessage2 = TwentyEightThree.messageCreation("finish");
        TwentyEightThree.send(this.wordsObject, tempMessage2);
    }

    private void askFilterWord(List<Object> message) {
        CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("ask_for_word");
        TwentyEightThree.send(wordsObject, tempMessage);
    }

    private void filterStopWords(List<Object> message) {
        String word = (String) message.get(0);
        if (!stop_words.contains(word)) {
            CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("count", word);
            TwentyEightThree.send(this.countAndSort, tempMessage);
        } else {
            CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("get_word");
            TwentyEightThree.send(this.wordsObject, tempMessage);
        }
    }
}

class CountAndSort extends ActiveWFObject2 {
    private Map<String, Integer> word_freqs = new HashMap<>();
    private Object nonStopWordsObject;
    private Object printResultsObject;

    @Override
    public void dispatch(List<Object> message) {
        if (message.get(0).equals("count")) {
            this.count(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("sort")) {
            this.sort(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("init")) {
            this.init(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("ask_sorted_map")) {
            this.askSortedMap(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("ask_counted_map")) {
            this.askCountedMap(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("print_final_result")) {
            this.printFinalResult();
        } else if (message.get(0).equals("finish")) {
            this.finish();
        }
    }

    private void init(List<Object> message) {
        this.nonStopWordsObject = message.get(0);
        this.printResultsObject = message.get(1);
    }

    private void finish() {
        CopyOnWriteArrayList<Object> tempMessage1 = TwentyEightThree.messageCreation("die");
        TwentyEightThree.send(this, tempMessage1);

        CopyOnWriteArrayList<Object> tempMessage2 = TwentyEightThree.messageCreation("finish");
        TwentyEightThree.send(this.nonStopWordsObject, tempMessage2);
    }

    private void askSortedMap(List<Object> message) {
        CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("ask_counted_map");
        TwentyEightThree.send(this, tempMessage);
    }

    private void askCountedMap(List<Object> message) {
        CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("ask_filter_word");
        TwentyEightThree.send(nonStopWordsObject, tempMessage);
    }

    private void count(List<Object> message) {
        String word = (String) message.get(0);
        if (word_freqs.containsKey(word)) {
            word_freqs.put(word, word_freqs.get(word) + 1);
        } else {
            word_freqs.put(word, 1);
        }
        CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("sort", word);
        TwentyEightThree.send(this, tempMessage);
    }

    private void sort(List<Object> message) {
        List<Map.Entry<String, Integer>> list = new CopyOnWriteArrayList<>(word_freqs.entrySet());

        list.sort((o1, o2) -> (o2.getValue()).compareTo(o1.getValue()));

        HashMap<String, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        word_freqs = temp;

        CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("print", word_freqs);
        TwentyEightThree.send(this.printResultsObject, tempMessage);
    }

    private void printFinalResult() {
        CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("print_last", word_freqs);
        TwentyEightThree.send(this.printResultsObject, tempMessage);
    }
}

class PrintResults extends ActiveWFObject2 {
    private Object countAndSort;
    int i = 0;

    @Override
    public void dispatch(List<Object> message) {
        if (message.get(0).equals("init")) {
            this.init(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if (message.get(0).equals("print")) {
            this.displayResults(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if(message.get(0).equals("ask_to_print")) {
            this.askToPrint(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        } else if(message.get(0).equals("print_last")) {
            this.printLast(new CopyOnWriteArrayList<>(message.subList(1, message.size())));
        }
    }

    private void init(List<Object> message) {
        this.countAndSort = message.get(0);
        CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("ask_to_print");
        TwentyEightThree.send(this, tempMessage);
    }

    private void askToPrint(List<Object> message) {
        CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("ask_sorted_map");
        TwentyEightThree.send(this.countAndSort, tempMessage);
    }

    private void print(List<Object> message) {
        HashMap<String, Integer> word_freqs = (HashMap<String, Integer>) message.get(0);
        int count = 0;
        System.out.println();
        for (Map.Entry m : word_freqs.entrySet()) {
            if (count >= 25) break;
            count++;
            System.out.println(m.getKey() + " - " + m.getValue());
        }
        System.out.println("----------------------------------------");
    }

    private void printLast(List<Object> message) {
        print(message);
        CopyOnWriteArrayList<Object> tempMessage1 = TwentyEightThree.messageCreation("die");
        TwentyEightThree.send(this, tempMessage1);

        CopyOnWriteArrayList<Object> tempMessage2 = TwentyEightThree.messageCreation("finish");
        TwentyEightThree.send(this.countAndSort, tempMessage2);
    }

    private void displayResults(List<Object> message) {
        if(i%5000==0) {
            print(message);
        }
        i++;

        CopyOnWriteArrayList<Object> tempMessage = TwentyEightThree.messageCreation("ask_to_print");
        TwentyEightThree.send(this, tempMessage);

    }
}

