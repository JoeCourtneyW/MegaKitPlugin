package com.sly.main.gametype;

public enum TeamColor
{
	BLUE,
	YELLOW;
	public TeamColor getOpposite() {
		if (this == BLUE) {
			return YELLOW;
		} else {
			return BLUE;
		}
	}
}
