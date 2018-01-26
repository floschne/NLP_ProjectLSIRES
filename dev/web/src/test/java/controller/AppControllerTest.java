package controller;

import controller.RouteConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String queryValue = "veryUnusualQueryString";

    @Test
    public void rendersForm() throws Exception {
        mockMvc.perform(get(RouteConfiguration.SEARCH_MAPPING_ROUTE))
                .andExpect(content().string(containsString("Welcome to LSIRES: Search")));
    }

    @Test
    public void submitsForm() throws Exception {
        mockMvc.perform(post(RouteConfiguration.RESULTS_MAPPING_ROUTE).param("value", queryValue))
                .andExpect(content().string(containsString("LSIRES: Results")))
                .andExpect(content().string(containsString(queryValue)));
    }
}
