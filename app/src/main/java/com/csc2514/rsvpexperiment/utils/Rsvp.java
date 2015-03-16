package com.csc2514.rsvpexperiment.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Rsvp implements Iterator<OffsetWord> {

	ArrayList<String> corpus;
	int wpm;
    int iteratorIndex;
    int offset;
	
	public Rsvp(String text, int wpm, int offset) throws IOException{
        corpus = new ArrayList<String>();
        String[] words = text.split("[ \\n]+");
        corpus.addAll(Arrays.asList(words));
        this.wpm = wpm;
        this.offset = offset;
        iteratorIndex = 0;
	}
	
	public void go() throws InterruptedException, IOException{
		int wait = this.calculateMillisecondsWord();
		for (int i = 0; i < corpus.size(); i++) {
			String word = corpus.get(i);
			OffsetWord offsetWord = this.offsetWord(word, this.getSplit(word));
			this.print(offsetWord, wait);
		}
	}
	
	public void print(OffsetWord rsvpOffsetWord, int wait) throws InterruptedException{
		System.out.println("_________________________");
		System.out.println("         |");		
		String offsetSpaces = String.format("%"+rsvpOffsetWord.getOffset()+"s", " ");
		System.out.println(offsetSpaces + rsvpOffsetWord);
		System.out.println("_________|_______________");
		Thread.sleep(wait);
	}

	public int calculateMillisecondsWord(){
		int milliseconds = 60000;
		return milliseconds / wpm;
	}
	
	public int getSplit(String word)
    {
        if (word == "")
        {
            return -1;
        }
        // Figure out breaking point
        int split = 0;
        word = word.replaceAll("[^a-zA-Z0-9]", "");
        if (word.length() == 1)
        {
            split = 1;
        } else if (word.length() < 6)
        {
        	String firstChar = word.charAt(1) + "";
            if (firstChar.matches("/[^a-zA-Z]/") && word.length() == 2)
            {
                split = 1;
            }else
            {
                split = 2;
            }
        } else if (word.length() < 10)
        {
            split = 3;
        } else if (word.length() < 14)
        {
            split = 4;
        } else if (word.length() < 60)
        {
            split = 5;
        }
        
        //System.out.println("split: " + split);
        return split;
    }
	
	public OffsetWord offsetWord(String word,int split){

		if(split < 0){
			return null;
		}
		
        /* Get pieces */
        String pre =  word.substring(0,split-1);
        String focus =  word.substring(split-1,split);
        String post =  word.substring(split);

        /* Calculate words position */
        int preLength   = pre.length();
        int focusLength = focus.length();

        //System.out.println("pre: " + pre + " > " + preLength);
        //System.out.println("focus: " + focus + " > " + focusLength);
        //System.out.println("post: " + post + " > " + post.length());
        
        focusLength = focusLength / 2;
        int offsetSpaces = (offset - preLength - focusLength);

		return new OffsetWord(word, pre, focus, post, split, offsetSpaces);
	}

    @Override
    public boolean hasNext() {
        if(iteratorIndex < corpus.size()){
            return true;
        }
        return false;
    }

    @Override
    public OffsetWord next() {
        if(iteratorIndex < corpus.size()) {
            String word = corpus.get(iteratorIndex++);
            OffsetWord offsetWord = this.offsetWord(word, this.getSplit(word));
            return offsetWord;
        }
        return null;
    }

    public void remove() {
    }
}