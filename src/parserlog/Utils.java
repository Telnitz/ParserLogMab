package parserlog;

public class Utils {


	// Pseudo contenu dans un autre
	@Deprecated
	public static boolean isInNames(player p, String name) {
		boolean found = false;
		int index = 0;
		int l = p.getNames().size();

		String fp = p.getNames().get(index);
		while(!found && index < l) {
			fp = p.getNames().get(index);
			index++;
			if(fp.contains(name) || name.contains(fp)) {
				found = true;
			}
		}
		return found;
	}

	// Pseudo exactement identiques
	public static boolean isInNamesEqual(player p, String name) {
		boolean found = false;
		int index = 0;
		int l = p.getNames().size();

		String fp = p.getNames().get(index);
		while(!found && index < l) {
			fp = p.getNames().get(index);
			index++;
			if(fp.equals(name)) {
				found = true;
			}
		}
		return found;
	}

}
