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
package dmLab.utils.dirCrawler;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;


public class DirCrawler {

	protected String rootpath;
	protected FilenameFilter filter;
	protected long maxFiles;
	protected int maxLevel;
	//  *******************************************
	public DirCrawler(DirCrawlerProps cfg) {

		this.rootpath = cfg.rootPath;
		this.filter = new FileExtFilter(cfg.filter);;
		this.maxFiles = cfg.maxFiles;
		this.maxLevel=cfg.maxLevel;
	}
	//  *******************************************
	public File[] getFiles()
	{
		ArrayList<File> files = new ArrayList<File>();
		ArrayList<File> dirs = new ArrayList<File>();
		dirs.add(new File(this.rootpath));
		return this.getFiles(0, dirs, files);
	}
	//  *******************************************
	protected File[] getFiles(int index, ArrayList<File> dirs, ArrayList<File> files)
	{
		File path = dirs.get(index);
		File[] list = path.listFiles();
		if(filter != null)	
			list = path.listFiles(this.filter);
		
		int size = 0;		
		if(list!=null)
			size = list.length;
		
		for(int i = 0; i < size; i++)
		{
			if (list[i].isDirectory())
			{
				String dirPath=list[i].toString();
				dirPath=dirPath.substring(rootpath.length(),dirPath.length());

				//System.out.println("dir: " + list[i]);
				//System.out.println("dirPath: " + dirPath+" cnt: "+cntPattern(dirPath,"//")+" maxLevel "+maxLevel);
				//MDR \\ na //
				if(cntPattern(dirPath,"//")<maxLevel)
				{
					dirs.add(list[i]);
					//System.out.println("dodany");
				}

			}
			if (list[i].isFile())
				if (files.size() < this.maxFiles) {
					try {
						files.add(list[i].getCanonicalFile());
						//System.out.println("file: " + list[i]);
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					File[] fileArr = new File[1];  
					fileArr = files.toArray(fileArr);
					//Arrays.sort(fileArr, new FilenameComparator());
					return fileArr;
				}
		}

		if (index+1 < dirs.size()) 
			getFiles(index+1, dirs, files);

		File[] fileArr = new File[files.size()];
		for(int i=0; i<fileArr.length; i++ )
			fileArr[i]=files.get(i);
		//Arrays.sort(fileArr, new FilenameComparator());
		return fileArr;
	}
	//	******************
	private static int cntPattern(String chain,String pattern)
	{
		int cnt=0;
		int start=0;
		while(start!=-1)
		{
			start=chain.indexOf(pattern,start);
			if(start!=-1)
			{
				start=start+pattern.length();
				cnt++;
			}
			if(start>=chain.length())
				start=-1;
		}
		return cnt;
	}
	//	*******************************************
	public static void test()
	{
		DirCrawlerProps props=new DirCrawlerProps();
		props.rootPath="D://TEMP4//training_set//";
		props.filter= ".txt";//"(.htm)|(.html)|(.txt)";

		DirCrawler dirCrawler=new DirCrawler(props);

		File[] files=dirCrawler.getFiles();
		for (int i = 0; i < files.length; i++)
			try {
				System.out.println(files[i].getCanonicalPath().toString());
			} catch (IOException e)
		{
				e.printStackTrace();
		}					
	}
	//	*******************************************
}

