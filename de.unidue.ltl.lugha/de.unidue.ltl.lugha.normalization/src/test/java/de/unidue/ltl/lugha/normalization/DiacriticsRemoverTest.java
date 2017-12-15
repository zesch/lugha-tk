/*******************************************************************************
 * 
 * Copyright 2015
 * Language Technology Lab (LTL)
 * University of Duisburg-Essen
 * 
 ******************************************************************************/

package de.unidue.ltl.lugha.normalization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.unidue.ltl.lugha.normalization.DiacriticsRemover;

/**
 * 
 * This class contains a JTest for Arabic diacritics remover
 * 
 */

public class DiacriticsRemoverTest {

	@Test
	public void diacriticsRemoverTest() {
		  	
    	String withDiacritics = "قَامَ الْقَسَمِ الْعَرَبِيِّ فِيْ جُوْجِلْ بِبَرْمَجَةٌ أَدَاةِ مُفِيْدَةٍ تَقُوْمُ بِتَشْكِيْلَ الْكَلِمَاتِ";
    	
    	System.out.println(withDiacritics.length());
    	
    	String withoutDiacritics = DiacriticsRemover.removeDiacritics(withDiacritics);
    	
    	System.out.println(withoutDiacritics.length());
    	
		assertEquals("قام القسم العربي في جوجل ببرمجة أداة مفيدة تقوم بتشكيل الكلمات", withoutDiacritics);
		
		String withAdditionalDiacritics = "الرَّحْمٰن الرَّحِيم";
		
		String withoutAdditionalDiacritics = DiacriticsRemover.replaceAdditionalDiacritics(withAdditionalDiacritics);	
		
		System.out.println(withoutAdditionalDiacritics);

		assertEquals("الرَّحْمَن الرَّحِيم", withoutAdditionalDiacritics);
		
		String withSukun = "بِسْمِ";
		
		String withoutSukun = DiacriticsRemover.removeSukun(withSukun);	
		
		System.out.println(withoutSukun);

		assertEquals("بِسمِ", withoutSukun);


	}
	
	@Test
	public void testDiacriticsRemoverTest() {
		
		assertEquals(false, DiacriticsRemover.hasCaseEndings("تقوم"));		
//		System.out.println(DiacriticsRemover.lastLetterDiacritics("تقوم"));//empty
		System.out.println(DiacriticsRemover.removeLastLetterDiacritics("تقوم"));

				
		assertEquals(false, DiacriticsRemover.hasCaseEndings("عِلْم"));
//		System.out.println(DiacriticsRemover.lastLetterDiacritics("عِلْم"));//empty
		System.out.println(DiacriticsRemover.lastLetterDiacritics("عِلْم"));//empty
		
		assertEquals(true, DiacriticsRemover.hasCaseEndings("بِسمِ"));//Kasra		
//		System.out.println(DiacriticsRemover.lastLetterDiacritics("بِسمِ"));
		System.out.println(DiacriticsRemover.removeLastLetterDiacritics("بِسمِ"));

		assertEquals(true, DiacriticsRemover.hasCaseEndings("ذَكَرٌ"));//Damma		
//		System.out.println(DiacriticsRemover.lastLetterDiacritics("ذَكَرٌ"));
		System.out.println(DiacriticsRemover.removeLastLetterDiacritics("ذَكَرٌ"));

		
		assertEquals(true, DiacriticsRemover.hasCaseEndings("بَيَّتَ"));//Fatha		
//		System.out.println(DiacriticsRemover.lastLetterDiacritics("بَيَّتَ"));
		System.out.println(DiacriticsRemover.removeLastLetterDiacritics("بَيَّتَ"));

		assertEquals(true, DiacriticsRemover.hasCaseEndings("عِلْمً"));//Fathataan		
//		System.out.println(DiacriticsRemover.lastLetterDiacritics("عِلْمً"));
		System.out.println(DiacriticsRemover.removeLastLetterDiacritics("عِلْمً")); 

		assertEquals(true, DiacriticsRemover.hasCaseEndings("عَيَّنَّ"));//Fathataan		
//		System.out.println(DiacriticsRemover.lastLetterDiacritics("عَيَّنَّ"));
		System.out.println(DiacriticsRemover.removeLastLetterDiacritics("عَيَّنَّ")); 

		
	}


}
