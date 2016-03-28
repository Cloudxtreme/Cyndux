package com.github.daphexion.cyndux;

public class ANSI {
	public static String format(ANSIFormat... codes){
		String result = "\u001B[";
		for (ANSIFormat code : codes){
			result+=code.getCode();
		}
		result+="m";
		return result;
	}
	public static String clear(){
		return "\u001B[2J";		
	}
}
