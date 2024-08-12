package com.task11.model;

import com.task11.Task11Util;

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
        table.id = (int) body.get(Task11Util.TableField.ID);
        table.number = (int) body.get(Task11Util.TableField.NUMBER);
        table.places = (int) body.get(Task11Util.TableField.PLACES);
        table.isVip = (boolean) body.get(Task11Util.TableField.IS_VIP);
        table.minOrder = (int) body.get(Task11Util.TableField.MIN_ORDER);
        return table;
    }
}
