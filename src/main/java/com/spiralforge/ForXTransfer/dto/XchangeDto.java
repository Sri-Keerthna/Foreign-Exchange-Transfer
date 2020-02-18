package com.spiralforge.forxtransfer.dto;

import java.io.Serializable;
import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XchangeDto implements Serializable {
	
	private HashMap<String, Double> rates;
	private String base;
	private String date;

}

