package urlshortener.domain;


public class Browser {

	private String name;
	private int counter;

	public Browser(String name, int counter) {
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
