var fs=require('fs');

class TFQuarantine{

    constructor(func){
        this.funcs=[func];
    }
	
	bind(func) {
        this.funcs.push(func);
        return this;
	}
	
	execute(){
		var guard_callable = (v) => {
            if (v instanceof Function){
                return v();
            }
            else{
                return v;
            }
        };
        var value; 
        for(var func in this.funcs){
            value=this.funcs[func](guard_callable(value));
        }
        guard_callable(value);
    }
}
    

var get_input = (arg) => {return () => {return process.argv[2];};};

var extract_words=function(path_to_file){
    var f = function(){
       

        var string=fs.readFileSync(path_to_file).toString('utf-8');
        string=string.replace(/[\W_]+/g," ").toLowerCase();
        string=string.replace("'s","");
        var data=string.split(" ");
        return data;
    };
    return f;
};

var remove_stop_words=function(word_list){
    var f = function(){
        var string=fs.readFileSync("../stop_words.txt").toString('utf-8');
        string=string.toLowerCase();
        var stop_words=string.split(",");
        var charA='a';
        var charZ='z';
        var i = charA.charCodeAt(0);
        var j = charZ.charCodeAt(0);
        for (; i <= j; ++i) {
            stop_words.push(String.fromCharCode(i));
        }
        var final_word_list = [];
        for(var w in word_list){
            if(!stop_words.includes(word_list[w]))
                final_word_list.push(word_list[w]);
        }
        
        return final_word_list;
    };
    return f;
};

function frequencies(word_list){
    var word_freqs=new Map();
    for(var w in word_list){    
        if( word_freqs.has(word_list[w])){
           word_freqs.set(word_list[w],word_freqs.get(word_list[w])+1);
        }
        else{
            word_freqs.set(word_list[w],1);
        }
    }
    
    return word_freqs;
}

function sort(word_freq){
    word_freq[Symbol.iterator] = function* () {
        yield* [...this.entries()].sort((a, b) => b[1] - a[1]);
    };
    return word_freq;
}

var top25_freqs=(word_freqs) => {return () => {
    
    var count=0;
    for (let [key, value] of word_freqs) {  
          
       console.log ((key + '  ' + '-' + '  ' + value).toString());

        if(count==24){
            break;
        }
        count+=1; 
    }
};
    
};

new TFQuarantine(get_input).bind(extract_words)
.bind(remove_stop_words)
.bind(frequencies)
.bind(sort)
.bind(top25_freqs)
.execute();
