package com.arcing.naviaux.music.library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import com.He.W.onebone.Circuit.Cu.exception.ParseException;
import com.He.W.onebone.Circuit.Cu.parser.CCSGenerator;
import com.He.W.onebone.Circuit.Cu.parser.CCSParser;
import com.arcing.naviaux.music.window.components.AllMusic;

public class Library {
	public static int selectedSong = 0;
	
	public static HashMap<String, File> library = new HashMap<String, File>();
	
	public static void createLibrary() throws IOException, ParseException {
		File f = new File("library.ccs");
		FileInputStream fis;
		try {
			fis = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			createEmptyLibrary();
			return;
		}
		InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
		BufferedReader br = new BufferedReader(isr);
		TreeMap<String, String> map = CCSParser.parseCCS(br).get("library");
		br.close();
		
		br = null;
		isr = null;
		fis = null;
		f = null;
		if(map != null){
			map.forEach((k, v) -> {
				if(k != null && v != null){
					library.put(k, new File(v));
				}
			});
		}
		map = null;
	}

	public static String[] getLibraryNames() {
		ArrayList<String> names = new ArrayList<>();
		library.forEach((k, v) -> {
			names.add(k);
		});
		return names.toArray(new String[]{});
	}
	
	public static String[] getLibraryFiles() {
		ArrayList<String> files = new ArrayList<>();
		library.forEach((k, v) -> {
			files.add(v.getAbsolutePath());
		});
		return files.toArray(new String[]{});
	}
	
	public static void addFolderToLibrary() {
		JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView());
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.showOpenDialog(chooser);
		File selected = chooser.getSelectedFile();
		if (selected != null)
			if (selected.isDirectory()) {
				File[] folders = selected.listFiles((f) -> {
					return f.isDirectory();
				});
					
				try {
					loadFolders(folders);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		try {
			saveLibrary();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}
		reloadLibrary();
	}
	
	public static void loadFolders(File[] folders) throws IOException{
		File f = null;
		for(int i = 0; i < folders.length; i++){
			f = chooseOsuMusic(folders[i]);
			if(f != null){
				String s = folders[i].getName();
				String[] split = s.split(" ");
				if(split.length > 1){
					s = "";
					for(int j = 1; j < split.length; j++){
						s += (" " + split[j]);
					}
					s = s.substring(1);
				}
				System.out.println("song folder name : " + s);
				
				System.out.println("File added : "  + s );
				library.put(s, f);
			}
		}
		reloadLibrary();
	}
	
	public static File chooseOsuMusic(File folder) throws IOException{
		if(folder.getName() == "tutorial" | folder.getName() == "failed"){
			return null;
		}
		if(folder.isFile()) return null;
		File[] osuFiles = folder.listFiles((File v) -> {
			if(v.getName().endsWith(".osu")){
				System.out.println("osu! file found : " + v.getName());
				return true;
			}
			return false;
		});
		if(osuFiles == null){
			return null;
		}
		
		if(osuFiles.length == 0){
			return null;
		}
		File f = osuFiles[0];
		FileInputStream fis = new FileInputStream(f);
		InputStreamReader isr = new InputStreamReader(fis);
		BufferedReader br = new BufferedReader(isr);
		String s;
		while((s = br.readLine()) != null){
			if(s.startsWith("AudioFilename")){
				String[] splits = s.split(":");
				s = "";
				for(int i = 1; i < splits.length; i++){
					s += splits[i];
				}
				while(s.startsWith(" ")){
					s = s.substring(1);
				}
				System.out.println("music file found : " + s);
				br.close();
				return new File(folder, s);
			}
		}
		br.close();
		return null;
	}

	
	public static TreeMap<String, String> attr;
	public static void saveLibrary() throws IOException {
		attr = new TreeMap<String, String>();
		TreeMap<String, TreeMap<String, String>> content = new TreeMap<>();
		library.forEach((k,v) -> {
			attr.put(k, v.getAbsolutePath());
		});
		content.put("Library", attr);
		File f = new File("library.ccs");
		FileOutputStream fos = new FileOutputStream(f);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		CCSGenerator.genCCS(bw, content, true);
		bw = null;
		osw = null;
		fos = null;
		f = null;
		content = null;
		attr = null;
	}
	
	public static void createEmptyLibrary() throws IOException{
		TreeMap<String, String> attr = new TreeMap<String, String>();
		TreeMap<String, TreeMap<String, String>> content = new TreeMap<>();
		content.put("Library", attr);
		File f = new File("library.ccs");
		if(f.exists()){
			System.out.println("File already exists!");
			return;
		}
		f.createNewFile();
		FileOutputStream fos = new FileOutputStream(f);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		CCSGenerator.genCCS(bw, content, true);
		bw = null;
		osw = null;
		fos = null;
		f = null;
		content = null;
		attr = null;
	}
	
	public static void reloadLibrary() {
		AllMusic.updateLibrary();
	}
}
