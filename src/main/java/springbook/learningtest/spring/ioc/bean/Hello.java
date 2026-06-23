package springbook.learningtest.spring.ioc.bean;

public class Hello {
    private String name;
    private Printer printer;

    public Hello() {
    }

    public Hello(String name) {
        this.name = name;
    }

    public Hello(String name, Printer printer) {
        this.name = name;
        this.printer = printer;
    }

    public String sayHello() {
        return "Hello " + name;
    }

    public void print() {
        printer.print(sayHello());
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrinter(StringPrinter printer) {
        this.printer = printer;
    }
}
