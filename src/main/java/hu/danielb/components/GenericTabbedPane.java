/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.danielb.components;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @param <T>
 * @author danielb
 */
public class GenericTabbedPane<T> extends JTabbedPane {

    private T data;
    private Map<Integer, Tab> tabs;

    public GenericTabbedPane(T data) {
        super();
        this.data = data;
        tabs = new HashMap<>();
    }

    public <K> void addTab(K id, String title, Component component) {
        super.addTab(title, component);
        tabs.put(indexOfComponent(component), new Tab<K>(id, title, component));
    }

    public T getData() {
        return data;
    }

    public void setData(T model) {
        this.data = model;
    }

    public Tab getTab(int position) {
        return tabs.get(position);
    }

    public class Tab<K> {

        K id;
        String title;
        Component component;

        public Tab(K id, String title, Component component) {
            this.id = id;
            this.title = title;
            this.component = component;
        }

        public K getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public Component getComponent() {
            return component;
        }
    }
}
