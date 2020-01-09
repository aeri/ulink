package urlshortener.web;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import urlshortener.domain.ShortURL;
import urlshortener.service.ClickService;
import urlshortener.service.ShortURLService;

import java.net.URI;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static urlshortener.fixtures.ShortURLFixture.someUrl;

@RunWith(SpringRunner.class)
@WebMvcTest(UrlShortenerController.class)
public class UrlShortenerTests {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private ClickService clickService;

        @MockBean
        private ShortURLService shortUrlService;

        @Test
        public void thatRedirectToReturnsTemporaryRedirectIfKeyExists() throws Exception {
                when(shortUrlService.findByKey("someKey")).thenReturn(someUrl());

                mockMvc.perform(get("/{id}", "someKey")).andDo(print()).andExpect(status().isTemporaryRedirect())
                                .andExpect(redirectedUrl("http://example.com/"));
        }

        @Test
        public void thatRedirecToReturnsNotFoundIdIfKeyDoesNotExist() throws Exception {
                when(shortUrlService.findByKey("someKey")).thenReturn(null);
                when(shortUrlService.findByKey("someKey")).thenReturn(null);

                mockMvc.perform(get("/{id}", "someKey")).andDo(print()).andExpect(status().isNotFound());
        }

        @Test
        public void thatShortenerCreatesARedirectIfTheURLisOK() throws Exception {
                configureSave(null);

                mockMvc.perform(post("/link").param("url", "http://example.com/")).andDo(print())
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.target", is("http://example.com/")));
        }

        @Test
        public void thatShortenerFailsIfTheURLisWrong() throws Exception {
                configureSave(null);

                mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
                                .andExpect(status().isBadRequest());
        }

        @Test
        public void thatShortenerFailsIfTheRepositoryReturnsNull() throws Exception {
                when(shortUrlService.save(any(String.class), any(String.class), any(String.class))).thenReturn(null);

                mockMvc.perform(post("/link").param("url", "someKey")).andDo(print())
                                .andExpect(status().isBadRequest());
        }

        private void configureSave(String sponsor) {
                when(shortUrlService.save(any(), any(), any()))
                                .then((Answer<ShortURL>) invocation -> new ShortURL("f684a3c4", "http://example.com/",
                                                URI.create("http://localhost/f684a3c4"), null, "example", "127.0.0.1",
                                                null));
        }
}
