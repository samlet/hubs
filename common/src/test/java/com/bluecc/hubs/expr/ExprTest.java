package com.bluecc.hubs.expr;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ExprTest {
    @Value("#{workersHolder.salaryByWorkers['John']}") // 35000
    private Integer johnSalary;

    @Value("#{workersHolder.salaryByWorkers['George']}") // 14000
    private Integer georgeSalary;

    @Value("#{workersHolder.salaryByWorkers['Susie']}") // 47000
    private Integer susieSalary;

    @Value("#{workersHolder.workers[0]}") // John
    private String firstWorker;

    @Value("#{workersHolder.workers[3]}") // George
    private String lastWorker;

    @Value("#{workersHolder.workers.size()}") // 4
    private Integer numberOfWorkers;

    @Test
    void testExpr(){
        System.out.println(numberOfWorkers);
        assertEquals(4, numberOfWorkers);
    }
}
