package parserlog;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) {

		FileWriter adminCo = null;
		FileWriter invalidCommand = null;
		FileWriter permBan = null;
		FileWriter playerList = null;
		FileWriter shootLanceTkRecord = null;
		FileWriter adminCoRes = null;
		FileWriter chatRecord = null;
		FileWriter playerListCSV = null;
		FileWriter playerListHtml = null;

		// Data location
		String dataPath = "C:\\Users\\Fixe-Maxime\\Google Drive\\8e\\Serveur cav gf\\";

		String configPath = dataPath + "Config\\";
		String logPath = dataPath + "Log\\";
		String resPath = dataPath + "Resultats\\";

		String adminListPath = configPath + "adminsList.txt";
		String invalidCommandPath = resPath + "invalidCommand.txt";
		String adminCoPath = resPath + "adminCo.txt";
		String permBanPath = resPath + "permBan.txt";
		String playersListPath = resPath + "playersList.txt";
		String shootLanceTkRecordPath = resPath + "shootLanceTkRecord.txt";
		String adminCoResPath = resPath + "adminCoRes.txt";
		String chatPath = resPath + "chatLog.txt";
		String playersListPathCSV = resPath + "playersListCSV.csv";
		String playersListPathHtml = resPath + "playersListHTML.html";
		String playersListPathSer = resPath + "playersList_";

		Calendar current_date = Calendar.getInstance();
		// jour de debut et de fin de parsing
		Calendar debut = Calendar.getInstance();
		//debut.set(2013, Calendar.DECEMBER, 10);
		debut.set(current_date.get(Calendar.YEAR), current_date.get(Calendar.MONTH), current_date.get(Calendar.DAY_OF_MONTH)-30, 0, 0, 0);//current_date.get(Calendar.DAY_OF_MONTH)-8);
		Calendar fin = Calendar.getInstance();
		// +1 to get the last file, dunno why the <= in the if doesnt work
		//fin.set(2013, Calendar.DECEMBER, 15);
		//fin.set(current_date.get(Calendar.YEAR), current_date.get(Calendar.MONTH), 31 + 1, 0, 0, 0);
		fin.set(current_date.get(Calendar.YEAR), current_date.get(Calendar.MONTH), current_date.get(Calendar.DAY_OF_MONTH), 0, 0, 0);;
		// Percentage of the max number of kills under witch the player is strip from the playersList for the ratio ranking
		// double percentageKill = 0.2;
		// Limit of player to strip
		final int limit = 100;

		adminsList adminsList = new adminsList(adminListPath);
		
		Calendar dateSer = Calendar.getInstance();
		dateSer.setTimeInMillis(debut.getTimeInMillis());
		/*FilenameFilter javaFilterSer = new FilenameFilter() {

			public boolean accept(File arg0, String arg1) {
				return arg1.endsWith(".ser");
			}
		};
		File ser = new File(resPath);
		String[] filesSer = ser.list(javaFilterSer);
		System.out.println("Retrieve serialized with file : " + filesSer[0]);
		// Retrieve date
		int slength = filesSer[0].length();
		// January is month number 0 so we set with month-1
		// +1 to get the last file, dunno why the <= in the if doesnt work
		dateSer.set(Integer.parseInt(filesSer[0].substring(slength-8, slength-4)), Integer.parseInt(filesSer[0].substring(slength-14, slength-12)) - 1, Integer.parseInt(filesSer[0].substring(slength-11, slength-9)) + 1);

		playersList playersList = new playersList(playersListPath, resPath + filesSer[0]);*/
		playersList playersList = new playersList(playersListPath);
		
		FilenameFilter javaFilterLog = new FilenameFilter() {

			public boolean accept(File arg0, String arg1) {
				return arg1.contains("server_log");
			}
		};
		FilenameFilter javaFilterRep = new FilenameFilter() {
			public boolean accept(File arg0, String arg1) {
				//String moisS = String.valueOf(mois);
				//if(mois < 10) moisS = "0" + moisS;
				Pattern pattern = Pattern.compile("Log \\d{2}-\\d{2}");
				return pattern.matcher(arg1).matches();
			}
		};
		File rep = new File(logPath);
		String[] repertoires = rep.list(javaFilterRep);
		List<String> fichiersLogsList = new ArrayList<>();
		String[] temp;

		for(int i = 0; i < repertoires.length ; i++) {
			File fichierslog = new File(logPath + repertoires[i].toString());
			temp = fichierslog.list(javaFilterLog);
			for(int k = 0; k < temp.length ; k++) {
				// On ajoute le nom du dossier parent dans le chemin du fichier
				temp[k] = repertoires[i] + "\\" + temp[k];
			}
			
			fichiersLogsList.addAll(Arrays.asList(temp));
		}

		// Effacement des pr�c�dents fichiers de log
		try{
			adminCo = new FileWriter(invalidCommandPath, false);
			invalidCommand = new FileWriter(adminCoPath, false);
			permBan = new FileWriter(permBanPath, false);
			playerList = new FileWriter(playersListPath, false);
			shootLanceTkRecord = new FileWriter(shootLanceTkRecordPath, false);
			adminCoRes = new FileWriter(adminCoResPath, false);
			chatRecord = new FileWriter(chatPath, false);
			playerListCSV = new FileWriter(playersListPathCSV, false);
			playerListHtml = new FileWriter(playersListPathHtml, false);

			adminCo.write(fichiersLogsList.size() + " fichiers de log trouv�s pour le parsing\n\n");
			invalidCommand.write(fichiersLogsList.size() + " fichiers de log trouv�s pour le parsing\n\n");
			permBan.write(fichiersLogsList.size() + " fichiers de log trouv�s pour le parsing\n\n");
			playerList.write(fichiersLogsList.size() + " fichiers de log trouv�s pour le parsing\n\n");
			shootLanceTkRecord.write(fichiersLogsList.size() + " fichiers de log trouv�s pour le parsing\n\n");
			adminCoRes.write(fichiersLogsList.size() + " fichiers de log trouv�s pour le parsing\n\n");
			chatRecord.write(fichiersLogsList.size() + " fichiers de log trouv�s pour le parsing\n\n");
			playerListCSV.write("ID;NbCo;Tag;Kill;Death;Ratio;KS\n");
		}
		catch(IOException ex) {
			ex.printStackTrace();
		} 
		finally {
			if(adminCo != null){
				try {
					adminCo.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(invalidCommand != null){
				try {
					invalidCommand.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(permBan != null){
				try {
					permBan.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(playerList != null){
				try {
					playerList.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(shootLanceTkRecord != null){
				try {
					shootLanceTkRecord.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(adminCoRes != null){
				try {
					adminCoRes.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(chatRecord != null){
				try {
					chatRecord.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(playerListCSV != null){
				try {
					playerListCSV.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(playerListHtml != null){
				try {
					playerListHtml.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		int lonLog, dayLog, moisLog, anLog;
		double time = System.currentTimeMillis();
		Calendar dateLog = Calendar.getInstance();
		Calendar lastdateLogUsed = Calendar.getInstance();
		for(int i=0;i<fichiersLogsList.size();i++) {
			lonLog = fichiersLogsList.get(i).length();
			dayLog = Integer.parseInt(fichiersLogsList.get(i).substring(lonLog-9, lonLog-7));
			moisLog = Integer.parseInt(fichiersLogsList.get(i).substring(lonLog-12, lonLog-10));
			//System.out.println(fichiersLogsList.get(i));
			anLog = Integer.parseInt("20" + fichiersLogsList.get(i).substring(lonLog-6, lonLog-4));
			// January is month number 0 so we set with month-1
			dateLog.set(anLog, moisLog - 1, dayLog, 0, 0, 1);
			// Check if date is after date of the serialized file !
			//System.out.println(dateLog.compareTo(dateSer) > 0);
			//System.out.println(debut.getTime().toString() + "         " +  fin.getTime().toString() + "        " + dateLog.getTime().toString());
			//System.out.println((dateLog.compareTo(debut)) + "     " + (dateLog.compareTo(fin)) + "     " + (dateLog.compareTo(dateSer)));
			//System.out.println((dateLog.compareTo(debut) > 0) + "     " + (dateLog.compareTo(fin) < 0) + "     " + (dateLog.compareTo(dateSer) > 0) + "       " + (dateLog.compareTo(debut) > 0 && dateLog.compareTo(fin) < 0 && dateLog.compareTo(dateSer) > 0));
			//System.out.println(dateSer.getTime() + " " + dateLog.getTime());
			if(dateLog.compareTo(debut) > 0 && dateLog.compareTo(fin) < 0 && dateLog.compareTo(dateSer) > 0) {
				System.out.println(fichiersLogsList.get(i) + " va �tre pars�");
				Parsing par = new Parsing(logPath + fichiersLogsList.get(i), adminsList, playersList);
				par.loadData(adminCoPath, shootLanceTkRecordPath, chatPath);
				par.printNonValidCommands(invalidCommandPath);
				par.printPermBanCommands(permBanPath);
				lastdateLogUsed.setTimeInMillis(dateLog.getTimeInMillis());
			}
		}
		System.out.println("FIN Parsing en : " + (System.currentTimeMillis() - time));
		time = System.currentTimeMillis();
		playersList.sortPlayersList();
		playersList.printPlayersList(playersListPath);
		adminsList.printAdminList(adminCoResPath);
		playersList.printPlayersListCSV(playersListPathCSV);
		playersList bestKilleur = playersList.stripPlayersList(limit);
		bestKilleur.printPlayersListHtml(playersListPathHtml);
		System.out.println("FIN files generation : " + (System.currentTimeMillis() - time));

		// Serialize the playerList object, only in full mode
		/*try{
			// Serialize data object to a file
			FileOutputStream fileOut = new FileOutputStream(playersListPathSer + String.format("%02d", lastdateLogUsed.get(Calendar.MONTH) + 1) + "_" + String.format("%02d", lastdateLogUsed.get(Calendar.DAY_OF_MONTH)) + "_" + lastdateLogUsed.get(Calendar.YEAR) + ".ser");
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(playersList);
			out.close();
			fileOut.close();

		}
		catch (IOException e) {
			e.printStackTrace();
		}*/
		System.out.println("FIN serialization en : " + (System.currentTimeMillis() - time));
	}
}
