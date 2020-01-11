package urlshortener.service;
import java.security.SecureRandom;


public class SecureHash {

	    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
	    private static final String NUMBER = "0123456789";

	    private static final String DATA_FOR_RANDOM_STRING = CHAR_LOWER + NUMBER;
	    
	    private static SecureRandom random = new SecureRandom();


		/**
		 * Generates recursively a random string of size 'length'
		 * 
		 * @param length final length of the string
		 * @return random string
		 */
	    public String generateRandomString(int length) {
	        if (length < 1) throw new IllegalArgumentException();

	        StringBuilder sb = new StringBuilder(length);
	        for (int i = 0; i < length; i++) {
	            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
	            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
	            sb.append(rndChar);
	        }
	        return sb.toString();
	    }

	}


