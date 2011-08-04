package com.personalityextractor.entity.extractor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.personalityextractor.commons.data.NounPhrase;
import com.personalityextractor.commons.data.Tweet;

public class SennaNounPhraseExtractor implements IEntityExtractor{
	private static File sennaInstallDir;
	
	static {
		sennaInstallDir = new File("/Users/tejaswi/Documents/StanfordCourses/SRL/senna-v2.0");	
	}
	
	public static String getSennaOutput(String line) {
		try {
			String cmd = "echo " + line + " | " + sennaInstallDir + "/senna ";
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			pb.directory(sennaInstallDir);
			Process shell = pb.start();
			InputStream shellIn = shell.getInputStream();
			shell.waitFor();
			int c;
			StringBuffer s = new StringBuffer();
			while ((c = shellIn.read()) != -1) {
				s.append((char) c);
			}

			return s.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public ArrayList<String> getNounPhrases(String sennaOutput) {
		ArrayList<String> nounPhrases = new ArrayList<String>();
		String[] lineArr = sennaOutput.split("\n");

		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> posTags = new ArrayList<String>();
		ArrayList<String> chunkerTokens = new ArrayList<String>();
		try {
			for (String line : lineArr) {
				String[] tokens = line.trim().split("[ \t]+");
				if (tokens.length < 3)
					continue;
				words.add(tokens[0].trim());
				posTags.add(tokens[1].trim());
				chunkerTokens.add(tokens[2].trim());
			}
			
			boolean flag = false;
			StringBuffer npBuf= new StringBuffer();			
			for(int i=0; i < posTags.size(); i++){
				if(posTags.get(i).startsWith("NN")){
					flag = true;
					npBuf.append(words.get(i)+" ");
				} else if(flag == true){
					flag = false;
					nounPhrases.add(npBuf.toString().trim().toLowerCase());
					npBuf = new StringBuffer();
				}
			}
			if(flag==true)
				nounPhrases.add(npBuf.toString().trim().toLowerCase());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nounPhrases;
	}
	
	public ArrayList<NounPhrase> getNounPhrasesWithType(String sennaOutput){
		ArrayList<NounPhrase> nounPhrases = new ArrayList<NounPhrase>();
		String[] lineArr = sennaOutput.split("\n");

		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> posTags = new ArrayList<String>();
		ArrayList<String> chunkerTokens = new ArrayList<String>();
		try {
			for (String line : lineArr) {
				String[] tokens = line.trim().split("[ \t]+");
				if (tokens.length < 3)
					continue;
				words.add(tokens[0].trim());
				posTags.add(tokens[1].trim());
				chunkerTokens.add(tokens[2].trim());
			}
		
			boolean flag = false;
			StringBuffer npBuf= new StringBuffer();			
		    
			// get Proper Noun Phrases
			for(int i=0; i < posTags.size(); i++){
				if(posTags.get(i).startsWith("NNP")){
					flag = true;
					npBuf.append(words.get(i)+" ");
				} else if(flag == true){
					flag = false;
					nounPhrases.add(new NounPhrase(npBuf.toString().trim().toLowerCase(), "PN"));
					npBuf = new StringBuffer();
				}
			}
			if(flag==true)
				nounPhrases.add(new com.personalityextractor.commons.data.NounPhrase(npBuf.toString().trim().toLowerCase(), "PN"));
			
			//re-initalize variables
			flag = false;
			npBuf= new StringBuffer();
			
			
			//get Common Noun Phrases
			for(int i=0; i < posTags.size(); i++){
				if(posTags.get(i).startsWith("NN") && !posTags.get(i).startsWith("NNP")) {
					flag = true;
					npBuf.append(words.get(i)+" ");
				} else if(flag == true){
					flag = false;
					nounPhrases.add(new NounPhrase(npBuf.toString().trim().toLowerCase(), "CN"));
					npBuf = new StringBuffer();
				}
			}
			
			if(flag==true)
				nounPhrases.add(new NounPhrase(npBuf.toString().trim().toLowerCase(), "CN"));
		
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return nounPhrases;

	}


	@Override
	public List<String> extract(String line) {
		List<String> entities = new ArrayList<String>();
		String sennaOutput = getSennaOutput(line);
		//System.out.println(sennaOutput);
		
		List<NounPhrase> nps = getNounPhrasesWithType(sennaOutput);
		for(NounPhrase np : nps){
			entities.add(np.getText());
//			if(np.getType().equalsIgnoreCase("pn")){
//				entities.add(np.getText());
//			} else if (np.getType().equalsIgnoreCase("cn")){
//				for(String token : np.getText().split("\\s+")){
//					entities.add(token);
//				}
//			}
		}
		return entities;
	}
	
	
	public List<String> readLinesinFile(String file){
		List<String> lines = new ArrayList<String>();
		try{
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line ="";
			while((line=br.readLine())!=null){
				lines.add(line);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return lines;
	}
	
	
	public static void main(String[] args){
		SennaNounPhraseExtractor sn = new SennaNounPhraseExtractor();
		String line1 = "New Video: �The Sims Social� Producers Show Off Upcoming Facebook Game - http://on.mash.to/orMXcI";
		List<String> lines = new ArrayList<String>();
		//List<String> lines = sn.readLinesinFile(args[0]);
		lines.add(line1);
		for(String line : lines){
			System.out.println(sn.extract(line));
			
			System.out.println("Tweet: "+ line);
			Tweet t = new Tweet(line);
			for(String sent: t.getSentences()) {
				System.out.println("Sentence: "+sent);
				System.out.println(sn.extract(sent));
			}
		}
	}

}
