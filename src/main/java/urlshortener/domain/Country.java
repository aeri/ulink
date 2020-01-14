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

	
	/** 
	 * @return String
	 */
	public String getName() {
		return name;
	}
	
	
	/** 
	 * @return String
	 */
	public String getId() {
		return id;
	}

	
	/** 
	 * @return int
	 */
	public int getCounter() {
		return value;
	}
	
	
	/** 
	 * @return String
	 */
	public String getFill() {
		return fill;
	}

}
