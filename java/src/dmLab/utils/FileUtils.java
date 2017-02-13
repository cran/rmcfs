/*******************************************************************************
 * #-------------------------------------------------------------------------------
 * # Copyright (c) 2003-2016 IPI PAN.
 * # All rights reserved. This program and the accompanying materials
 * # are made available under the terms of the GNU Public License v3.0
 * # which accompanies this distribution, and is available at
 * # http://www.gnu.org/licenses/gpl.html
 * # 
 * #-------------------------------------------------------------------------------
 * # @description: data mining (dmLab) library that implements MCFS-ID algorithm
 * # @author: Michal Draminski [mdramins@ipipan.waw.pl]
 * # @company: Polish Academy of Sciences - Institute of Computer Science
 * # @homepage: http://www.ipipan.eu/
 * #-------------------------------------------------------------------------------
 * # Algorithm 'SLIQ' developed by Mariusz Gromada
 * # R Package developed by Michal Draminski & Julian Zubek
 * #-------------------------------------------------------------------------------
 * # If you want to use dmLab or MCFS/MCFS-ID, please cite the following paper:
 * # M.Draminski, A.Rada-Iglesias, S.Enroth, C.Wadelius, J. Koronacki, J.Komorowski 
 * # "Monte Carlo feature selection for supervised classification", 
 * # BIOINFORMATICS 24(1): 110-117 (2008)
 * #-------------------------------------------------------------------------------
 *******************************************************************************/
package dmLab.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class FileUtils {
	private static final int  ZIP_BUFFER_SIZE = 4096;

	//********************************
	public static String getFileExtension(String fileName)
	{
		String extension="";    
		int index=fileName.lastIndexOf('.');
		if (index > 0 &&  index < fileName.length() - 1)        
			extension=fileName.substring(index+1).toLowerCase();
		return extension;   
	}
	// ************************************************
	public static String getFilePath(File file)
	{
		String path="";
		String absPath=file.getAbsolutePath();
		int index=absPath.lastIndexOf(file.getName());
		path=absPath.substring(0,index);
		return path;
	}
	//********************************
	public static String dropFileExtension(File file)
	{
		String label="";
		String name = file.getName();
		String ext = getFileExtension(name);              
		int index = name.lastIndexOf(ext);
		label = name.substring(0,index-1);
		return label;
	}
	//********************************
	public static boolean saveString(String fileName, String data){
		FileWriter file;
		try{
			file= new FileWriter(fileName,false);
		}       
		catch(IOException ex){
			System.err.println("Error opening file: "+fileName);
			return false;
		}               
		try {
			file.write(data);
			file.close();
		} catch (IOException e) {
			System.err.println("Error writing or closing file: "+fileName);
			return false;
		}      
		return true;		
	}
	//********************************
	public static String loadString(String fileName) 
	{
		BufferedReader reader=null;
		try {
			reader = new BufferedReader( new FileReader (fileName));
		} catch (FileNotFoundException e1) {
			System.err.println("Error opening file: "+fileName);
			e1.printStackTrace();
		}
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		try {
			while( ( line = reader.readLine() ) != null ) {
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
		} catch (IOException e) {
			System.err.println("Error reading file: "+fileName);
			e.printStackTrace();
		}	    

		try {
			if(reader!=null)
				reader.close();
		} catch (IOException e) {
			System.err.println("Error closing file: "+fileName);
			e.printStackTrace();
		}

		return stringBuilder.toString();
	}
	//********************************
	public static void deleteFile(String fileName) {

		File file = new File(fileName);
		if(file.exists())
			file.delete();
	}
	//********************************
	public static void addFileToZip(String fileName, ZipOutputStream zos) throws IOException {

		File file = new File(fileName);
		if(file.exists()){
			//System.out.println("Writing '" + fileName + "' to zip file");
			FileInputStream fis = new FileInputStream(file);
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zos.putNextEntry(zipEntry);

			byte[] bytes = new byte[ZIP_BUFFER_SIZE];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
			zos.closeEntry();
			fis.close();
		}
	}
	//********************************
	public static void extractFile(ZipInputStream in, File outdir, String name) throws IOException
	{
		byte[] buffer = new byte[ZIP_BUFFER_SIZE];
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(outdir, name)));
		int count = -1;
		while ((count = in.read(buffer)) != -1)
			out.write(buffer, 0, count);
		out.close();
	}
	//********************************
	private static void mkdirs(File outdir, String path)
	{
		File d = new File(outdir, path);
		if( !d.exists() )
			d.mkdirs();
	}
	//********************************
	private static String dirpart(String name)
	{
		int s = name.lastIndexOf( File.separatorChar );
		return s == -1 ? null : name.substring( 0, s );
	}
	//********************************
	public static File[] extract(File zipfile, File outdir, String fileNamePrefix)
	{
		ArrayList<File> extracted = new ArrayList<File>(); 
		try{
			ZipInputStream zin = new ZipInputStream(new FileInputStream(zipfile));
			ZipEntry entry;
			String name, dir;

			while ((entry = zin.getNextEntry()) != null){
				name = entry.getName();
				if(entry.isDirectory()){
					mkdirs(outdir, name);
					continue;
				}
				dir = dirpart(name);
				if(dir != null)
					mkdirs(outdir, dir);

				if(fileNamePrefix.length() > 0 && name.toLowerCase().startsWith(fileNamePrefix.toLowerCase())){	
					extractFile(zin, outdir, name);
					extracted.add(new File(name));
				}

			}
			zin.close();
		}catch (IOException e){
			e.printStackTrace();
		}
		
		if(extracted.size() == 0){
			return null;
		}else{
			File[] f = new File[1];
			return extracted.toArray(f);
		}
	}
	//********************************
	public static BufferedReader openFile(File file)
	{      	
		BufferedReader fileReader;
		if(!file.exists()){
			System.err.println("File does not exist. File: " + file.toString());
			return null;
		}	

		try{
			fileReader = new BufferedReader(new FileReader(file));
		}
		catch (Exception e) {
			System.err.println("Error opening file. File: "+file.toString());
			return null;
		}
		return fileReader;
	}
	//	************************************************
	public static boolean closeFile(BufferedReader fileReader)
	{
		try{
			if(fileReader != null)
				fileReader.close();
		}
		catch (Exception e) {
			System.err.println("Error closing file.");
			return false;
		}
		return true;
	}
	//	************************************************

}
