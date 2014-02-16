package parserlog;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class adminsList {

	private final List<admin> adminsList;
	private final String adminListpath;

	public adminsList(String adString) {
		this.adminListpath = adString;
		this.adminsList = new ArrayList<>();
		buildAdminList();
	}

	public List<admin> getAdminsList() {
		return adminsList;
	}

	public void buildAdminList() {
		try{
			BufferedReader buff = new BufferedReader(new FileReader(adminListpath));
			try {
				String line;
				String[] tab;
				while ((line = buff.readLine()) != null) {
					admin ad = new admin();
					tab = line.split(" ");
					ad.setPlayer(tab[0],Integer.parseInt(tab[1]));
					ad.getPlayer().setAdminName(tab[0]);
					this.adminsList.add(ad);
				}
			} finally {
				buff.close();
			}
		} catch (IOException ioe) {
			System.out.println("Erreur --" + ioe.toString());
		}
	}

	public boolean isValid(String name, int id, String dataLocation, MyTimestamp time, String adminCoPath) {
		boolean found = false;
		int index = 0;
		int l = this.adminsList.size();
		if(!this.adminsList.isEmpty()) {
			admin fp = this.adminsList.get(index);
			while(!found && index < l) {
				fp = this.adminsList.get(index);
				index++;
				if(name.contains(fp.getPlayer().getAdminName())) {
					found = true;
					fp.getPlayer().incrNbConnection();
				}
			}
			FileWriter writer = null;
			try{
				writer = new FileWriter(adminCoPath, true);
				if(found) {
					// Verifie si on a le bon ID
					if(fp.getPlayer().getId() != id) {
						System.out.println("++++");
						writer.write(time.format() + " L'ID " + fp.getPlayer().getId() + " de la database et l'ID " + id + " des logs ne correspondent pas pour l'admin " + name + "\n");
					}
				}
				else {
					// on a pas trouvé l'admin par son pseudo, on essaye par son id
					try {
						writer.write(time.format() + " L'admin " + findName(id) +  " a utilisé un tag différent de celui autorisé : " + name + "\n");
					}
					catch(AdminListException e) {
						writer.write(time.format() + " Le tag " + name + " et l'ID " + id + " ne sont pas répertoriés dans la liste des admins\n");
					}
					catch (Exception e) {
						System.out.println(e);
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
		return found;
	}

	public String findName(int id) throws Exception {
		boolean found = false;
		int index = 0;
		int l = this.adminsList.size();

		if(!this.adminsList.isEmpty()) {
			admin fp = this.adminsList.get(index);
			while(!found && index < l) {
				fp = this.adminsList.get(index);
				index++;
				if(fp.getPlayer().getId() == id) {
					found = true;
				}
			}
			if(found) {
				return fp.getPlayer().getAdminName();
			}
			else {
				throw new AdminListException("findName : pas d'admin pour cet id : " + id);
			}
		}
		else {
			throw new Exception("findName : pas d'admin pour cet id : " + id);
		}
	}
}
