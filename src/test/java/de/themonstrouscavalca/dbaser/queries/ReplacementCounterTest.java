package de.themonstrouscavalca.dbaser.queries;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReplacementCounterTest{
    @Test
    public void getCount() throws Exception{
        QueryBuilder.ReplacementCounter counter = new QueryBuilder.ReplacementCounter();
        assertEquals("Counter returned incorrectly", 1, counter.getCount());
    }

    @Test
    public void increment() throws Exception{
        QueryBuilder.ReplacementCounter counter = new QueryBuilder.ReplacementCounter();
        assertEquals("Counter returned incorrectly", 1, counter.getCount());
        counter.increment();
        counter.increment();
        assertEquals("Counter returned incorrectly", 3, counter.getCount());
    }
}