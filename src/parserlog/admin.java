package parserlog;

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
}
