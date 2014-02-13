package parserlog;

public class Commande {

	private String nameAdmin;
	private String namePlayer;
	public enum adminCommand {slay, tempBan, permBan, kick, swap, other};
	private adminCommand command;
	private MyTimestamp date;
	private String log;

	public Commande() {
	}

	public String getNameAdmin() {
		return nameAdmin;
	}
	public void setNameAdmin(String nameAdmin) {
		this.nameAdmin = nameAdmin;
	}
	public String getNamePlayer() {
		return namePlayer;
	}
	public void setNamePlayer(String namePlayer) {
		this.namePlayer = namePlayer;
	}
	public adminCommand getCommand() {
		return command;
	}
	public void setCommand(adminCommand command) {
		this.command = command;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public MyTimestamp getDate() {
		return date;
	}

	public void setDate(MyTimestamp date) {
		this.date = date;
	}
	
	public Boolean isValid() {
		return this.command != adminCommand.other;
	}
	
	public String format() {
		// On limite le nb de caractère a afficher
		return date.format() + log.substring(20, Math.min(log.length(), 120));
	}
}
