package com.testSpringBoot.SpringDemoBot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestClass {

    @Test
    public void sum(){
        int a = 2;
        int b = 3;
        int result = a+b;
        assertEquals(5, result);
    }
}
