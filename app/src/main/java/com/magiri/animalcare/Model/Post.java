package com.magiri.animalcare.Model;

public class Post {
    private String askedBy, date, postId, publisher, question, questionimage, topic;

    public Post() {
    }

    public Post(String askedBy, String date, String postId, String publisher, String question, String questionimage, String topic) {
        this.askedBy = askedBy;
        this.date = date;
        this.postId = postId;
        this.publisher = publisher;
        this.question = question;
        this.questionimage = questionimage;
        this.topic = topic;
    }

    public String getAskedBy() {
        return askedBy;
    }

    public void setAskedBy(String askedBy) {
        this.askedBy = askedBy;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String data) {
        this.date = data;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionimage() {
        return questionimage;
    }

    public void setQuestionimage(String questionimage) {
        this.questionimage = questionimage;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
