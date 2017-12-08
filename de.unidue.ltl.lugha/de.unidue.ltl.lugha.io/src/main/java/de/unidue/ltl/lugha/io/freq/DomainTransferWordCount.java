package de.unidue.ltl.lugha.io.freq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.JCasIterable;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.ConditionalFrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.frequency.util.FrequencyDistribution;
import de.tudarmstadt.ukp.dkpro.core.api.resources.DkproContext;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import de.tudarmstadt.ukp.dkpro.core.tokit.RegexTokenizer;
import de.unidue.ltl.lugha.normalization.DiacriticsRemover;
import de.unidue.ltl.lugha.normalization.PunctuationRemover;
import de.unidue.ltl.lugha.normalization.TextNormalizer;
import de.unidue.ltl.lugha.transliteration.BuckwalterTransliterator;
import de.unidue.ltl.lugha.transliteration.Transliterator;
import de.unidue.ltl.lugha.uima.FarasaSegmenter;


public class DomainTransferWordCount {
	
	private Transliterator buckwalter = new BuckwalterTransliterator();

	public static void main(String[] args) 
			throws Exception
	{
		DomainTransferWordCount exp = new DomainTransferWordCount();
		exp.analyze();
	}
		
	public void analyze() 
		throws Exception
	{
//		String corpusFile = CorpusFile.getCorpusPath(CorpusName.Tashkeela11Books);

		String corpusFile = DkproContext.getContext().getWorkspace("corpora").getAbsolutePath() + "/arabic/tashkeela.txt";
					
	    ConditionalFrequencyDistribution<String,String> cfd = getCFD(
	    		CollectionReaderFactory.createReaderDescription(
						CorporaStatisitcsArabicReader.class,
						CorporaStatisitcsArabicReader.PARAM_SENTENCES_FILE, corpusFile,
						CorporaStatisitcsArabicReader.PARAM_ENCODING, "UTF-8",
						CorporaStatisitcsArabicReader.HAS_DIACRITICS, "Yes"
	    		),
	    		AnalysisEngineFactory.createEngine(
						FarasaSegmenter.class
				)
//	    		AnalysisEngineFactory.createEngine(
//						RegexTokenizer.class,
//						RegexTokenizer.PARAM_TOKEN_BOUNDARY_REGEX, " "
//				)
	    );
        
        String[] roots = new String[]{"Elm", "*kr", "Eyn", "Zlm", "Eql", "$Er", "fjr", "nbE", "byt"};
        
        for (String root : roots) {
        	printDivider(root);
            printSet(cfd, root);
        }
        
        System.out.println();
        
        // plot ambiguity
        FrequencyDistribution<Integer> ambiguity = new FrequencyDistribution<Integer>();
        for (String root : cfd.getConditions()) {
        	ambiguity.inc(cfd.getFrequencyDistribution(root).getKeys().size());
        }
        List<Integer> levels = new ArrayList<Integer>(ambiguity.getKeys());
        Collections.sort(levels);
        for (Integer level : levels) {
        	System.out.println(level + " : " + ambiguity.getCount(level));
        }
        
        int[] sizes = new int[]{12,13,14,15,16,17,18,19,20};
        for (int size : sizes) {
        	printDistribution(cfd, size);
        }

    }
	
	public DomainTransferWordCount() {
		buckwalter = new BuckwalterTransliterator();
	}
	
	private ConditionalFrequencyDistribution<String, String> getCFD(CollectionReaderDescription reader, AnalysisEngine tokenizer)
			throws AnalysisEngineProcessException
	{
	    ConditionalFrequencyDistribution<String,String> cfd = new ConditionalFrequencyDistribution<String, String>();

	    int wordCount = 0;
        for (JCas jcas : new JCasIterable(reader)) {
        	tokenizer.process(jcas);
        	
        	for (Token token : JCasUtil.select(jcas, Token.class)) {
				wordCount++;
				
				// TODO why do we need double normalization?
	            String word = TextNormalizer.fullyNormalizeText(
	            				PunctuationRemover.removePunctuation(
	            						TextNormalizer.fullyNormalizeText(token.getCoveredText())));

				// remove nunation
				word = DiacriticsRemover.removeTanweenFath(word);
				word = DiacriticsRemover.removeTanweenDamm(word);
				word = DiacriticsRemover.removeTanweenKasr(word);

				String root = DiacriticsRemover.removeDiacritics(word);
			
				String readableRoot = buckwalter.getLatinString(root);
			
				cfd.inc(readableRoot, word);
			}
        }
        System.out.println("# words: " + wordCount);
        System.out.println("# types: " + cfd.getConditions().size());

        return cfd;
	}
	
	private void printSet(ConditionalFrequencyDistribution<String, String> cfd, String root) {
        FrequencyDistribution<String> fd = cfd.getFrequencyDistribution(root);
        for(String key : fd.getKeys()){
			System.out.println(key + " : " + fd.getCount(key) + " - " + buckwalter.getLatinString(key));
		}
	}
	
	private void printDivider(String root) {
        System.out.println("====== "+ root + " ===================");
	}
	
	private void printDistribution(ConditionalFrequencyDistribution<String, String> cfd, int sizeToPlot) {
		System.out.println("Ambiguity size: " + sizeToPlot);
		for (String root : cfd.getConditions()) {
        	if (cfd.getFrequencyDistribution(root).getKeys().size() == sizeToPlot) {
        		System.out.println(root + " (" + getDistributionString(cfd.getFrequencyDistribution(root)) + ")");
        	}
        }
		System.out.println();
	}
	
	private String getDistributionString(FrequencyDistribution<String> fd) {
		List<Integer> values = new ArrayList<Integer>();
		for (String key : fd.getKeys()) {
			values.add(new Long(fd.getCount(key)).intValue());
			// normalized
//			values.add(new Double(Math.floor((double) fd.getCount(key)*1000 / fd.getN())).intValue());
		}
		Collections.sort(values);
		return StringUtils.join(values, ",");
	}

}
