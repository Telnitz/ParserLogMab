package parserlog;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class admin {

	private player joueur;

	public admin(player Joueur, int id) {
		this.joueur = Joueur;
	}
	
	public admin() {
	}

	public player getPlayer() {
		return joueur;
	}
	public void setPlayer(String name, int id) {
		joueur = new player(name, id);
	}
	
	public void printAdmin(String path) {
		FileWriter writer = null;
		try{
			writer = new FileWriter(path, true);
			writer.write(joueur.getAdminName() + " ");
			DecimalFormat df = new DecimalFormat("0.0#");
			String ratio = df.format(joueur.computeRatio());
			writer.write(joueur.getNbKill() + " " + joueur.getNbDead() + " " + ratio);
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
}
