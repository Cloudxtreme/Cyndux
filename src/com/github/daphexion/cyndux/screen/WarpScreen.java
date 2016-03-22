package com.github.daphexion.cyndux.screen;

import com.github.daphexion.cyndux.players.Player;

public class WarpScreen {
	public static void print(Player player){
		int maploc = player.getLocation();
		int mapn = (maploc - 100);
		int maps = maploc + 100;
		int mape = maploc + 1; // how the fuck does increment
								// operators work
		int mapw = maploc - 1;
		int mapne = mapn + 1;
		int mapse = maps + 1;
		int mapsw = maps - 1;
		int mapnw = mapn - 1;
		if (((maploc % 100) <= 1)){ //T R U L Y S P H E R I C A L B O I S
			switch (maploc % 100){
			case 0:
				mape = maploc - 99;
				break;
			case 1:
				mapw = maploc + 99;
				break;
			}
		}else{
			if (maploc <=100){
				mapn = maploc + 50;
			}
			if (maploc >= 9900){
				maps = maploc + 50;
			}
		} 
		int warp = 0;
		switch (player.getMapCursor()) {
		case 1:
			warp = mapnw;
			break;
		case 2:
			warp = mapn;
			break;
		case 3:
			warp = mapne;
			break;
		case 4:
			warp = mapw;
			break;
		case 5:
			warp = maploc;
			break;
		case 6:
			warp = mape;
			break;
		case 7:
			warp = mapsw;
			break;
		case 8:
			warp = maps;
			break;
		case 9:
			warp = mapse;
			break;
		default:
			warp = maploc;
		}
		player.send("Are you sure you want to warp to sector " + warp + "?");
	}
}
