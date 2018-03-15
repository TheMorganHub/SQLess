package com.sqless.sql.objects;

public class SQLTrigger extends SQLObject implements SQLDroppable {

    private String event;
    private String timing;
    private String statement;

    /**
     * Constructor para un trigger.
     *
     * @param name El nombre del trigger.
     * @param event El tipo de evento. INSERT, UPDATE, DELETE
     * @param timing En qué momento se dispara el trigger. BEFORE, AFTER.
     * @param statement El cuerpo del trigger.
     */
    public SQLTrigger(String name, String event, String timing, String statement) {
        super(name);
        this.event = event;
        this.timing = timing;
        this.statement = statement;
    }

    public String getEvent() {
        return event;
    }

    public String getTiming() {
        return timing;
    }

    public String getStatement() {
        return statement;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String getDropStatement() {
        return "DROP TRIGGER " + getName(true) + "\nGO";
    }
}
