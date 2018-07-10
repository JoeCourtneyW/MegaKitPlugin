package com.sly.main.utils;

import java.util.StringJoiner;

public class SerializationUtil
{

	public static String serialize(Integer[] values){
		StringJoiner serialized = new StringJoiner(",");

		for (int value : values) {
			serialized.add(value + "");
		}

		return serialized.toString();
	}
	public static Integer[] deserialize(String serialized){
		Integer[] deserialized = new Integer[9];
		String[] values = serialized.split(",");

		for (int i = 0; i < values.length; i++) {
			deserialized[i] = Integer.parseInt(values[i]);
		}

		return deserialized;
		
	}
}
