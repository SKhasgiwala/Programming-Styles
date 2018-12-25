
var fs=require('fs');

all_words = [[] , null];
stop_words = [[] , null];
non_stop_words = [[] , () => {
    
    return all_words[0].map( (w) => {
        if(!stop_words[0].includes(w)){
            return w;
        }
        else{
            return '';
        }

    });
}];

unique_words = [[], () => {
    var mySet=new Set();
    for(val of non_stop_words[0]){
        if(val!=='' && val.length>1){
            mySet.add(val);
        }
    }
    return mySet;
}];

function formMap(list){
    result = new Map()
    for(word of list){
        if(result.has(word)){
            result.set(word,result.get(word)+1)
        }
        else{
            result.set(word,1)
        }
    }
    return result
}

counts = [[],()=>{

   word_freq = formMap(non_stop_words[0])
   return [...unique_words[0]].map((word) => 
    {      
        return word_freq.get(word)
    })
}]
 sorted_data = [[], ()=>{ 
    var result = {};
    [...unique_words[0]].forEach((key, i) => result[key] = counts[0][i]);
    var items = Object.keys(result).map(function(key) {
        return [key, result[key]];
      });
    items.sort(function(first, second) {
        return second[1] - first[1];
      });
    return items;
    }];

 all_columns = [all_words, stop_words, non_stop_words, unique_words,counts,sorted_data];

 function update(){
     for(c of all_columns){
         if(c[1]!=null){
             c[0]=c[1]();
         }
     }
 }

 var string=fs.readFileSync(process.argv[2]).toString('utf-8');
 string=string.replace(/[\W_]+/g," ").toLowerCase();
 string=string.replace("'s","");
 all_words[0] =string.split(" ");  

 var string=fs.readFileSync("../stop_words.txt").toString('utf-8');
 string=string.toLowerCase();
 stop_words[0]=string.split(",");
 update();

 var count=0;
 for (let [key, value] of sorted_data[0]) {  
        count+=1;   
        console.log(key + '  ' + '-' + '  ' + value);
        if(count===25){
            break;
        }
    }



