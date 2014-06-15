package pl.website.bcsn.sysinfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;

public class RecorderThread implements Runnable{
	
	boolean enabled = false;
	int interval = 30;
	String filename = "ram-record.txt";
	File file;
	FileOutputStream out;
	String splitter = "|";
	@Override
	public void run() {
		while(true){
			
			String s = "";
			s += Sysinfo.locale.getString("record.log-prefix");
			s += Sysinfo.locale.getString("ram-usage");
			s += ":";
			s += InfoGatherer.getRawRamUsage()[0];
			
			Sysinfo.log(s);
			record();
			
			try {
				Thread.sleep(interval*60*1000); //sleep for <interval> minutes
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}


	private void record() {
		try{
			if(!file.exists()){
				file.createNewFile();
				file.setWritable(true);
			}
			Sysinfo.log("Writing:" + informationLine());
			out.write(informationLine().getBytes());
			out.write("\n".getBytes()); //Write line feed
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


	@SuppressWarnings("deprecation")
	private String informationLine() {
		String str = "";
		StringBuilder sb = new StringBuilder(str);
		GregorianCalendar gc = new GregorianCalendar();
		Date d = gc.getTime();
		
//		sb.append(d.getYear());
//		sb.append(splitter);
//		sb.append(d.getMonth());
//		sb.append(splitter);
//		sb.append(d.getDay());
//		sb.append(splitter);
//		sb.append(d.getHours());
//		sb.append(splitter);
//		sb.append(d.getMinutes());
//		sb.append(splitter);
//		sb.append(d.getSeconds());
//		sb.append(splitter);
//		sb.append(InfoGatherer.getRawRamUsage()[0]);
//		sb.append(splitter);
//		sb.append(InfoGatherer.getRawRamUsage()[1]);
//		sb.append(splitter);
//		sb.append(InfoGatherer.getRawRamUsage()[2]);
		
		
		sb.append(d.toGMTString());
		sb.append(splitter);
		sb.append(InfoGatherer.getRawRamUsage()[0]);
		sb.append(splitter);
		sb.append(InfoGatherer.getRawRamUsage()[1]);
		sb.append(splitter);
		sb.append(InfoGatherer.getRawRamUsage()[2]);
		return sb.toString();
	}


	public void init() {
		try{
		enabled = Sysinfo.config.getBoolean("recorder.enabled");
		interval = Sysinfo.config.getInt("recorder.interval");
		filename = Sysinfo.config.getString("recorder.filename");
		splitter = Sysinfo.config.getString("recorder.splitter");
		}catch(Exception e){
			Sysinfo.log(Sysinfo.locale.getString("log.config-error"));
		}
		file = new File(Sysinfo.dataFolder, filename);
		try {
			out = new FileOutputStream(file);
			out.write("#: UTC DATE|PERCENTAGE|USEDRAM|MAXRAM\n".getBytes());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
