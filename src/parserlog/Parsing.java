package parserlog;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import parserlog.Commande.adminCommand;


public class Parsing {

	private static String dataLocation;
	private List<Commande> commands = new ArrayList<>();
	private adminsList admins;
	private playersList players;
	private int prevKilleurId;

	Parsing(String cheminFichier, adminsList adminsList, playersList playersList) {
		this.admins = adminsList;
		this.players = playersList;
		dataLocation = cheminFichier;
	}

	public adminsList getAdmins() {
		return admins;
	}

	public void loadData(String adminCoPath, String shootLanceTkRecordPath, String chatPath)
	{
		try{
			BufferedReader buff = new BufferedReader(new FileReader(dataLocation));
			try {
				String line;
				Boolean ligneExploitee;
				while ((line = buff.readLine()) != null) {
					ligneExploitee = false;
					// regle un prob qui fait que la 1er ligne du fichier de log ne commence pas par un espace
					if(!line.startsWith(" ")) {line = " " + line;}
					// Lignes des commandes admins
					if(line.contains("[SERVER]")) {
						parseServer(line);
						ligneExploitee = true;
					}
					// Lignes du chat admin
					if(line.contains("*Admin*")) {
						parseChat(line, chatPath);
						ligneExploitee = true;
					}
					// Lignes du chat dead
					if(line.contains("*DEAD*")) {
						parseChat(line, chatPath);
						ligneExploitee = true;
					}
					// Lignes de deco
					if(line.contains("has left the game")) {
						parseLeft(line);
						ligneExploitee = true;
					}
					// Lignes de co
					if(line.contains("has joined the game")) {
						parseJoin(line);
						ligneExploitee = true;
						// Lignes de connection des admins
						if(line.contains("administrator rights")) {
							parseAdminCo(line, adminCoPath);
						}
					}
					// Lignes de kill
					if(line.contains("<img=ico_swordone>")) {
						parseKill(line);
						ligneExploitee = true;
					}
					// Ligne de teamkill
					if(line.contains(" teamkilled ") && line.endsWith(". ")) {
						parseTk(line, shootLanceTkRecordPath);
						ligneExploitee = true;
					}
					// Lignes de lance
					if(line.contains("<img=ico_spear>")) {
						parseLance(line, shootLanceTkRecordPath);
						ligneExploitee = true;
					}
					// Lignes de tir
					if(line.contains("<img=ico_crossbow>")) {
						parseTir(line, shootLanceTkRecordPath);
						ligneExploitee = true;
					}
					// Lignes de HS
					if(line.contains("<img=ico_headshot>")) {
						ligneExploitee = true;
					}
					if(ligneExploitee == false) {
						//System.out.println("Ligne non pars�e : " + line);
						parseChat(line, chatPath);
					}
					// 16:38:00 -  <img=ico_headshot> Jack_Spears 
					// 16:56:38 - Hencock <img=ico_horseimpact> 11e_Huss_Adj_Sheepy 

				}
			} finally {
				buff.close();
			}
		} catch (IOException ioe) {
			System.out.println("Erreur : " + ioe.toString());
		}
	}

	// 14:32:04 - [SERVER]: 1erCrb_Cpt_Martastik Slayed Player 35e_Cdt_ToToF. 
	private void parseServer(String s) {
		Commande c = new Commande();
		String[] tab = s.split(" ");

		int l = dataLocation.length();
		MyTimestamp time = new MyTimestamp(s.substring(1, 3), s.substring(4, 6), s.substring(7, 9), dataLocation.substring(l-9, l-7), dataLocation.substring(l-12, l-10), dataLocation.substring(l-6, l-4));
		c.setDate(time);
		c.setLog(s);

		c.setNameAdmin(tab[4]);

		switch(tab[5])
		{
		case "Slayed" :
			c.setCommand(Commande.adminCommand.slay);
			c.setNamePlayer(tab[tab.length - 1]);
			break;
		case "Kicked" :
			c.setCommand(Commande.adminCommand.kick);
			c.setNamePlayer(tab[tab.length - 1]);
			break;
		case "Temporary" :
			c.setCommand(Commande.adminCommand.tempBan);
			c.setNamePlayer(tab[tab.length - 1]);
			break;
		case "Permanently" :
			c.setCommand(Commande.adminCommand.permBan);
			c.setNamePlayer(tab[tab.length - 1]);
			break;
		case "Swapped" :
			c.setCommand(Commande.adminCommand.swap);
			c.setNamePlayer(tab[tab.length - 1]);
			break;
		default:
			c.setCommand(Commande.adminCommand.other);
		}
		commands.add(c);
	}

