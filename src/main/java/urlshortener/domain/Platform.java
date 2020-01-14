package urlshortener.domain;


public class Platform {

	private String name;
	private int counter;

	public Platform(String name, int counter) {
		this.name = name;
		this.counter = counter;
	}

	
	/** 
	 * @return String
	 */
	public String getName() {
		return name;
	}

	
	/** 
	 * @return int
	 */
	public int getCounter() {
		return counter;
	}

}
