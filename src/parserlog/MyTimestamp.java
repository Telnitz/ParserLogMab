package parserlog;

public class MyTimestamp implements Comparable<MyTimestamp> {

	private String hours;
	private String minutes;
	private String secondes;

	private String day;
	private String month;
	private String years;
	public String getHours() {
		return hours;
	}

	public MyTimestamp(String hours, String minutes, String secondes, String day,
			String month, String years) {
		this.hours = hours;
		this.minutes = minutes;
		this.secondes = secondes;
		this.day = day;
		this.month = month;
		this.years = years;
	}

	public void setHours(String hours) {
		this.hours = hours;
	}
	public String getMinutes() {
		return minutes;
	}
	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}
	public String getSecondes() {
		return secondes;
	}
	public void setSecondes(String secondes) {
		this.secondes = secondes;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public String getYears() {
		return years;
	}
	public void setYears(String years) {
		this.years = years;
	}

	public String format() {
		return (this.day + "/" +
				this.month + "/" +
				this.years + " " +
				this.hours + ":" +
				this.minutes + ":" +
				this.secondes);
	}

	// compare uniquement les jours pour le moment
	public int compareTo(MyTimestamp t) {
		int nombre1 = Integer.parseInt(t.getDay()); 
		int nombre2 = Integer.parseInt(this.getDay()); 
		if (nombre1 > nombre2)  return 1; 
		else if(nombre1 == nombre2) return 0; 
		else return -1; 
	}
}
