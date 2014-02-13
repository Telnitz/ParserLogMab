package parserlog;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class player implements Comparable<player> {

	private List<String> names;
	private String adminName; 
	private final int id;
	private int nbConnection;
	private int nbKill;
	private int nbDead;

	public player(String name, int id) {
		names = new ArrayList<>();
		names.add(name);
		this.id = id;
		this.setNbConnection(0);
	}

	public List<String> getNames() {
		return names;
	}

	public void addName(String name) {
		if(!isInNamesEqual(name)) {
			names.add(name);
		}
	}

	// Pseudo contenu dans un autre
	// depreacated
	/*public boolean isInNames(String name) {
		boolean found = false;
		int index = 0;
		int l = this.names.size();

		String fp = this.names.get(index);
		while(!found && index < l) {
			fp = this.names.get(index);
			index++;
			if(fp.contains(name) || name.contains(fp)) {
				found = true;
			}
		}
		return found;
	}*/
	
	// Pseudo exactement identiques
	public boolean isInNamesEqual(String name) {
		boolean found = false;
		int index = 0;
		int l = this.names.size();

		String fp = this.names.get(index);
		while(!found && index < l) {
			fp = this.names.get(index);
			index++;
			if(fp.equals(name)) {
				found = true;
			}
		}
		return found;
	}

	public int getId() {
		return id;
	}

	public int getNbConnection() {
		return nbConnection;
	}

	public void setNbConnection(int nbConnection) {
		this.nbConnection = nbConnection;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public void incrNbConnection() {
		this.nbConnection++;
	}

	public int getNbKill() {
		return nbKill;
	}

	public void setNbKill(int nbKill) {
		this.nbKill = nbKill;
	}

	public void incrNbKill() {
		this.nbKill++;
	}

	public int getNbDead() {
		return nbDead;
	}

	public void setNbDead(int nbDead) {
		this.nbDead = nbDead;
	}

	public void incrNbDead() {
		this.nbDead++;
	}

	public float computeRatio() {
		if(nbDead != 0) {
			return (float)nbKill/(float)nbDead;
		}
		else {
			//System.out.println("Warning ratio infini pour le joueur : " + names.get(0) + ", on renvoie un ratio nul");
			return 0;
		}
	}

	public void printPlayer(String path) {
		FileWriter writer = null;
		try{
			writer = new FileWriter(path, true);
			writer.write(id + " : ");
			writer.write(nbConnection + " ");
			for(int i = 0; i < names.size(); i++) {
				writer.write(names.get(i) + " | ");
			}
			DecimalFormat df = new DecimalFormat("0.0#");
			String ratio = df.format(computeRatio());
			writer.write(nbKill + " " + nbDead + " " + ratio);
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
	
	public void printPlayerCSV(String path) {
		FileWriter writer = null;
		try{
			writer = new FileWriter(path, true);
			writer.write(id + ";");
			writer.write(nbConnection + ";");
			for(int i = 0; i < names.size(); i++) {
				writer.write(names.get(i) + " ");
			}
			DecimalFormat df = new DecimalFormat("0.0#");
			String ratio = df.format(computeRatio());
			writer.write(";" + nbKill + ";" + nbDead + ";" + ratio);
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

	public int compareTo(player p) {
		int nombre1 = p.getNbKill(); 
		int nombre2 = this.getNbKill(); 
		if (nombre1 > nombre2)  return 1; 
		else if(nombre1 == nombre2) return 0; 
		else return -1; 

	}

}
