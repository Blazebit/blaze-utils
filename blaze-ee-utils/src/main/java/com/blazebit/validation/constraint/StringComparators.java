package com.blazebit.validation.constraint;

import java.util.Comparator;

public enum StringComparators implements Comparator<String> {

	EQUAL_IGNORE_CASE{
		@Override
		public boolean compareString(String o1, String o2) {
			return o1.equalsIgnoreCase(o2);
		}
	},
	EQUAL_TRIMMED{
		@Override
		public boolean compareString(String o1, String o2) {
			return o1.trim().equals(o2.trim());
		}
	},
	EQUAL_IGNORE_CASE_TRIMMED{
		@Override
		public boolean compareString(String o1, String o2) {
			return o1.trim().equalsIgnoreCase(o2.trim());
		}
	};
	
	@Override
	public int compare(String o1, String o2) {
		if(o1 == o2){
			return 0;
		}
		if(o1 == null || o2 == null){
			return -1;
		}
		
		return compareString(o1, o2) ? 0 : -1;
	}
	
	abstract boolean compareString(String o1, String o2);

}
