package urlshortener.service;
import java.security.SecureRandom;


public class SecureHash {

	    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	    private static final String NUMBER = "0123456789";

	    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + NUMBER;
	    
	    private static SecureRandom random = new SecureRandom();


	    public String generateRandomString(int length, String secure) {
	        if (length < 1) throw new IllegalArgumentException();

	        StringBuilder sb = new StringBuilder(length);
	        for (int i = 0; i < length; i++) {

				// 0-62 (exclusive), random returns 0-61
	            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
	            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);

	            // debug
	            System.out.format("%d\t:\t%c%n", rndCharAt, rndChar);

	            sb.append(rndChar);

	        }

	        return sb.toString();

	    }

	}


