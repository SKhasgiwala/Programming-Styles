var fs=require('fs');

function extract_words(obj,path_to_file){   
    var string=fs.readFileSync(path_to_file).toString('utf-8');
    string=string.replace(/[\W_]+/g," ").toLowerCase();
    string=string.replace("'s","");
    obj['data']=string.split(" ");   
}

function load_stop_words(obj){
    var string=fs.readFileSync("../stop_words.txt").toString('utf-8');
    string=string.toLowerCase();
    obj['stop_words']=string.split(",");
}

function increment_count(obj,w){
    if(!obj['freqs'].has(w)&&w!=="s"){
        obj['freqs'].set(w,1);
    }
    else{
        obj['freqs'].set(w,obj['freqs'].get(w)+1);
    }
}



var data_storage_obj={
    'data':[],
    'init': function(path_to_file){
        var me=this;
        extract_words(me,path_to_file);
    },
    'words': function(){
        var me=this;
        return me.data;
    }
};


var stop_words_obj={
    'stop_words':[],
    'init': function(){
        var me=this;
        load_stop_words(me);
    },
    'is_stop_word':function(word){
        var me=this;
        return me.stop_words.indexOf(word)>-1;
    }

};


var word_freqs_obj={
    'freqs':new Map(),
    'increment_count':function(w){
        var me=this;
        increment_count(me,w);
    },
    'sorted' : function(){
        var me=this;
        me.freqs[Symbol.iterator] = function* () {
            yield* [...this.entries()].sort((a, b) => b[1] - a[1]);
        };
    }
};


const args = process.argv.slice(2);
data_storage_obj.init.call(data_storage_obj,args[0]);
stop_words_obj.init.call(stop_words_obj);

var i;
for(i=0;i<data_storage_obj['data'].length;i++){
    
     if(!stop_words_obj.is_stop_word.call(stop_words_obj,data_storage_obj.words.call(data_storage_obj)[i])){
         word_freqs_obj.increment_count.call(word_freqs_obj,data_storage_obj.words.call(data_storage_obj)[i]);
     }
    }

word_freqs_obj['top25']=function(){
    word_freqs_obj.sorted.call(word_freqs_obj);
    var count=0;
    for (let [key, value] of word_freqs_obj['freqs']) {  
        count+=1;   
        console.log(key + '  ' + '-' + '  ' + value);
        if(count===25){
            break;
        }
    }
};

word_freqs_obj['top25']();