	// 14:11:47 - 1erCrb_Cpt_Martastik has joined the game with ID: 306854 and has administrator rights. 
	private void parseAdminCo(String s, String adminCoPath) {
		String[] tab = s.split(" ");
		int l = dataLocation.length();
		MyTimestamp time = new MyTimestamp(s.substring(1, 3), s.substring(4, 6), s.substring(7, 9), dataLocation.substring(l-9, l-7), dataLocation.substring(l-12, l-10), dataLocation.substring(l-6, l-4));
		this.admins.isValid(tab[3], Integer.parseInt(tab[10]), dataLocation, time, adminCoPath);
	}

	// 01:09:28 - 12e_Sdt_nezit has joined the game with ID: 634632  
	private void parseJoin(String s) {
		String[] tab = s.split(" ");
		// Check if Betty
		if(tab[3].contains("Betty")) {
			System.out.println("WARNING : Betty connection : " + s);
		}
		players.addPlayer(tab[3], Integer.parseInt(tab[10]));
	}

	// 01:08:16 - IVe_7e_Huss_Cvl_Elite has left the game with ID: 493393 
	private void parseLeft(String s) {

	}

	// 18:20:31 - 90th_Loth_CoH_Louis <img=ico_swordone> 2aBH_7oDN_Sgt_DarkLight 
	private void parseKill(String s) {
		String[] tab = s.split(" ");
		try {
			int killeurId = players.findTag(tab[3]);
			//System.out.println("K" + killeurId + " " + players.getPlayersList().get(killeurId).getNames().get(0));
			int deadId = players.findTag(tab[5]);
			//System.out.println("D" + deadId + " " + players.getPlayersList().get(deadId).getNames().get(0));
			players.getPlayersList().get(killeurId).incrkillStreak();
			if(killeurId != prevKilleurId)
			{
				if(players.getPlayersList().get(prevKilleurId).getkillStreak() > players.getPlayersList().get(prevKilleurId).getbestKillStreak())
				{
					players.getPlayersList().get(prevKilleurId).setbestKillStreak(players.getPlayersList().get(prevKilleurId).getkillStreak());
				}
				players.getPlayersList().get(prevKilleurId).setkillStreak(0);
				prevKilleurId = killeurId; 	
			}
			players.getPlayersList().get(killeurId).incrNbKill();
			players.getPlayersList().get(deadId).incrNbDead();

		} catch (Exception e) {
			System.out.println("parseKill : Echec en parsant un kill : " + s);
		}
	}

