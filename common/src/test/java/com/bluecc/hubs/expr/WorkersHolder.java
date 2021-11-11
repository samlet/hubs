package com.bluecc.hubs.expr;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.*;

@Component("workersHolder")
public class WorkersHolder {
    private List<String> workers = new LinkedList<>();

    public List<String> getWorkers() {
        return workers;
    }

    public Map<String, Integer> getSalaryByWorkers() {
        return salaryByWorkers;
    }

    private Map<String, Integer> salaryByWorkers = new HashMap<>();

    public WorkersHolder() {
        workers.add("John");
        workers.add("Susie");
        workers.add("Alex");
        workers.add("George");

        salaryByWorkers.put("John", 35000);
        salaryByWorkers.put("Susie", 47000);
        salaryByWorkers.put("Alex", 12000);
        salaryByWorkers.put("George", 14000);
    }

    //Getters and setters
}

