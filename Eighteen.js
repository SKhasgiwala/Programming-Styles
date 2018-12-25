



var fs=require('fs')

var extract_words = (path_to_file) => {
    let data = fs.readFileSync(path_to_file, 'utf8');
    let pattern = /[\W_]+/g;
    let data_str = data.replace(pattern, ' ').toLowerCase();
    let word_list = data_str.split(' ');

    let sdata = fs.readFileSync("../stop_words.txt", 'utf8');
    let stop_words = sdata.split(',');
    const listofchar = "abcdefghijklmnopqrstuvwxyz".split('');
    stop_words.push.apply(stop_words,listofchar);

    let final_word_list = [];
    for(const word of word_list){
        if(!stop_words.includes(word))
            final_word_list.push(word)
    }
    return final_word_list
}

var frequencies = (word_list) => {
    let word_freqs = new Map()
    for(const word of word_list){
        if(!word_freqs.has(word)){
            word_freqs.set(word, 1);
        }
        else{
            word_freqs.set(word, word_freqs.get(word)+1);
        }
    }
    return word_freqs
}

var sort = (word_freq) =>{
    word_freq[Symbol.iterator] = function* () {
        yield* [...this.entries()].sort((a, b) => b[1] - a[1]);
    };
    return word_freq
}

function profile(f) {
    function profilewrapper(args){
        start_time=Date.now()
        ret_value=f(args)
        elapsed=Date.now()-start_time
        console.log(f.name+" took "+elapsed+" milliseconds ")
        
        return ret_value
    }
    return profilewrapper
}

let tracked_functions = [extract_words, frequencies, sort]
let dict = {}
console.log("---------------------")
for(func of tracked_functions){
    dict[func.name]=profile(func)
}

word_freqs = dict.sort(dict.frequencies(dict.extract_words(process.argv[2])))
console.log("---------------------")
var count=0
for (let [key, value] of word_freqs) {
    console.log ((key + '  ' + '-' + '  ' + value).toString());
    if(count==24) break
    count++
}

