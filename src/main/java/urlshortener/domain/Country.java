package urlshortener.domain;


public class Country {

	private String name;
	private String id;
	private int value;
	private String fill;

	public Country(String id, String name, int counter) {
		this.name = name;
		this.value = counter;
		this.id = id;
		this.fill = "blue";
	}

	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}

	public int getCounter() {
		return value;
	}
	
	public String getFill() {
		return fill;
	}

}
