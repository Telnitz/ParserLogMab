package parserlog;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public final class playersList implements java.io.Serializable {

	private static final long serialVersionUID = -5850802717559186171L;

	private final List<player> playersList;
	private final String playerListpath;

	public playersList(String adString) {
		this.playerListpath = adString;
		this.playersList = new ArrayList<>();
	}

	public playersList(String adString, List<player> newplayersList) {
		this.playerListpath = adString;
		this.playersList = newplayersList;
	}

	// Retrieve playerList from a serialized object
	public playersList(String adString, String serFileName) {
		playersList playerListSer = null;
		try{ 
			FileInputStream fileIn = new FileInputStream(serFileName);
			ObjectInputStream reader = new ObjectInputStream(fileIn);
			playerListSer = (playersList) reader.readObject();
			reader.close();
			fileIn.close();
		}
		catch(IOException i)
		{
			i.printStackTrace();
		}
		catch(ClassNotFoundException c)
		{
			System.out.println("ERROR : Deserialization failed " + adString + " " + serFileName); 
			c.printStackTrace();
		}
		
		this.playerListpath = adString;
		this.playersList = playerListSer.getPlayersList();
	}

	public List<player> getPlayersList() {
		return playersList;
	}

	public String getPlayerListpath() {
		return playerListpath;
	}

	public void addPlayer(String name, int id) {
		if(!isInList(id)) {
			player p = new player(name, id);
			p.incrNbConnection();
			playersList.add(p);
		}
		else {
			int player = findIndex(id);
			playersList.get(player).addName(name);
			playersList.get(player).incrNbConnection();
		}
	}

	// Renvoie true si l'id est dans la liste
	public boolean isInList(int id) {
		boolean found = false;
		int index = 0;
		int l = this.playersList.size();
		if(!playersList.isEmpty()) {
			player fp = this.playersList.get(index);
			while(!found && index < l) {
				fp = this.playersList.get(index);
				index++;
				if(fp.getId() == id) {
					found = true;
				}
			}
		}
		return found;
	}

	// If isInList = true
	// Return l'index de cet id
	public int findIndex(int id) {
		boolean found = false;
		int index = 0;
		int l = this.playersList.size();

		player fp = this.playersList.get(index);
		while(!found && index < l) {
			fp = this.playersList.get(index);
			index++;
			if(fp.getId() == id) {
				found = true;
			}
		}
		return index - 1;
	}

	// return le premier nom enregistre pour cet ID
	public String findName(int id) throws Exception {
		boolean found = false;
		int index = 0;
		int l = this.playersList.size();

		if(!this.playersList.isEmpty()) {
			player fp = this.playersList.get(index);
			while(!found && index < l) {
				fp = this.playersList.get(index);
				index++;
				if(fp.getId() == id) {
					found = true;
				}
			}
			if(found) {
				return fp.getNames().get(0);
			}
			else {
				throw new AdminListException("findName : pas de joueur déjà enregistré pour cet id : " + id);
			}
		}
		else {
			throw new Exception("findName : pas de joueur déjà enregistré pour cet id : " + id);
		}
	}

	// Return l'index correspondant EXACTEMENT a ce tag
	public int findTag(String s) throws Exception {
		boolean found = false;
		int index = 0;
		int l = this.playersList.size();
		if(!this.playersList.isEmpty()) {
			player fp = this.playersList.get(index);
			while(!found && index < l) {
				fp = this.playersList.get(index);
				index++;
				if(fp.isInNamesEqual(s)) {
					found = true;
				}
			}
			if(found) {
				return index - 1;
			}
			else {
				throw new AdminListException("findTag : pas de joueur déjà enregistré pour ce tag : " + s);
			}
		}
		else {
			throw new Exception("findTag : pas de joueur déjà enregistré pour ce tag : " + s);
		}
	}

	public void sortPlayersList() {
		Collections.sort(playersList);	
	}

	// Strip players under a certain percentage of best score
	@Deprecated
	public playersList stripPlayersListKill(double percentage) {
		this.sortPlayersList();
		boolean found = false;
		int l = playersList.size();
		int i = 0;
		int killMin = (int)(percentage * playersList.get(0).getNbKill());
		while(!found && i < l) {
			if(playersList.get(i).getNbKill() < killMin) {
				found = true;
			}
			else {
				i++;
			}
		}
		return new playersList(playerListpath, playersList.subList(0, i));
	}

	// Restrict the number of player
	public playersList stripPlayersList(int limit) {
		int limitation = limit;
		if (limit > playersList.size()) {
			System.out.println("WARNING stripPlayersList : Pas assez de joueur " + playersList.size());
			limitation = playersList.size();
		}
		return new playersList(playerListpath, playersList.subList(0, limitation));
	}

	// Sort by ratio
	public void sortPlayersListRatio() {
		Comparator<player> compareRatio = new Comparator<player>() {
			@Override
			public int compare(player o1, player o2) {
				double nombre1 = o1.computeRatio(); 
				double nombre2 = o2.computeRatio(); 
				if (nombre1 < nombre2)  return 1; 
				else if(nombre1 == nombre2) return 0; 
				else return -1; 
			}
		};
		Collections.sort(playersList, compareRatio);	
	}

	public void printPlayersList(String path) {
		for(int i = 0; i < playersList.size(); i++) {
			playersList.get(i).printPlayer(path);
		}
	}

	public void printPlayersListCSV(String path) {
		for(int i = 0; i < playersList.size(); i++) {
			playersList.get(i).printPlayerCSV(path);
		}
	}


}
