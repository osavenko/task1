package com.task10.model;

import com.task10.Task10Util;

import java.util.Map;

public class Table {
    private int id;
    private int number;
    private int places;
    private boolean isVip;
    private int minOrder;

    public Table() {
    }

    public int getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public int getPlaces() {
        return places;
    }

    public boolean isVip() {
        return isVip;
    }

    public int getMinOrder() {
        return minOrder;
    }
    public static Table getInstance(Map<String, Object> body){
        final Table table = new Table();
        table.id = (int) body.get(Task10Util.TableField.ID);
        table.number = (int) body.get(Task10Util.TableField.NUMBER);
        table.places = (int) body.get(Task10Util.TableField.PLACES);
        table.isVip = (boolean) body.get(Task10Util.TableField.IS_VIP);
        table.minOrder = (int) body.get(Task10Util.TableField.MIN_ORDER);
        return table;
    }
}
