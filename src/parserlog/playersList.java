package parserlog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class playersList {

	private final List<player> playersList;
	private final String playerListpath;

	public playersList(String adString) {
		this.playerListpath = adString;
		this.playersList = new ArrayList<>();
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
