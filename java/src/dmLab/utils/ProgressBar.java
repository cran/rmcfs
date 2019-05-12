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

public class ProgressBar {
	private int max;
	private int current;

	private int barSize = 70;

	private long start;
	private long lastUpdate;
	private long lastValue;

	private boolean showPercent;
	private boolean showTime;
	private boolean showETA;
	
// java -cp dmLab.jar -Xmx6g -Xms6g dmLab.utils.ProgressBar
	//************************************
	public ProgressBar(int max) {
		this.showPercent = true; 
		this.showTime = true; 
		this.showETA = true;
		init(max);
	}
	//************************************
	public ProgressBar(int max, boolean showPercent, boolean showTime, boolean showETA)
	{
		this.showPercent = showPercent; 
		this.showTime = showTime; 
		this.showETA = showETA;
		init(max);
	}
	//************************************
	private void init(int max) {
		this.start = System.currentTimeMillis();
		this.max = max;
		this.printBar(false);	  
	}
	//************************************
	public void setVal(int val)
	{
		if(val <= max){
			this.current = val;		
			int delta = (int)Math.round(100*(float)val/(float)max) - (int)Math.round(100*(float)lastValue/(float)max);
			if((System.currentTimeMillis() - this.lastUpdate) > 1000 || delta >= 10){
				this.lastUpdate = System.currentTimeMillis();
				this.lastValue = val;
				this.printBar(false);
			}
		}
	}
	//************************************
	public void finish()
	{
		this.current = this.max;
		this.printBar(true);
	}
	//************************************
	private StringBuilder createBar() {
		StringBuilder strbar = new StringBuilder("[");
		double numbar = Math.floor(barSize * (float)current/(float)max);
		int ii = 0;
		for(ii = 0; ii < numbar; ii++){
			if(ii==(numbar-1))
				strbar.append(">");
			else
				strbar.append("=");
		}
		for(ii = (int)numbar; ii < barSize; ii++){
			strbar.append(" ");
		}
		strbar.append("]");
		return strbar;
	}
	//************************************
	private void printBar(boolean finished)
	{
		StringBuilder strbar = createBar();
		
		long elapsed = (System.currentTimeMillis() - this.start);
		int seconds = (int)(elapsed / 1000)%60;
		int minutes = (int)(elapsed / 1000)/60;

		String strTime = "";
		if(showTime) {
			strTime = " Time: " + String.format("%02d",minutes)+":"+String.format("%02d",seconds);
		}
		
		//calc Percentage
		String strPercentage = "";
		if(showPercent) {
			strPercentage = " " + (int)Math.round(100.0f*(float)current/(float)max) + "%";
		}

		//calc ETA
		String strETA = "";
		if(showETA) {
			if (elapsed < 2000){
				strETA = "--:--";
			}else{
				long timeETA = elapsed * (long)((double)max/(double)current);
				int ETAseconds = (int)(timeETA /1000)%60;
				int ETAminutes = (int)(timeETA /1000)/60;
				strETA = String.format("%02d",ETAminutes)+":"+String.format("%02d",ETAseconds);
			}
			if(finished)
				strETA = "               ";
			else
				strETA = " ETA: " + strETA + "    ";
		}

		String strend = strPercentage + strTime + strETA;    
		System.out.print(strbar.toString() + " " + strend);

		if(finished){
			System.out.print("\n");
		}else{
			System.out.print("\r");
		}
	}	
	//************************************
	public static void main(String[] argv){
		int size = 300;
		ProgressBar bar = new ProgressBar(size,true,true,true);
		for(int i=0;i<size;i++) {
			bar.setVal(i);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		bar.finish();
	}
	//************************************
}
