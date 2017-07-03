package com.me.sly.kits.main.resources;

import java.util.Random;

public class GenRandom {
    public static int gen(int h, int l) {
        double genNum = Math.random() * (h - l);
        int intGenNum = (int) Math.ceil(genNum);
        return intGenNum;
    }

    public static int genNeg(int h, int l) {
        int h2 = h + h;
        double genNumOrg = Math.random() * (h2 - l);
        double genNum = genNumOrg - h;
        int intGenNum = (int) Math.ceil(genNum);
        return intGenNum;
    }
	public int rand(int min, int max) {
		max += 1;
        return min + (new Random()).nextInt(max-min);
    }

}
