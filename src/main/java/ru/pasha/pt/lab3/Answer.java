package ru.pasha.pt.lab3;

public class Answer {
    private static final String OK = "ok";
    private static final String ERROR = "error";

    private String status;
    private Object body;

    private Answer(String status, Object body) {
        this.status = status;
        this.body = body;
    }

    public static Answer error(Object o) {
        return new Answer(ERROR, o);
    }

    public static Answer ok(Object o) {
        return new Answer(OK, o);
    }

    public String getStatus() {
        return status;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
