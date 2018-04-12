/**
 * Copyright 2017 IBM Corp. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.ibm.watson.developer_cloud.natural_language_understanding.v1;

import com.ibm.watson.developer_cloud.WatsonServiceTest;
import com.ibm.watson.developer_cloud.natural_language_understanding.v1.model.*;
import com.ibm.watson.developer_cloud.util.RetryRunner;
import org.junit.Assume;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.*;

/**
 * Natural Language Understanding integration tests.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(RetryRunner.class)
public class Demo extends WatsonServiceTest {

    private NaturalLanguageUnderstanding service;
    private String text = "In 2009, Elliot Turner launched AlchemyAPI to process the written word,"
            + " with all of its quirks and nuances, and got immediate traction.";

    private static final String NLP ="Natural language understanding (NLU) is a subtopic of natural language processing in artificial intelligence that deals with machine reading comprehension. NLU is considered an AI-hard problem.[1]\n" +
            "\n" +
            "The process of disassembling and parsing input is more complex than the reverse process of assembling output in natural language generation because of the occurrence of unknown and unexpected features in the input and the need to determine the appropriate syntactic and semantic schemes to apply to it, factors which are pre-determined when outputting language.[dubious – discuss]\n" +
            "\n" +
            "There is considerable commercial interest in the field because of its application to news-gathering, text categorization, voice-activation, archiving, and large-scale content-analysis.";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String username = getProperty("natural_language_understanding.username");
        String password = getProperty("natural_language_understanding.password");

        Assume.assumeFalse("config.properties doesn't have valid credentials.",
                (username == null) || username.equals(PLACEHOLDER));

        service = new NaturalLanguageUnderstanding(NaturalLanguageUnderstanding.VERSION_DATE_2017_02_27);
        service.setDefaultHeaders(getDefaultHeaders());
        service.setUsernameAndPassword(username, password);
        service.setEndPoint(getProperty("natural_language_understanding.url"));
    }

    /**
     * Analyze input text for keywords.
     *
     * @throws Exception the exception
     */
    @Test
    public void analyzeTextForKeywordsIsSuccessful() throws Exception {
        KeywordsOptions keywords = new KeywordsOptions.Builder()
                .sentiment(true)
                .emotion(true)
                .limit(3)
                .build();
        RelationsOptions relations = new RelationsOptions.Builder()
                .build();
        Features features = new Features.Builder().keywords(keywords).relations(relations).build();

        AnalyzeOptions parameters =
                new AnalyzeOptions.Builder()
                        .text(NLP)
                        .features(features)
                        .returnAnalyzedText(true)
                        .build();

        AnalysisResults results = service.analyze(parameters).execute();

        for (KeywordsResult result : results.getKeywords()) {
            System.out.println(result.getRelevance());
            System.out.println(result.getText());
            System.out.println(result.getSentiment());
            System.out.println(result.getEmotion());
        }

        AnalysisResults response = service
                .analyze(parameters)
                .execute();
        System.out.println(response);
    }

    /**
     * Analyze input text for relations.
     *
     * @throws Exception the exception
     */
    @Test
    public void analyzeTextForRelationsIsSuccessful() throws Exception {
        Features features = new Features.Builder().relations(new RelationsOptions.Builder().build()).build();
        AnalyzeOptions parameters =
                new AnalyzeOptions.Builder().text(text).features(features).returnAnalyzedText(true).build();

        AnalysisResults results = service.analyze(parameters).execute();

        assertNotNull(results);
        assertEquals(results.getAnalyzedText(), text);
        assertEquals(results.getLanguage(), "en");
        assertNotNull(results.getRelations());
    }

    /**
     * Analyze input text for semantic roles.
     *
     * @throws Exception the exception
     */
    @Test
    public void analyzeTextForSemanticRolesIsSuccessful() throws Exception {
        SemanticRolesOptions options = new SemanticRolesOptions.Builder()
                .limit(7)
                .keywords(true)
                .entities(true)
                .build();

        Features features = new Features.Builder().semanticRoles(options).build();
        AnalyzeOptions parameters =
                new AnalyzeOptions.Builder().text(text).features(features).returnAnalyzedText(true).build();

        AnalysisResults results = service.analyze(parameters).execute();

        assertNotNull(results);
        assertEquals(results.getAnalyzedText(), text);
        assertEquals(results.getLanguage(), "en");
        assertNotNull(results.getSemanticRoles());
        for (SemanticRolesResult result : results.getSemanticRoles()) {
            assertEquals(result.getSentence(), text);
            if (result.getSubject() != null) {
                assertNotNull(result.getSubject().getText());
            }
            if (result.getAction() != null) {
                assertNotNull(result.getAction().getText());
            }
            if (result.getObject() != null) {
                assertNotNull(result.getObject().getText());
            }
        }
    }

}
