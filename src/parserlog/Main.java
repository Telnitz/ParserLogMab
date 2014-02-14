package parserlog;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

	public static void main(String[] args) {

		FileWriter adminCo = null;
		FileWriter invalidCommand = null;
		FileWriter permBan = null;
		FileWriter playerList = null;
		FileWriter shootLanceRecord = null;
		FileWriter playerListCSV = null;
		FileWriter playerListCSVRatio = null;
		
		String invalidCommandPath = "invalidCommand.txt";
		String adminCoPath = "adminCo.txt";
		String permBanPath = "permBan.txt";
		String playersListPath = "playersList.txt";
		String shootLanceRecordPath = "shootLanceRecord.txt"; // TODO : aussi defini dans parsing, a supp
		String playersListPathCSV = "playersListCSV.csv";
		String playersListPathCSVRatio = "playersListCSVRatio.csv";
		

		// jour de debut et de fin de parsion
		int debutPar = 13;
		int finPar = 31;
		// mois du parsing
		int mois = 2;
		// Percentage of the max number of kills under witch the player is strip from the playersList for the ratio ranking
		//double percentageKill = 0.2;
		// Limit of player to strip
		int limit = 20;
		
		adminsList adminsList = new adminsList("C:\\Users\\Galloux\\Google Drive\\8e\\Serveur cav gf\\Config\\" + "adminsList.txt");

		playersList playersList = new playersList(args[0] + playersListPath);

		FilenameFilter javaFilterLog = new FilenameFilter() {

			public boolean accept(File arg0, String arg1) {
				return arg1.contains("server_log");
			}
		};
		FilenameFilter javaFilterRep = new FilenameFilter() {

			public boolean accept(File arg0, String arg1) {
				return arg1.contains("Log");
			}
		};
		File rep = new File(args[0]);
		String[] repertoires = rep.list(javaFilterRep);
		List<String> fichiersLogsList = new ArrayList<>();
		String[] temp;

		for(int i = 0; i < repertoires.length ; i++) {
			File fichierslog = new File(args[0] + repertoires[i].toString());
			temp = fichierslog.list(javaFilterLog);
			for(int k = 0; k < temp.length ; k++) {
				// On ajoute le nom du dossier parent dans le chemin du fichier
				temp[k] = repertoires[i] + "\\" + temp[k];
			}
			fichiersLogsList.addAll(Arrays.asList(temp));
		}

		// Effacement des précédents fichiers de log
		try{
			adminCo = new FileWriter(invalidCommandPath, false);
			invalidCommand = new FileWriter(adminCoPath, false);
			permBan = new FileWriter(permBanPath, false);
			playerList = new FileWriter(playersListPath, false);
			shootLanceRecord = new FileWriter(shootLanceRecordPath, false);
			playerListCSV = new FileWriter(playersListPathCSV, false);
			playerListCSVRatio = new FileWriter(playersListPathCSVRatio, false);

			adminCo.write(fichiersLogsList.size() + " fichiers de log trouvés pour le parsing\n\n");
			invalidCommand.write(fichiersLogsList.size() + " fichiers de log trouvés pour le parsing\n\n");
			permBan.write(fichiersLogsList.size() + " fichiers de log trouvés pour le parsing\n\n");
			playerList.write(fichiersLogsList.size() + " fichiers de log trouvés pour le parsing\n\n");
			shootLanceRecord.write(fichiersLogsList.size() + " fichiers de log trouvés pour le parsing\n\n");
			playerListCSV.write("Id;nb Connexion;Pseudo;nb de tues;nb de morts;Ratio\n");
			playerListCSVRatio.write("Id;nb Connexion;Pseudo;nb de tues;nb de morts;Ratio\n");
			
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
			if(shootLanceRecord != null){
				try {
					shootLanceRecord.close();
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
			if(playerListCSVRatio != null){
				try {
					playerListCSVRatio.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		int dayLog, lonLog, moisLog;
		for(int i=0;i<fichiersLogsList.size();i++) {
			lonLog = fichiersLogsList.get(i).length();
			dayLog = Integer.parseInt(fichiersLogsList.get(i).substring(lonLog-9, lonLog-7));
			moisLog = Integer.parseInt(fichiersLogsList.get(i).substring(lonLog-12, lonLog-10));
			if( debutPar <= dayLog  && dayLog <= finPar && moisLog == mois) {
				System.out.println(fichiersLogsList.get(i) + " va être parsé");
				Parsing par = new Parsing(args[0] + fichiersLogsList.get(i), adminsList, playersList);
				par.printNonValidCommands(invalidCommandPath);
				par.printPermBanCommands(permBanPath);
			}
		}
		playersList.sortPlayersList();
		playersList.printPlayersList(playersListPath);
		playersList.printPlayersListCSV(playersListPathCSV);
		playersList bestKilleur = playersList.stripPlayersList(limit);
		bestKilleur.sortPlayersListRatio();
		bestKilleur.printPlayersListCSV(playersListPathCSVRatio);
		System.out.println("FIN");
	}
}
