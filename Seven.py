import re, sys, operator

# Y combinator
Y = lambda f: (lambda x: x(x))(lambda y: f(lambda *args: y(y)(*args)))

RECURSION_LIMIT = 9500

sys.setrecursionlimit(RECURSION_LIMIT + 10)

def count(word_list,stopwords,wordfreqs):
        word = word_list[0]
        if word not in stopwords:
            if word in wordfreqs:
                wordfreqs[word] += 1
            else:
                wordfreqs[word] = 1
        return word_list[1:]

def wf_printf(wordfreq):
        (w, c) = wordfreq[0]
        print w, ' - ', c
        return wordfreq[1:]

stop_words = set(open('../stop_words.txt').read().split(','))
words = re.findall('[a-z]{2,}', open('../pride-and-prejudice.txt').read().lower())
word_freqs = {}

# Lambda functions to eliminate two recursive functions
printing = lambda f: lambda word_freqs: None if (not word_freqs) else f(wf_printf(word_freqs))
counting = lambda f: lambda wordList: lambda stopWords: lambda wordFreqs: None if(not wordList) else f(count(wordList,stopWords,wordFreqs))(stopWords)(wordFreqs)

for i in range(0, len(words), RECURSION_LIMIT):
    Y(counting)(words[i:i + RECURSION_LIMIT])(stop_words)(word_freqs)

Y(printing)(sorted(word_freqs.iteritems(), key=operator.itemgetter(1), reverse=True)[:25])