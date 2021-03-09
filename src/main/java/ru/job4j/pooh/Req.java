package ru.job4j.pooh;

public class Req {
    private final String text;

    public Req(String text) {
        this.text = text;
    }

    public String mode() {
        return text.split("/")[1];
    }

    public String getNameQueue() {
        return text.split("/")[2].split(" ")[0];
    }

    public String typeRequest() {
        return text.split(" ")[0];
    }

    public String getText() {
        return text.split(System.lineSeparator())[7];
    }

    public String getID() {
        return text.split("/")[3].split(" ")[0];
    }

}