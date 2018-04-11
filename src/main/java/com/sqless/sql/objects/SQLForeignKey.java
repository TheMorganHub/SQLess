package com.sqless.sql.objects;

public class SQLForeignKey extends SQLObject implements SQLEditable, SQLDroppable, SQLCreatable {

    private String tableName;
    private String field;
    private String referencedTableName;
    private String referencedColumnName;
    private String updateRule;
    private String deleteRule;
    private boolean hasUncommittedChanges;
    private boolean brandNew;
    private String uncommittedName;
    private String firstTimeChangeStatement;
    public static final String RULE_CASCADE = "CASCADE";

    public SQLForeignKey(String name, String tableName, String field, String referencedTableName, String referencedColumnName, String updateRule, String deleteRule) {
        super(name);
        this.tableName = tableName;
        this.field = field;
        this.referencedTableName = referencedTableName;
        this.referencedColumnName = referencedColumnName;
        this.updateRule = updateRule;
        this.deleteRule = deleteRule;
        uncommittedName = name;
        firstTimeChangeStatement = getChangeConstraintStatement();
    }

    public SQLForeignKey(String tableName) {
        super("");
        this.tableName = tableName;
        brandNew = true;
    }

    public String getFirstTimeChangeStatement() {
        return firstTimeChangeStatement;
    }

    public void setUncommittedName(String uncommittedName) {
        this.uncommittedName = uncommittedName;
    }

    public String getUncommittedName() {
        return uncommittedName;
    }

    public boolean hasUncommittedChanges() {
        return hasUncommittedChanges;
    }
    
    @Override
    public void commit(String... args) {
        hasUncommittedChanges = false;
        brandNew = false;
        tableName = args[0];
        rename(uncommittedName);
        firstTimeChangeStatement = getChangeConstraintStatement();
    }

    /**
     * Evalúa y marca esta FK como pendiente de cambios SOLO SI:
     * <ul>
     * <li>La FK no es completamente nueva.</li>
     * <li>El firstTimeChangeStatement original no es igual al ChangeStatement
     * generado por el edit que llamó a este método. Esto impide que se generen
     * cambios innecesarios.</li>
     * </ul>
     * @return 
     */
    public boolean evaluateUncommittedChanges() {
        if (!brandNew && !firstTimeChangeStatement.equals(getChangeConstraintStatement())) {
            hasUncommittedChanges = true;
            return true;
        }

        if (hasUncommittedChanges && firstTimeChangeStatement.equals(getChangeConstraintStatement())) {
            this.hasUncommittedChanges = false;
        }
        return false;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setField(String field) {
        this.field = field;
    }

    public boolean isBrandNew() {
        return brandNew;
    }

    public void isBrandNew(boolean flag) {
        this.brandNew = flag;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    public void setReferencedColumnName(String referencedColumnName) {
        this.referencedColumnName = referencedColumnName;
    }

    public void setUpdateRule(String updateRule) {
        this.updateRule = updateRule;
    }

    public void setDeleteRule(String deleteRule) {
        this.deleteRule = deleteRule;
    }

    public String getTableName() {
        return tableName;
    }

    public String getField() {
        return field;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public String getReferencedColumnName() {
        return referencedColumnName;
    }

    public String getUpdateRule() {
        return updateRule;
    }

    public String getDeleteRule() {
        return deleteRule;
    }

    @Override
    public String getCreateStatement() {
        return "CONSTRAINT `" + uncommittedName + "` FOREIGN KEY (`" + field + "`) REFERENCES `" + referencedTableName + "` (`" + referencedColumnName + "`) "
                + "ON DELETE " + (deleteRule == null ? "CASCADE" : deleteRule) + " ON UPDATE " + (updateRule == null ? "CASCADE" : updateRule);
    }

    @Override
    public String getDropStatement() {
        return "ALTER TABLE `" + tableName + "` DROP FOREIGN KEY `" + getName() + "`;";
    }

    public String getChangeConstraintStatement() {
        return "ALTER TABLE `" + tableName + "` ADD " + getCreateStatement() + ";";
    }

    @Override
    public String toString() {
        return uncommittedName;
    }

}
