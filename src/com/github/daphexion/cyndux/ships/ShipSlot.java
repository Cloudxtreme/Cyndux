package com.github.daphexion.cyndux.ships;

public enum ShipSlot {
	HI1("hi.1"),
	HI2("hi.2"),
	HI3("hi.3"),
	MID1("mid.1"),
	MID2("mid.2"),
	MID3("mid.3"),
	LO1("lo.1"),
	LO2("lo.2"),
	LO3("lo.3");
	private final String PropName;
	ShipSlot(String PropName){
		this.PropName = PropName;
	}
	public String getPropName(){
		return this.PropName;
	}
	public String getSlot(){
		return getPropName().substring(0, getPropName().indexOf("."));
	}
}
