package com.crosslang.uimahtmltotext.controller;

import com.crosslang.uimahtmltotext.model.HtmlInput;
import com.crosslang.uimahtmltotext.utils.FileReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MainControllerIT {
    public static final Logger logger = LoggerFactory.getLogger(MainControllerIT.class);

    @Autowired
    private MockMvc mockMvc;

    public static final String INPUT_HTML =
            "<table class='outer'>" +
                "<p>hm</p> New table " +
                "<p>one " +
                    "<p class='para'>two</p>" +
                "</p>" +
                "<table class='inner'>inner table</table>" +
            "</table>";

    public static final String NLP_MOCK = "nlp_mock.xml";

    @Test
    @DisplayName("GET /html2text/typesystem")
    void testTypeSystem() throws Exception {
        mockMvc.perform(get("/html2text/typesystem"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andReturn();
    }

    @Test
    @DisplayName("POST /html2text")
    void testHtmlToText() throws Exception {

        HtmlInput input = new HtmlInput(INPUT_HTML);

        mockMvc.perform(post("/html2text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));
    }

    @Test
    @DisplayName("POST /text2html")
    void testTextToHtml() throws Exception {
        HtmlInput input = new HtmlInput(INPUT_HTML);
        MvcResult result = mockMvc.perform(post("/html2text")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(input)))
                .andReturn();

        mockMvc.perform(post("/text2html")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(result.getResponse().getContentAsString())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andReturn();
    }

    @Test
    @DisplayName("Full test like in our Django application with a NLP Mock XMI")
    void fullTestWithNlpMock() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(NLP_MOCK).getFile());
        String absolutePath = file.getAbsolutePath();
        String xmi = FileReader.readAllBytes(absolutePath);

        mockMvc.perform(post("/text2html")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(xmi)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML))
                .andReturn();
    }
}
