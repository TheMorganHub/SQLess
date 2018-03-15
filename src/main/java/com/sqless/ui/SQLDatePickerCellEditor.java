package com.sqless.ui;

import com.sqless.sql.objects.SQLColumn;
import com.sqless.utils.SQLUtils;
import com.sqless.utils.UIUtils;
import org.jdesktop.swingx.calendar.SingleDaySelectionModel;
import org.jdesktop.swingx.table.DatePickerCellEditor;

public class SQLDatePickerCellEditor extends DatePickerCellEditor {

    public SQLDatePickerCellEditor(SQLColumn column) {
        super(column.getDataType().equals("date") ? SQLUtils.MYSQL_DATE_FORMAT : SQLUtils.MYSQL_DATETIME_FORMAT);
        datePicker.getMonthView().setSelectionModel(new SingleDaySelectionModel());
        datePicker.getEditor().setFont(UIUtils.SEGOE_UI_FONT);
    }

}
