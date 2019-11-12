package urlshortener.domain;


public class Platform {

	private String name;
	private int counter;

	public Platform(String name, int counter) {
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