	// 23:27:29 - 8e_Huss_Cvl_Pompo teamkilled 2nd_Cav_LCpl_twisted. 
	private void parseTk(String s, String shootLanceTkRecordPath) {
		FileWriter writer = null;
		// Remove the ". " at the end of the line
		s = s.substring(0, s.length()-2);
		String[] tab = s.split(" ");
		if(prevKilleurId!= 0)
			players.getPlayersList().get(prevKilleurId).setkillStreak(0);
		try {
			int killeurId = players.findTag(tab[3]);
			players.getPlayersList().get(killeurId).setkillStreak(0);
			players.getPlayersList().get(killeurId).decrNbKill();
			writer = new FileWriter(shootLanceTkRecordPath, true);
			writer.write("Teamkill : " + dataLocation.substring(dataLocation.length() - 12, dataLocation.length() - 4) + " " + s);
			try {
				//  510578 # [**] is banned permanently by 8e_Huss_Mchl_TelnitzLog.
				writer.write(" (a verifier avant de perm ban !) : " + players.getPlayersList().get(killeurId).getId() + " # " + tab[3] + " is banned permanently by 8e_Huss_Mchl_TelnitzLog." );
			} catch (Exception e) {
				System.out.println("parseTeamKill : Echec en parsant un kill : " + tab[5] + " est inconnu " + s);
			}
			writer.write("\n");
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
		catch (Exception e) {
			System.out.println("parseTeamKill : Echec en parsant un teamkill : " + tab[3] + " est inconnu " + s);
		}
		finally {
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 13:13:11 - LEhamamHD <img=ico_spear> Zhupan_Cav_Skibbz
	private void parseLance(String s, String shootLanceTkRecordPath) {
		String[] tab = s.split(" ");
		FileWriter writer = null;
		try{
			writer = new FileWriter(shootLanceTkRecordPath, true);
			writer.write("LANCE : " + dataLocation.substring(dataLocation.length() - 12, dataLocation.length() - 4) + " " + s);
			try {
				//  510578 # [**] is banned permanently by 8e_Huss_Mchl_TelnitzLog.
				writer.write(" (a verifier avant de perm ban !) : " + players.getPlayersList().get(players.findTag(tab[3])).getId() + " # " + tab[3] + " is banned permanently by 8e_Huss_Mchl_TelnitzLog." );
			} catch (Exception e) {
				System.out.println("parseLance : Echec en parsant un kill : " + tab[5] + " est inconnu " + s);
			}
			writer.write("\n");
		}
		catch(IOException ex) {
			ex.printStackTrace();
		} 
		finally {
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 13:12:56 - Zhupan_Cav_Skibbz <img=ico_crossbow> Bellator 
	private void parseTir(String s, String shootLanceRecordPath) {
		String[] tab = s.split(" ");

		FileWriter writer = null;
		try{
			writer = new FileWriter(shootLanceRecordPath, true);
			writer.write("TIR : " + dataLocation.substring(dataLocation.length() - 12, dataLocation.length() - 4) + " " + s);
			try {
				//  510578 # [**] is banned permanently by 8e_Huss_Mchl_TelnitzLog.
				writer.write(" (a verifier avant de perm ban !) : " + players.getPlayersList().get(players.findTag(tab[3])).getId() + " # " + tab[3] + " is banned permanently by 8e_Huss_Mchl_TelnitzLog." );
			} catch (Exception e) {
				System.out.println("parseTir : Echec en parsant un kill : " + tab[5] + " est inconnu " + s);
			}
			writer.write("\n");
		}
		catch(IOException ex) {
			ex.printStackTrace();
		} 
		finally {
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void parseChat(String s, String path) {
		FileWriter writer = null;
		try{
			writer = new FileWriter(path, true);
			writer.write(s + "\n");
		}
		catch(IOException ex) {
			ex.printStackTrace();
		} 
		finally {
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}	
	}

	public void printNonValidCommands(String path) {
		Commande c = new Commande();
		FileWriter writer = null;
		try{
			writer = new FileWriter(path, true);
			for(int i = 0; i < commands.size(); i++) {
				c= commands.get(i);
				if(!c.isValid()) {
					writer.write(c.format() + "\n");
				}
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
		} 
		finally {
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void printPermBanCommands(String path) {
		Commande c = new Commande();
		FileWriter writer = null;
		try{
			writer = new FileWriter(path, true);
			for(int i = 0; i < commands.size(); i++) {
				c= commands.get(i);
				if(c.getCommand() == adminCommand.permBan) {
					writer.write(c.format() + "\n");
				}
			}
		}
		catch(IOException ex) {
			ex.printStackTrace();
		} 
		finally {
			if(writer != null){
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void printAllCommands() {
		Commande c = new Commande();
		for(int i = 0; i < commands.size(); i++) {
			c= commands.get(i);
			System.out.println(c.getLog());
		}
	}
}
