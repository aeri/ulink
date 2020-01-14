package urlshortener.integration;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext
public class SystemTests {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Test
    public void testHome() {
        ResponseEntity<String> entity = restTemplate.getForEntity("/", String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertTrue(entity.getHeaders().getContentType().isCompatibleWith(new MediaType("text", "html")));
        assertThat(entity.getBody(), containsString("<title>ulink"));
    }

    @Test
    public void testCss() {
        ResponseEntity<String> entity = restTemplate.getForEntity("/webjars/bootstrap/3.3.5/css/bootstrap.min.css", String.class);
        assertThat(entity.getStatusCode(), is(HttpStatus.OK));
        assertThat(entity.getHeaders().getContentType(), is(MediaType.valueOf("text/css")));
        assertThat(entity.getBody(), containsString("body"));
    }

    
    /** 
     * @throws Exception
     */
    @Test
    public void testCreateLink() throws Exception {
        ResponseEntity<String> entity = postLink("http://example.com/");

        assertThat(entity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(entity.getHeaders().getContentType(), is(new MediaType("application", "json", StandardCharsets.UTF_8)));
        ReadContext rc = JsonPath.parse(entity.getBody());
        assertThat(rc.read("$.target"), is("http://example.com/"));
    }

    
    /** 
     * @param url
     * @return ResponseEntity<String>
     */
    private ResponseEntity<String> postLink(String url) {
        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add("url", url);
        return restTemplate.postForEntity("/link", parts, String.class);
    }


}