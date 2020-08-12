package com.crosslang.uimahtmltotext.controller;

import com.crosslang.uimahtmltotext.model.HtmlInput;
import com.crosslang.uimahtmltotext.service.UimaTextTransferService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@AutoConfigureMockMvc
@ContextConfiguration(classes = {HtmlInput.class, UimaTextTransferService.class})
@WebMvcTest
public class MainControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    HtmlInput input1 = new HtmlInput("<html>Hello World</html>");
    HtmlInput input2 = new HtmlInput("<html>Hello <b>World</b></html>");

    @Test
    public void html2text() throws Exception {
        testHtmlToText(input1);
        testHtmlToText(input2);
    }

    private void testHtmlToText(HtmlInput input) throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/html2text")
        .content(input.toJson())
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String resultXmi = result.getResponse().getContentAsString();
        assertNotNull(resultXmi);
    }
}
