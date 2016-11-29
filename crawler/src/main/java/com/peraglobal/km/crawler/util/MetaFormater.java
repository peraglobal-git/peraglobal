package com.peraglobal.km.crawler.util;

public interface MetaFormater<T> {
	
	

	String format(T o);

	String formatKm(T o);
}
