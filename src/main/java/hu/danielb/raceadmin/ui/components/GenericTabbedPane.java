package hu.danielb.raceadmin.ui.components;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GenericTabbedPane<T> extends JTabbedPane {

    private T mData;
    private Map<Integer, Tab> tabs;

    public GenericTabbedPane(T data) {
        super();
        this.mData = data;
        tabs = new HashMap<>();
    }

    public <K> void addTab(K id, String title, Component component) {
        super.addTab(title, component);
        tabs.put(indexOfComponent(component), new Tab<>(id, title, component));
    }

    public T getData() {
        return mData;
    }

    public void setData(T model) {
        this.mData = model;
    }

    public Tab getTab(int position) {
        return tabs.get(position);
    }

    public class Tab<K> {

        K id;
        String title;
        Component component;

        Tab(K id, String title, Component component) {
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
