package com.bluecc.hubs.common;

import org.junit.jupiter.api.Test;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

public class SpelExpressionTest {
    @Test
    void testExpr(){
        ExpressionParser parser = new SpelExpressionParser();
        Expression exp = parser.parseExpression("'Hello World'.concat('!')");
        String message = (String) exp.getValue();
        System.out.println(message);

        // invokes 'getBytes()'
        exp = parser.parseExpression("'Hello World'.bytes");
        byte[] bytes = (byte[]) exp.getValue();
        System.out.println(bytes.length);

        exp = parser.parseExpression("new String('hello world').toUpperCase()");
        message = exp.getValue(String.class);
        System.out.println(message);
    }
}
