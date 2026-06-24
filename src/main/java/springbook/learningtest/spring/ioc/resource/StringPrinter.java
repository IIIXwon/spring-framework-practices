package springbook.learningtest.spring.ioc.resource;

import org.springframework.stereotype.Component;

@Component("printer")
public class StringPrinter implements Printer {
    StringBuffer sb = new StringBuffer();

    public void print(String message) {
        sb.append(message);
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
