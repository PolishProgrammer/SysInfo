package pl.website.bcsn.sysinfo;

public class Util {

	public static String getPlayerSessions(){
		String message = "";
		
		message += "Player" + spaces(10 - "Player".length()) + "time\n";
		for(String s : Sysinfo.playerSessions.keySet()){
			message += s;
			message += spaces(10 - s.length()); //Because minecraft client doesn't accept Horizontal Tabs
			message += Sysinfo.playerSessions.get(s);
			
			message += "\n";
		}
		
		return message;
	}
	
	private static String spaces(int amt){
		String s = "";
		for(int i = 0; i < amt; i++){
			s+=" ";
		}
		return s;
	}
	
}
