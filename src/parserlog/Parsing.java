package parserlog;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import parserlog.Commande.adminCommand;


public class Parsing {

	private static String dataLocation;
	private List<Commande> commands = new ArrayList<>();
	private adminsList admins;
	private playersList players;

	Parsing(String cheminFichier, adminsList adminsList, playersList playersList) {
		this.admins = adminsList;
		this.players = playersList;
		dataLocation = cheminFichier;
	}

	public adminsList getAdmins() {
		return admins;
	}
	
	public void loadData(String adminCoPath, String shootLanceRecordPath)
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
					// Lignes de lance
					if(line.contains("<img=ico_spear>")) {
						parseLance(line, shootLanceRecordPath);
						ligneExploitee = true;
					}
					// Lignes de tir
					if(line.contains("<img=ico_crossbow>")) {
						parseTir(line, shootLanceRecordPath);
						ligneExploitee = true;
					}
					if(ligneExploitee == false) {
						//System.out.println("Ligne non parsée : " + line);
					}
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
			players.getPlayersList().get(players.findTag(tab[3])).incrNbKill();
		} catch (Exception e) {
			System.out.println("parseKill : Echec en parsant un kill : " + tab[3] + " est inconnu " + s);
		}
		try {
			players.getPlayersList().get(players.findTag(tab[5])).incrNbDead();
		} catch (Exception e) {
			System.out.println("parseKill : Echec en parsant un kill : " + tab[5] + " est inconnu " + s);
		}
	}

	// 13:13:11 - LEhamamHD <img=ico_spear> Zhupan_Cav_Skibbz
	private void parseLance(String s, String shootLanceRecordPath) {
		String[] tab = s.split(" ");

		FileWriter writer = null;
		try{
			writer = new FileWriter(shootLanceRecordPath, true);
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
