package com.spiralforge.forxtransfer.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Converter {
	
	private Converter() {
		
	}

	/**
	 * Passing month and year and converting in date format
	 * 
	 * @param month
	 * @param year
	 * @return
	 */
	public static List<LocalDateTime> getDateFromMonthAndYear(Integer month, Integer year) {
		List<LocalDateTime> dates = new ArrayList<>();
		LocalDateTime date1 = LocalDateTime.of(year, month, 01,1,0,0);
		LocalDate date3=date1.toLocalDate();
		int days = date3.lengthOfMonth();

		LocalDateTime date2 = LocalDateTime.of(year, month, days,23,59,0);
		if (date1.isBefore(date2)) {
			dates.add(date1);
			dates.add(date2);
		}
		return dates;
	}

}
