package com.randonautica.app.Classes;

public class ReportQuestions {

    private String rQeustions [] = {
            "Did you collect any artificats",
            "Was your trip 'wow and astounding!'?",
            "Did  you set an intent?",
            "Rate the meaningfulness of your trip",
            "Rate the emotional factor",
            "Rate the importance",
            "Rate the strangeness",
            "Rate the synchronicity factor",
            "Do you want to add anything?"
    };

    public String getQuestion(int a) {
        String question = rQeustions[a];
        return question;
    }


    private String q1Answers [] = {
            "Enriching",
            "Meaningful",
            "Casual",
            "Meaningless",
            "Special",
            "Somewhat Meaningful"
    };

    public String getbutton1Anwser(int a) {
        String question = q1Answers[a];
        return question;
    }


    private String q2Answers [] = {
            "Dopamine hit",
            "Inspirational",
            "Plain",
            "Anxious",
            "Despair",
            "Dread"
    };

    public String getbutton2Anwser(int a) {
        String question = q2Answers[a];
        return question;
    }

    private String q3Answers [] = {
            "Life-changing",
            "Influential",
            "Ordinary",
            "Personal",
            "Somewhat important",
            "Waste of time",
    };

    public String getbutton3Anwser(int a) {
        String question = q3Answers[a];
        return question;
    }

    private String q4Answers [] = {
            "Woo-woo weird",
            "Strange",
            "Normal",
            "Almost",
            "Unbelievable",
            "Nothing",
    };

    public String getbutton4Anwser(int a) {
        String question = q4Answers[a];
        return question;
    }

    private String q5Answers [] = {
            "Dirk gently",
            "Mind-blowing",
            "Unbelievable",
            "Dream-like",
            "Somewhat",
            "Nothing",

    };

    public String getbutton5Anwser(int a) {
        String question = q5Answers[a];
        return question;
    }



}


