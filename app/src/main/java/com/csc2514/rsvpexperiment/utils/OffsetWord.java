package com.csc2514.rsvpexperiment.utils;

public class OffsetWord {
	String word;
	String pre;
	String focus;
	String post;
	int split;
	int offset;
	public OffsetWord(String word, String pre, String focus, String post, int split, int offset) {
		super();
		this.word = word;
		this.pre = pre;
		this.focus = focus;
		this.post = post;
		this.split = split;
		this.offset = offset;
	}
	public String getWord() {
		return word;
	}
	public String getPre() {
		return pre;
	}
	public String getFocus() {
		return focus;
	}
	public String getPost() {
		return post;
	}
	public int getSplit() {
		return split;
	}
	public int getOffset() {
		return offset;
	}	
	@Override
	public String toString(){
		return word;
	}
}
