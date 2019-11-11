package urlshortener.domain;


public class Browser {

	private String name;
	private int counter;

	public Browser(String name, int counter) {
		this.name = name;
		this.counter = counter;
	}

	public String getName() {
		return name;
	}

	public int getCounter() {
		return counter;
	}

}
