package parserlog;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

public class admin extends player {

	private List<String> adminNames; 

	public admin(String name, int id) {
		super(name, id);		
	}
	
	/*public admin() {
		adminNames = new ArrayList<>();
	}*/
	
	public List<String> getAdminNames() {
		return adminNames;
	}

	public void addAdminName(String adminName) {
		if(!Utils.isInNamesEqual(this, adminName)) {
			adminNames.add(adminName);
		}
	}

	public void printAdmin(String path) {
		FileWriter writer = null;
		try{
			writer = new FileWriter(path, true);
			for(int i = 0; i < adminNames.size(); i++) {
				writer.write(adminNames.get(i) + " | ");
			}
			DecimalFormat df = new DecimalFormat("0.0#");
			String ratio = df.format(this.computeRatio());
			writer.write(this.getNbKill() + " " + this.getNbDead() + " " + ratio);
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
