
 var fs=require('fs')

function* character(filename){
var string=fs.readFileSync(filename, 'utf-8')
arrayOfLines = string.match(/[^\r\n]+/g);
for(line of arrayOfLines){
    yield line
}
}

function* all_words(filename){
    for(let line of character(filename)){
        line=line.toLowerCase()
        line=line.replace(/[\W_]+/g," ")
        list=line.split(" ")
        for(word of list)
        {
            yield word;
        }
    }
}

function* non_stop_words(filename){
    var stringStop=fs.readFileSync("../stop_words.txt").toString('utf-8')
    stringStop=stringStop.toLowerCase()
    stop_words=new Set(stringStop.split(","))
    var asciiChars = new Set(['a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'])
    var merged = new Set([...stop_words, ...asciiChars ])

    for(w of all_words(filename)){
        
        if(!stop_words.has(w)){
           yield w
        }
    }
}

function* count_and_sort(filename){
    freqs = new Map()
    i=1

    for(w of non_stop_words(filename)){
        if(w.length<2)
            continue;
        if(w!='s'&&freqs.has(w)){
            freqs.set(w,freqs.get(w)+1)
        }
        else if(w!='s'&&!freqs.has(w)){
            freqs.set(w,1)
        }
        if(i%50000==0){
            yield new Map([...freqs.entries()].sort((a, b) => b[1] - a[1]))
        }
        i+=1
    }
    yield new Map([...freqs.entries()].sort((a, b) => b[1] - a[1]));

}

for(word_freqs of count_and_sort(process.argv[2])){
    console.log("---------------------------")
    var count=0
    for (let [key, value] of word_freqs) {  
        count+=1   
        console.log(key + '  ' + '-' + '  ' + value);
        if(count==25){
            break
        }
    }
}

