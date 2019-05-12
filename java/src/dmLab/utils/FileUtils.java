/*******************************************************************************
 * #-------------------------------------------------------------------------------
 * # dmLab 2003-2019
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
		File fileDir = (new File(fileName)).getParentFile();
		if(!fileDir.exists()) {
			fileDir.mkdir();
		}
		
		FileWriter file;
		try{
			file = new FileWriter(fileName,false);
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
	public static boolean deleteFile(String fileName) {

		File file = new File(fileName);
		if(file.exists()) {
			file.delete();
			return true;
		}else {
			return false;
		}		
	}
	//*********************************
	public static int deleteFiles(ArrayList<String> files)
	{
		int cnt = 0;
		for(int i=0;i<files.size();i++){
			if(FileUtils.deleteFile(files.get(i)))
				cnt ++;
		}
		return cnt;
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
				else
					mkdirs(outdir, "");

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
	public static String getTmpDir(String prefix, int length) {
		File tmpPath = new File(System.getProperty("java.io.tmpdir") + File.separator);
		File tmpDir = new File(tmpPath.getAbsolutePath() + File.separator + prefix + "_" + StringUtils.getRandomString(length));		
		while(tmpDir.exists()) {
			tmpDir = new File(tmpPath.getAbsolutePath() + StringUtils.getRandomString(length));
		}
		
		return tmpDir.getAbsolutePath() + File.separator;
	}
	//	************************************************
	public static boolean deleteDir(File dir) {
	    if (dir.isDirectory()) {
	      String[] children = dir.list();
	      for (int i = 0; i < children.length; i++) {
	        boolean success = deleteDir(new File(dir, children[i]));
	        if (!success) {
	          return false;
	        }
	      }
	    }
	    return dir.delete();
	  }
	//	************************************************

}
