package com.github.daphexion.cyndux;

public enum ANSIFormat {
	RESETCOLOR("39;49"),
	RESETALL("0"),
	BLINK("5"),
	BOLD("1"),
	BRIGHT("1"),
	DEFAULT_BACKGROUND("49"),
	DEFAULT_FOREGROUND("39"),
	BLACK_FOREGROUND("40"),
	BLACK_BACKGROUND("30"),
	RED_FOREGROUND("41"),
	RED_BACKGROUND("31"),
	GREEN_FOREGROUND("42"),
	GREEN_BACKGROUND("32"),
	YELLOW_FOREGROUND("43"),
	YELLOW_BACKGROUND("33"),
	BLUE_FOREGROUND("44"),
	BLUE_BACKGROUND("34"),
	MAGENTA_FOREGROUND("45"),
	MAGENTA_BACKGROUND("35"),
	CYAN_FOREGROUND("46"),
	CYAN_BACKGROUND("36"),
	WHITE_FOREGROUND("47"),
	WHITE_BACKGROUND("37")
	;
	private final String code;
	ANSIFormat(String code){
		this.code=code;
	}
	public String getCode() {
		return code;
	}
}
