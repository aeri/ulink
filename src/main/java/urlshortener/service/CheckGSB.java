package urlshortener.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.safebrowsing.Safebrowsing;
import com.google.api.services.safebrowsing.model.ClientInfo;
import com.google.api.services.safebrowsing.model.FindThreatMatchesRequest;
import com.google.api.services.safebrowsing.model.FindThreatMatchesResponse;
import com.google.api.services.safebrowsing.model.ThreatEntry;
import com.google.api.services.safebrowsing.model.ThreatInfo;
import com.google.api.services.safebrowsing.model.ThreatMatch;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class CheckGSB {

	public static final String GOOGLE_APPLICATION_NAME = "ulink"; // appication name

	public static String key;

	@Autowired
	private Environment GOOGLE_API_KEY;

	public static final JacksonFactory GOOGLE_JSON_FACTORY = JacksonFactory.getDefaultInstance();
	public static final List<String> GOOGLE_PLATFORM_TYPES = Arrays.asList("ANY_PLATFORM");
	public static final List<String> GOOGLE_THREAT_ENTRYTYPES = Arrays.asList("URL");
	public static final List<String> GOOGLE_THREAT_TYPES = Arrays.asList("MALWARE", "SOCIAL_ENGINEERING",
			"UNWANTED_SOFTWARE", "POTENTIALLY_HARMFUL_APPLICATION");
	public static final String GOOGLE_CLIENT_ID = "1"; // client id
	public static final String GOOGLE_CLIENT_VERSION = "0.0.1"; // client version
	public static NetHttpTransport httpTransport;
	

	/**
	 * Gets environment variables from properties file
	 */
	@PostConstruct
	public void init() {
		key = GOOGLE_API_KEY.getProperty("google.api_key");
	}

	/**
	 * Checks a list of URLs safetiness using Google Safe Browsing
	 * 
	 * @param urls list of urls that are going to be checked
	 * @return resulting list of THreadMatch
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public List<ThreatMatch> check(List<String> urls) throws GeneralSecurityException, IOException {

		httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		FindThreatMatchesRequest findThreatMatchesRequest = createFindThreatMatchesRequest(urls);

		Safebrowsing.Builder safebrowsingBuilder = new Safebrowsing.Builder(httpTransport, GOOGLE_JSON_FACTORY, null)
				.setApplicationName(GOOGLE_APPLICATION_NAME);
		Safebrowsing safebrowsing = safebrowsingBuilder.build();
		
		FindThreatMatchesResponse findThreatMatchesResponse = safebrowsing.threatMatches()
				.find(findThreatMatchesRequest).setKey(key).execute();
		return findThreatMatchesResponse.getMatches();
	}

	/**
	 * Checks a URL safetiness using Google Safe Browsing
	 * 
	 * @param url url that is going to be checked
	 * @return if url is safe, returns empty string, if not, a threat code
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public String checkSingleUrl(String url) throws GeneralSecurityException, IOException {
		List<ThreatMatch> threatMatches = check(Arrays.asList(url));
		if (threatMatches != null && threatMatches.size() > 0) {
			return threatMatches.get(0).getThreatType();
		}
		else{
			return ("");
		}
	}

	/**
	 * Builds a request to GSB from a list of strings (URLs)
	 * 
	 * @param urls URLs that are going to be checked
	 * @return a FindThreatMatchesRequest entity
	 */
	private FindThreatMatchesRequest createFindThreatMatchesRequest(List<String> urls) {
		FindThreatMatchesRequest findThreatMatchesRequest = new FindThreatMatchesRequest();

		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setClientId(GOOGLE_CLIENT_ID);
		clientInfo.setClientVersion(GOOGLE_CLIENT_VERSION);
		findThreatMatchesRequest.setClient(clientInfo);

		ThreatInfo threatInfo = new ThreatInfo();
		threatInfo.setThreatTypes(GOOGLE_THREAT_TYPES);
		threatInfo.setPlatformTypes(GOOGLE_PLATFORM_TYPES);
		threatInfo.setThreatEntryTypes(GOOGLE_THREAT_ENTRYTYPES);

		List<ThreatEntry> threatEntries = new ArrayList<ThreatEntry>();

		for (String url : urls) {
			ThreatEntry threatEntry = new ThreatEntry();
			threatEntry.set("url", url);
			threatEntries.add(threatEntry);
		}
		threatInfo.setThreatEntries(threatEntries);
		findThreatMatchesRequest.setThreatInfo(threatInfo);

		return findThreatMatchesRequest;
	}

}
