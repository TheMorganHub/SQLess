package com.sqless.ui;

import com.sqless.sql.objects.SQLExecutable;
import com.sqless.sql.objects.SQLParameter;
import com.sqless.utils.UIUtils;
import java.util.List;

public class UIExecuteCallable {

    private SQLExecutable executable;

    public UIExecuteCallable(SQLExecutable executable) {
        this.executable = executable;
    }

    public void execute() {
        List<SQLParameter> parameters = executable.getParameters();
        if (parameters != null && !parameters.isEmpty()) {
            for (SQLParameter parameter : parameters) {
                Object value = UIUtils.showInputDialog("Ingresar valor a parámetro", parameter.toString(), null);
                if (value == null) {
                    return;
                }
                parameter.assignValue(value);
            }
        }
        UIClient.getInstance().createNewQueryPanelAndRun(executable.getCallStatement());
    }

}
