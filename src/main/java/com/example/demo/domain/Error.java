package com.example.demo.domain;

import java.util.List;
import java.util.Objects;

public class Error {

    private List<String> errors;

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Error)) return false;
        Error error1 = (Error) o;
        return Objects.equals(errors, error1.errors);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors);
    }

    @Override
    public String toString() {
        return "Error{" +
                "errors='" + errors + '\'' +
                '}';
    }
}
