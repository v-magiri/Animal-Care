package com.magiri.animalcare.Model;

public class Data {
    private String Title;
    private String Message;
    private String Typepage;

    public Data(String title, String message, String typepage) {
        this.Title = title;
        this.Message = message;
        this.Typepage = typepage;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public String getTypepage() {
        return Typepage;
    }

    public void setTypepage(String typepage) {
        Typepage = typepage;
    }
}
